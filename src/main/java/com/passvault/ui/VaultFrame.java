package com.passvault.ui;

import com.passvault.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import javax.swing.Timer;

public class VaultFrame extends JFrame {
    private AuthService authService;
    private VaultService vaultService;
    private StorageManager storageManager;
    private JTable passwordTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel entryCountLabel;
    private JLabel masterPasswordInfoLabel;

    public VaultFrame(AuthService authService) throws Exception {
        this.authService = authService;
        this.storageManager = new StorageManager();
        this.vaultService = new VaultService(storageManager);
        this.vaultService.loadVault(authService.getCurrentKey());

        setTitle("PassVault - Password Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Build UI
        add(createTopBar(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel();
        topBar.setBackground(new Color(45, 45, 48));
        topBar.setPreferredSize(new Dimension(1100, 70));
        topBar.setLayout(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Left side: Title and password info
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(45, 45, 48));
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));

        JLabel titleLabel = new JLabel("🔒 PassVault");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);

        // Master password info
        masterPasswordInfoLabel = new JLabel();
        masterPasswordInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        masterPasswordInfoLabel.setForeground(new Color(180, 180, 180));
        updateMasterPasswordInfo();
        leftPanel.add(masterPasswordInfoLabel);

        topBar.add(leftPanel, BorderLayout.WEST);

        // Right side: Entry count and lock button
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(45, 45, 48));
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));

        entryCountLabel = new JLabel();
        entryCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        entryCountLabel.setForeground(new Color(150, 150, 150));
        updateEntryCount();
        rightPanel.add(entryCountLabel);

        JButton lockButton = new JButton("🔐 Lock");
        lockButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lockButton.setPreferredSize(new Dimension(80, 30));
        lockButton.setBackground(new Color(200, 50, 50));
        lockButton.setForeground(Color.WHITE);
        lockButton.setBorder(BorderFactory.createEmptyBorder());
        lockButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lockButton.addActionListener(e -> lockVault());
        rightPanel.add(lockButton);

        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Content area
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Search bar
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(250, 250, 250));
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchPanel.add(searchLabel);

        searchField = new JTextField(25);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setPreferredSize(new Dimension(250, 30));
        searchField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                performSearch();
            }
        });
        searchPanel.add(searchField);

        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        createPasswordTable();
        JScrollPane scrollPane = new JScrollPane(passwordTable);
        scrollPane.setPreferredSize(new Dimension(800, 500));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(240, 240, 240));
        sidebar.setPreferredSize(new Dimension(200, 700));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        // Sidebar title
        JLabel sidebarTitle = new JLabel("Actions");
        sidebarTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        sidebarTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        sidebar.add(sidebarTitle);

        // Add Entry Button
        JButton addButton = createSidebarButton("➕ Add Entry", e -> openAddEntryDialog());
        sidebar.add(addButton);

        // Edit Entry Button
        JButton editButton = createSidebarButton("✏️ Edit Entry", e -> editSelectedEntry());
        sidebar.add(editButton);

        // Delete Entry Button
        JButton deleteButton = createSidebarButton("🗑️ Delete Entry", e -> deleteSelectedEntry());
        sidebar.add(deleteButton);

        // Generate Password Button
        JButton generateButton = createSidebarButton("🔧 Generate Password", e -> openGeneratorDialog());
        sidebar.add(generateButton);

        // Copy Password Button
        JButton copyButton = createSidebarButton("📋 Copy Password", e -> copyPasswordToClipboard());
        sidebar.add(copyButton);

        sidebar.add(Box.createVerticalGlue());

        // Settings section
        JLabel settingsLabel = new JLabel("Settings");
        settingsLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        settingsLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        sidebar.add(settingsLabel);

        JButton aboutButton = createSidebarButton("ℹ️ About", e -> showAbout());
        sidebar.add(aboutButton);

        return sidebar;
    }

    private JButton createSidebarButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 11));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(170, 35));
        button.setMaximumSize(new Dimension(170, 35));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        button.setMargin(new Insets(5, 5, 5, 5));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }

    private void createPasswordTable() {
        tableModel = new DefaultTableModel(new String[]{"Name", "Username", "Password", "URL", "Strength", "Last Modified"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        passwordTable = new JTable(tableModel);
        passwordTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        passwordTable.setRowHeight(25);
        passwordTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        passwordTable.getTableHeader().setBackground(new Color(240, 240, 240));
        passwordTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        passwordTable.setGridColor(new Color(220, 220, 220));
        passwordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        passwordTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedEntry();
                }
            }
        });

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<PasswordEntry> entries = vaultService.getEntries();

        for (PasswordEntry entry : entries) {
            long lastModified = entry.getLastModifiedAt();
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(lastModified));

            tableModel.addRow(new Object[]{
                    entry.getName(),
                    entry.getUsername(),
                    "••••••••",
                    entry.getUrl(),
                    entry.getPasswordStrength(),
                    formattedDate
            });
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        tableModel.setRowCount(0);

        List<PasswordEntry> results = query.isEmpty() ?
                vaultService.getEntries() :
                vaultService.searchEntries(query);

        for (PasswordEntry entry : results) {
            long lastModified = entry.getLastModifiedAt();
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(lastModified));

            tableModel.addRow(new Object[]{
                    entry.getName(),
                    entry.getUsername(),
                    "••••••••",
                    entry.getUrl(),
                    entry.getPasswordStrength(),
                    formattedDate
            });
        }
    }

    private void openAddEntryDialog() {
        AddEditDialog dialog = new AddEditDialog(this, vaultService, null);
        dialog.setVisible(true);
        refreshTable();
        updateEntryCount();
    }

    private void editSelectedEntry() {
        int selectedRow = passwordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an entry to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String entryName = (String) tableModel.getValueAt(selectedRow, 0);
        PasswordEntry entry = vaultService.getEntries().stream()
                .filter(e -> e.getName().equals(entryName))
                .findFirst()
                .orElse(null);

        if (entry != null) {
            AddEditDialog dialog = new AddEditDialog(this, vaultService, entry);
            dialog.setVisible(true);
            refreshTable();
        }
    }

    private void deleteSelectedEntry() {
        int selectedRow = passwordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an entry to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String entryName = (String) tableModel.getValueAt(selectedRow, 0);
        PasswordEntry entry = vaultService.getEntries().stream()
                .filter(e -> e.getName().equals(entryName))
                .findFirst()
                .orElse(null);

        if (entry != null) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete '" + entry.getName() + "'?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                try {
                    vaultService.deleteEntry(entry.getId());
                    refreshTable();
                    updateEntryCount();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting entry: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void copyPasswordToClipboard() {
        int selectedRow = passwordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an entry", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String entryName = (String) tableModel.getValueAt(selectedRow, 0);
        PasswordEntry entry = vaultService.getEntries().stream()
                .filter(e -> e.getName().equals(entryName))
                .findFirst()
                .orElse(null);

        if (entry != null) {
            StringSelection stringSelection = new StringSelection(entry.getPassword());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            JOptionPane.showMessageDialog(this, "Password copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Auto-clear after 30 seconds
            Timer timer = new Timer(30000, e -> {
                try {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(""), null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void openGeneratorDialog() {
        PasswordGeneratorDialog dialog = new PasswordGeneratorDialog(this);
        String generated = dialog.getGeneratedPassword();
        if (generated != null) {
            StringSelection stringSelection = new StringSelection(generated);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            JOptionPane.showMessageDialog(this, "Generated password copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateEntryCount() {
        int count = vaultService.getEntries().size();
        entryCountLabel.setText(count + " entries");
    }

    private void updateMasterPasswordInfo() {
        try {
            Map<String, String> metadata = authService.getPasswordMetadata();
            if (metadata != null) {
                long createdAt = Long.parseLong(metadata.get("createdAt"));
                long lastUsedAt = Long.parseLong(metadata.get("lastUsedAt"));

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                String createdDate = sdf.format(new Date(createdAt));
                String lastUsedDate = sdf.format(new Date(lastUsedAt));

                masterPasswordInfoLabel.setText("Created: " + createdDate + " | Last used: " + lastUsedDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lockVault() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to lock the vault?",
                "Lock Vault",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                vaultService.clear();
                LoginFrame loginFrame = new LoginFrame(authService);
                loginFrame.setVisible(true);
                this.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "PassVault v1.0\n\n" +
                "A secure, encrypted password manager\n\n" +
                "Features:\n" +
                "• AES-256 GCM Encryption\n" +
                "• PBKDF2 Key Derivation\n" +
                "• Password Strength Analysis\n" +
                "• Password Generator\n" +
                "• Secure Clipboard Clearing\n\n" +
                "© 2024 PassVault",
                "About PassVault",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
