package com.samjakob.spigui.menu;

import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.toolbar.SGToolbarBuilder;
import com.samjakob.spigui.toolbar.SGToolbarButtonType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;


public class SGMenu implements InventoryHolder {

    private final JavaPlugin owner;
    private final SpiGUI spiGUI;
    private String name;
    private String tag;
    private int rowsPerPage;
    private final Map<Integer, SGButton> items;
    private final HashSet<Integer> stickiedSlots;
    private int currentPage;
    private boolean blockDefaultInteractions;
    private boolean enableAutomaticPagination;

    private SGToolbarBuilder toolbarBuilder;
    private Consumer<SGMenu> onClose;
    private Consumer<SGMenu> onPageChange;


    private HashSet<ClickType> permittedMenuClickTypes;

    private HashSet<InventoryAction> blockedMenuActions;

    private HashSet<InventoryAction> blockedAdjacentActions;

    private static final ClickType[] DEFAULT_PERMITTED_MENU_CLICK_TYPES = new ClickType[]{
            ClickType.LEFT,
            ClickType.RIGHT
    };

    private static final InventoryAction[] DEFAULT_BLOCKED_MENU_ACTIONS = new InventoryAction[] {
            InventoryAction.MOVE_TO_OTHER_INVENTORY,
            InventoryAction.COLLECT_TO_CURSOR
    };

    private static final InventoryAction[] DEFAULT_BLOCKED_ADJACENT_ACTIONS = new InventoryAction[] {
            InventoryAction.MOVE_TO_OTHER_INVENTORY,
            InventoryAction.COLLECT_TO_CURSOR
    };

    public SGMenu(JavaPlugin owner, SpiGUI spiGUI, String name, int rowsPerPage, String tag) {
        this.owner = owner;
        this.spiGUI = spiGUI;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.rowsPerPage = rowsPerPage;
        this.tag = tag;

        this.items = new HashMap<>();
        this.stickiedSlots = new HashSet<>();

        this.currentPage = 0;
        // Initialize permitted and blocked actions with defaults
        this.permittedMenuClickTypes = new HashSet<>(Arrays.asList(DEFAULT_PERMITTED_MENU_CLICK_TYPES));
        this.blockedMenuActions = new HashSet<>(Arrays.asList(DEFAULT_BLOCKED_MENU_ACTIONS));
        this.blockedAdjacentActions = new HashSet<>(Arrays.asList(DEFAULT_BLOCKED_ADJACENT_ACTIONS));
    }


    /// INVENTORY SETTINGS ///

    /**
     * This is a per-inventory version of {@link SpiGUI#setBlockDefaultInteractions(boolean)}.
     *
     * @see SpiGUI#setBlockDefaultInteractions(boolean)
     * @param blockDefaultInteractions Whether the default behavior of click events should be cancelled.
     */
    public void setBlockDefaultInteractions(boolean blockDefaultInteractions) {
        this.blockDefaultInteractions = blockDefaultInteractions;
    }

    /**
     * This is a per-inventory version of {@link SpiGUI#areDefaultInteractionsBlocked()}.
     *
     * @see SpiGUI#areDefaultInteractionsBlocked()
     * @return Whether the default behavior of click events should be cancelled.
     */
    public Boolean areDefaultInteractionsBlocked() {
        return blockDefaultInteractions;
    }

    /**
     * This is a per-inventory version of {@link SpiGUI#setEnableAutomaticPagination(boolean)}.
     * If this value is set, it overrides the per-plugin option set in {@link SpiGUI}.
     *
     * @see SpiGUI#setEnableAutomaticPagination(boolean)
     * @param enableAutomaticPagination Whether pagination buttons should be automatically added.
     */
    public void setAutomaticPaginationEnabled(boolean enableAutomaticPagination) {
        this.enableAutomaticPagination = enableAutomaticPagination;
    }

    public Boolean isAutomaticPaginationEnabled() {
        return enableAutomaticPagination;
    }

    public void setToolbarBuilder(SGToolbarBuilder toolbarBuilder) {
        this.toolbarBuilder = toolbarBuilder;
    }

    public SGToolbarBuilder getToolbarBuilder() {
        return this.toolbarBuilder;
    }

    public JavaPlugin getOwner() {
        return owner;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public int getPageSize() {
        return rowsPerPage * 9;
    }

    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public String getTag() {
        return tag;
    }


    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setName(String name) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
    }


