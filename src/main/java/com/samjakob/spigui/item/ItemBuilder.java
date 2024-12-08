package com.samjakob.spigui.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.Component; // For Adventure Components
import net.kyori.adventure.text.minimessage.MiniMessage; // For MiniMessage support
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer; // For legacy serialization
import org.bukkit.ChatColor; // For color code translation (if needed)
import org.bukkit.Material; // For ItemStack material types
import org.bukkit.enchantments.Enchantment; // For enchantment handling
import org.bukkit.inventory.ItemFlag; // For item flag handling
import org.bukkit.inventory.ItemStack; // For creating/modifying items
import org.bukkit.inventory.meta.ItemMeta; // For setting item metadata
import org.bukkit.inventory.meta.SkullMeta; // For skull metadata (if using player heads)


import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A helper class for creating or modifying ItemStacks with support for Adventure's Component system, MiniMessage,
 * and legacy string compatibility.
 */
public class ItemBuilder {

    private final ItemStack stack;

    /* CONSTRUCTORS */

    public ItemBuilder(Material material) {
        this.stack = new ItemStack(material);
    }

    public ItemBuilder(ItemStack stack) {
        this.stack = stack;
    }

    /* ITEM TYPE */

    public ItemBuilder type(Material material) {
        this.stack.setType(material);
        return this;
    }

    public Material getType() {
        return this.stack.getType();
    }


    /* NAME METHODS */

    public ItemBuilder name(Component name) {
        ItemMeta meta = this.stack.getItemMeta();
        if (meta != null) {
            meta.displayName(name); // Modern API for Adventure Components
            this.stack.setItemMeta(meta);
        }
        return this;
    }





    public ItemBuilder nameFromMiniMessage(String miniMessage) {
        return this.name(MiniMessage.miniMessage().deserialize(miniMessage));
    }

    public ItemBuilder name(String name) {
        return this.name(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
    }

    public ItemBuilder nameWithChatColor(String name) {
        return this.name(ChatColor.translateAlternateColorCodes('&', name));
    }

    public Component getNameComponent() {
        if (!this.stack.hasItemMeta()) {
            return null;
        }
        String legacyName = this.stack.getItemMeta().getDisplayName();
        return legacyName != null && !legacyName.isEmpty()
                ? LegacyComponentSerializer.legacySection().deserialize(legacyName)
                : null;
    }

    public String getName() {
        Component nameComponent = this.getNameComponent();
        return nameComponent != null ? LegacyComponentSerializer.legacyAmpersand().serialize(nameComponent) : null;
    }

    /* LORE METHODS */

    public ItemBuilder lore(List<Component> lore) {
        ItemMeta meta = this.stack.getItemMeta();
        if (meta != null) {
            meta.lore(lore); // Modern API for Adventure Components
            this.stack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder lore(Component... lore) {
        return lore(Arrays.asList(lore)); // Delegate to the List<Component> implementation
    }



    public ItemBuilder loreFromMiniMessage(List<String> miniMessages) {
        List<Component> components = miniMessages.stream()
                .map(MiniMessage.miniMessage()::deserialize)
                .collect(Collectors.toList());
        return this.lore(components);
    }

    public ItemBuilder lore(String... lore) {
        return this.lore(Arrays.stream(lore)
                .map(LegacyComponentSerializer.legacyAmpersand()::deserialize)
                .collect(Collectors.toList()));
    }

    public ItemBuilder loreWithChatColor(String... lore) {
        return this.lore(Arrays.stream(lore)
                .map(line -> LegacyComponentSerializer.legacyAmpersand().deserialize(
                        ChatColor.translateAlternateColorCodes('&', line)))
                .collect(Collectors.toList()));
    }

    public List<Component> getLoreComponents() {
        if (!this.stack.hasItemMeta() || this.stack.getItemMeta().getLore() == null) {
            return null;
        }
        return this.stack.getItemMeta().getLore().stream()
                .map(LegacyComponentSerializer.legacySection()::deserialize)
                .collect(Collectors.toList());
    }

    public List<String> getLore() {
        List<Component> loreComponents = this.getLoreComponents();
        return loreComponents != null
                ? loreComponents.stream()
                .map(LegacyComponentSerializer.legacyAmpersand()::serialize)
                .collect(Collectors.toList())
                : null;
    }

    /* ITEM AMOUNT */

    public ItemBuilder amount(int amount) {
        this.stack.setAmount(amount);
        return this;
    }

    public int getAmount() {
        return this.stack.getAmount();
    }

    /* DURABILITY / DATA / COLOR */

    public ItemBuilder durability(short durability) {
        this.stack.setDurability(durability);
        return this;
    }

    public short getDurability() {
        return this.stack.getDurability();
    }

    public ItemBuilder data(short data) {
        return this.durability(data);
    }

    public ItemBuilder color(ItemDataColor color) {
        return this.durability(color.getValue());
    }

    public ItemDataColor getColor() {
        return ItemDataColor.getByValue(this.stack.getDurability());
    }

    /* ENCHANTMENTS */

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        this.stack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder unenchant(Enchantment enchantment) {
        this.stack.removeEnchantment(enchantment);
        return this;
    }

    /* ITEM FLAGS */

    public ItemBuilder flag(ItemFlag... flags) {
        ItemMeta meta = this.stack.getItemMeta();
        meta.addItemFlags(flags);
        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder deflag(ItemFlag... flags) {
        ItemMeta meta = this.stack.getItemMeta();
        meta.removeItemFlags(flags);
        this.stack.setItemMeta(meta);
        return this;
    }

    /* SKULL OWNER */

    public ItemBuilder skullOwner(String name) {
        if (!(this.stack.getItemMeta() instanceof SkullMeta)) {
            return this;
        }
        this.stack.setDurability((short) 3);
        SkullMeta meta = (SkullMeta) this.stack.getItemMeta();
        meta.setOwner(name);
        this.stack.setItemMeta(meta);
        return this;
    }

    /* CONDITIONAL OPERATIONS */

    public ItemBuilder ifThen(Predicate<ItemBuilder> condition, Function<ItemBuilder, Object> action) {
        if (condition.test(this)) {
            action.apply(this);
        }
        return this;
    }

    /* BUILD METHODS */

    public ItemStack build() {
        return this.stack;
    }

    public ItemStack get() {
        return this.stack;
    }
}
