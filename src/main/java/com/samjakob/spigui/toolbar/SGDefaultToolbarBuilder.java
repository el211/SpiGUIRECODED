package com.samjakob.spigui.toolbar;

import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.event.Event;

/**
 * The default implementation of {@link SGToolbarBuilder}.
 * <br>
 * This class is used by default by SpiGUI, but you can override this class by
 * extending it and passing your custom implementation to
 * {@link SpiGUI#setDefaultToolbarBuilder(SGToolbarBuilder)}
 * (or to use it for a specific menu, pass it to
 * {@link SGMenu#setToolbarBuilder(SGToolbarBuilder)}).
 */
public class SGDefaultToolbarBuilder implements SGToolbarBuilder {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public SGButton buildToolbarButton(int slot, int page, SGToolbarButtonType type, SGMenu menu) {
        switch (type) {
            case PREV_BUTTON:
                if (menu.getCurrentPage() > 0) {
                    return new SGButton(new ItemBuilder(Material.ARROW)
                            .name(miniMessage.deserialize("<green><bold>← Previous Page"))
                            .lore(
                                    miniMessage.deserialize("<green>Click to move back to"),
                                    miniMessage.deserialize("<green>page " + menu.getCurrentPage() + ".")
                            ).build()
                    ).withListener(event -> {
                        event.setResult(Event.Result.DENY);
                        menu.previousPage(event.getWhoClicked());
                    });
                } else return null;

            case CURRENT_BUTTON:
                return new SGButton(new ItemBuilder(Material.NAME_TAG)
                        .name(miniMessage.deserialize("<gray><bold>Page " + (menu.getCurrentPage() + 1) + " of " + menu.getMaxPage()))
                        .lore(
                                miniMessage.deserialize("<gray>You are currently viewing"),
                                miniMessage.deserialize("<gray>page " + (menu.getCurrentPage() + 1) + ".")
                        ).build()
                ).withListener(event -> event.setResult(Event.Result.DENY));

            case NEXT_BUTTON:
                if (menu.getCurrentPage() < menu.getMaxPage() - 1) {
                    return new SGButton(new ItemBuilder(Material.ARROW)
                            .name(miniMessage.deserialize("<green><bold>Next Page →"))
                            .lore(
                                    miniMessage.deserialize("<green>Click to move forward to"),
                                    miniMessage.deserialize("<green>page " + (menu.getCurrentPage() + 2) + ".")
                            ).build()
                    ).withListener(event -> {
                        event.setResult(Event.Result.DENY);
                        menu.nextPage(event.getWhoClicked());
                    });
                } else return null;

            case UNASSIGNED:
            default:
                return null;
        }
    }

}