    public void setRawName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public void addButton(SGButton button) {
        // If slot 0 is empty, but it's the 'highest filled slot', then set slot 0 to contain button.
        // (This is an edge case for when the whole inventory is empty).
        if (getHighestFilledSlot() == 0 && getButton(0) == null) {
            setButton(0, button);
            return;
        }

        // Otherwise, add one to the highest filled slot, then use that slot for the new button.
        setButton(getHighestFilledSlot() + 1, button);
    }


    public void addButtons(SGButton... buttons) {
        for (SGButton button : buttons) addButton(button);
    }


    public void setButton(int slot, SGButton button) {
        items.put(slot, button);
    }


    public void setButton(int page, int slot, SGButton button) {
        if (slot < 0 || slot > getPageSize())
            return;

        setButton((page * getPageSize()) + slot, button);
    }


    public void removeButton(int slot) {
        items.remove(slot);
    }


    public void removeButton(int page, int slot) {
        if (slot < 0 || slot > getPageSize())
            return;

        removeButton((page * getPageSize()) + slot);
    }


    public SGButton getButton(int slot) {
        if (slot < 0 || slot > getHighestFilledSlot())
            return null;

        return items.get(slot);
    }


    public SGButton getButton(int page, int slot) {
        if (slot < 0 || slot > getPageSize())
            return null;

        return getButton((page * getPageSize()) + slot);
    }


    public int getCurrentPage() {
        return currentPage;
    }


    public void setCurrentPage (int page) {
        this.currentPage = page;
        if (this.onPageChange != null) this.onPageChange.accept(this);
    }


    public int getMaxPage() {
        return (int) Math.ceil(((double) getHighestFilledSlot() + 1) / ((double) getPageSize()));
    }


    public int getHighestFilledSlot() {
        int slot = 0;

        for (int nextSlot : items.keySet()) {
            if (items.get(nextSlot) != null && nextSlot > slot)
                slot = nextSlot;
        }

        return slot;
    }


    public boolean nextPage(HumanEntity viewer) {
        if (currentPage < getMaxPage() - 1) {
            currentPage++;
            refreshInventory(viewer);
            if (this.onPageChange != null) this.onPageChange.accept(this);
            return true;
        } else {
            return false;
        }
    }

    public boolean previousPage(HumanEntity viewer) {
        if (currentPage > 0) {
            currentPage--;
            refreshInventory(viewer);
            if (this.onPageChange != null) this.onPageChange.accept(this);
            return true;
        } else {
            return false;
        }
    }


    public void stickSlot(int slot) {
        if (slot < 0 || slot >= getPageSize())
            return;

        this.stickiedSlots.add(slot);
    }


    public void unstickSlot(int slot) {
        this.stickiedSlots.remove(slot);
    }


    public void clearStickiedSlots() {
        this.stickiedSlots.clear();
    }


    public boolean isStickiedSlot(int slot) {
        if (slot < 0 || slot >= getPageSize())
            return false;

        return this.stickiedSlots.contains(slot);
    }

    public void clearAllButStickiedSlots() {
        this.currentPage = 0;
        items.entrySet().removeIf(item -> !isStickiedSlot(item.getKey()));
    }


    public Consumer<SGMenu> getOnClose() {
        return this.onClose;
    }


    public void setOnClose(Consumer<SGMenu> onClose) {
        this.onClose = onClose;
    }

    public Consumer<SGMenu> getOnPageChange() {
        return this.onPageChange;
    }


    public void setOnPageChange(Consumer<SGMenu> onPageChange) {
        this.onPageChange = onPageChange;
    }

    public Set<ClickType> getPermittedMenuClickTypes() {
        if (this.permittedMenuClickTypes == null) {
            this.permittedMenuClickTypes = new HashSet<>(Arrays.asList(DEFAULT_PERMITTED_MENU_CLICK_TYPES));
        }
        return this.permittedMenuClickTypes;
    }

    public HashSet<InventoryAction> getBlockedMenuActions() {
        if (this.blockedMenuActions == null) {
            this.blockedMenuActions = new HashSet<>(Arrays.asList(DEFAULT_BLOCKED_MENU_ACTIONS));
        }
        return this.blockedMenuActions;
    }

    public HashSet<InventoryAction> getBlockedAdjacentActions() {
        if (this.blockedAdjacentActions == null) {
            this.blockedAdjacentActions = new HashSet<>(Arrays.asList(DEFAULT_BLOCKED_ADJACENT_ACTIONS));
        }
        return this.blockedAdjacentActions;
    }

    public void setPermittedMenuClickTypes(ClickType... clickTypes) {
        if (this.permittedMenuClickTypes == null) {
            this.permittedMenuClickTypes = new HashSet<>();
        }
        this.permittedMenuClickTypes.clear();
        this.permittedMenuClickTypes.addAll(Arrays.asList(clickTypes));
    }

