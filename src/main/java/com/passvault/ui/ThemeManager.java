package com.passvault.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import com.passvault.AuthService;
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.BorderFactory;

public class ThemeManager {
    private static AuthService authService;
    private static boolean isDark = false;

    // Theme Color Constants
    public static final Color ACCENT_BLUE = new Color(0x3B, 0x82, 0xF6);
    public static final Color DESTRUCTIVE_RED = new Color(0xEF, 0x44, 0x44);
    public static final Color WARNING_AMBER = new Color(0xFB, 0xBF, 0x24);

    // Light Theme Colors
    public static final Color LIGHT_BG = new Color(0xF8, 0xF9, 0xFB);
    public static final Color LIGHT_CARD = Color.WHITE;
    public static final Color LIGHT_BORDER = new Color(0xE5, 0xE7, 0xEB);
    public static final Color LIGHT_TEXT_PRIMARY = new Color(0x11, 0x18, 0x27);
    public static final Color LIGHT_TEXT_SECONDARY = new Color(0x6B, 0x72, 0x80);
    public static final Color LIGHT_TABLE_ROW_ALT = new Color(0xF9, 0xFA, 0xFB);
    public static final Color LIGHT_TABLE_ROW_HOVER = new Color(0xF3, 0xF4, 0xF6);
    public static final Color LIGHT_TABLE_ROW_SELECTED = new Color(0xDB, 0xEA, 0xFE);
    public static final Color LIGHT_BLUE_ACCENT = new Color(0x25, 0x63, 0xEB);
    public static final Color LIGHT_DESTRUCTIVE_RED = new Color(0xDC, 0x26, 0x26);

    // Dark Theme Colors
    public static final Color DARK_BG = new Color(0x1E, 0x1E, 0x2E);
    public static final Color DARK_CARD = new Color(0x2A, 0x2D, 0x3A);
    public static final Color DARK_BORDER = new Color(0x3B, 0x3F, 0x4C);
    public static final Color DARK_TEXT_PRIMARY = new Color(0xF5, 0xF5, 0xF5);
    public static final Color DARK_TEXT_SECONDARY = new Color(0xA0, 0xA4, 0xAE);
    public static final Color DARK_TABLE_ROW_ALT = new Color(0x23, 0x25, 0x30);
    public static final Color DARK_TABLE_ROW_HOVER = new Color(0x33, 0x37, 0x46);
    public static final Color DARK_TABLE_ROW_SELECTED = new Color(0x3B, 0x82, 0xF6);

    public static void initialize(AuthService service) {
        authService = service;
        String theme = authService.getThemePreference();
        isDark = "dark".equalsIgnoreCase(theme);
        applyLaf();
    }

    public static boolean isDark() {
        return isDark;
    }

    public static void toggleTheme() {
        setDark(!isDark);
    }

    public static void setDark(boolean dark) {
        isDark = dark;
        if (authService != null) {
            authService.setThemePreference(isDark ? "dark" : "light");
        }
        applyLaf();
    }

    // Return theme-dependent colors
    public static Color getBgColor() {
        return isDark ? DARK_BG : LIGHT_BG;
    }

    public static Color getCardColor() {
        return isDark ? DARK_CARD : LIGHT_CARD;
    }

    public static Color getBorderColor() {
        return isDark ? DARK_BORDER : LIGHT_BORDER;
    }

    public static Color getTextPrimary() {
        return isDark ? DARK_TEXT_PRIMARY : LIGHT_TEXT_PRIMARY;
    }

    public static Color getTextSecondary() {
        return isDark ? DARK_TEXT_SECONDARY : LIGHT_TEXT_SECONDARY;
    }

    public static Color getTableAlternateRow() {
        return isDark ? DARK_TABLE_ROW_ALT : LIGHT_TABLE_ROW_ALT;
    }

    public static Color getTableHover() {
        return isDark ? DARK_TABLE_ROW_HOVER : LIGHT_TABLE_ROW_HOVER;
    }

    public static Color getTableSelection() {
        return isDark ? DARK_TABLE_ROW_SELECTED : LIGHT_TABLE_ROW_SELECTED;
    }

    public static Color getBlueAccent() {
        return isDark ? ACCENT_BLUE : LIGHT_BLUE_ACCENT;
    }

    public static Color getDestructiveRed() {
        return isDark ? DESTRUCTIVE_RED : LIGHT_DESTRUCTIVE_RED;
    }

