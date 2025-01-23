package com.samjakob.spigui.buttons;

import com.samjakob.spigui.events.SGButtonClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a button in the SG GUI, which consists of an icon and an optional listener for interactions.
 */
public class SGButton {
    private ItemStack icon; // Removed 'final' to allow dynamic updates
    private SGButtonListener listener;

    /**
     * Creates an SGButton with the specified icon.
     *
     * @param icon The item stack representing the button's icon.
     */
    public SGButton(ItemStack icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Icon cannot be null.");
        }
        this.icon = icon;
    }

    /**
     * Sets the icon for this button dynamically.
     *
     * @param icon The new icon for the button.
     */
    public void setIcon(ItemStack icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Icon cannot be null.");
        }
        this.icon = icon;
    }

    /**
     * Gets the listener associated with this button.
     *
     * @return The listener, or null if none is set.
     */
    public SGButtonListener getListener() {
        return listener;
    }

    /**
     * Sets the listener for this button.
     *
     * @param listener The listener to handle button interactions.
     */
    public void setListener(SGButtonListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the listener for this button using a fluent API.
     *
     * @param listener The listener to handle button interactions.
     * @return This button instance for chaining.
     */
    public SGButton withListener(SGButtonListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * Gets the icon associated with this button.
     *
     * @return The item stack representing the button's icon.
     */
    public ItemStack getIcon() {
        return icon;
    }

    /**
     * Safely invokes the listener for this button, if one is set.
     *
     * @param event The event to pass to the listener.
     */
    public void invokeListener(SGButtonClickEvent event) {
        if (listener != null) {
            listener.onClick(event);
        }
    }

    /**
     * Builder class for creating immutable SGButton instances.
     */
    public static class Builder {
        private final ItemStack icon;
        private SGButtonListener listener;

        /**
         * Creates a new builder with the specified icon.
         *
         * @param icon The icon for the button.
         * @throws IllegalArgumentException if the icon is null.
         */
        public Builder(ItemStack icon) {
            if (icon == null) {
                throw new IllegalArgumentException("Icon cannot be null.");
            }
            this.icon = icon;
        }

        /**
         * Sets the listener for the button.
         *
         * @param listener The listener for the button.
         * @return This builder instance for chaining.
         */
        public Builder withListener(SGButtonListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * Builds and returns a new SGButton instance.
         *
         * @return The newly created SGButton.
         */
        public SGButton build() {
            SGButton button = new SGButton(icon);
            button.setListener(listener);
            return button;
        }
    }
}
