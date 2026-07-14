package com.passvault.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;

public class IconUtils {
    /**
     * Load an SVG icon from classpath resources at icons/{name}.svg with specified size.
     */
    public static FlatSVGIcon getIcon(String name, int size) {
        try {
            return new FlatSVGIcon("icons/" + name + ".svg", size, size);
        } catch (Exception e) {
            System.err.println("Error loading SVG icon " + name + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Load an SVG icon from classpath resources at icons/{name}.svg with specified size and custom color override.
     */
    public static FlatSVGIcon getIcon(String name, int size, Color color) {
        FlatSVGIcon icon = getIcon(name, size);
        if (icon != null && color != null) {
            icon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> color));
        }
        return icon;
    }

    /**
     * Get a list of logo images of various sizes for window decoration.
     */
    public static java.util.List<java.awt.Image> getLogoImages() {
        java.util.List<java.awt.Image> images = new java.util.ArrayList<>();
        int[] sizes = {16, 32, 48, 64, 128, 256};
        for (int size : sizes) {
            FlatSVGIcon icon = getIcon("logo", size);
            if (icon != null) {
                icon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> new Color(0x3B, 0x82, 0xF6))); // Theme accent blue
                images.add(icon.getImage());
            }
        }
        return images;
    }
}