    private static void applyLaf() {
        try {
            if (isDark) {
                // Setup UIManager values for Dark theme
                UIManager.put("Panel.background", DARK_BG);
                UIManager.put("panel.background", DARK_BG);
                UIManager.put("Background", DARK_BG);
                UIManager.put("background", DARK_BG);

                UIManager.put("Component.background", DARK_CARD);
                UIManager.put("Component.borderColor", DARK_BORDER);
                UIManager.put("Component.focusedBorderColor", ACCENT_BLUE);
                UIManager.put("Component.arc", 12); // Default rounded corners to 12px

                UIManager.put("TextComponent.background", DARK_CARD);
                UIManager.put("TextComponent.foreground", DARK_TEXT_PRIMARY);

                UIManager.put("TextField.background", DARK_CARD);
                UIManager.put("TextField.foreground", DARK_TEXT_PRIMARY);
                UIManager.put("TextField.caretColor", DARK_TEXT_PRIMARY);

                UIManager.put("PasswordField.background", DARK_CARD);
                UIManager.put("PasswordField.foreground", DARK_TEXT_PRIMARY);
                UIManager.put("PasswordField.caretColor", DARK_TEXT_PRIMARY);

                UIManager.put("TextArea.background", DARK_CARD);
                UIManager.put("TextArea.foreground", DARK_TEXT_PRIMARY);
                UIManager.put("TextArea.caretColor", DARK_TEXT_PRIMARY);

                UIManager.put("Label.foreground", DARK_TEXT_PRIMARY);

                UIManager.put("Button.background", DARK_CARD);
                UIManager.put("Button.foreground", DARK_TEXT_PRIMARY);
                UIManager.put("Button.borderColor", DARK_BORDER);
                UIManager.put("Button.hoverBackground", DARK_BORDER);
                UIManager.put("Button.focusedBorderColor", ACCENT_BLUE);
                UIManager.put("Button.arc", 10);

                UIManager.put("Table.background", DARK_BG);
                UIManager.put("Table.foreground", DARK_TEXT_PRIMARY);
                UIManager.put("Table.gridColor", DARK_BORDER);
                UIManager.put("Table.alternateRowColor", DARK_TABLE_ROW_ALT);
                UIManager.put("Table.selectionBackground", DARK_TABLE_ROW_SELECTED);
                UIManager.put("Table.selectionForeground", DARK_TEXT_PRIMARY);

                UIManager.put("TableHeader.background", DARK_CARD);
                UIManager.put("TableHeader.foreground", DARK_TEXT_SECONDARY);
                UIManager.put("TableHeader.separatorColor", DARK_BORDER);

                UIManager.put("ScrollPane.background", DARK_BG);

                UIManager.put("ComboBox.background", DARK_CARD);
                UIManager.put("ComboBox.foreground", DARK_TEXT_PRIMARY);
                UIManager.put("ComboBox.selectionBackground", ACCENT_BLUE);
                UIManager.put("ComboBox.selectionForeground", DARK_TEXT_PRIMARY);

                UIManager.put("ScrollBar.track", DARK_BG);
                UIManager.put("ScrollBar.thumb", DARK_BORDER);

                UIManager.put("Separator.foreground", DARK_BORDER);

                UIManager.put("ToolTip.background", DARK_CARD);
                UIManager.put("ToolTip.foreground", DARK_TEXT_PRIMARY);
                UIManager.put("ToolTip.border", BorderFactory.createLineBorder(DARK_BORDER));
                
                // FlatLaf animation settings
                UIManager.put("Component.animationSpeed", 1.0); // Normal speed
                UIManager.put("Button.animationSpeed", 1.0);

                FlatDarkLaf.setup();
            } else {
                // Clear custom overrides to let Light theme use defaults
                UIManager.put("Panel.background", null);
                UIManager.put("panel.background", null);
                UIManager.put("Background", null);
                UIManager.put("background", null);
                UIManager.put("Component.background", null);
                UIManager.put("Component.borderColor", null);
                UIManager.put("Component.focusedBorderColor", null);
                UIManager.put("Component.arc", null);
                UIManager.put("TextComponent.background", null);
                UIManager.put("TextComponent.foreground", null);
                UIManager.put("TextField.background", null);
                UIManager.put("TextField.foreground", null);
                UIManager.put("TextField.caretColor", null);
                UIManager.put("PasswordField.background", null);
                UIManager.put("PasswordField.foreground", null);
                UIManager.put("PasswordField.caretColor", null);
                UIManager.put("TextArea.background", null);
                UIManager.put("TextArea.foreground", null);
                UIManager.put("TextArea.caretColor", null);
                UIManager.put("Label.foreground", null);
                UIManager.put("Button.background", null);
                UIManager.put("Button.foreground", null);
                UIManager.put("Button.borderColor", null);
                UIManager.put("Button.hoverBackground", null);
                UIManager.put("Button.focusedBorderColor", null);
                UIManager.put("Button.arc", null);
                UIManager.put("Table.background", null);
                UIManager.put("Table.foreground", null);
                UIManager.put("Table.alternateRowColor", null);
                UIManager.put("Table.selectionBackground", null);
                UIManager.put("Table.selectionForeground", null);
                UIManager.put("TableHeader.background", null);
                UIManager.put("TableHeader.foreground", null);
                UIManager.put("TableHeader.separatorColor", null);
                UIManager.put("ScrollPane.background", null);
                UIManager.put("ComboBox.background", null);
                UIManager.put("ComboBox.foreground", null);
                UIManager.put("ComboBox.selectionBackground", null);
                UIManager.put("ComboBox.selectionForeground", null);
                UIManager.put("ScrollBar.track", null);
                UIManager.put("ScrollBar.thumb", null);
                UIManager.put("Separator.foreground", null);
                UIManager.put("ToolTip.background", null);
                UIManager.put("ToolTip.foreground", null);
                UIManager.put("ToolTip.border", null);

                FlatLightLaf.setup();
            }
            FlatLaf.updateUI();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
