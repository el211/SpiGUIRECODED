package com.samjakob.spigui.item;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the colors supported by ItemData with various utilities like hex codes, RGB values, and gradient support.
 */
public enum ItemDataColor {
    WHITE((short) 0, "White", "#FFFFFF", 255, 255, 255),
    ORANGE((short) 1, "Orange", "#FFA500", 255, 165, 0),
    MAGENTA((short) 2, "Magenta", "#FF00FF", 255, 0, 255),
    LIGHT_BLUE((short) 3, "Light Blue", "#ADD8E6", 173, 216, 230),
    YELLOW((short) 4, "Yellow", "#FFFF00", 255, 255, 0),
    LIME((short) 5, "Lime", "#00FF00", 0, 255, 0),
    PINK((short) 6, "Pink", "#FFC0CB", 255, 192, 203),
    GRAY((short) 7, "Gray", "#808080", 128, 128, 128),
    LIGHT_GRAY((short) 8, "Light Gray", "#D3D3D3", 211, 211, 211),
    CYAN((short) 9, "Cyan", "#00FFFF", 0, 255, 255),
    PURPLE((short) 10, "Purple", "#800080", 128, 0, 128),
    BLUE((short) 11, "Blue", "#0000FF", 0, 0, 255),
    BROWN((short) 12, "Brown", "#A52A2A", 165, 42, 42),
    GREEN((short) 13, "Green", "#008000", 0, 128, 0),
    RED((short) 14, "Red", "#FF0000", 255, 0, 0),
    BLACK((short) 15, "Black", "#000000", 0, 0, 0);

    private final short value;
    private final String friendlyName;
    private final String hexCode;
    private final int red;
    private final int green;
    private final int blue;

    /**
     * Constructs an ItemDataColor.
     *
     * @param value        The short value associated with the color.
     * @param friendlyName A human-readable name for the color.
     * @param hexCode      The hexadecimal representation of the color.
     * @param red          The red component (0-255).
     * @param green        The green component (0-255).
     * @param blue         The blue component (0-255).
     */
    private ItemDataColor(short value, String friendlyName, String hexCode, int red, int green, int blue) {
        if (value < 0 || value > 15) {
            throw new IllegalArgumentException("Value must be between 0 and 15.");
        }
        this.value = value;
        this.friendlyName = friendlyName;
        this.hexCode = hexCode;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public short getValue() {
        return this.value;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    public String getHexCode() {
        return this.hexCode;
    }

    public int getRed() {
        return this.red;
    }

    public int getGreen() {
        return this.green;
    }

    public int getBlue() {
        return this.blue;
    }

    /**
     * Finds an ItemDataColor by its value.
     *
     * @param value The value to search for.
     * @return The matching color, or null if not found.
     */
    public static ItemDataColor getByValue(short value) {
        for (ItemDataColor color : values()) {
            if (color.value == value) {
                return color;
            }
        }
        return null;
    }

    /**
     * Finds an ItemDataColor by its friendly name (case-insensitive).
     *
     * @param name The friendly name to search for.
     * @return The matching color, or null if not found.
     */
    public static ItemDataColor getByFriendlyName(String name) {
        for (ItemDataColor color : values()) {
            if (color.friendlyName.equalsIgnoreCase(name)) {
                return color;
            }
        }
        return null;
    }

    /**
     * Finds an ItemDataColor by its hex code (case-insensitive).
     *
     * @param hexCode The hex code to search for.
     * @return The matching color, or null if not found.
     */
    public static ItemDataColor getByHexCode(String hexCode) {
        for (ItemDataColor color : values()) {
            if (color.hexCode.equalsIgnoreCase(hexCode)) {
                return color;
            }
        }
        return null;
    }

    /**
     * Finds the closest matching color by its RGB values.
     *
     * @param red   The red component (0-255).
     * @param green The green component (0-255).
     * @param blue  The blue component (0-255).
     * @return The closest matching color.
     */
    public static ItemDataColor getClosestByRGB(int red, int green, int blue) {
        ItemDataColor closestColor = null;
        double closestDistance = Double.MAX_VALUE;

        for (ItemDataColor color : values()) {
            double distance = Math.sqrt(
                    Math.pow(color.red - red, 2) +
                            Math.pow(color.green - green, 2) +
                            Math.pow(color.blue - blue, 2)
            );

            if (distance < closestDistance) {
                closestDistance = distance;
                closestColor = color;
            }
        }

        return closestColor;
    }

    /**
     * Generates a gradient between two colors.
     *
     * @param startColor The starting color.
     * @param endColor   The ending color.
     * @param steps      The number of steps in the gradient.
     * @return A list of hex codes representing the gradient.
     */
    public static List<String> generateGradient(ItemDataColor startColor, ItemDataColor endColor, int steps) {
        List<String> gradient = new ArrayList<>();

        for (int i = 0; i <= steps; i++) {
            double ratio = (double) i / steps;

            int red = (int) (startColor.getRed() + ratio * (endColor.getRed() - startColor.getRed()));
            int green = (int) (startColor.getGreen() + ratio * (endColor.getGreen() - startColor.getGreen()));
            int blue = (int) (startColor.getBlue() + ratio * (endColor.getBlue() - startColor.getBlue()));

            String hex = String.format("#%02X%02X%02X", red, green, blue);
            gradient.add(hex);
        }

        return gradient;
    }
}
