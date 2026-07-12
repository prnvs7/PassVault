package com.passvault.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ModernPasswordField extends JPasswordField {
    private int cornerRadius = 8;
    private Color borderColor = new Color(0xE5, 0xE7, 0xEB);

    public ModernPasswordField() {
        init();
    }

    public ModernPasswordField(int columns) {
        super(columns);
        init();
    }

    private void init() {
        setOpaque(false);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
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

        g2.dispose();
        super.paintComponent(g);
    }
}
