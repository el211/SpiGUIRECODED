package com.samjakob.spiguitest;

import com.samjakob.spigui.menu.SGMenu;
import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * SpiGUITest
 * <br>
 * Simple test plugin to showcase some of the functionality of SpiGUI.
 * You can build this from the main repository with the 'testJar' Gradle task.
 *
 * @author SamJakob
 * @version 1.3.0
 */
public class SpiGUITest extends JavaPlugin {

    /*
    Please feel free to use code from here. Though, do note that it is a very rough proof of concept intended to
    showcase and test some of the functionality of SpiGUI.
    */

    private static SpiGUI spiGUI;

    // Start: variables for demonstration purposes.
    private final Map<Player, Integer> gems = new HashMap<>();
    // End: variables for demonstration purposes.

    @Override
    public void onEnable() {
        spiGUI = new SpiGUI(this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getLabel().equalsIgnoreCase("spigui")) {

            if (!(sender instanceof Player)) {
                sender.sendMessage("[SpiGUI] [ERROR] You must be a player to run this command.");
                return true;
            }

            Player player = (Player) sender;

            if (args.length == 0) {
                // Create a test SpiGUI menu.
                SGMenu myAwesomeMenu = SpiGUITest.getSpiGUI().create("&c&lSpiGUI &c(Page {currentPage}/{maxPage})", 3);

                myAwesomeMenu.setToolbarBuilder((slot, page, defaultType, menu) -> {
                    if (slot == 8) {
                        return new SGButton(
                                new ItemBuilder(Material.EMERALD)
                                        .name(String.format("&a&l%d gems", gems.getOrDefault(player, 5)))
                                        .lore(
                                                "&aUse gems to buy cosmetics",
                                                "&aand other items in the store!",
                                                "",
                                                "&7&o(Click to add more)"
                                        )
                                        .build()
                        ).withListener(event -> {
                            gems.put(player, gems.getOrDefault(player, 5) + 5);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l&oSUCCESS!  &aYou have been given &25 &agems!"));
                            menu.refreshInventory(event.getPlayer());
                        });
                    }

                    // Fallback to rendering the default button for a slot.
                    return spiGUI.getDefaultToolbarBuilder().buildToolbarButton(slot, page, defaultType, menu);
                });

                myAwesomeMenu.setButton(0, 10, new SGButton(
                        new ItemBuilder(Material.PLAYER_HEAD)
                                .skullOwner(player.getName())
                                .name("&e&l" + player.getDisplayName())
                                .lore(
                                        "&eGame Mode: &6" + player.getGameMode().toString(),
                                        "&eLocation: &6" + String.format(
                                                "%.0f, %.0f, %.0f",
                                                player.getLocation().getX(),
                                                player.getLocation().getY(),
                                                player.getLocation().getZ()
                                        ),
                                        "&eExperience: &6" + player.getTotalExperience()
                                )
                                .build()
                ));

                myAwesomeMenu.setButton(1, 0, new SGButton(
                        new ItemBuilder(Material.GOLD_ORE)
                                .name("&6Get rich quick!")
                                .build()
                ).withListener(event -> {
                    Inventory playerInventory = event.getPlayer().getInventory();

                    IntStream.range(0, 9).forEach(hotBarSlot -> playerInventory.setItem(
                            hotBarSlot, new ItemBuilder(
                                    event.getButton().getIcon().getType() == Material.GOLD_ORE
                                            ? Material.GOLD_BLOCK
                                            : event.getButton().getIcon().getType()
                            ).amount(64).build()
                    ));

                    event.getPlayer().sendMessage(
                            ChatColor.translateAlternateColorCodes('&',
                                    event.getButton().getIcon().getType() == Material.GOLD_ORE
                                            ? "&e&lYou are now rich!"
                                            : "&7&lYou are now poor."
                            )
                    );

                    Material newMaterial = event.getButton().getIcon().getType() == Material.GOLD_ORE
                            ? Material.DIRT
                            : Material.GOLD_ORE;

                    myAwesomeMenu.getButton(1, 0).setIcon(
                            new ItemBuilder(newMaterial).name(
                                    newMaterial == Material.GOLD_ORE ? "&6Get rich quick!" : "&7Get poor quick!"
                            ).amount(1).build()
                    );

                    myAwesomeMenu.refreshInventory(event.getPlayer());
                    player.updateInventory();
                }));

                AtomicReference<BukkitTask> borderRunnable = new AtomicReference<>();

                myAwesomeMenu.setOnPageChange(inventory -> {
                    if (inventory.getCurrentPage() != 0) {
                        if (borderRunnable.get() != null) borderRunnable.get().cancel();
                    } else borderRunnable.set(
                            new BukkitRunnable() {
                                private final int[] TILES_TO_UPDATE = {
                                        0, 1, 2, 3, 4, 5, 6, 7, 8,
                                        9,                             17,
                                        18, 19, 20, 21, 22, 23, 24, 25, 26
                                };

                                private short currentColor = 1;

                                @Override
                                public void run() {
                                    for (int i = 0; i < TILES_TO_UPDATE.length; i++) {
                                        int index = TILES_TO_UPDATE.length - i - 1;
                                        myAwesomeMenu.setButton(TILES_TO_UPDATE[index], nextColorButton());
                                    }
                                    currentColor = (short) ((currentColor + 1) % 16);
                                    myAwesomeMenu.refreshInventory(player);
                                }

                                private SGButton nextColorButton() {
                                    return new SGButton(
                                            new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
                                                    .name("&" + Integer.toHexString(currentColor) + "&lSpiGUI!!!")
                                                    .data(currentColor)
                                                    .build()
                                    );
                                }
                            }.runTaskTimer(this, 0L, 20L)
                    );
                });

                myAwesomeMenu.setOnClose(inventory -> {
                    if (borderRunnable.get() != null) borderRunnable.get().cancel();
                });

                myAwesomeMenu.getOnPageChange().accept(myAwesomeMenu);
                player.openInventory(myAwesomeMenu.getInventory());

                return true;
            }

            player.sendMessage("Unrecognized command.");
        }

        return false;
    }


