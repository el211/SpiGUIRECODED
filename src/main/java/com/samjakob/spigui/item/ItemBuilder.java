package com.samjakob.spigui.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A helper class for creating or modifying ItemStacks with support for Adventure's Component system, MiniMessage,
 * and legacy string compatibility. This class includes additional methods for managing item colors and data values.
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
        stack.setType(material);
        return this;
    }

    public Material getType() {
        return stack.getType();
    }

    /* NAME METHODS */

    public ItemBuilder name(Component name) {
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(name);
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder nameFromMiniMessage(String miniMessage) {
        return name(MiniMessage.miniMessage().deserialize(miniMessage));
    }

    public ItemBuilder name(String name) {
        return name(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
    }

    public Component getNameComponent() {
        if (!stack.hasItemMeta()) return null;
        return stack.getItemMeta().displayName();
    }

    public String getName() {
        Component nameComponent = getNameComponent();
        return nameComponent != null ? LegacyComponentSerializer.legacyAmpersand().serialize(nameComponent) : null;
    }

    /* LORE METHODS */

    public ItemBuilder lore(List<Component> lore) {
        ItemMeta meta = stack.getItemMeta();
        meta.lore(lore);
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder loreFromMiniMessage(List<String> miniMessages) {
        List<Component> components = miniMessages.stream()
                .map(MiniMessage.miniMessage()::deserialize)
                .collect(Collectors.toList());
        return lore(components);
    }

    public ItemBuilder lore(String... lore) {
        return lore(Arrays.stream(lore)
                .map(LegacyComponentSerializer.legacyAmpersand()::deserialize)
                .collect(Collectors.toList()));
    }

    public List<Component> getLoreComponents() {
        if (!stack.hasItemMeta()) return null;
        return stack.getItemMeta().lore();
    }

    public List<String> getLore() {
        List<Component> loreComponents = getLoreComponents();
        return loreComponents != null ? loreComponents.stream()
                .map(LegacyComponentSerializer.legacyAmpersand()::serialize)
                .collect(Collectors.toList()) : null;
    }

    /* ITEM AMOUNT */

    public ItemBuilder amount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public int getAmount() {
        return stack.getAmount();
    }

    /* DURABILITY / DATA / COLOR */

    public ItemBuilder durability(short durability) {
        stack.setDurability(durability);
        return this;
    }

    public short getDurability() {
        return stack.getDurability();
    }

    public ItemBuilder data(short data) {
        return durability(data);
    }

    public ItemBuilder color(ItemDataColor color) {
        return durability(color.getValue());
    }

    public ItemDataColor getColor() {
        return ItemDataColor.getByValue(stack.getDurability());
    }

    /* ENCHANTMENTS */

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        stack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder unenchant(Enchantment enchantment) {
        stack.removeEnchantment(enchantment);
        return this;
    }

    /* ITEM FLAGS */

    public ItemBuilder flag(ItemFlag... flags) {
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(flags);
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder deflag(ItemFlag... flags) {
        ItemMeta meta = stack.getItemMeta();
        meta.removeItemFlags(flags);
        stack.setItemMeta(meta);
        return this;
    }

    /* SKULL OWNER */

    public ItemBuilder skullOwner(String name) {
        if (!(stack.getItemMeta() instanceof SkullMeta)) return this;

        stack.setDurability((byte) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(name);
        stack.setItemMeta(meta);

        return this;
    }

    /* CONDITIONAL OPERATIONS */

    public ItemBuilder ifThen(Predicate<ItemBuilder> condition, Function<ItemBuilder, Object> action) {
        if (condition.test(this)) action.apply(this);
        return this;
    }

    /* BUILD METHODS */

    public ItemStack build() {
        return stack;
    }

    public ItemStack get() {
        return stack;
    }
}
