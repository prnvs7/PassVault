package com.passvault.ui;

import com.passvault.PasswordGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class PasswordGeneratorDialog extends JDialog {
    private String generatedPassword;
    private JTextArea passwordDisplay;
    private JSlider lengthSlider;
    private JCheckBox lowercaseCheckbox;
    private JCheckBox uppercaseCheckbox;
    private JCheckBox digitsCheckbox;
    private JCheckBox symbolsCheckbox;

    public PasswordGeneratorDialog(JFrame parent) {
        super(parent, "Password Generator", true);
        this.generatedPassword = null;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(460, 440);
        setLocationRelativeTo(parent);
        setResizable(false);

        initializeUI();
        generateNewPassword();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(248, 249, 251),
                    getWidth(), getHeight(), new Color(241, 245, 249)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        JLabel titleLabel = new JLabel("Generate Strong Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0x11, 0x18, 0x27));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Password display
        passwordDisplay = new JTextArea(2, 40);
        passwordDisplay.setFont(new Font("Consolas", Font.BOLD, 18));
        passwordDisplay.setEditable(false);
        passwordDisplay.setLineWrap(true);
        passwordDisplay.setWrapStyleWord(true);
        passwordDisplay.setBackground(new Color(0xF3, 0xF4, 0xF6));
        passwordDisplay.setForeground(new Color(0x11, 0x18, 0x27));
        passwordDisplay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE5, 0xE7, 0xEB)),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        gbc.gridy = 1;
        mainPanel.add(passwordDisplay, gbc);

        // Length slider label and slider
        JPanel sliderPanel = new JPanel(new BorderLayout(12, 0));
        sliderPanel.setOpaque(false);

        JLabel lengthTextLabel = new JLabel("Length: 16");
        lengthTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lengthTextLabel.setForeground(new Color(0x37, 0x41, 0x51));
        sliderPanel.add(lengthTextLabel, BorderLayout.WEST);

        lengthSlider = new JSlider(8, 32, 16);
        lengthSlider.setOpaque(false);
        lengthSlider.setMajorTickSpacing(4);
        lengthSlider.setPaintTicks(true);
        lengthSlider.setPaintLabels(true);
        lengthSlider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lengthSlider.setForeground(new Color(0x6B, 0x72, 0x80));
        sliderPanel.add(lengthSlider, BorderLayout.CENTER);

        gbc.gridy = 2;
        mainPanel.add(sliderPanel, gbc);

        // Checkboxes in a nice grid
        JPanel cbPanel = new JPanel(new GridLayout(2, 2, 12, 8));
        cbPanel.setOpaque(false);

        lowercaseCheckbox = createModernCheckbox("Lowercase (a-z)", true);
        uppercaseCheckbox = createModernCheckbox("Uppercase (A-Z)", true);
        digitsCheckbox = createModernCheckbox("Digits (0-9)", true);
        symbolsCheckbox = createModernCheckbox("Symbols (!@#$)", true);

        cbPanel.add(lowercaseCheckbox);
        cbPanel.add(uppercaseCheckbox);
        cbPanel.add(digitsCheckbox);
        cbPanel.add(symbolsCheckbox);

        gbc.gridy = 3;
        mainPanel.add(cbPanel, gbc);

        // Add action listeners to dynamically update
        Runnable regenerate = () -> {
            int len = lengthSlider.getValue();
            lengthTextLabel.setText("Length: " + len);
            boolean useLower = lowercaseCheckbox.isSelected();
            boolean useUpper = uppercaseCheckbox.isSelected();
            boolean useDigits = digitsCheckbox.isSelected();
            boolean useSymbols = symbolsCheckbox.isSelected();
            if (!useLower && !useUpper && !useDigits && !useSymbols) {
                lowercaseCheckbox.setSelected(true);
                useLower = true;
            }
            generatedPassword = PasswordGenerator.generatePassword(len, useLower, useUpper, useDigits, useSymbols);
            passwordDisplay.setText(generatedPassword);
        };

        lengthSlider.addChangeListener(e -> regenerate.run());
        lowercaseCheckbox.addActionListener(e -> regenerate.run());
        uppercaseCheckbox.addActionListener(e -> regenerate.run());
        digitsCheckbox.addActionListener(e -> regenerate.run());
        symbolsCheckbox.addActionListener(e -> regenerate.run());

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        RoundedButton regenerateBtn = new RoundedButton("Regenerate", 10);
        regenerateBtn.setIcon(IconUtils.getIcon("wand-sparkles", 16, new Color(0x37, 0x41, 0x51)));
        regenerateBtn.setIconTextGap(6);
        regenerateBtn.setPreferredSize(new Dimension(130, 36));
        regenerateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        regenerateBtn.setBackground(new Color(0xF3, 0xF4, 0xF6));
        regenerateBtn.setForeground(new Color(0x37, 0x41, 0x51));
        regenerateBtn.setHoverColor(new Color(0xE5, 0xE7, 0xEB));
        regenerateBtn.addActionListener(e -> generateNewPassword());
        buttonPanel.add(regenerateBtn);

        RoundedButton copyBtn = new RoundedButton("Copy", 10);
        copyBtn.setIcon(IconUtils.getIcon("copy", 16, new Color(0x37, 0x41, 0x51)));
        copyBtn.setIconTextGap(6);
        copyBtn.setPreferredSize(new Dimension(110, 36));
        copyBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        copyBtn.setBackground(new Color(0xF3, 0xF4, 0xF6));
        copyBtn.setForeground(new Color(0x37, 0x41, 0x51));
        copyBtn.setHoverColor(new Color(0xE5, 0xE7, 0xEB));
        copyBtn.addActionListener(e -> copyToClipboard());
        buttonPanel.add(copyBtn);

        RoundedButton useBtn = new RoundedButton("Use This", 10);
        useBtn.setIcon(IconUtils.getIcon("plus", 16, Color.WHITE));
        useBtn.setIconTextGap(6);
        useBtn.setPreferredSize(new Dimension(120, 36));
        useBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        useBtn.setBackground(new Color(0x25, 0x63, 0xEB)); // Blue Accent
        useBtn.setForeground(Color.WHITE);
        useBtn.addActionListener(e -> usePassword());
        buttonPanel.add(useBtn);

        gbc.gridy = 4;
        gbc.insets = new Insets(16, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
    }

    private JCheckBox createModernCheckbox(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setForeground(new Color(0x37, 0x41, 0x51));
        cb.setOpaque(false);
        cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return cb;
    }

    private void generateNewPassword() {
        int length = lengthSlider.getValue();
        boolean useLower = lowercaseCheckbox.isSelected();
        boolean useUpper = uppercaseCheckbox.isSelected();
        boolean useDigits = digitsCheckbox.isSelected();
        boolean useSymbols = symbolsCheckbox.isSelected();

        if (!useLower && !useUpper && !useDigits && !useSymbols) {
            lowercaseCheckbox.setSelected(true);
        }

        generatedPassword = PasswordGenerator.generatePassword(length,
                lowercaseCheckbox.isSelected(),
                uppercaseCheckbox.isSelected(),
                digitsCheckbox.isSelected(),
                symbolsCheckbox.isSelected());

        passwordDisplay.setText(generatedPassword);
    }

    private void copyToClipboard() {
        StringSelection stringSelection = new StringSelection(generatedPassword);
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        JOptionPane.showMessageDialog(this, "Password copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void usePassword() {
        dispose();
    }

    public String getGeneratedPassword() {
        return generatedPassword;
    }
}
