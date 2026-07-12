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
}