    public static SpiGUI getSpiGUI() {
        return spiGUI;
    }

    // The following is mock classes/data for the above test GUIs.

    private static class Kit {
        private final String name;
        private final ItemStack icon;

        public Kit(String name, ItemStack icon) {
            this.name = name;
            this.icon = icon;
        }

        public String getName() {
            return this.name;
        }

        public ItemStack getIcon() {
            return this.icon;
        }
    }

    private static class Match {

        private enum MatchState {
            /** Waiting to start. */
            WAITING,
            /** Currently ongoing. */
            ONGOING,
            /** Ended. */
            ENDED
        }

        // Begin mock data.
        private static final String[] fakePlayerNames = {"MoreHaro", "Pixelle", "SpyPlenty", "Winlink", "Herobrine", "Notch", "Dinnerbone", "CinnamonTown", "TreeMushrooms"};
        private static final Kit[] fakeKits = {
            new Kit("Classic Battle", new ItemBuilder(Material.STONE_SWORD).name("&7Classic Battle").build()),
            new Kit("OP Battle", new ItemBuilder(Material.DIAMOND_SWORD).name("&bOP Battle").build()),
            new Kit("Classic UHC", new ItemBuilder(Material.GOLDEN_APPLE).name("&eClassic UHC").build()),
            new Kit("OP UHC", new ItemBuilder(Material.GOLDEN_APPLE).data((short) 1).name("&6OP UHC").build()),
        };
        private static final String[] fakeArenas = {"King's Road", "Ilios", "Fort Starr", "The Hopper"};

        /** Generates a Match with fake data. */
        public static Match generateFakeMatch() { return generateFakeMatch(false); }

        public static Match generateFakeMatch(boolean alreadyStarted) {
            // Ensure unique values are generated for player1 and player2.
            int player1 = ThreadLocalRandom.current().nextInt(fakePlayerNames.length);
            int player2;
            do {
                player2 = ThreadLocalRandom.current().nextInt(fakePlayerNames.length);
            } while (player2 == player1);

            Match fakeMatch = new Match(
                new String[]{fakePlayerNames[player1], fakePlayerNames[player2]},
                fakeKits[ThreadLocalRandom.current().nextInt(fakeKits.length)],
                fakeArenas[ThreadLocalRandom.current().nextInt(fakeArenas.length)]
            );

            if (alreadyStarted) {
                // If alreadyStarted specified to true, then generate a match with current time minus up to 5 minutes.
                fakeMatch.matchStartTime = System.currentTimeMillis()
                        - ThreadLocalRandom.current().nextLong(5 * 60000);
            }

            return fakeMatch;
        }
        // End mock data.

        /** List of players in match. Two players implies a duel. */
        private final String[] playerNames;

        public String[] getPlayerNames() { return playerNames; }

        /** Match start time in UNIX milliseconds. */
        private Long matchStartTime;

        /** Match end time in UNIX milliseconds. */
        private Long matchEndTime;

        /** Name of the kit used for the duel. */
        private final Kit kit;

        public Kit getKit() { return kit; }

        /** Name of the arena used for the duel. */
        private final String arena;

        public String getArena() { return arena; }

        public String getTime() {
            switch (getState()) {
                case WAITING: return "Waiting...";
                case ONGOING:
                case ENDED: {
                    long duration = (matchEndTime != null ? matchEndTime : System.currentTimeMillis()) - matchStartTime;

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minutes);
                    return String.format("%02d:%02d", minutes, seconds);
                }
            }

            return "ERROR";
        }

        public Match(String[] playerNames, Kit kit, String arena) {
            this.playerNames = playerNames;
            this.kit = kit;
            this.arena = arena;
        }

        public void start() {
            if (this.matchStartTime != null) throw new IllegalStateException("Match already started!");
            this.matchStartTime = System.currentTimeMillis();
        }

        public void stop() {
            if (this.matchEndTime != null) throw new IllegalStateException("Match already finished!");
            this.matchEndTime = System.currentTimeMillis();
        }

        public MatchState getState() {
            if (this.matchStartTime == null) return MatchState.WAITING;
            else if (this.matchEndTime == null) return MatchState.ONGOING;
            return MatchState.ENDED;
        }

    }

}
