//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.samjakob.spigui.menu;

import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.toolbar.SGToolbarBuilder;
import com.samjakob.spigui.toolbar.SGToolbarButtonType;
import net.md_5.bungee.api.ChatColor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public class SGMenu implements InventoryHolder {
    private final JavaPlugin owner;
    private final SpiGUI SpiGUI;
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
    private static final ClickType[] DEFAULT_PERMITTED_MENU_CLICK_TYPES;
    private static final InventoryAction[] DEFAULT_BLOCKED_MENU_ACTIONS;
    private static final InventoryAction[] DEFAULT_BLOCKED_ADJACENT_ACTIONS;

    public SGMenu(JavaPlugin owner, SpiGUI SpiGUI, String name, int rowsPerPage, String tag) {
        this.owner = owner;
        this.SpiGUI = SpiGUI;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.rowsPerPage = rowsPerPage;
        this.tag = tag;
        this.items = new HashMap();
        this.stickiedSlots = new HashSet();
        this.currentPage = 0;
        this.permittedMenuClickTypes = new HashSet(Arrays.asList(DEFAULT_PERMITTED_MENU_CLICK_TYPES));
    }

    public void setBlockDefaultInteractions(boolean blockDefaultInteractions) {
        this.blockDefaultInteractions = blockDefaultInteractions;
    }

    public Boolean areDefaultInteractionsBlocked() {
        return this.blockDefaultInteractions;
    }

    public void setAutomaticPaginationEnabled(boolean enableAutomaticPagination) {
        this.enableAutomaticPagination = enableAutomaticPagination;
    }
    public static String translateHexColors(String input) {
        return input.replaceAll("(?i)&#([0-9A-F]{6})", "§x§$1§x")
                .replaceAll("(?i)(?<!§x§x)§(?=[0-9A-F]{6})", "");
    }
    public static String applyGradient(String text, Color startColor, Color endColor) {
        StringBuilder result = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            int red = (int) (startColor.getRed() * (1 - ratio) + endColor.getRed() * ratio);
            int green = (int) (startColor.getGreen() * (1 - ratio) + endColor.getGreen() * ratio);
            int blue = (int) (startColor.getBlue() * (1 - ratio) + endColor.getBlue() * ratio);

            String hexColor = String.format("§x§%s§%s§%s§%s§%s§%s",
                    Integer.toHexString((red >> 4) & 0xF),
                    Integer.toHexString(red & 0xF),
                    Integer.toHexString((green >> 4) & 0xF),
                    Integer.toHexString(green & 0xF),
                    Integer.toHexString((blue >> 4) & 0xF),
                    Integer.toHexString(blue & 0xF)
            );
            result.append(hexColor).append(text.charAt(i));
        }
        return result.toString();
    }

    public Boolean isAutomaticPaginationEnabled() {
        return this.enableAutomaticPagination;
    }

    public void setToolbarBuilder(SGToolbarBuilder toolbarBuilder) {
        this.toolbarBuilder = toolbarBuilder;
    }

    public SGToolbarBuilder getToolbarBuilder() {
        return this.toolbarBuilder;
    }

    public JavaPlugin getOwner() {
        return this.owner;
    }

    public int getRowsPerPage() {
        return this.rowsPerPage;
    }

    public int getPageSize() {
        return this.rowsPerPage * 9;
    }

    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public String getTag() {
        return this.tag;
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
        return this.name;
    }

    public void addButton(SGButton button) {
        if (this.getHighestFilledSlot() == 0 && this.getButton(0) == null) {
            this.setButton(0, button);
        } else {
            this.setButton(this.getHighestFilledSlot() + 1, button);
        }
    }

    public void addButtons(SGButton... buttons) {
        for(SGButton button : buttons) {
            this.addButton(button);
        }

    }

    public void setButton(int slot, SGButton button) {
        this.items.put(slot, button);
    }

    public void setButton(int page, int slot, SGButton button) {
        if (slot >= 0 && slot <= this.getPageSize()) {
            this.setButton(page * this.getPageSize() + slot, button);
        }
    }

    public void removeButton(int slot) {
        this.items.remove(slot);
    }

    public void removeButton(int page, int slot) {
        if (slot >= 0 && slot <= this.getPageSize()) {
            this.removeButton(page * this.getPageSize() + slot);
        }
    }

    public SGButton getButton(int slot) {
        return slot >= 0 && slot <= this.getHighestFilledSlot() ? (SGButton)this.items.get(slot) : null;
    }

    public SGButton getButton(int page, int slot) {
        return slot >= 0 && slot <= this.getPageSize() ? this.getButton(page * this.getPageSize() + slot) : null;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
        if (this.onPageChange != null) {
            this.onPageChange.accept(this);
        }

    }

    public int getMaxPage() {
        return (int)Math.ceil(((double)this.getHighestFilledSlot() + (double)1.0F) / (double)this.getPageSize());
    }

    public int getHighestFilledSlot() {
        int slot = 0;

        for(int nextSlot : this.items.keySet()) {
            if (this.items.get(nextSlot) != null && nextSlot > slot) {
                slot = nextSlot;
            }
        }

        return slot;
    }

    public boolean nextPage(HumanEntity viewer) {
        if (this.currentPage < this.getMaxPage() - 1) {
            ++this.currentPage;
            this.refreshInventory(viewer);
            if (this.onPageChange != null) {
                this.onPageChange.accept(this);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean previousPage(HumanEntity viewer) {
        if (this.currentPage > 0) {
            --this.currentPage;
            this.refreshInventory(viewer);
            if (this.onPageChange != null) {
                this.onPageChange.accept(this);
            }

            return true;
        } else {
            return false;
        }
    }

    public void stickSlot(int slot) {
        if (slot >= 0 && slot < this.getPageSize()) {
            this.stickiedSlots.add(slot);
        }
    }

    public void unstickSlot(int slot) {
        this.stickiedSlots.remove(slot);
    }

    public void clearStickiedSlots() {
        this.stickiedSlots.clear();
    }

    public boolean isStickiedSlot(int slot) {
        return slot >= 0 && slot < this.getPageSize() ? this.stickiedSlots.contains(slot) : false;
    }

    public void clearAllButStickiedSlots() {
        this.currentPage = 0;
        this.items.entrySet().removeIf((item) -> !this.isStickiedSlot((Integer)item.getKey()));
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

    public HashSet<ClickType> getPermittedMenuClickTypes() {
        if (this.permittedMenuClickTypes == null) {
            this.permittedMenuClickTypes = new HashSet(Arrays.asList(DEFAULT_PERMITTED_MENU_CLICK_TYPES));
        }

        return this.permittedMenuClickTypes;
    }

    public HashSet<InventoryAction> getBlockedMenuActions() {
        return this.blockedMenuActions != null ? this.blockedMenuActions : new HashSet();
    }

    public HashSet<InventoryAction> getBlockedAdjacentActions() {
        return this.blockedAdjacentActions;
    }

    public void setPermittedMenuClickTypes(ClickType... clickTypes) {
        if (this.permittedMenuClickTypes == null) {
            this.permittedMenuClickTypes = new HashSet();
        }

        this.permittedMenuClickTypes.clear();
        this.permittedMenuClickTypes.addAll(Arrays.asList(clickTypes));
    }

    public void setBlockedMenuActions(InventoryAction... actions) {
        this.blockedMenuActions = new HashSet(Arrays.asList(actions));
    }

    public void setBlockedAdjacentActions(InventoryAction... actions) {
        this.blockedAdjacentActions = new HashSet(Arrays.asList(actions));
    }

    public void addPermittedClickType(ClickType clickType) {
        if (this.permittedMenuClickTypes == null) {
            this.permittedMenuClickTypes = new HashSet(Arrays.asList(DEFAULT_PERMITTED_MENU_CLICK_TYPES));
        }

        this.permittedMenuClickTypes.add(clickType);
    }

    public void addBlockedMenuAction(InventoryAction action) {
        this.blockedMenuActions.add(action);
    }

    public void addBlockedAdjacentAction(InventoryAction action) {
        this.getBlockedAdjacentActions().add(action);
    }

    public void removePermittedClickType(ClickType clickType) {
        if (this.permittedMenuClickTypes != null) {
            this.permittedMenuClickTypes.remove(clickType);
        }

    }

    public void removeBlockedMenuAction(InventoryAction action) {
        this.blockedMenuActions.remove(action);
    }

    public void removeBlockedAdjacentAction(InventoryAction action) {
        this.getBlockedAdjacentActions().remove(action);
    }

    public void refreshInventory(HumanEntity viewer) {
        if (viewer.getOpenInventory().getTopInventory().getHolder() instanceof SGMenu && viewer.getOpenInventory().getTopInventory().getHolder() == this) {
            if (viewer.getOpenInventory().getTopInventory().getSize() != this.getPageSize() + (this.getMaxPage() > 0 ? 9 : 0)) {
                viewer.openInventory(this.getInventory());
            } else {
                String newName = this.name.replace("{currentPage}", String.valueOf(this.currentPage + 1)).replace("{maxPage}", String.valueOf(this.getMaxPage()));
                if (!viewer.getOpenInventory().getTitle().equals(newName)) {
                    viewer.openInventory(this.getInventory());
                } else {
                    viewer.getOpenInventory().getTopInventory().setContents(this.getInventory().getContents());
                }
            }
        }
    }

    @Override
    public Inventory getInventory() {
        boolean isAutomaticPaginationEnabled = this.SpiGUI.isAutomaticPaginationEnabled();
        if (this.isAutomaticPaginationEnabled() != null) {
            isAutomaticPaginationEnabled = this.isAutomaticPaginationEnabled();
        }

        boolean needsPagination = this.getMaxPage() > 0 && isAutomaticPaginationEnabled;

        // Create inventory with pagination size or normal size.
        String inventoryTitle = this.name
                .replace("{currentPage}", String.valueOf(this.currentPage + 1))
                .replace("{maxPage}", String.valueOf(this.getMaxPage()));
        Inventory inventory = Bukkit.createInventory(this,
                needsPagination ? this.getPageSize() + 9 : this.getPageSize(),
                inventoryTitle);

        // Populate inventory with items for the current page.
        for (int key = this.currentPage * this.getPageSize();
             key < (this.currentPage + 1) * this.getPageSize() && key <= this.getHighestFilledSlot();
             ++key) {
            SGButton button = (SGButton) this.items.get(key);
            if (button != null && button.getIcon() != null) {
                inventory.setItem(key - this.currentPage * this.getPageSize(), button.getIcon());
            }
        }

        // Add stickied slots (always present on all pages).
        for (int stickiedSlot : this.stickiedSlots) {
            SGButton stickiedButton = (SGButton) this.items.get(stickiedSlot);
            if (stickiedButton != null && stickiedButton.getIcon() != null) {
                inventory.setItem(stickiedSlot, stickiedButton.getIcon());
            }
        }

        // Handle pagination buttons if pagination is enabled.
        if (needsPagination) {
            SGToolbarBuilder toolbarButtonBuilder = this.SpiGUI.getDefaultToolbarBuilder();
            if (this.getToolbarBuilder() != null) {
                toolbarButtonBuilder = this.getToolbarBuilder();
            }

            int pageSize = this.getPageSize();
            for (int i = pageSize; i < pageSize + 9; ++i) {
                int offset = i - pageSize;
                SGToolbarButtonType buttonType = SGToolbarButtonType.getDefaultForSlot(offset);
                SGButton paginationButton = toolbarButtonBuilder.buildToolbarButton(offset, this.getCurrentPage(), buttonType, this);
                if (paginationButton != null && paginationButton.getIcon() != null) {
                    inventory.setItem(i, paginationButton.getIcon());
                }
            }
        }

        return inventory;
    }

    static {
        DEFAULT_PERMITTED_MENU_CLICK_TYPES = new ClickType[]{ClickType.LEFT, ClickType.RIGHT};
        DEFAULT_BLOCKED_MENU_ACTIONS = new InventoryAction[]{InventoryAction.MOVE_TO_OTHER_INVENTORY, InventoryAction.COLLECT_TO_CURSOR};
        DEFAULT_BLOCKED_ADJACENT_ACTIONS = new InventoryAction[]{InventoryAction.MOVE_TO_OTHER_INVENTORY, InventoryAction.COLLECT_TO_CURSOR};
    }
}
