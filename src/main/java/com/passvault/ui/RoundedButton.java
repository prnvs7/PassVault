package com.passvault.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {
    private int cornerRadius = 12;
    private Color hoverColor;
    private Color pressedColor;
    private boolean isCustomHover = false;
    private boolean isCustomPressed = false;

    public RoundedButton(String text) {
        super(text);
        init();
    }

    public RoundedButton(String text, int cornerRadius) {
        super(text);
        this.cornerRadius = cornerRadius;
        init();
    }

    private void init() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }
        });
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (bg != null) {
            if (!isCustomHover) {
                this.hoverColor = calculateHoverColor(bg);
            }
            if (!isCustomPressed) {
                this.pressedColor = calculatePressedColor(bg);
            }
        }
    }

    private Color calculateHoverColor(Color bg) {
        int r = bg.getRed();
        int g = bg.getGreen();
        int b = bg.getBlue();
        int brightness = (r * 299 + g * 587 + b * 114) / 1000;
        int offset = brightness > 128 ? -15 : 20;
        return new Color(
            Math.max(0, Math.min(255, r + offset)),
            Math.max(0, Math.min(255, g + offset)),
            Math.max(0, Math.min(255, b + offset))
        );
    }

    private Color calculatePressedColor(Color bg) {
        int r = bg.getRed();
        int g = bg.getGreen();
        int b = bg.getBlue();
        int brightness = (r * 299 + g * 587 + b * 114) / 1000;
        int offset = brightness > 128 ? -15 : 20;
        return new Color(
            Math.max(0, Math.min(255, r + 2 * offset)),
            Math.max(0, Math.min(255, g + 2 * offset)),
            Math.max(0, Math.min(255, b + 2 * offset))
        );
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
        this.isCustomHover = (hoverColor != null);
    }

    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
        this.isCustomPressed = (pressedColor != null);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (!isCustomHover) {
            Color bg = getBackground();
            if (bg != null) {
                this.hoverColor = calculateHoverColor(bg);
            }
        }
        if (!isCustomPressed) {
            Color bg = getBackground();
            if (bg != null) {
                this.pressedColor = calculatePressedColor(bg);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = getBackground();
        if (bg == null) {
            bg = Color.LIGHT_GRAY;
        }

        if (!isEnabled()) {
            g2.setColor(ThemeManager.isDark() ? new Color(0x2A, 0x2D, 0x3A) : new Color(0xE5, 0xE7, 0xEB));
        } else if (getModel().isPressed()) {
            g2.setColor(pressedColor != null ? pressedColor : bg.darker());
        } else if (getModel().isRollover()) {
            g2.setColor(hoverColor != null ? hoverColor : bg.brighter());
        } else {
            g2.setColor(bg);
        }

        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        g2.dispose();

        super.paintComponent(g);
    }
}
