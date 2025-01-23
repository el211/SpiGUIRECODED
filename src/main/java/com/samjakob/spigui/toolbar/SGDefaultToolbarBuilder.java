package com.samjakob.spigui.toolbar;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import org.bukkit.Material;

public class SGDefaultToolbarBuilder implements SGToolbarBuilder {

    public SGDefaultToolbarBuilder() {
    }

    @Override
    public SGButton buildToolbarButton(int slot, int page, SGToolbarButtonType type, SGMenu menu) {
        switch (type) {
            case PREV_BUTTON:
                if (menu.getCurrentPage() > 0) {
                    return new SGButton(new ItemBuilder(Material.ARROW)
                            .name("&a&l← Previous Page")
                            .lore("&aClick to move back to", "&apage " + menu.getCurrentPage() + ".")
                            .build())
                            .withListener(event -> {
                                event.setResult(org.bukkit.event.Event.Result.DENY);
                                menu.previousPage(event.getWhoClicked());
                            });
                }
                return null;

            case CURRENT_BUTTON:
                return new SGButton(new ItemBuilder(Material.NAME_TAG)
                        .name("&7&lPage " + (menu.getCurrentPage() + 1) + " of " + menu.getMaxPage())
                        .lore("&7You are currently viewing", "&7page " + (menu.getCurrentPage() + 1) + ".")
                        .build())
                        .withListener(event -> event.setResult(org.bukkit.event.Event.Result.DENY));

            case NEXT_BUTTON:
                if (menu.getCurrentPage() < menu.getMaxPage() - 1) {
                    return new SGButton(new ItemBuilder(Material.ARROW)
                            .name("&a&lNext Page →")
                            .lore("&aClick to move forward to", "&apage " + (menu.getCurrentPage() + 2) + ".")
                            .build())
                            .withListener(event -> {
                                event.setResult(org.bukkit.event.Event.Result.DENY);
                                menu.nextPage(event.getWhoClicked());
                            });
                }
                return null;

            case UNASSIGNED:
            default:
                return null;
        }
    }
}
