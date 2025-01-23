package com.samjakob.spigui.item;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * A utility class for building and customizing ItemStacks with ease.
 */
public class ItemBuilder {
    private final ItemStack stack;

    /**
     * Constructs an ItemBuilder with a given material.
     *
     * @param material The material for the item.
     */
    public ItemBuilder(Material material) {
        this.stack = new ItemStack(material);
    }

    public ItemBuilder data(short data) {
        this.stack.setDurability(data);
        return this;
    }

    /**
     * Constructs an ItemBuilder with an existing ItemStack.
     *
     * @param stack The ItemStack to customize.
     */
    public ItemBuilder(ItemStack stack) {
        this.stack = stack.clone();
    }

    /**
     * Sets the material type of the item.
     *
     * @param material The material to set.
     * @return This ItemBuilder instance for chaining.
     */
    public ItemBuilder type(Material material) {
        this.stack.setType(material);
        return this;
    }

    public Material getType() {
        return this.stack.getType();
    }

    /**
     * Translates HEX color codes in strings.
     *
     * @param input The input string with HEX color codes.
     * @return The translated string.
     */
    public static String translateHexColors(String input) {
        return input.replaceAll("(?i)&#([0-9A-F]{6})", "§x§$1§x")
                .replaceAll("(?i)(?<!§x§x)§(?=[0-9A-F]{6})", "");
    }

    /**
     * Applies a color gradient to a string.
     *
     * @param text The text to apply the gradient to.
     * @param startColor The starting color.
     * @param endColor The ending color.
     * @return The text with a color gradient applied.
     */
    public static String applyGradient(String text, Color startColor, Color endColor) {
        StringBuilder result = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            int red = (int) (startColor.getRed() * (1 - ratio) + endColor.getRed() * ratio);
            int green = (int) (startColor.getGreen() * (1 - ratio) + endColor.getGreen() * ratio);
            int blue = (int) (startColor.getBlue() * (1 - ratio) + endColor.getBlue() * ratio);

            String hexColor = String.format("§x§%s§%s§%s§%s§%s§%s",
                    Integer.toHexString((red >> 4) & 0xF),
                    Integer.toHexString(red & 0xF),
                    Integer.toHexString((green >> 4) & 0xF),
                    Integer.toHexString(green & 0xF),
                    Integer.toHexString((blue >> 4) & 0xF),
                    Integer.toHexString(blue & 0xF)
            );
            result.append(hexColor).append(text.charAt(i));
        }
        return result.toString();
    }

    /**
     * Sets the display name of the item.
     *
     * @param name The display name to set, supporting HEX color codes.
     * @return This ItemBuilder instance for chaining.
     */
    public ItemBuilder name(String name) {
        ItemMeta meta = this.stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET + translateHexColors(name));
            this.stack.setItemMeta(meta);
        }
        return this;
    }

    public String getName() {
        ItemMeta meta = this.stack.getItemMeta();
        return (meta != null && meta.hasDisplayName()) ? meta.getDisplayName() : null;
    }

    /**
     * Sets the amount of items in the stack.
     *
     * @param amount The number of items in the stack.
     * @return This ItemBuilder instance for chaining.
     */
    public ItemBuilder amount(int amount) {
        this.stack.setAmount(amount);
        return this;
    }

    public int getAmount() {
        return this.stack.getAmount();
    }

    /**
     * Sets the lore of the item.
     *
     * @param lore The lore lines to set.
     * @return This ItemBuilder instance for chaining.
     */
    public ItemBuilder lore(String... lore) {
        return this.lore(Arrays.asList(lore));
    }

    public ItemBuilder lore(List<String> lore) {
        lore.replaceAll(line -> ChatColor.RESET + translateHexColors(line));
        ItemMeta meta = this.stack.getItemMeta();
        if (meta != null) {
            meta.setLore(lore);
            this.stack.setItemMeta(meta);
        }
        return this;
    }

    public List<String> getLore() {
        ItemMeta meta = this.stack.getItemMeta();
        return (meta != null && meta.hasLore()) ? meta.getLore() : null;
    }

    /**
     * Sets the color for leather armor.
     *
     * @param color The color to set.
     * @return This ItemBuilder instance for chaining.
     */
    public ItemBuilder leatherArmorColor(Color color) {
        if (this.stack.getItemMeta() instanceof LeatherArmorMeta meta) {
            meta.setColor(color);
            this.stack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder durability(short durability) {
        this.stack.setDurability(durability);
        return this;
    }

    public short getDurability() {
        return this.stack.getDurability();
    }

    /**
     * Adds an unsafe enchantment to the item.
     *
     * @param enchantment The enchantment to add.
     * @param level       The level of the enchantment.
     * @return This ItemBuilder instance for chaining.
     */
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        this.stack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder unenchant(Enchantment enchantment) {
        this.stack.removeEnchantment(enchantment);
        return this;
    }

    /**
     * Adds item flags to hide specific item attributes in the tooltip.
     *
     * @param flags The flags to add.
     * @return This ItemBuilder instance for chaining.
     */
    public ItemBuilder flag(ItemFlag... flags) {
        ItemMeta meta = this.stack.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(flags);
            this.stack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder deflag(ItemFlag... flags) {
        ItemMeta meta = this.stack.getItemMeta();
        if (meta != null) {
            meta.removeItemFlags(flags);
            this.stack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Sets the skull owner for player head items.
     *
     * @param name The player's name.
     * @return This ItemBuilder instance for chaining.
     */
    public ItemBuilder skullOwner(String name) {
        if (this.stack.getItemMeta() instanceof SkullMeta meta) {
            this.stack.setDurability((short) 3); // Set durability to player skull type
            meta.setOwner(name);
            this.stack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Conditionally applies a function if a predicate is true.
     *
     * @param ifTrue The condition to check.
     * @param then   The function to apply if true.
     * @return This ItemBuilder instance for chaining.
     */
    public ItemBuilder ifThen(Predicate<ItemBuilder> ifTrue, Function<ItemBuilder, Object> then) {
        if (ifTrue.test(this)) {
            then.apply(this);
        }
        return this;
    }

    /**
     * Builds the item stack.
     *
     * @return The customized ItemStack.
     */
    public ItemStack build() {
        return this.get();
    }

    public ItemStack get() {
        return this.stack.clone();
    }
}
