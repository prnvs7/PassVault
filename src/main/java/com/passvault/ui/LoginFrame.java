package com.passvault.ui;

import com.passvault.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private AuthService authService;
    private JPasswordField passwordField;
    private JLabel titleLabel;
    private JButton loginButton;
    private JLabel statusLabel;
    private boolean isFirstTime;

    public LoginFrame(AuthService authService) {
        this.authService = authService;
        this.isFirstTime = authService.isFirstTime();

        setTitle("PassVault - " + (isFirstTime ? "Setup Master Password" : "Unlock Vault"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top panel with title
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(45, 45, 48));
        topPanel.setPreferredSize(new Dimension(400, 60));
        topPanel.setLayout(new BorderLayout());

        titleLabel = new JLabel(isFirstTime ? "🔒 Setup Master Password" : "🔓 Unlock Your Vault");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        topPanel.add(titleLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // Center panel with form
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Instructions label
        JLabel instructionLabel = new JLabel(
                isFirstTime ? "Create a strong master password for your vault" : "Enter your master password"
        );
        instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        instructionLabel.setForeground(new Color(100, 100, 100));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(instructionLabel, gbc);

        // Password label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel passwordLabel = new JLabel("Master Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        centerPanel.add(passwordLabel, gbc);

        // Password field
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        passwordField.setPreferredSize(new Dimension(250, 35));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        passwordField.addActionListener(e -> performAction());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        centerPanel.add(passwordField, gbc);

        // Strength indicator (for setup mode)
        if (isFirstTime) {
            JLabel strengthLabel = new JLabel("Strength: None");
            strengthLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            strengthLabel.setForeground(new Color(150, 150, 150));

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            centerPanel.add(strengthLabel, gbc);

            passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    String pwd = new String(passwordField.getPassword());
                    String strength = evaluatePasswordStrength(pwd);
                    strengthLabel.setText("Strength: " + strength);
                    updateStrengthColor(strengthLabel, strength);
                }
            });
        }

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(200, 50, 50));

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        centerPanel.add(statusLabel, gbc);

        // Login button
        loginButton = new JButton(isFirstTime ? "Create Password" : "Unlock");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginButton.setPreferredSize(new Dimension(250, 40));
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> performAction());

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        centerPanel.add(loginButton, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void performAction() {
        String password = new String(passwordField.getPassword());

        if (password.isEmpty()) {
            statusLabel.setText("Password cannot be empty");
            return;
        }

        if (isFirstTime) {
            if (password.length() < 8) {
                statusLabel.setText("Password must be at least 8 characters");
                return;
            }
            if (authService.setupMasterPassword(password)) {
                openVault();
            } else {
                statusLabel.setText("Failed to setup password");
            }
        } else {
            if (authService.verifyMasterPassword(password)) {
                openVault();
            } else {
                statusLabel.setText("Incorrect password");
                passwordField.setText("");
            }
        }
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

    private void openVault() {
        try {
            VaultFrame vaultFrame = new VaultFrame(authService);
            vaultFrame.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error opening vault");
        }
    }
}
