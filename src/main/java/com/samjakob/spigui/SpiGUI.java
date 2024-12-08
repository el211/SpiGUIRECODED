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


public class SpiGUI {

    private final JavaPlugin plugin;

    private boolean blockDefaultInteractions = true;

    private boolean enableAutomaticPagination = true;

    private SGToolbarBuilder defaultToolbarBuilder = new SGDefaultToolbarBuilder();

    public SpiGUI(JavaPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(
            new SGMenuListener(plugin, this), plugin
        );
    }

    public SGMenu create(String name, int rows) {
        return create(name, rows, null);
    }

    public SGMenu create(String name, int rows, String tag) {
        return new SGMenu(plugin, this, name, rows, tag);
    }

    public void setBlockDefaultInteractions(boolean blockDefaultInteractions) {
        this.blockDefaultInteractions = blockDefaultInteractions;
    }

    public boolean areDefaultInteractionsBlocked() {
        return blockDefaultInteractions;
    }


    public void setEnableAutomaticPagination(boolean enableAutomaticPagination) {
        this.enableAutomaticPagination = enableAutomaticPagination;
    }


    public boolean isAutomaticPaginationEnabled() {
        return enableAutomaticPagination;
    }

    public void setDefaultToolbarBuilder(SGToolbarBuilder defaultToolbarBuilder) {
        this.defaultToolbarBuilder = defaultToolbarBuilder;
    }


    public SGToolbarBuilder getDefaultToolbarBuilder() {
        return defaultToolbarBuilder;
    }

    public List<SGOpenMenu> findOpenWithTag(String tag) {

        List<SGOpenMenu> foundInventories = new ArrayList<>();

        // Loop through every online player...
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            // ...if that player has an open inventory with a top inventory...
            if (player.getOpenInventory().getTopInventory() != null) {
                // ...get that top inventory.
                Inventory topInventory = player.getOpenInventory().getTopInventory();

                // If the top inventory is an SGMenu,
                if (topInventory.getHolder() != null && topInventory.getHolder() instanceof SGMenu) {
                    // and the SGMenu has the tag matching the one we're checking for,
                    SGMenu inventory = (SGMenu) topInventory.getHolder();
                    if (Objects.equals(inventory.getTag(), tag))
                        // add the SGMenu to our list of found inventories.
                        foundInventories.add(new SGOpenMenu(inventory, player));
                }
            }
        }

        return foundInventories;

    }

}
