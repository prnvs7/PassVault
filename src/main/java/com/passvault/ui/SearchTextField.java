package com.passvault.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SearchTextField extends JTextField {
    private String placeholder = "Search passwords...";
    private int cornerRadius = 12;
    private Color borderColor = new Color(0xE5, 0xE7, 0xEB);

    public SearchTextField() {
        init();
    }

    public SearchTextField(String placeholder) {
        this.placeholder = placeholder;
        init();
    }

    private void init() {
        setOpaque(false);
        setBorder(new EmptyBorder(8, 34, 8, 12));
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setForeground(new Color(0x11, 0x18, 0x27));
        setCaretColor(new Color(0x11, 0x18, 0x27));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                borderColor = new Color(0x25, 0x63, 0xEB);
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                borderColor = new Color(0xE5, 0xE7, 0xEB);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        com.formdev.flatlaf.extras.FlatSVGIcon searchIcon = IconUtils.getIcon("search", 16, new Color(0x9C, 0xA3, 0xAF));
        if (searchIcon != null) {
            int iconY = (getHeight() - 16) / 2;
            searchIcon.paintIcon(this, g2, 12, iconY);
        }

        g2.dispose();

        super.paintComponent(g);

        if (getText().isEmpty() && !hasFocus()) {
            Graphics2D gPlaceholder = (Graphics2D) g.create();
            gPlaceholder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gPlaceholder.setColor(new Color(0x9C, 0xA3, 0xAF));
            gPlaceholder.setFont(getFont());
            FontMetrics placeholderFm = gPlaceholder.getFontMetrics();
            int textY = (getHeight() - placeholderFm.getHeight()) / 2 + placeholderFm.getAscent();
            gPlaceholder.drawString(placeholder, 34, textY);
            gPlaceholder.dispose();
        }
    }
}
