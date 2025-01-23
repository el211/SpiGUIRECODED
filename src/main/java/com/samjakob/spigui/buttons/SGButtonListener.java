package com.samjakob.spigui.buttons;

import com.samjakob.spigui.events.SGButtonClickEvent;

/**
 * Holds the event handler for an SGButton.
 */
@FunctionalInterface
public interface SGButtonListener {

    /**
     * The event handler that should be executed when an SGButton is clicked.
     * Implement this with a lambda when you create an SGButton.
     *
     * @param event The custom {@link SGButtonClickEvent} representing the button click.
     */
    void onClick(SGButtonClickEvent event);
}
