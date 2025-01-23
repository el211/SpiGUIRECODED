package com.samjakob.spigui;

import com.samjakob.spigui.menu.SGMenu;
import com.samjakob.spigui.menu.SGMenuListener;
import com.samjakob.spigui.menu.SGOpenMenu;
import com.samjakob.spigui.toolbar.SGDefaultToolbarBuilder;
import com.samjakob.spigui.toolbar.SGToolbarBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Main class for managing SpiGUI menus and configurations.
 */
public class SpiGUI {

    private final JavaPlugin plugin;
    private boolean blockDefaultInteractions = true;
    private boolean enableAutomaticPagination = true;
    private SGToolbarBuilder defaultToolbarBuilder = new SGDefaultToolbarBuilder();

    /**
     * Constructs a SpiGUI instance for the provided plugin.
     *
     * @param plugin The plugin using SpiGUI.
     */
    public SpiGUI(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
        plugin.getServer().getPluginManager().registerEvents(new SGMenuListener(plugin, this), plugin);
    }

    /**
     * Creates a new SGMenu with the specified name and number of rows.
     *
     * @param name The name of the menu.
     * @param rows The number of rows in the menu.
     * @return The created SGMenu.
     */
    public SGMenu create(String name, int rows) {
        return this.create(name, rows, null);
    }

    /**
     * Creates a new SGMenu with the specified name, number of rows, and optional tag.
     *
     * @param name The name of the menu.
     * @param rows The number of rows in the menu.
     * @param tag  An optional tag for identifying the menu.
     * @return The created SGMenu.
     */
    public SGMenu create(String name, int rows, String tag) {
        return new SGMenu(this.plugin, this, name, rows, tag);
    }

    /**
     * Enables or disables blocking of default inventory interactions.
     *
     * @param blockDefaultInteractions True to block default interactions, false otherwise.
     */
    public void setBlockDefaultInteractions(boolean blockDefaultInteractions) {
        this.blockDefaultInteractions = blockDefaultInteractions;
    }

    /**
     * Checks if default inventory interactions are blocked.
     *
     * @return True if default interactions are blocked, false otherwise.
     */
    public boolean areDefaultInteractionsBlocked() {
        return this.blockDefaultInteractions;
    }

    /**
     * Enables or disables automatic pagination for menus.
     *
     * @param enableAutomaticPagination True to enable automatic pagination, false otherwise.
     */
    public void setEnableAutomaticPagination(boolean enableAutomaticPagination) {
        this.enableAutomaticPagination = enableAutomaticPagination;
    }

    /**
     * Checks if automatic pagination is enabled.
     *
     * @return True if automatic pagination is enabled, false otherwise.
     */
    public boolean isAutomaticPaginationEnabled() {
        return this.enableAutomaticPagination;
    }

    /**
     * Sets the default toolbar builder used by menus.
     *
     * @param defaultToolbarBuilder The toolbar builder to use.
     */
    public void setDefaultToolbarBuilder(SGToolbarBuilder defaultToolbarBuilder) {
        this.defaultToolbarBuilder = Objects.requireNonNull(defaultToolbarBuilder, "Toolbar builder cannot be null");
    }

    /**
     * Gets the default toolbar builder.
     *
     * @return The default toolbar builder.
     */
    public SGToolbarBuilder getDefaultToolbarBuilder() {
        return this.defaultToolbarBuilder;
    }

    /**
     * Finds all open menus with a specific tag.
     *
     * @param tag The tag to search for.
     * @return A list of SGOpenMenu objects matching the tag.
     */
    public List<SGOpenMenu> findOpenWithTag(String tag) {
        return plugin.getServer().getOnlinePlayers().stream()
                .map(player -> {
                    Inventory topInventory = player.getOpenInventory().getTopInventory();
                    if (topInventory != null && topInventory.getHolder() instanceof SGMenu) {
                        SGMenu inventory = (SGMenu) topInventory.getHolder();
                        if (Objects.equals(inventory.getTag(), tag)) {
                            return new SGOpenMenu(inventory, player);
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Provides a string representation of the SpiGUI instance for debugging.
     *
     * @return A string describing the SpiGUI instance.
     */
    @Override
    public String toString() {
        return "SpiGUI{" +
                "plugin=" + plugin.getName() +
                ", blockDefaultInteractions=" + blockDefaultInteractions +
                ", enableAutomaticPagination=" + enableAutomaticPagination +
                ", defaultToolbarBuilder=" + defaultToolbarBuilder.getClass().getSimpleName() +
                '}';
    }
}
