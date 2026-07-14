package com.passvault.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class SearchTextField extends JTextField {
    private String placeholder = "Search passwords...";
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
        // Increase padding on the right for the clear button
        setBorder(new EmptyBorder(8, 36, 8, 32));
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        updateColors();

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                borderColor = ThemeManager.getBlueAccent();
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                borderColor = ThemeManager.getBorderColor();
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!getText().isEmpty()) {
                    int x = e.getX();
                    int y = e.getY();
                    int iconX = getWidth() - 28;
                    int iconY = (getHeight() - 16) / 2;
                    if (x >= iconX - 4 && x <= iconX + 20 && y >= iconY - 4 && y <= iconY + 20) {
                        setText("");
                        // Trigger listeners
                        java.awt.event.KeyEvent keyEvent = new java.awt.event.KeyEvent(
                            SearchTextField.this, 
                            java.awt.event.KeyEvent.KEY_RELEASED, 
                            System.currentTimeMillis(), 
                            0, 
                            java.awt.event.KeyEvent.VK_UNDEFINED, 
                            java.awt.event.KeyEvent.CHAR_UNDEFINED
                        );
                        for (java.awt.event.KeyListener listener : getKeyListeners()) {
                            listener.keyReleased(keyEvent);
                        }
                        repaint();
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!getText().isEmpty()) {
                    int x = e.getX();
                    int iconX = getWidth() - 28;
                    if (x >= iconX - 4 && x <= iconX + 20) {
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        return;
                    }
                }
                setCursor(new Cursor(Cursor.TEXT_CURSOR));
            }
        });
    }

    @Override
    public void updateUI() {
        super.updateUI();
        updateColors();
    }

    private void updateColors() {
        setForeground(ThemeManager.getTextPrimary());
        setCaretColor(ThemeManager.getTextPrimary());
        borderColor = ThemeManager.getBorderColor();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int r = getHeight() - 2;

        // Background
        g2.setColor(ThemeManager.getCardColor());
        g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, r, r);

        // Focus Glow
        if (hasFocus()) {
            g2.setColor(new Color(0x3B, 0x82, 0xF6, 60)); // Focus glow
            g2.setStroke(new BasicStroke(3.0f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, r, r);
        }

        // Border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, r, r);

        // Search Icon (monochrome, gray)
        FlatSVGIcon searchIcon = IconUtils.getIcon("search", 16, ThemeManager.getTextSecondary());
        if (searchIcon != null) {
            int iconY = (getHeight() - 16) / 2;
            searchIcon.paintIcon(this, g2, 14, iconY);
        }

        // Clear Icon (when text is present)
        if (!getText().isEmpty()) {
            FlatSVGIcon clearIcon = IconUtils.getIcon("x", 16, ThemeManager.getTextSecondary());
            if (clearIcon != null) {
                int iconY = (getHeight() - 16) / 2;
                clearIcon.paintIcon(this, g2, getWidth() - 26, iconY);
            }
        }

        g2.dispose();

        super.paintComponent(g);

        // Placeholder Text
        if (getText().isEmpty() && !hasFocus()) {
            Graphics2D gPlaceholder = (Graphics2D) g.create();
            gPlaceholder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gPlaceholder.setColor(ThemeManager.getTextSecondary());
            gPlaceholder.setFont(getFont());
            FontMetrics placeholderFm = gPlaceholder.getFontMetrics();
            int textY = (getHeight() - placeholderFm.getHeight()) / 2 + placeholderFm.getAscent();
            gPlaceholder.drawString(placeholder, 36, textY);
            gPlaceholder.dispose();
        }
    }
}
