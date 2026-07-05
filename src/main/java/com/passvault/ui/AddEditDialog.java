package com.passvault.ui;

import com.passvault.PasswordEntry;
import com.passvault.PasswordGenerator;
import com.passvault.VaultService;

import javax.swing.*;
import java.awt.*;

public class AddEditDialog extends JDialog {
    private VaultService vaultService;
    private PasswordEntry entry;
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField urlField;
    private JLabel strengthLabel;
    private boolean isSaved = false;

    public AddEditDialog(JFrame parent, VaultService vaultService, PasswordEntry entry) {
        super(parent, entry == null ? "Add New Entry" : "Edit Entry", true);
        this.vaultService = vaultService;
        this.entry = entry;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 450);
        setLocationRelativeTo(parent);
        setResizable(false);

        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Site/App Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Site/App Name:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        mainPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nameField.setPreferredSize(new Dimension(300, 30));
        nameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        gbc.gridy = 1;
        mainPanel.add(nameField, gbc);

        // Username/Email
        gbc.gridy = 2;
        JLabel usernameLabel = new JLabel("Username/Email:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        usernameField.setPreferredSize(new Dimension(300, 30));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        gbc.gridy = 3;
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = 4;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        mainPanel.add(passwordLabel, gbc);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BorderLayout(5, 0));

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        passwordField.setPreferredSize(new Dimension(230, 30));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateStrengthIndicator();
            }
        });
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        JButton generateBtn = new JButton("🔧 Generate");
        generateBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
        generateBtn.setPreferredSize(new Dimension(90, 30));
        generateBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        generateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateBtn.addActionListener(e -> generatePassword());
        passwordPanel.add(generateBtn, BorderLayout.EAST);

        gbc.gridy = 5;
        mainPanel.add(passwordPanel, gbc);

        // Strength indicator
        strengthLabel = new JLabel("Strength: None");
        strengthLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        strengthLabel.setForeground(new Color(150, 150, 150));
        gbc.gridy = 6;
        mainPanel.add(strengthLabel, gbc);

        // URL
        gbc.gridy = 7;
        JLabel urlLabel = new JLabel("URL (optional):");
        urlLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        mainPanel.add(urlLabel, gbc);

        urlField = new JTextField(20);
        urlField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        urlField.setPreferredSize(new Dimension(300, 30));
        urlField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        gbc.gridy = 8;
        mainPanel.add(urlField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(80, 35));
        cancelBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cancelBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);

        JButton saveBtn = new JButton("Save");
        saveBtn.setPreferredSize(new Dimension(80, 35));
        saveBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        saveBtn.setBackground(new Color(0, 120, 215));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBorder(BorderFactory.createEmptyBorder());
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> saveEntry());
        buttonPanel.add(saveBtn);

        gbc.gridy = 9;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);

        // Pre-fill if editing
        if (entry != null) {
            nameField.setText(entry.getName());
            usernameField.setText(entry.getUsername());
            passwordField.setText(entry.getPassword());
            urlField.setText(entry.getUrl());
            updateStrengthIndicator();
        }

        setContentPane(mainPanel);
    }

    private void generatePassword() {
        String password = PasswordGenerator.generatePassword(16);
        passwordField.setText(password);
        updateStrengthIndicator();
    }

    private void updateStrengthIndicator() {
        String pwd = new String(passwordField.getPassword());
        String strength = evaluatePasswordStrength(pwd);
        strengthLabel.setText("Strength: " + strength);
        updateStrengthColor(strengthLabel, strength);
    }

    private String evaluatePasswordStrength(String password) {
        if (password.isEmpty()) return "None";

        int score = 0;
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};:'\",.<>?/].*")) score++;

        if (score <= 2) return "Weak";
        if (score <= 4) return "Medium";
        if (score <= 6) return "Strong";
        return "Very Strong";
    }

    private void updateStrengthColor(JLabel label, String strength) {
        switch (strength) {
            case "Weak":
                label.setForeground(new Color(200, 50, 50));
                break;
            case "Medium":
                label.setForeground(new Color(200, 150, 50));
                break;
            case "Strong":
                label.setForeground(new Color(100, 200, 50));
                break;
            case "Very Strong":
                label.setForeground(new Color(50, 150, 50));
                break;
        }
    }

    private void saveEntry() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String url = urlField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (entry == null) {
                // Add new entry
                PasswordEntry newEntry = new PasswordEntry(name, username, password, url);
                vaultService.addEntry(newEntry);
            } else {
                // Update existing entry
                entry.setName(name);
                entry.setUsername(username);
                entry.setPassword(password);
                entry.setUrl(url);
                vaultService.updateEntry(entry);
            }

            isSaved = true;
            JOptionPane.showMessageDialog(this, "Entry saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving entry: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
