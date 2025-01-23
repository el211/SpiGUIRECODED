package com.samjakob.spigui.events;

import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerEvent;

/**
 * Represents an event that occurs when an SGButton is clicked.
 */
public class SGButtonClickEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final SGButton button;
    private final ClickType clickType;
    private final InventoryAction action;
    private final String inventoryTitle;
    private final ItemStack currentItem;
    private final InventoryClickEvent originalEvent;

    private boolean cancelled;

    /**
     * Constructs an SGButtonClickEvent.
     *
     * @param player         The player who clicked the button.
     * @param button         The button that was clicked.
     * @param clickType      The type of click performed.
     * @param action         The inventory action performed.
     * @param inventoryTitle The title of the inventory where the button resides.
     * @param currentItem    The item currently clicked on.
     * @param originalEvent  The original InventoryClickEvent.
     */
    public SGButtonClickEvent(Player player, SGButton button, ClickType clickType, InventoryAction action, String inventoryTitle, ItemStack currentItem, InventoryClickEvent originalEvent) {
        super(player);
        this.button = button;
        this.clickType = clickType;
        this.action = action;
        this.inventoryTitle = inventoryTitle;
        this.currentItem = currentItem;
        this.originalEvent = originalEvent;
    }

    /**
     * Gets the player who clicked the button.
     *
     * @return The player.
     */
    public Player getWhoClicked() {
        return getPlayer();
    }

    /**
     * Gets the SGButton that was clicked.
     *
     * @return The button.
     */
    public SGButton getButton() {
        return button;
    }

    /**
     * Gets the type of click performed.
     *
     * @return The ClickType.
     */
    public ClickType getClickType() {
        return clickType;
    }

    /**
     * Gets the inventory action performed.
     *
     * @return The InventoryAction.
     */
    public InventoryAction getAction() {
        return action;
    }

    /**
     * Gets the title of the inventory where the button resides.
     *
     * @return The inventory title.
     */
    public String getInventoryTitle() {
        return inventoryTitle;
    }

    /**
     * Gets the item currently clicked on in the inventory.
     *
     * @return The current item.
     */
    public ItemStack getCurrentItem() {
        return currentItem;
    }

    /**
     * Gets the original InventoryClickEvent.
     *
     * @return The original event.
     */
    public InventoryClickEvent getOriginalEvent() {
        return originalEvent;
    }

    /**
     * Sets the result of the original event.
     *
     * @param result The result to set.
     */
    public void setResult(InventoryClickEvent.Result result) {
        originalEvent.setResult(result);
    }

    /**
     * Checks if the event is cancelled.
     *
     * @return True if the event is cancelled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation status of the event.
     *
     * @param cancelled True to cancel the event, false otherwise.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        originalEvent.setCancelled(cancelled); // Reflect cancellation in the original event
    }

    /**
     * Checks if the click was a left-click.
     *
     * @return True if the click was a left-click, false otherwise.
     */
    public boolean isLeftClick() {
        return clickType == ClickType.LEFT || clickType == ClickType.SHIFT_LEFT;
    }

    /**
     * Checks if the click was a right-click.
     *
     * @return True if the click was a right-click, false otherwise.
     */
    public boolean isRightClick() {
        return clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT;
    }

    /**
     * Checks if the click involved a middle mouse button.
     *
     * @return True if the click was a middle mouse button, false otherwise.
     */
    public boolean isMiddleClick() {
        return clickType == ClickType.MIDDLE;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return The HandlerList.
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the static handler list for this event class.
     *
     * @return The HandlerList.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Provides a string representation of the event for debugging purposes.
     *
     * @return A string representation of the event.
     */
    @Override
    public String toString() {
        return "SGButtonClickEvent{" +
                "player=" + getPlayer().getName() +
                ", button=" + button +
                ", clickType=" + clickType +
                ", action=" + action +
                ", inventoryTitle='" + inventoryTitle + '\'' +
                ", currentItem=" + (currentItem != null ? currentItem.toString() : "null") +
                ", cancelled=" + cancelled +
                '}';
    }
}
