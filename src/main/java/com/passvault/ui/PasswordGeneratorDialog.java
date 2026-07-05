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
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setResizable(false);

        initializeUI();
        generateNewPassword();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Generate Strong Password");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Password display
        passwordDisplay = new JTextArea(3, 40);
        passwordDisplay.setFont(new Font("Monospaced", Font.PLAIN, 13));
        passwordDisplay.setEditable(false);
        passwordDisplay.setLineWrap(true);
        passwordDisplay.setWrapStyleWord(true);
        passwordDisplay.setBackground(new Color(240, 240, 240));
        passwordDisplay.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(passwordDisplay, gbc);

        // Length slider
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JLabel lengthLabel = new JLabel("Length:");
        lengthLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        mainPanel.add(lengthLabel, gbc);

        lengthSlider = new JSlider(8, 32, 16);
        lengthSlider.setMajorTickSpacing(4);
        lengthSlider.setMinorTickSpacing(1);
        lengthSlider.setPaintTicks(true);
        lengthSlider.setPaintLabels(true);
        lengthSlider.addChangeListener(e -> generateNewPassword());

        gbc.gridx = 1;
        mainPanel.add(lengthSlider, gbc);

        // Checkboxes
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;

        lowercaseCheckbox = new JCheckBox("Lowercase (a-z)", true);
        lowercaseCheckbox.addActionListener(e -> generateNewPassword());
        mainPanel.add(lowercaseCheckbox, gbc);

        gbc.gridy = 4;
        uppercaseCheckbox = new JCheckBox("Uppercase (A-Z)", true);
        uppercaseCheckbox.addActionListener(e -> generateNewPassword());
        mainPanel.add(uppercaseCheckbox, gbc);

        gbc.gridy = 5;
        digitsCheckbox = new JCheckBox("Digits (0-9)", true);
        digitsCheckbox.addActionListener(e -> generateNewPassword());
        mainPanel.add(digitsCheckbox, gbc);

        gbc.gridy = 6;
        symbolsCheckbox = new JCheckBox("Symbols (!@#$)", true);
        symbolsCheckbox.addActionListener(e -> generateNewPassword());
        mainPanel.add(symbolsCheckbox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton regenerateBtn = new JButton("🔄 Regenerate");
        regenerateBtn.setPreferredSize(new Dimension(130, 35));
        regenerateBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        regenerateBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        regenerateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regenerateBtn.addActionListener(e -> generateNewPassword());
        buttonPanel.add(regenerateBtn);

        JButton copyBtn = new JButton("📋 Copy");
        copyBtn.setPreferredSize(new Dimension(130, 35));
        copyBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        copyBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        copyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        copyBtn.addActionListener(e -> copyToClipboard());
        buttonPanel.add(copyBtn);

        JButton useBtn = new JButton("✓ Use This");
        useBtn.setPreferredSize(new Dimension(130, 35));
        useBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        useBtn.setBackground(new Color(0, 120, 215));
        useBtn.setForeground(Color.WHITE);
        useBtn.setBorder(BorderFactory.createEmptyBorder());
        useBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        useBtn.addActionListener(e -> usePassword());
        buttonPanel.add(useBtn);

        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
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
