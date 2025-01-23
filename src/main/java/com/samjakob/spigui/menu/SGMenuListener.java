package com.samjakob.spigui.menu;

import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.events.SGButtonClickEvent;
import com.samjakob.spigui.toolbar.SGToolbarBuilder;
import com.samjakob.spigui.toolbar.SGToolbarButtonType;
import java.util.Objects;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;

public class SGMenuListener implements Listener {
    private final JavaPlugin owner;
    private final SpiGUI SpiGUI;

    public SGMenuListener(JavaPlugin owner, SpiGUI SpiGUI) {
        this.owner = owner;
        this.SpiGUI = SpiGUI;
    }

    private static boolean isInvalidSGMenuInventory(Inventory inventory) {
        return inventory == null || !(inventory.getHolder() instanceof SGMenu);
    }

    public static boolean willHandleInventoryEvent(JavaPlugin plugin, Inventory inventory) {
        return !isInvalidSGMenuInventory(inventory) && Objects.equals(((SGMenu) inventory.getHolder()).getOwner(), plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isInvalidSGMenuInventory(event.getClickedInventory())) {
            return; // Exit early for invalid inventories
        }

        SGMenu clickedGui = (SGMenu) event.getClickedInventory().getHolder();
        if (clickedGui != null) {
            Player player = (Player) event.getWhoClicked();

            // Handle permitted click types
            if (clickedGui.getPermittedMenuClickTypes().stream().noneMatch(type -> type == event.getClick())) {
                event.setResult(Result.DENY);
            }
            // Handle blocked menu actions
            else if (clickedGui.getBlockedMenuActions().stream().anyMatch(action -> action == event.getAction())) {
                event.setResult(Result.DENY);
            }
            // Handle pagination buttons
            else if (event.getSlot() > clickedGui.getPageSize()) {
                int offset = event.getSlot() - clickedGui.getPageSize();
                SGToolbarBuilder paginationButtonBuilder = this.SpiGUI.getDefaultToolbarBuilder();
                if (clickedGui.getToolbarBuilder() != null) {
                    paginationButtonBuilder = clickedGui.getToolbarBuilder();
                }

                SGToolbarButtonType buttonType = SGToolbarButtonType.getDefaultForSlot(offset);
                SGButton paginationButton = paginationButtonBuilder.buildToolbarButton(offset, clickedGui.getCurrentPage(), buttonType, clickedGui);
                if (paginationButton != null && paginationButton.getListener() != null) {
                    SGButtonClickEvent customEvent = new SGButtonClickEvent(
                            player,
                            paginationButton,
                            event.getClick(),
                            event.getAction(),
                            Objects.requireNonNull(event.getView().getTopInventory().getHolder(), "Inventory holder cannot be null").toString(),
                            event.getCurrentItem(),  // Added the current item
                            event                    // Added the original event
                    );
                    paginationButton.getListener().onClick(customEvent);
                }

            }
            // Handle stickied slots
            else if (clickedGui.isStickiedSlot(event.getSlot())) {
                SGButton button = clickedGui.getButton(0, event.getSlot());
                if (button != null && button.getListener() != null) {
                    SGButtonClickEvent customEvent = new SGButtonClickEvent(
                            player,
                            button,
                            event.getClick(),
                            event.getAction(),
                            Objects.requireNonNull(event.getView().getTopInventory().getHolder(), "Inventory holder cannot be null").toString(),
                            event.getCurrentItem(),  // Added the current item
                            event                    // Added the original event
                    );
                    button.getListener().onClick(customEvent);
                }

            }
            // Handle regular buttons
            else {
                SGButton button = clickedGui.getButton(clickedGui.getCurrentPage(), event.getSlot());
                if (button != null && button.getListener() != null) {
                    SGButtonClickEvent customEvent = new SGButtonClickEvent(
                            player,
                            button,
                            event.getClick(),
                            event.getAction(),
                            Objects.requireNonNull(event.getView().getTopInventory().getHolder(), "Inventory holder cannot be null").toString(),
                            event.getCurrentItem(),  // Added the current item
                            event                    // Added the original event
                    );
                    button.getListener().onClick(customEvent);
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onAdjacentInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory() == null || isInvalidSGMenuInventory(event.getView().getTopInventory())) {
            return; // Exit early for invalid inventories
        }

        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            SGMenu clickedGui = (SGMenu) event.getClickedInventory().getHolder();
            if (clickedGui.getBlockedAdjacentActions().stream().anyMatch(action -> action == event.getAction())) {
                event.setResult(Result.DENY);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isInvalidSGMenuInventory(event.getInventory())) {
            return; // Exit early for invalid inventories
        }

        SGMenu clickedGui = (SGMenu) event.getInventory().getHolder();
        if (this.slotsIncludeTopInventory(event.getView(), event.getRawSlots())) {
            event.setResult(Result.DENY);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (isInvalidSGMenuInventory(event.getInventory())) {
            return; // Exit early for invalid inventories
        }

        SGMenu clickedGui = (SGMenu) event.getInventory().getHolder();
        if (Objects.equals(clickedGui.getOwner(), this.owner)) {
            if (clickedGui.getOnClose() != null) {
                clickedGui.getOnClose().accept(clickedGui);
            }
        }
    }

    private boolean slotsIncludeTopInventory(InventoryView view, Set<Integer> slots) {
        return slots.stream().anyMatch(slot -> {
            if (slot >= view.getTopInventory().getSize()) {
                return false;
            } else {
                return slot == view.convertSlot(slot);
            }
        });
    }
}
