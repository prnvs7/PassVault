package com.passvault.ui;

import com.passvault.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {
    private AuthService authService;
    private ModernPasswordField passwordField;
    private RoundedButton loginButton;
    private JLabel titleLabel;
    private JLabel statusLabel;
    private boolean isFirstTime;

    // Password recovery components (First-time setup only)
    private JComboBox<String> securityQuestionCb;
    private ModernTextField securityAnswerField;

    public LoginFrame(AuthService authService) {
        this.authService = authService;
        this.isFirstTime = authService.isFirstTime();

        setTitle("PassVault - " + (isFirstTime ? "Setup Master Password" : "Unlock Vault"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Adjust size dynamically to fit recovery fields
        if (isFirstTime) {
            setSize(460, 600);
        } else {
            setSize(460, 420);
        }
        
        setLocationRelativeTo(null);

        // Background Panel using Flat Light background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0xF8, 0xF9, 0xFB));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        setContentPane(backgroundPanel);

        // White Card container panel
        RoundedPanel cardPanel = new RoundedPanel(16, new Color(0xE5, 0xE7, 0xEB));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        backgroundPanel.add(cardPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        // Icon / Logo Symbol
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(IconUtils.getIcon("shield", 48, new Color(0x25, 0x63, 0xEB)));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        cardPanel.add(logoLabel, gbc);

        // Title Label
        titleLabel = new JLabel(isFirstTime ? "Setup Master Password" : "Unlock Your Vault");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0x11, 0x18, 0x27));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        cardPanel.add(titleLabel, gbc);

        // Instructions Label
        JLabel instructionLabel = new JLabel(
                isFirstTime ? "Create a strong master password for your vault" : "Enter your master password to decrypt credentials"
        );
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instructionLabel.setForeground(new Color(0x6B, 0x72, 0x80));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        cardPanel.add(instructionLabel, gbc);

        // Password field label for first time setup
        if (isFirstTime) {
            JLabel passLabel = new JLabel("Master Password:");
            passLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            passLabel.setForeground(new Color(0x37, 0x41, 0x51));
            gbc.gridy = 3;
            cardPanel.add(passLabel, gbc);
        }

        // Password field (rounded and customized)
        passwordField = new ModernPasswordField();
        passwordField.setPreferredSize(new Dimension(280, 38));
        passwordField.addActionListener(e -> performAction());
        gbc.gridy = isFirstTime ? 4 : 3;
        cardPanel.add(passwordField, gbc);

        // Show password checkbox
        JCheckBox showPasswordCb = new JCheckBox("Show Password");
        showPasswordCb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCb.setForeground(new Color(0x6B, 0x72, 0x80));
        showPasswordCb.setOpaque(false);
        showPasswordCb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        char defaultEchoChar = passwordField.getEchoChar();
        showPasswordCb.addActionListener(e -> {
            if (showPasswordCb.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultEchoChar);
            }
        });
        gbc.gridy = isFirstTime ? 5 : 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        cardPanel.add(showPasswordCb, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Security Question & Answer (first time setup only)
        if (isFirstTime) {
            JLabel qLabel = new JLabel("Security Question:");
            qLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            qLabel.setForeground(new Color(0x37, 0x41, 0x51));
            gbc.gridy = 6;
            cardPanel.add(qLabel, gbc);

            String[] questions = {
                "What was the name of your first pet?",
                "What is your mother's maiden name?",
                "What was the name of your elementary school?",
                "In what city were you born?",
                "What was the make and model of your first car?"
            };
            securityQuestionCb = new JComboBox<>(questions);
            securityQuestionCb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            securityQuestionCb.setPreferredSize(new Dimension(280, 36));
            gbc.gridy = 7;
            cardPanel.add(securityQuestionCb, gbc);

            JLabel aLabel = new JLabel("Security Answer (Case Insensitive):");
            aLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            aLabel.setForeground(new Color(0x37, 0x41, 0x51));
            gbc.gridy = 8;
            cardPanel.add(aLabel, gbc);

            securityAnswerField = new ModernTextField();
            securityAnswerField.setPreferredSize(new Dimension(280, 38));
            gbc.gridy = 9;
            cardPanel.add(securityAnswerField, gbc);
        }

        // Strength indicator (for setup mode)
        if (isFirstTime) {
            JLabel strengthLabel = new JLabel("Strength: None");
            strengthLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            strengthLabel.setForeground(new Color(0x6B, 0x72, 0x80));
            strengthLabel.setHorizontalAlignment(SwingConstants.CENTER);

            gbc.gridy = 10;
            cardPanel.add(strengthLabel, gbc);

            passwordField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent evt) {
                    String pwd = new String(passwordField.getPassword());
                    String strength = evaluatePasswordStrength(pwd);
                    strengthLabel.setText("Strength: " + strength);
                    updateStrengthColor(strengthLabel, strength);
                }
            });
        }

        // Status Label
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(new Color(0xDC, 0x26, 0x26)); // Red Accent
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = isFirstTime ? 11 : 5;
        cardPanel.add(statusLabel, gbc);

        // Action Button
        loginButton = new RoundedButton(isFirstTime ? "Create Password" : "Unlock Vault", 10);
        loginButton.setIcon(IconUtils.getIcon(isFirstTime ? "plus" : "lock", 16, Color.WHITE));
        loginButton.setIconTextGap(8);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setPreferredSize(new Dimension(280, 40));
        loginButton.setBackground(new Color(0x25, 0x63, 0xEB)); // Blue Accent
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> performAction());

        gbc.gridy = isFirstTime ? 12 : 6;
        gbc.insets = new Insets(8, 0, 0, 0);
        cardPanel.add(loginButton, gbc);

        // Forgot Master Password option
        if (!isFirstTime) {
            JButton resetBtn = new JButton("Forgot Master Password? Recover Vault");
            resetBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            resetBtn.setForeground(new Color(0x25, 0x63, 0xEB));
            resetBtn.setContentAreaFilled(false);
            resetBtn.setBorderPainted(false);
            resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            resetBtn.addActionListener(e -> recoverVaultFlow());

            gbc.gridy = 7;
            gbc.insets = new Insets(6, 0, 0, 0);
            cardPanel.add(resetBtn, gbc);
        }

        // Auto-focus on password field
        SwingUtilities.invokeLater(() -> passwordField.requestFocusInWindow());
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

            String question = (String) securityQuestionCb.getSelectedItem();
            String answer = securityAnswerField.getText().trim();
            if (answer.isEmpty()) {
                statusLabel.setText("Security answer cannot be empty");
                return;
            }

            if (authService.setupMasterPassword(password, question, answer)) {
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
                label.setForeground(new Color(0xDC, 0x26, 0x26));
                break;
            case "Medium":
                label.setForeground(new Color(0xD9, 0x77, 0x06));
                break;
            case "Strong":
                label.setForeground(new Color(0x16, 0xA3, 0x4A));
                break;
            case "Very Strong":
                label.setForeground(new Color(0x15, 0x80, 0x3D));
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

    private void recoverVaultFlow() {
        String securityQuestion = authService.getSecurityQuestion();
        
        if (securityQuestion == null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "No password recovery question is set up for this vault.\n" +
                    "To use the application, you must reset the vault (this deletes ALL stored passwords permanently).\n\n" +
                    "Do you wish to proceed?",
                    "No Recovery Configured - Reset Vault",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                performDestructiveReset();
            }
            return;
        }

        String answer = JOptionPane.showInputDialog(this,
                "Security Question:\n" + securityQuestion + "\n\nEnter your answer:",
                "Master Password Recovery",
                JOptionPane.QUESTION_MESSAGE);

        if (answer == null) {
            return;
        }

        if (answer.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Answer cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String oldPassword = authService.recoverMasterPassword(answer);
        if (oldPassword == null) {
            JOptionPane.showMessageDialog(this, "Incorrect answer. Access denied.", "Verification Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prompt for new master password
        JPanel passPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        JPasswordField newPassField = new JPasswordField(20);
        JPasswordField confirmPassField = new JPasswordField(20);
        
        passPanel.add(new JLabel("New Master Password:"));
        passPanel.add(newPassField);
        passPanel.add(new JLabel("Confirm New Password:"));
        passPanel.add(confirmPassField);

        int result = JOptionPane.showConfirmDialog(this, passPanel, "Set New Master Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newPassword = new String(newPassField.getPassword());
        String confirmPassword = new String(confirmPassField.getPassword());

        if (newPassword.isEmpty() || newPassword.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters.", "Invalid Password", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Match Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = authService.changeMasterPassword(newPassword, securityQuestion, answer);
        if (success) {
            JOptionPane.showMessageDialog(this, "Master Password updated successfully! Please login with your new password.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            passwordField.setText("");
            statusLabel.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update master password. Database error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performDestructiveReset() {
        try {
            String dataDir = System.getProperty("user.home") + "/.passvault";
            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(dataDir + "/config.json"));
            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(dataDir + "/vault.json"));

            com.passvault.StorageManager sm = new com.passvault.StorageManager();
            AuthService newAuthService = new AuthService(sm);
            LoginFrame newLoginFrame = new LoginFrame(newAuthService);
            newLoginFrame.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error resetting vault: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