    public void setBlockedMenuActions(InventoryAction... actions) {
        if (this.blockedMenuActions == null) {
            this.blockedMenuActions = new HashSet<>();
        }
        this.blockedMenuActions.clear();
        this.blockedMenuActions.addAll(Arrays.asList(actions));
    }


    public void setBlockedAdjacentActions(InventoryAction... actions) {
        this.blockedAdjacentActions = new HashSet<>(Arrays.asList(actions));
    }


    public void addPermittedClickType(ClickType clickType) {
        this.permittedMenuClickTypes.add(clickType);
    }


    public void addBlockedMenuAction(InventoryAction action) {
        this.blockedMenuActions.add(action);
    }


    public void addBlockedAdjacentAction(InventoryAction action) {
        this.getBlockedAdjacentActions().add(action);
    }


    public void removePermittedClickType(ClickType clickType) {
        this.permittedMenuClickTypes.remove(clickType);
    }


    public void removeBlockedMenuAction(InventoryAction action) {
        this.blockedMenuActions.remove(action);
    }


    public void removeBlockedAdjacentAction(InventoryAction action) {
        this.getBlockedAdjacentActions().remove(action);
    }


    public void refreshInventory(HumanEntity viewer) {
        // If the open inventory isn't an SGMenu - or if it isn't this inventory, do nothing.
        if (
                !(viewer.getOpenInventory().getTopInventory().getHolder() instanceof SGMenu)
                        || viewer.getOpenInventory().getTopInventory().getHolder() != this
        ) return;

        // If the new size is different, we'll need to open a new inventory.
        if (viewer.getOpenInventory().getTopInventory().getSize() != getPageSize() + (getMaxPage() > 0 ? 9 : 0)) {
            viewer.openInventory(getInventory());
            return;
        }

        // If the name has changed, we'll need to open a new inventory.
        String newName = name.replace("{currentPage}", String.valueOf(currentPage + 1))
                .replace("{maxPage}", String.valueOf(getMaxPage()));
        if (!viewer.getOpenInventory().getTitle().equals(newName)) {
            viewer.openInventory(getInventory());
            return;
        }

        // Otherwise, we can refresh the contents without re-opening the inventory.
        viewer.getOpenInventory().getTopInventory().setContents(getInventory().getContents());
    }

    @Override
    public Inventory getInventory() {
        boolean isAutomaticPaginationEnabled = spiGUI.isAutomaticPaginationEnabled();
        if (isAutomaticPaginationEnabled() != null) {
            isAutomaticPaginationEnabled = isAutomaticPaginationEnabled();
        }

        boolean needsPagination = getMaxPage() > 0 && isAutomaticPaginationEnabled;

        Inventory inventory = Bukkit.createInventory(this, (
                        (needsPagination)
                                // Pagination enabled: add the bottom toolbar row.
                                ? getPageSize() + 9
                                // Pagination not required or disabled.
                                : getPageSize()
                ),
                name.replace("{currentPage}", String.valueOf(currentPage + 1))
                        .replace("{maxPage}", String.valueOf(getMaxPage()))
        );

        // Add the main inventory items.
        for (int key = currentPage * getPageSize(); key < (currentPage + 1) * getPageSize(); key++) {
            // If we've already reached the maximum assigned slot, stop assigning
            // slots.
            if (key > getHighestFilledSlot()) break;

            if (items.containsKey(key)) {
                inventory.setItem(key - (currentPage * getPageSize()), items.get(key).getIcon());
            }
        }

        // Update the stickied slots.
        for (int stickiedSlot : stickiedSlots) {
            inventory.setItem(stickiedSlot, items.get(stickiedSlot).getIcon());
        }

        // Render the pagination items.
        if (needsPagination) {
            SGToolbarBuilder toolbarButtonBuilder = spiGUI.getDefaultToolbarBuilder();
            if (getToolbarBuilder() != null) {
                toolbarButtonBuilder = getToolbarBuilder();
            }

            int pageSize = getPageSize();
            for (int i = pageSize; i < pageSize + 9; i++) {
                int offset = i - pageSize;

                SGButton paginationButton = toolbarButtonBuilder.buildToolbarButton(
                        offset, getCurrentPage(), SGToolbarButtonType.getDefaultForSlot(offset),this
                );
                inventory.setItem(i, paginationButton != null ? paginationButton.getIcon() : null);
            }
        }

        return inventory;
    }

}