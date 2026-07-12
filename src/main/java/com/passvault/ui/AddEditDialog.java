package com.passvault.ui;

import com.passvault.PasswordEntry;
import com.passvault.PasswordGenerator;
import com.passvault.VaultService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddEditDialog extends JDialog {
    private VaultService vaultService;
    private PasswordEntry entry;
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField urlField;
    private JTextArea notesArea;
    private JLabel strengthLabel;
    private boolean isSaved = false;

    public AddEditDialog(JFrame parent, VaultService vaultService, PasswordEntry entry) {
        super(parent, entry == null ? "Add New Entry" : "Edit Entry", true);
        this.vaultService = vaultService;
        this.entry = entry;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(520, 520);
        setLocationRelativeTo(parent);
        setResizable(false);

        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Paint beautiful modern subtle background gradient
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 20, 24));

        // Layout constraints for label column (Column 0)
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.gridx = 0;
        gbcLabel.anchor = GridBagConstraints.WEST;
        gbcLabel.insets = new Insets(8, 0, 8, 12);
        gbcLabel.fill = GridBagConstraints.NONE;
        gbcLabel.weightx = 0.0;

        // Layout constraints for field column (Column 1)
        GridBagConstraints gbcField = new GridBagConstraints();
        gbcField.gridx = 1;
        gbcField.fill = GridBagConstraints.HORIZONTAL;
        gbcField.weightx = 1.0;
        gbcField.insets = new Insets(8, 0, 8, 0);

        // Site/App Name
        gbcLabel.gridy = 0;
        JLabel nameLabel = new JLabel("Site/App Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(new Color(0x37, 0x41, 0x51));
        mainPanel.add(nameLabel, gbcLabel);

        nameField = new ModernTextField(20);
        gbcField.gridy = 0;
        mainPanel.add(nameField, gbcField);

        // Username/Email
        gbcLabel.gridy = 1;
        JLabel usernameLabel = new JLabel("Username/Email:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel.setForeground(new Color(0x37, 0x41, 0x51));
        mainPanel.add(usernameLabel, gbcLabel);

        usernameField = new ModernTextField(20);
        gbcField.gridy = 1;
        mainPanel.add(usernameField, gbcField);

        // Password
        gbcLabel.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(0x37, 0x41, 0x51));
        mainPanel.add(passwordLabel, gbcLabel);

        JPanel passwordPanel = new JPanel(new BorderLayout(8, 0));
        passwordPanel.setOpaque(false);

        passwordField = new ModernPasswordField(20);
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateStrengthIndicator();
            }
        });
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        JPanel eastPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        eastPanel.setOpaque(false);

        JCheckBox showPasswordCb = new JCheckBox("Show");
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
        eastPanel.add(showPasswordCb);

        RoundedButton generateBtn = new RoundedButton("Generate", 8);
        generateBtn.setIcon(IconUtils.getIcon("wand-sparkles", 16, new Color(0x37, 0x41, 0x51)));
        generateBtn.setIconTextGap(6);
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        generateBtn.setPreferredSize(new Dimension(110, 32));
        generateBtn.setBackground(new Color(0xF3, 0xF4, 0xF6));
        generateBtn.setForeground(new Color(0x37, 0x41, 0x51));
        generateBtn.setHoverColor(new Color(0xE5, 0xE7, 0xEB));
        generateBtn.addActionListener(e -> generatePassword());
        eastPanel.add(generateBtn);

        passwordPanel.add(eastPanel, BorderLayout.EAST);

        gbcField.gridy = 2;
        mainPanel.add(passwordPanel, gbcField);

        // Strength badge
        gbcLabel.gridy = 3;
        JLabel strengthTitleLabel = new JLabel("Strength:");
        strengthTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        strengthTitleLabel.setForeground(new Color(0x37, 0x41, 0x51));
        mainPanel.add(strengthTitleLabel, gbcLabel);

        strengthLabel = new JLabel("None");
        strengthLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        strengthLabel.setForeground(new Color(150, 150, 150));
        gbcField.gridy = 3;
        mainPanel.add(strengthLabel, gbcField);

        // URL
        gbcLabel.gridy = 4;
        JLabel urlLabel = new JLabel("Website URL:");
        urlLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        urlLabel.setForeground(new Color(0x37, 0x41, 0x51));
        mainPanel.add(urlLabel, gbcLabel);

        urlField = new ModernTextField(20);
        gbcField.gridy = 4;
        mainPanel.add(urlField, gbcField);

        // Notes Area
        gbcLabel.gridy = 5;
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        notesLabel.setForeground(new Color(0x37, 0x41, 0x51));
        mainPanel.add(notesLabel, gbcLabel);

        notesArea = new JTextArea(4, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setForeground(new Color(0x11, 0x18, 0x27));
        notesArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(BorderFactory.createLineBorder(new Color(0xE5, 0xE7, 0xEB)));
        
        gbcField.gridy = 5;
        gbcField.fill = GridBagConstraints.BOTH;
        gbcField.weighty = 1.0;
        mainPanel.add(notesScroll, gbcField);

        // Reset constraints for buttons
        gbcField.fill = GridBagConstraints.HORIZONTAL;
        gbcField.weighty = 0.0;

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);

        RoundedButton cancelBtn = new RoundedButton("Cancel", 10);
        cancelBtn.setPreferredSize(new Dimension(100, 36));
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cancelBtn.setBackground(new Color(0xF3, 0xF4, 0xF6));
        cancelBtn.setForeground(new Color(0x37, 0x41, 0x51));
        cancelBtn.setHoverColor(new Color(0xE5, 0xE7, 0xEB));
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);

        RoundedButton saveBtn = new RoundedButton("Save", 10);
        saveBtn.setIcon(IconUtils.getIcon("plus", 16, Color.WHITE));
        saveBtn.setIconTextGap(6);
        saveBtn.setPreferredSize(new Dimension(100, 36));
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.setBackground(new Color(0x25, 0x63, 0xEB)); // Blue Accent
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> saveEntry());
        buttonPanel.add(saveBtn);

        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridx = 0;
        gbcButtons.gridy = 6;
        gbcButtons.gridwidth = 2;
        gbcButtons.fill = GridBagConstraints.HORIZONTAL;
        gbcButtons.weightx = 1.0;
        gbcButtons.anchor = GridBagConstraints.SOUTH;
        gbcButtons.insets = new Insets(16, 0, 0, 0);
        mainPanel.add(buttonPanel, gbcButtons);

        // Pre-fill if editing
        if (entry != null) {
            nameField.setText(entry.getName());
            usernameField.setText(entry.getUsername());
            passwordField.setText(entry.getPassword());
            urlField.setText(entry.getUrl());
            notesArea.setText(entry.getNotes());
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
        strengthLabel.setText(strength);
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
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\] {};:'\",.<>?/].*")) score++;

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
            default:
                label.setForeground(new Color(0x6B, 0x72, 0x80));
                break;
        }
    }

    private void saveEntry() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String url = urlField.getText().trim();
        String notes = notesArea.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (entry == null) {
                PasswordEntry newEntry = new PasswordEntry(name, username, password, url, notes);
                vaultService.addEntry(newEntry);
            } else {
                entry.setName(name);
                entry.setUsername(username);
                entry.setPassword(password);
                entry.setUrl(url);
                entry.setNotes(notes);
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
