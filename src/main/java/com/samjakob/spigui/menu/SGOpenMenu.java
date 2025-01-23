package com.samjakob.spigui.menu;

import org.bukkit.entity.Player;
import java.util.Objects;

/**
 * Represents an open SGMenu instance associated with a specific player.
 */
public class SGOpenMenu {

    private final SGMenu gui;
    private final Player player;

    /**
     * Constructs an SGOpenMenu instance.
     *
     * @param gui    The SGMenu instance.
     * @param player The player associated with the open menu.
     */
    public SGOpenMenu(SGMenu gui, Player player) {
        this.gui = Objects.requireNonNull(gui, "GUI cannot be null");
        this.player = Objects.requireNonNull(player, "Player cannot be null");
    }

    /**
     * Gets the SGMenu instance associated with this open menu.
     *
     * @return The SGMenu instance.
     */
    public final SGMenu getMenu() {
        return this.gui;
    }

    /**
     * Gets the player associated with this open menu.
     *
     * @return The player.
     */
    public final Player getPlayer() {
        return this.player;
    }

    /**
     * Provides a string representation of this open menu for debugging purposes.
     *
     * @return A string describing this open menu.
     */
    @Override
    public String toString() {
        return "SGOpenMenu{" +
                "menu=" + gui +
                ", player=" + player.getName() +
                '}';
    }

    /**
     * Compares this open menu to another object for equality.
     *
     * @param o The object to compare against.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SGOpenMenu that = (SGOpenMenu) o;
        return gui.equals(that.gui) && player.equals(that.player);
    }

    /**
     * Computes the hash code for this open menu.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(gui, player);
    }
}
