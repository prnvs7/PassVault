package com.passvault.ui;

import com.passvault.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.net.URI;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class VaultFrame extends JFrame {
    // Services
    private AuthService authService;
    private VaultService vaultService;
    private StorageManager storageManager;

    // Main Panels / Components
    private CardLayout mainCardLayout;
    private JPanel mainContentPanel;
    
    // Header Components
    private JPanel headerPanel;
    private JLabel headerTitleLabel;
    private JPanel badgePanel;
    private RoundedButton lockButton;
    private JLabel entryCountBadge;
    private JLabel lastUsedLabel;
    
    // Sidebar Navigation
    private JPanel sidebar;
    private JLabel navTitle;
    private JLabel actionsTitle;
    private JLabel systemTitle;
    private JButton navVaultBtn;
    private JButton navGenBtn;
    private JButton navSettingsBtn;
    private JButton navAboutBtn;
    private JButton activeNavButton;

    // Vault View Components
    private RoundedPanel roundedTableContainer;
    private ModernJTable passwordTable;
    private DefaultTableModel tableModel;
    private SearchTextField searchField;
    
    // Details Panel Components
    private RoundedPanel detailsCard;
    private CardLayout detailsCardLayout;
    private JLabel detailsTitleLabel;
    private JPanel detailsStrengthBadge;
    private JLabel detailsStrengthText;
    private JTextField detailsUsernameField;
    private JPasswordField detailsPasswordField;
    private JLabel detailsUrlLabel;
    private JTextArea detailsNotesArea;
    private JLabel detailsCreatedLabel;
    private JLabel detailsModifiedLabel;
    private JButton btnCopyUser;
    private JButton btnCopyPass;
    private JButton btnTogglePass;
    private JButton btnOpenUrl;
    private JPanel detailsContent;
    private JButton btnEditDetail;
    private JButton btnDeleteDetail;
    private JLabel placeholderKeyIcon;
    private JLabel placeholderInfoText;
    
    // Generator View Components
    private RoundedPanel generatorCard;
    private JLabel generatorTitle;
    private JTextArea generatorPasswordDisplay;
    private JLabel generatorLengthLabel;
    private RoundedButton generatorRegenBtn;
    private RoundedButton generatorCopyBtn;
    
    // Settings View Components
    private RoundedPanel settingsCard;
    private JLabel settingsTitle;
    private JLabel settingsInfo;
    private JLabel settingsRecoveryInfo;
    private RoundedButton settingsConfigureRecoveryBtn;
    private JLabel settingsWarningText;
    private RoundedButton settingsResetBtn;
    private JLabel settingsThemeTitle;
    private JLabel settingsThemeInfo;
    private RoundedButton settingsThemeBtn;

    // About View Components
    private RoundedPanel aboutCard;
    private JLabel aboutLogo;
    private JLabel aboutTitle;
    private JLabel aboutVersion;
    private JLabel aboutDesc;
    private JLabel aboutCopyright;

    // Active entry reference in details panel
    private PasswordEntry selectedEntry;

    public VaultFrame(AuthService authService) throws Exception {
        this.authService = authService;
        this.storageManager = new StorageManager();
        this.vaultService = new VaultService(storageManager);
        this.vaultService.loadVault(authService.getCurrentKey());

        setTitle("PassVault - Encrypted Password Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 780);
        setMinimumSize(new Dimension(1000, 680));
        setLocationRelativeTo(null);
        
        // Set application icon images
        setIconImages(IconUtils.getLogoImages());
        
        // Frame styling
        getContentPane().setBackground(new Color(0xF8, 0xF9, 0xFB));
        setLayout(new BorderLayout());

        // Build main UI components
        add(createHeader(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);
        
        // Content Area CardLayout
        mainCardLayout = new CardLayout();
        mainContentPanel = new JPanel(mainCardLayout);
        mainContentPanel.setOpaque(false);
        
        // Register view panels
        mainContentPanel.add(createVaultView(), "VAULT");
        mainContentPanel.add(createGeneratorPanel(), "GENERATOR");
        mainContentPanel.add(createSettingsPanel(), "SETTINGS");
        mainContentPanel.add(createAboutPanel(), "ABOUT");
        
        add(mainContentPanel, BorderLayout.CENTER);

        // Apply theme styling dynamically
        applyTheme();

        // Highlight vault by default
        selectNavButton(navVaultBtn, "VAULT");

        setVisible(true);
    }

    private JPanel createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0x1F, 0x29, 0x37)); // Dark background
        headerPanel.setPreferredSize(new Dimension(1200, 75));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        // Left section: Title & metadata
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        
        headerTitleLabel = new JLabel("PassVault");
        headerTitleLabel.setIcon(IconUtils.getIcon("logo", 24, Color.WHITE));
        headerTitleLabel.setIconTextGap(10);
        headerTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerTitleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        leftPanel.add(headerTitleLabel, gbc);

        lastUsedLabel = new JLabel("Loading vault metadata...");
        lastUsedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lastUsedLabel.setForeground(new Color(0x9C, 0xA3, 0xAF));
        gbc.gridy = 1;
        gbc.insets = new Insets(2, 0, 0, 0);
        leftPanel.add(lastUsedLabel, gbc);
        updateMasterPasswordInfo();

        headerPanel.add(leftPanel, BorderLayout.WEST);

        // Right section: Counter badge and Lock button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
        rightPanel.setOpaque(false);

        // Entry counter badge
        entryCountBadge = new JLabel("0 stored passwords");
        entryCountBadge.setIcon(IconUtils.getIcon("archive", 18, Color.WHITE));
        entryCountBadge.setIconTextGap(8);
        entryCountBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        entryCountBadge.setForeground(Color.WHITE);
        entryCountBadge.setOpaque(true);
        entryCountBadge.setBackground(new Color(0x37, 0x41, 0x51));
        entryCountBadge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        
        badgePanel = new RoundedPanel(12, null) {
            {
                setBackground(new Color(0x37, 0x41, 0x51));
                setLayout(new BorderLayout());
                add(entryCountBadge, BorderLayout.CENTER);
            }
        };
        rightPanel.add(badgePanel);

        lockButton = new RoundedButton("Lock Vault", 12);
        lockButton.setIcon(IconUtils.getIcon("lock", 18, Color.WHITE));
        lockButton.setIconTextGap(8);
        lockButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lockButton.setPreferredSize(new Dimension(145, 36));
        lockButton.setBackground(new Color(0xDC, 0x26, 0x26)); // Red Accent
        lockButton.setForeground(Color.WHITE);
        lockButton.addActionListener(e -> lockVault());
        rightPanel.add(lockButton);

        headerPanel.add(rightPanel, BorderLayout.EAST);
        updateEntryCount();

        return headerPanel;
    }

    private JPanel createSidebar() {
        sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(220, 700));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0xE5, 0xE7, 0xEB)),
            BorderFactory.createEmptyBorder(0, 16, 0, 16)
        ));
        sidebar.setOpaque(true);

        sidebar.add(Box.createVerticalStrut(16));

        // Group 1: Navigation
        navTitle = new JLabel("NAVIGATION");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navTitle.setForeground(new Color(0x6B, 0x72, 0x80));
        navTitle.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navTitle);

        navVaultBtn = createSidebarNavButton("All Passwords", "VAULT", "vault");
        sidebar.add(navVaultBtn);
        sidebar.add(Box.createVerticalStrut(6));

        navGenBtn = createSidebarNavButton("Generator", "GENERATOR", "wand-sparkles");
        sidebar.add(navGenBtn);
        sidebar.add(Box.createVerticalStrut(20));

        // Group 2: Actions
        actionsTitle = new JLabel("VAULT OPERATIONS");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        actionsTitle.setForeground(new Color(0x6B, 0x72, 0x80));
        actionsTitle.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        actionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(actionsTitle);

        Color grayColor = new Color(0x6B, 0x72, 0x80);
        JButton addBtn = createSidebarActionButton("Add Password", "plus", grayColor, e -> openAddEntryDialog());
        sidebar.add(addBtn);
        sidebar.add(Box.createVerticalStrut(6));

        JButton editBtn = createSidebarActionButton("Edit Entry", "pencil", grayColor, e -> editSelectedEntry());
        sidebar.add(editBtn);
        sidebar.add(Box.createVerticalStrut(6));

        JButton deleteBtn = createSidebarActionButton("Delete Entry", "trash-2", new Color(0xDC, 0x26, 0x26), e -> deleteSelectedEntry());
        deleteBtn.setForeground(new Color(0xDC, 0x26, 0x26));
        sidebar.add(deleteBtn);
        sidebar.add(Box.createVerticalStrut(6));

        JButton copyBtn = createSidebarActionButton("Copy Password", "copy", grayColor, e -> copyPasswordToClipboard());
        sidebar.add(copyBtn);
        sidebar.add(Box.createVerticalStrut(20));

        // Group 3: System
        systemTitle = new JLabel("SYSTEM");
        systemTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        systemTitle.setForeground(new Color(0x6B, 0x72, 0x80));
        systemTitle.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        systemTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(systemTitle);

        navSettingsBtn = createSidebarNavButton("Settings", "SETTINGS", "settings");
        sidebar.add(navSettingsBtn);
        sidebar.add(Box.createVerticalStrut(6));

        navAboutBtn = createSidebarNavButton("About PassVault", "ABOUT", "info");
        sidebar.add(navAboutBtn);

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JButton createSidebarNavButton(String text, String cardName, String iconName) {
        RoundedButton button = new RoundedButton(text, 10);
        button.putClientProperty("iconName", iconName);
        button.setIcon(IconUtils.getIcon(iconName, 20, new Color(0x11, 0x18, 0x27)));
        button.setIconTextGap(10);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(188, 38));
        button.setMaximumSize(new Dimension(188, 38));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(0x11, 0x18, 0x27));
        button.setHoverColor(new Color(0xF3, 0xF4, 0xF6));
        button.addActionListener(e -> selectNavButton(button, cardName));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    private JButton createSidebarActionButton(String text, String iconName, Color iconColor, ActionListener action) {
        RoundedButton button = new RoundedButton(text, 10);
        button.setIcon(IconUtils.getIcon(iconName, 18, iconColor));
        button.setIconTextGap(10);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(188, 38));
        button.setMaximumSize(new Dimension(188, 38));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(0x11, 0x18, 0x27));
        button.setHoverColor(new Color(0xF3, 0xF4, 0xF6));
        button.addActionListener(action);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    private void selectNavButton(JButton button, String cardName) {
        activeNavButton = button;
        updateSidebarButtons();
        mainCardLayout.show(mainContentPanel, cardName);
    }

    private JPanel createVaultView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // 1. Search Bar panel (North)
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        searchField = new SearchTextField("Search credentials by name, username, or URL...");
        searchField.setPreferredSize(new Dimension(800, 42));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);
        panel.add(searchPanel, BorderLayout.NORTH);

        // 2. Center Content Split Pane: Left JTable, Right Details Panel
        JPanel splitContainer = new JPanel(new BorderLayout(20, 0));
        splitContainer.setOpaque(false);

        createPasswordTable();
        JScrollPane tableScrollPane = new JScrollPane(passwordTable);
        tableScrollPane.setOpaque(false);
        tableScrollPane.getViewport().setOpaque(false);
        tableScrollPane.setBorder(null);

        roundedTableContainer = new RoundedPanel(12, ThemeManager.getBorderColor());
        roundedTableContainer.setBackground(ThemeManager.getCardColor());
        roundedTableContainer.setLayout(new BorderLayout());
        roundedTableContainer.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        roundedTableContainer.add(tableScrollPane, BorderLayout.CENTER);
        splitContainer.add(roundedTableContainer, BorderLayout.CENTER);

        createDetailsPanel();
        splitContainer.add(detailsCard, BorderLayout.EAST);

        panel.add(splitContainer, BorderLayout.CENTER);

        refreshTable();

        return panel;
    }

    private void createPasswordTable() {
        tableModel = new DefaultTableModel(new String[]{"Name", "Username", "Password", "URL", "Strength", "Last Modified"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        passwordTable = new ModernJTable(tableModel);
        
        TableColumnModel colModel = passwordTable.getColumnModel();
        
        TableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                
                if (isSelected) {
                    c.setBackground(ThemeManager.getTableSelection());
                    c.setForeground(ThemeManager.isDark() ? Color.WHITE : new Color(0x11, 0x18, 0x27));
                } else if (row == ((ModernJTable) table).getHoveredRow()) {
                    c.setBackground(ThemeManager.getTableHover());
                    c.setForeground(ThemeManager.getTextPrimary());
                } else {
                    c.setBackground(row % 2 == 0 ? ThemeManager.getCardColor() : ThemeManager.getTableAlternateRow());
                    c.setForeground(ThemeManager.getTextPrimary());
                }
                return c;
            }
        };

        for (int i = 0; i < passwordTable.getColumnCount(); i++) {
            passwordTable.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }

        colModel.getColumn(2).setCellRenderer(new PasswordCellRenderer());
        colModel.getColumn(3).setCellRenderer(new UrlCellRenderer());
        colModel.getColumn(4).setCellRenderer(new StrengthCellRenderer());

        colModel.getColumn(0).setPreferredWidth(140);
        colModel.getColumn(1).setPreferredWidth(140);
        colModel.getColumn(2).setPreferredWidth(160);
        colModel.getColumn(3).setPreferredWidth(160);
        colModel.getColumn(4).setPreferredWidth(110);
        colModel.getColumn(5).setPreferredWidth(130);

        JTableHeader header = passwordTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(0xF8, 0xF9, 0xFB));
        header.setForeground(new Color(0x6B, 0x72, 0x80));
        header.setPreferredSize(new Dimension(800, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE5, 0xE7, 0xEB)));
        
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        passwordTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = passwordTable.rowAtPoint(e.getPoint());
                int col = passwordTable.columnAtPoint(e.getPoint());
                if (row < 0 || col < 0) return;

                if (e.getClickCount() == 2 && col != 2 && col != 3) {
                    editSelectedEntry();
                } else if (col == 2) {
                    Rectangle rect = passwordTable.getCellRect(row, col, true);
                    int clickX = e.getX() - rect.x;
                    
                    PasswordEntry entry = getEntryAtRow(row);
                    if (entry != null) {
                        if (clickX >= rect.width - 28 && clickX <= rect.width - 4) {
                            copyTextToClipboard(entry.getPassword(), "Password copied to clipboard!");
                        } else if (clickX >= rect.width - 54 && clickX < rect.width - 28) {
                            passwordTable.togglePasswordVisibility(row);
                        }
                    }
                } else if (col == 3) {
                    String url = passwordTable.getValueAt(row, col).toString();
                    openBrowser(url);
                }
            }
        });

        passwordTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = passwordTable.getSelectedRow();
                updateDetailsPanel(selectedRow);
            }
        });
    }

    private void createDetailsPanel() {
        detailsCard = new RoundedPanel(12, new Color(0xE5, 0xE7, 0xEB));
        detailsCard.setBackground(Color.WHITE);
        detailsCard.setPreferredSize(new Dimension(340, 500));
        
        detailsCardLayout = new CardLayout();
        detailsCard.setLayout(detailsCardLayout);

        // Card 1: Placeholder Panel
        JPanel placeholderPanel = new JPanel(new GridBagLayout());
        placeholderPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        placeholderKeyIcon = new JLabel();
        placeholderKeyIcon.setIcon(IconUtils.getIcon("vault", 48, new Color(0x9C, 0xA3, 0xAF)));
        placeholderPanel.add(placeholderKeyIcon, gbc);

        placeholderInfoText = new JLabel("Select an entry to view details");
        placeholderInfoText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        placeholderInfoText.setForeground(new Color(0x6B, 0x72, 0x80));
        gbc.gridy = 1;
        gbc.insets = new Insets(12, 0, 0, 0);
        placeholderPanel.add(placeholderInfoText, gbc);

        detailsCard.add(placeholderPanel, "PLACEHOLDER");

        // Card 2: Details Content Panel
        detailsContent = new JPanel();
        detailsContent.setOpaque(false);
        detailsContent.setLayout(new BorderLayout());
        detailsContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel scrollContent = new JPanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new GridBagLayout());
        GridBagConstraints dGbc = new GridBagConstraints();
        dGbc.fill = GridBagConstraints.HORIZONTAL;
        dGbc.weightx = 1.0;
        dGbc.anchor = GridBagConstraints.NORTHWEST;
        dGbc.insets = new Insets(4, 0, 12, 0);

        // Header Panel: Title and strength badge
        JPanel headerPanel = new JPanel(new BorderLayout(8, 0));
        headerPanel.setOpaque(false);

        detailsTitleLabel = new JLabel("Entry Title");
        detailsTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        detailsTitleLabel.setForeground(new Color(0x11, 0x18, 0x27));
        headerPanel.add(detailsTitleLabel, BorderLayout.CENTER);

        detailsStrengthText = new JLabel("STRONG");
        detailsStrengthText.setFont(new Font("Segoe UI", Font.BOLD, 9));
        detailsStrengthText.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        detailsStrengthBadge = new RoundedPanel(6, null) {
            {
                setLayout(new BorderLayout());
                add(detailsStrengthText, BorderLayout.CENTER);
            }
        };
        headerPanel.add(detailsStrengthBadge, BorderLayout.EAST);

        dGbc.gridy = 0;
        scrollContent.add(headerPanel, dGbc);

        // Username
        scrollContent.add(createFieldLabel("USERNAME", "user"), getLabelConstraints(1));
        
        JPanel userFieldPanel = new JPanel(new BorderLayout(8, 0));
        userFieldPanel.setOpaque(false);
        detailsUsernameField = new JTextField();
        detailsUsernameField.setEditable(false);
        detailsUsernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsUsernameField.setBorder(null);
        detailsUsernameField.setBackground(Color.WHITE);
        detailsUsernameField.setForeground(new Color(0x11, 0x18, 0x27));
        userFieldPanel.add(detailsUsernameField, BorderLayout.CENTER);

        btnCopyUser = createMiniIconButton("copy", "Copy Username", e -> copyTextToClipboard(detailsUsernameField.getText(), "Username copied!"));
        userFieldPanel.add(btnCopyUser, BorderLayout.EAST);

        dGbc.gridy = 2;
        scrollContent.add(userFieldPanel, dGbc);
        scrollContent.add(createFieldDivider(), getDividerConstraints(3));

        // Password
        scrollContent.add(createFieldLabel("PASSWORD", "key-round"), getLabelConstraints(4));

        JPanel passFieldPanel = new JPanel(new BorderLayout(8, 0));
        passFieldPanel.setOpaque(false);
        detailsPasswordField = new JPasswordField();
        detailsPasswordField.setEditable(false);
        detailsPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsPasswordField.setBorder(null);
        detailsPasswordField.setBackground(Color.WHITE);
        detailsPasswordField.setForeground(new Color(0x11, 0x18, 0x27));
        passFieldPanel.add(detailsPasswordField, BorderLayout.CENTER);

        JPanel passActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        passActions.setOpaque(false);
        
        char defaultEcho = detailsPasswordField.getEchoChar();
        btnTogglePass = createMiniIconButton("eye", "Show Password", e -> {
            if (detailsPasswordField.getEchoChar() == (char) 0) {
                detailsPasswordField.setEchoChar(defaultEcho);
                btnTogglePass.setIcon(IconUtils.getIcon("eye", 16, new Color(0x37, 0x41, 0x51)));
                btnTogglePass.setToolTipText("Show Password");
            } else {
                detailsPasswordField.setEchoChar((char) 0);
                btnTogglePass.setIcon(IconUtils.getIcon("eye-off", 16, new Color(0x37, 0x41, 0x51)));
                btnTogglePass.setToolTipText("Hide Password");
            }
        });
        passActions.add(btnTogglePass);

        btnCopyPass = createMiniIconButton("copy", "Copy Password", e -> {
            if (selectedEntry != null) {
                copyPasswordToClipboard();
            }
        });
        passActions.add(btnCopyPass);
        
        passFieldPanel.add(passActions, BorderLayout.EAST);

        dGbc.gridy = 5;
        scrollContent.add(passFieldPanel, dGbc);
        scrollContent.add(createFieldDivider(), getDividerConstraints(6));

        // URL
        scrollContent.add(createFieldLabel("WEBSITE URL", "globe"), getLabelConstraints(7));

        JPanel urlFieldPanel = new JPanel(new BorderLayout(8, 0));
        urlFieldPanel.setOpaque(false);
        
        detailsUrlLabel = new JLabel("http://www.google.com");
        detailsUrlLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsUrlLabel.setForeground(new Color(0x25, 0x63, 0xEB));
        detailsUrlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        detailsUrlLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openBrowser(detailsUrlLabel.getText());
            }
        });
        urlFieldPanel.add(detailsUrlLabel, BorderLayout.CENTER);

        btnOpenUrl = createMiniIconButton("external-link", "Open Website", e -> openBrowser(detailsUrlLabel.getText()));
        urlFieldPanel.add(btnOpenUrl, BorderLayout.EAST);

        dGbc.gridy = 8;
        scrollContent.add(urlFieldPanel, dGbc);
        scrollContent.add(createFieldDivider(), getDividerConstraints(9));

        // Notes
        scrollContent.add(createFieldLabel("NOTES", "notebook-pen"), getLabelConstraints(10));

        detailsNotesArea = new JTextArea();
        detailsNotesArea.setEditable(false);
        detailsNotesArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsNotesArea.setForeground(new Color(0x37, 0x41, 0x51));
        detailsNotesArea.setLineWrap(true);
        detailsNotesArea.setWrapStyleWord(true);
        detailsNotesArea.setBackground(new Color(0xF9, 0xFA, 0xFB));
        detailsNotesArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        JScrollPane notesScroll = new JScrollPane(detailsNotesArea);
        notesScroll.setPreferredSize(new Dimension(280, 80));
        notesScroll.setBorder(BorderFactory.createLineBorder(new Color(0xE5, 0xE7, 0xEB)));
        
        dGbc.gridy = 11;
        scrollContent.add(notesScroll, dGbc);
        scrollContent.add(createFieldDivider(), getDividerConstraints(12));

        // Metadata dates
        JPanel metaPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        metaPanel.setOpaque(false);
        
        detailsCreatedLabel = new JLabel("Created: -");
        detailsCreatedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detailsCreatedLabel.setForeground(new Color(0x6B, 0x72, 0x80));
        detailsCreatedLabel.setIcon(IconUtils.getIcon("calendar", 14, new Color(0x6B, 0x72, 0x80)));
        detailsCreatedLabel.setIconTextGap(6);
        
        detailsModifiedLabel = new JLabel("Modified: -");
        detailsModifiedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detailsModifiedLabel.setForeground(new Color(0x6B, 0x72, 0x80));
        detailsModifiedLabel.setIcon(IconUtils.getIcon("clock", 14, new Color(0x6B, 0x72, 0x80)));
        detailsModifiedLabel.setIconTextGap(6);
        
        metaPanel.add(detailsCreatedLabel);
        metaPanel.add(detailsModifiedLabel);

        dGbc.gridy = 13;
        scrollContent.add(metaPanel, dGbc);

        JScrollPane containerScroll = new JScrollPane(scrollContent);
        containerScroll.setBorder(null);
        containerScroll.setOpaque(false);
        containerScroll.getViewport().setOpaque(false);
        detailsContent.add(containerScroll, BorderLayout.CENTER);

        // Edit / Delete Buttons
        JPanel actionsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        btnEditDetail = new RoundedButton("Edit Entry", 10);
        btnEditDetail.setIcon(IconUtils.getIcon("pencil", 16, Color.WHITE));
        btnEditDetail.setIconTextGap(8);
        btnEditDetail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEditDetail.setPreferredSize(new Dimension(140, 36));
        btnEditDetail.setBackground(new Color(0x25, 0x63, 0xEB));
        btnEditDetail.setForeground(Color.WHITE);
        btnEditDetail.addActionListener(e -> editSelectedEntry());
        actionsPanel.add(btnEditDetail);

        btnDeleteDetail = new RoundedButton("Delete", 10);
        btnDeleteDetail.setIcon(IconUtils.getIcon("trash-2", 16, Color.WHITE));
        btnDeleteDetail.setIconTextGap(8);
        btnDeleteDetail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDeleteDetail.setPreferredSize(new Dimension(140, 36));
        btnDeleteDetail.setBackground(new Color(0xDC, 0x26, 0x26));
        btnDeleteDetail.setForeground(Color.WHITE);
        btnDeleteDetail.addActionListener(e -> deleteSelectedEntry());
        actionsPanel.add(btnDeleteDetail);

        detailsContent.add(actionsPanel, BorderLayout.SOUTH);
        detailsCard.add(detailsContent, "CONTENT");

        detailsCardLayout.show(detailsCard, "PLACEHOLDER");
    }

    private GridBagConstraints getLabelConstraints(int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = y;
        gbc.insets = new Insets(8, 0, 2, 0);
        return gbc;
    }

    private GridBagConstraints getDividerConstraints(int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = y;
        gbc.insets = new Insets(0, 0, 8, 0);
        return gbc;
    }

    private JLabel createFieldLabel(String text, String iconName) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(0x6B, 0x72, 0x80));
        if (iconName != null) {
            label.setIcon(IconUtils.getIcon(iconName, 14, new Color(0x6B, 0x72, 0x80)));
            label.setIconTextGap(6);
        }
        return label;
    }

    private JComponent createFieldDivider() {
        JPanel div = new JPanel();
        div.setPreferredSize(new Dimension(280, 1));
        div.setBackground(new Color(0xE5, 0xE7, 0xEB));
        return div;
    }

    private JButton createMiniIconButton(String iconName, String tooltip, ActionListener action) {
        RoundedButton btn = new RoundedButton("", 6);
        btn.setIcon(IconUtils.getIcon(iconName, 16, new Color(0x37, 0x41, 0x51)));
        btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(28, 24));
        btn.setBackground(new Color(0xF3, 0xF4, 0xF6));
        btn.setHoverColor(new Color(0xE5, 0xE7, 0xEB));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.addActionListener(action);
        return btn;
    }

    private void updateDetailsPanel(int selectedRow) {
        if (selectedRow == -1) {
            selectedEntry = null;
            detailsCardLayout.show(detailsCard, "PLACEHOLDER");
            return;
        }

        PasswordEntry entry = getEntryAtRow(selectedRow);
        if (entry == null) {
            selectedEntry = null;
            detailsCardLayout.show(detailsCard, "PLACEHOLDER");
            return;
        }

        selectedEntry = entry;
        detailsTitleLabel.setText(entry.getName());
        detailsUsernameField.setText(entry.getUsername());
        detailsPasswordField.setText(entry.getPassword());
        detailsPasswordField.setEchoChar('•');
        btnTogglePass.setIcon(IconUtils.getIcon("eye", 16, new Color(0x37, 0x41, 0x51)));
        btnTogglePass.setToolTipText("Show Password");
        
        detailsUrlLabel.setText(entry.getUrl().isEmpty() ? "No URL" : entry.getUrl());
        detailsUrlLabel.setEnabled(!entry.getUrl().isEmpty());
        btnOpenUrl.setEnabled(!entry.getUrl().isEmpty());

        detailsNotesArea.setText(entry.getNotes());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        detailsCreatedLabel.setText("Created: " + sdf.format(new Date(entry.getCreatedAt())));
        detailsModifiedLabel.setText("Modified: " + sdf.format(new Date(entry.getLastModifiedAt())));

        String str = entry.getPasswordStrength();
        detailsStrengthText.setText(str.toUpperCase());
        switch (str) {
            case "Weak":
                detailsStrengthText.setForeground(new Color(0x99, 0x1B, 0x1B));
                detailsStrengthBadge.setBackground(new Color(0xFE, 0xE2, 0xE2));
                break;
            case "Medium":
                detailsStrengthText.setForeground(new Color(0x92, 0x40, 0x0E));
                detailsStrengthBadge.setBackground(new Color(0xFE, 0xF3, 0xCB));
                break;
            case "Strong":
                detailsStrengthText.setForeground(new Color(0x06, 0x5F, 0x46));
                detailsStrengthBadge.setBackground(new Color(0xD1, 0xFA, 0xE5));
                break;
            case "Very Strong":
                detailsStrengthText.setForeground(new Color(0x06, 0x4E, 0x3B));
                detailsStrengthBadge.setBackground(new Color(0xA7, 0xF3, 0xD0));
                break;
            default:
                detailsStrengthText.setForeground(new Color(0x37, 0x41, 0x51));
                detailsStrengthBadge.setBackground(new Color(0xF3, 0xF4, 0xF6));
                break;
        }

        detailsCardLayout.show(detailsCard, "CONTENT");
    }

    private PasswordEntry getEntryAtRow(int tableRow) {
        if (tableRow == -1) return null;
        String nameVal = (String) tableModel.getValueAt(tableRow, 0);
        String userVal = (String) tableModel.getValueAt(tableRow, 1);
        
        return vaultService.getEntries().stream()
                .filter(e -> e.getName().equals(nameVal) && e.getUsername().equals(userVal))
                .findFirst()
                .orElse(null);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<PasswordEntry> entries = vaultService.getEntries();
        for (PasswordEntry entry : entries) {
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(entry.getLastModifiedAt()));
            tableModel.addRow(new Object[]{
                    entry.getName(),
                    entry.getUsername(),
                    entry.getPassword(),
                    entry.getUrl(),
                    entry.getPasswordStrength(),
                    formattedDate
            });
        }
        updateDetailsPanel(passwordTable.getSelectedRow());
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        tableModel.setRowCount(0);

        List<PasswordEntry> results = query.isEmpty() ?
                vaultService.getEntries() :
                vaultService.searchEntries(query);

        for (PasswordEntry entry : results) {
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(entry.getLastModifiedAt()));
            tableModel.addRow(new Object[]{
                    entry.getName(),
                    entry.getUsername(),
                    entry.getPassword(),
                    entry.getUrl(),
                    entry.getPasswordStrength(),
                    formattedDate
            });
        }
        updateDetailsPanel(passwordTable.getSelectedRow());
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

        PasswordEntry entry = getEntryAtRow(selectedRow);
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

        PasswordEntry entry = getEntryAtRow(selectedRow);
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

        PasswordEntry entry = getEntryAtRow(selectedRow);
        if (entry != null) {
            copyTextToClipboard(entry.getPassword(), "Password copied to clipboard!");
            
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

    private void copyTextToClipboard(String text, String successMsg) {
        StringSelection stringSelection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        JOptionPane.showMessageDialog(this, successMsg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateEntryCount() {
        int count = vaultService.getEntries().size();
        if (entryCountBadge != null) {
            entryCountBadge.setText(count + " stored passwords");
        }
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

                lastUsedLabel.setText("Created: " + createdDate + "  |  Last opened: " + lastUsedDate);
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

    private void openBrowser(String url) {
        if (url == null || url.trim().isEmpty() || url.equals("No URL")) return;
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            java.awt.Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ==========================================
    // Card Views (Generator, Settings, About)
    // ==========================================
    
    private JPanel createGeneratorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        generatorCard = new RoundedPanel(12, new Color(0xE5, 0xE7, 0xEB));
        generatorCard.setBackground(Color.WHITE);
        generatorCard.setPreferredSize(new Dimension(650, 480));
        generatorCard.setLayout(new GridBagLayout());
        generatorCard.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;

        // Title
        generatorTitle = new JLabel("Generate Strong Password");
        generatorTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        generatorTitle.setForeground(new Color(0x11, 0x18, 0x27));
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        generatorCard.add(generatorTitle, gbc);

        // Display area
        generatorPasswordDisplay = new JTextArea(2, 40);
        generatorPasswordDisplay.setFont(new Font("Consolas", Font.BOLD, 18));
        generatorPasswordDisplay.setEditable(false);
        generatorPasswordDisplay.setLineWrap(true);
        generatorPasswordDisplay.setWrapStyleWord(true);
        generatorPasswordDisplay.setBackground(new Color(0xF3, 0xF4, 0xF6));
        generatorPasswordDisplay.setForeground(new Color(0x11, 0x18, 0x27));
        generatorPasswordDisplay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE5, 0xE7, 0xEB)),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        gbc.gridy = 1;
        generatorCard.add(generatorPasswordDisplay, gbc);

        // Length panel
        JPanel lengthPanel = new JPanel(new BorderLayout(16, 0));
        lengthPanel.setOpaque(false);
        
        generatorLengthLabel = new JLabel("Length: 16");
        generatorLengthLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        generatorLengthLabel.setForeground(new Color(0x37, 0x41, 0x51));
        lengthPanel.add(generatorLengthLabel, BorderLayout.WEST);

        JSlider lengthSlider = new JSlider(8, 32, 16);
        lengthSlider.setOpaque(false);
        lengthSlider.setMajorTickSpacing(4);
        lengthSlider.setPaintTicks(true);
        lengthSlider.setPaintLabels(true);
        lengthSlider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lengthSlider.setForeground(new Color(0x6B, 0x72, 0x80));
        lengthPanel.add(lengthSlider, BorderLayout.CENTER);

        gbc.gridy = 2;
        generatorCard.add(lengthPanel, gbc);

        // Checkboxes Panel
        JPanel checkboxes = new JPanel(new GridLayout(2, 2, 16, 12));
        checkboxes.setOpaque(false);
        
        JCheckBox lowercaseCheckbox = createModernCheckbox("Lowercase (a-z)", true);
        JCheckBox uppercaseCheckbox = createModernCheckbox("Uppercase (A-Z)", true);
        JCheckBox digitsCheckbox = createModernCheckbox("Digits (0-9)", true);
        JCheckBox symbolsCheckbox = createModernCheckbox("Symbols (!@#$)", true);

        checkboxes.add(lowercaseCheckbox);
        checkboxes.add(uppercaseCheckbox);
        checkboxes.add(digitsCheckbox);
        checkboxes.add(symbolsCheckbox);

        gbc.gridy = 3;
        generatorCard.add(checkboxes, gbc);

        Runnable regenerate = () -> {
            int len = lengthSlider.getValue();
            generatorLengthLabel.setText("Length: " + len);
            boolean useLower = lowercaseCheckbox.isSelected();
            boolean useUpper = uppercaseCheckbox.isSelected();
            boolean useDigits = digitsCheckbox.isSelected();
            boolean useSymbols = symbolsCheckbox.isSelected();
            if (!useLower && !useUpper && !useDigits && !useSymbols) {
                lowercaseCheckbox.setSelected(true);
                useLower = true;
            }
            String pwd = PasswordGenerator.generatePassword(len, useLower, useUpper, useDigits, useSymbols);
            generatorPasswordDisplay.setText(pwd);
        };

        lengthSlider.addChangeListener(e -> {
            int len = lengthSlider.getValue();
            generatorLengthLabel.setText("Length: " + len);
            regenerate.run();
        });
        lowercaseCheckbox.addActionListener(e -> regenerate.run());
        uppercaseCheckbox.addActionListener(e -> regenerate.run());
        digitsCheckbox.addActionListener(e -> regenerate.run());
        symbolsCheckbox.addActionListener(e -> regenerate.run());

        // Buttons
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        actionButtons.setOpaque(false);

        generatorRegenBtn = new RoundedButton("Regenerate", 10);
        generatorRegenBtn.setIcon(IconUtils.getIcon("wand-sparkles", 16, new Color(0x37, 0x41, 0x51)));
        generatorRegenBtn.setIconTextGap(8);
        generatorRegenBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        generatorRegenBtn.setPreferredSize(new Dimension(150, 36));
        generatorRegenBtn.setBackground(new Color(0xF3, 0xF4, 0xF6));
        generatorRegenBtn.setForeground(new Color(0x37, 0x41, 0x51));
        generatorRegenBtn.addActionListener(e -> regenerate.run());
        actionButtons.add(generatorRegenBtn);

        generatorCopyBtn = new RoundedButton("Copy Password", 10);
        generatorCopyBtn.setIcon(IconUtils.getIcon("copy", 16, Color.WHITE));
        generatorCopyBtn.setIconTextGap(8);
        generatorCopyBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        generatorCopyBtn.setPreferredSize(new Dimension(150, 36));
        generatorCopyBtn.setBackground(new Color(0x25, 0x63, 0xEB));
        generatorCopyBtn.setForeground(Color.WHITE);
        generatorCopyBtn.addActionListener(e -> copyTextToClipboard(generatorPasswordDisplay.getText(), "Generated password copied!"));
        actionButtons.add(generatorCopyBtn);

        gbc.gridy = 4;
        gbc.insets = new Insets(16, 0, 0, 0);
        generatorCard.add(actionButtons, gbc);

        regenerate.run();

        panel.add(generatorCard, new GridBagConstraints());
        return panel;
    }

    private JCheckBox createModernCheckbox(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setForeground(new Color(0x37, 0x41, 0x51));
        cb.setOpaque(false);
        cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return cb;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        settingsCard = new RoundedPanel(12, new Color(0xE5, 0xE7, 0xEB));
        settingsCard.setBackground(Color.WHITE);
        settingsCard.setPreferredSize(new Dimension(650, 480));
        settingsCard.setLayout(new GridBagLayout());
        settingsCard.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        settingsTitle = new JLabel("Settings & Preferences");
        settingsTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        settingsTitle.setForeground(new Color(0x11, 0x18, 0x27));
        gbc.gridy = 0;
        settingsCard.add(settingsTitle, gbc);

        settingsCard.add(createFieldDivider(), getDividerConstraints(1));

        settingsInfo = new JLabel("<html><body><b>Local Database Settings</b><br>Data is stored locally on your machine in encrypted JSON files using AES-256-GCM.</body></html>");
        settingsInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        settingsInfo.setForeground(new Color(0x37, 0x41, 0x51));
        gbc.gridy = 2;
        settingsCard.add(settingsInfo, gbc);

        // Security controls
        JPanel securityControls = new JPanel(new GridBagLayout());
        securityControls.setOpaque(false);
        GridBagConstraints scGbc = new GridBagConstraints();
        scGbc.fill = GridBagConstraints.HORIZONTAL;
        scGbc.anchor = GridBagConstraints.WEST;
        scGbc.gridx = 0;
        scGbc.weightx = 1.0;
        scGbc.insets = new Insets(8, 0, 8, 0);

        // Password Recovery Setup
        settingsRecoveryInfo = new JLabel("<html><body><b>Password Recovery</b><br>Configure a security question to recover your vault if you forget your master password.</body></html>");
        settingsRecoveryInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        settingsRecoveryInfo.setForeground(new Color(0x37, 0x41, 0x51));
        scGbc.gridy = 0;
        securityControls.add(settingsRecoveryInfo, scGbc);

        settingsConfigureRecoveryBtn = new RoundedButton("Configure Recovery Question", 10);
        settingsConfigureRecoveryBtn.setIcon(IconUtils.getIcon("key-round", 16, Color.WHITE));
        settingsConfigureRecoveryBtn.setIconTextGap(8);
        settingsConfigureRecoveryBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        settingsConfigureRecoveryBtn.setPreferredSize(new Dimension(250, 36));
        settingsConfigureRecoveryBtn.setBackground(new Color(0x25, 0x63, 0xEB));
        settingsConfigureRecoveryBtn.setForeground(Color.WHITE);
        settingsConfigureRecoveryBtn.addActionListener(e -> showUpdateRecoveryDialog());
        
        scGbc.gridy = 1;
        scGbc.insets = new Insets(4, 0, 16, 0);
        securityControls.add(settingsConfigureRecoveryBtn, scGbc);

        // Reset Vault Button
        settingsWarningText = new JLabel("<html><body><font color='#DC2626'><b>CRITICAL WARNING:</b></font> Resetting your vault will permanently delete all stored credentials. This action is irreversible.</body></html>");
        settingsWarningText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        scGbc.gridy = 2;
        scGbc.insets = new Insets(12, 0, 4, 0);
        securityControls.add(settingsWarningText, scGbc);

        settingsResetBtn = new RoundedButton("Reset Vault Database", 10);
        settingsResetBtn.setIcon(IconUtils.getIcon("trash-2", 16, Color.WHITE));
        settingsResetBtn.setIconTextGap(8);
        settingsResetBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        settingsResetBtn.setPreferredSize(new Dimension(250, 36));
        settingsResetBtn.setBackground(new Color(0xDC, 0x26, 0x26));
        settingsResetBtn.setForeground(Color.WHITE);
        settingsResetBtn.addActionListener(e -> resetVaultFlow());
        
        scGbc.gridy = 3;
        scGbc.insets = new Insets(4, 0, 12, 0);
        securityControls.add(settingsResetBtn, scGbc);

        // Theme preference controls
        settingsThemeTitle = new JLabel("<html><body><b>App Theme</b></body></html>");
        settingsThemeTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        settingsThemeTitle.setForeground(new Color(0x37, 0x41, 0x51));
        scGbc.gridy = 4;
        scGbc.insets = new Insets(12, 0, 4, 0);
        securityControls.add(settingsThemeTitle, scGbc);

        settingsThemeInfo = new JLabel("Toggle between dynamic light and modern dark themes.");
        settingsThemeInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        settingsThemeInfo.setForeground(new Color(0x6B, 0x72, 0x80));
        scGbc.gridy = 5;
        scGbc.insets = new Insets(0, 0, 8, 0);
        securityControls.add(settingsThemeInfo, scGbc);

        settingsThemeBtn = new RoundedButton("Switch to Dark Theme", 10);
        settingsThemeBtn.setIcon(IconUtils.getIcon("moon", 16, new Color(0x37, 0x41, 0x51)));
        settingsThemeBtn.setIconTextGap(8);
        settingsThemeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        settingsThemeBtn.setPreferredSize(new Dimension(250, 36));
        settingsThemeBtn.addActionListener(e -> {
            ThemeManager.toggleTheme();
            applyTheme();
        });
        scGbc.gridy = 6;
        scGbc.insets = new Insets(4, 0, 12, 0);
        securityControls.add(settingsThemeBtn, scGbc);

        gbc.gridy = 3;
        settingsCard.add(securityControls, gbc);

        panel.add(settingsCard, new GridBagConstraints());
        return panel;
    }

    private void showUpdateRecoveryDialog() {
        JPanel recoveryPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;

        JPasswordField currentPassField = new JPasswordField(20);
        String[] questions = {
            "What was the name of your first pet?",
            "What is your mother's maiden name?",
            "What was the name of your elementary school?",
            "In what city were you born?",
            "What was the make and model of your first car?"
        };
        JComboBox<String> questionCb = new JComboBox<>(questions);
        JTextField answerField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0; recoveryPanel.add(new JLabel("Current Master Password:"), gbc);
        gbc.gridx = 1; recoveryPanel.add(currentPassField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; recoveryPanel.add(new JLabel("Security Question:"), gbc);
        gbc.gridx = 1; recoveryPanel.add(questionCb, gbc);

        gbc.gridx = 0; gbc.gridy = 2; recoveryPanel.add(new JLabel("Security Answer:"), gbc);
        gbc.gridx = 1; recoveryPanel.add(answerField, gbc);

        int result = JOptionPane.showConfirmDialog(this, recoveryPanel, "Configure Password Recovery",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String currentPassword = new String(currentPassField.getPassword());
            String question = (String) questionCb.getSelectedItem();
            String answer = answerField.getText().trim();

            if (currentPassword.isEmpty() || answer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password and answer fields cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = authService.updateSecurityQuestion(question, answer, currentPassword);
            if (success) {
                JOptionPane.showMessageDialog(this, "Password recovery configured successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect Master Password. Update failed.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetVaultFlow() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Warning: Resetting your master password will delete all your currently saved credentials as they cannot be decrypted without the old password.\n\nDo you wish to proceed?",
                "Reset Vault",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String dataDir = System.getProperty("user.home") + "/.passvault";
                java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(dataDir + "/config.json"));
                java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(dataDir + "/vault.json"));

                StorageManager sm = new StorageManager();
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

    private JPanel createAboutPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        aboutCard = new RoundedPanel(12, new Color(0xE5, 0xE7, 0xEB));
        aboutCard.setBackground(Color.WHITE);
        aboutCard.setPreferredSize(new Dimension(650, 480));
        aboutCard.setLayout(new GridBagLayout());
        aboutCard.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        aboutLogo = new JLabel();
        aboutLogo.setIcon(IconUtils.getIcon("logo", 64, new Color(0x25, 0x63, 0xEB)));
        gbc.gridy = 0;
        aboutCard.add(aboutLogo, gbc);

        aboutTitle = new JLabel("PassVault");
        aboutTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        aboutTitle.setForeground(new Color(0x11, 0x18, 0x27));
        gbc.gridy = 1;
        aboutCard.add(aboutTitle, gbc);

        aboutVersion = new JLabel("v1.0.0 (Stable Edition)");
        aboutVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        aboutVersion.setForeground(new Color(0x6B, 0x72, 0x80));
        gbc.gridy = 2;
        aboutCard.add(aboutVersion, gbc);

        aboutDesc = new JLabel("<html><center>A secure, military-grade encrypted password manager designed to keep your credentials stored locally on disk. Passwords never touch the cloud.</center></html>");
        aboutDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        aboutDesc.setForeground(new Color(0x37, 0x41, 0x51));
        aboutDesc.setPreferredSize(new Dimension(400, 60));
        gbc.gridy = 3;
        aboutCard.add(aboutDesc, gbc);

        JPanel featuresPanel = new JPanel(new GridLayout(4, 1, 0, 6));
        featuresPanel.setOpaque(false);
        featuresPanel.add(createFeatureCheck("✓  AES-256 GCM encryption pipeline"));
        featuresPanel.add(createFeatureCheck("✓  PBKDF2-SHA256 Master Key Derivation"));
        featuresPanel.add(createFeatureCheck("✓  Dynamic password strength checks"));
        featuresPanel.add(createFeatureCheck("✓  Secure auto-clearing clipboard buffer"));

        gbc.gridy = 4;
        gbc.insets = new Insets(12, 0, 12, 0);
        aboutCard.add(featuresPanel, gbc);

        aboutCopyright = new JLabel("© 2026 PassVault Open Source Contributors");
        aboutCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        aboutCopyright.setForeground(new Color(0x9C, 0xA3, 0xAF));
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 0, 0, 0);
        aboutCard.add(aboutCopyright, gbc);

        panel.add(aboutCard, new GridBagConstraints());
        return panel;
    }

    private JLabel createFeatureCheck(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(0x16, 0xA3, 0x4A)); // Success Green
        return l;
    }

    // ==========================================
    // Table Helper Classes
    // ==========================================

    class ModernJTable extends JTable {
        private int hoveredRow = -1;
        private java.util.Set<Integer> visiblePasswordRows = new java.util.HashSet<>();

        public ModernJTable(TableModel model) {
            super(model);
            setRowHeight(40);
            setShowGrid(false);
            setIntercellSpacing(new Dimension(0, 0));
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int row = rowAtPoint(e.getPoint());
                    if (row != hoveredRow) {
                        hoveredRow = row;
                        repaint();
                    }
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    hoveredRow = -1;
                    repaint();
                }
            });
        }

        public boolean isRowPasswordVisible(int row) {
            return visiblePasswordRows.contains(row);
        }

        public void togglePasswordVisibility(int row) {
            if (visiblePasswordRows.contains(row)) {
                visiblePasswordRows.remove(row);
            } else {
                visiblePasswordRows.add(row);
            }
            repaint();
        }

        public int getHoveredRow() {
            return hoveredRow;
        }
    }

    class PasswordCellRenderer extends JPanel implements TableCellRenderer {
        private JLabel maskLabel = new JLabel("••••••••");
        private JLabel showLabel = new JLabel();
        private JLabel copyLabel = new JLabel();

        public PasswordCellRenderer() {
            setLayout(new BorderLayout(8, 0));
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

            maskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            showLabel.setIcon(IconUtils.getIcon("eye", 16, new Color(0x6B, 0x72, 0x80)));
            copyLabel.setIcon(IconUtils.getIcon("copy", 16, new Color(0x6B, 0x72, 0x80)));

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            actions.setOpaque(false);
            actions.add(showLabel);
            actions.add(copyLabel);

            add(maskLabel, BorderLayout.CENTER);
            add(actions, BorderLayout.EAST);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Color grayColor = isSelected ? (ThemeManager.isDark() ? Color.WHITE : new Color(0x11, 0x18, 0x27)) : ThemeManager.getTextSecondary();

            if (isSelected) {
                setBackground(ThemeManager.getTableSelection());
                maskLabel.setForeground(ThemeManager.isDark() ? Color.WHITE : new Color(0x11, 0x18, 0x27));
            } else if (row == ((ModernJTable) table).getHoveredRow()) {
                setBackground(ThemeManager.getTableHover());
                maskLabel.setForeground(ThemeManager.getTextPrimary());
            } else {
                setBackground(row % 2 == 0 ? ThemeManager.getCardColor() : ThemeManager.getTableAlternateRow());
                maskLabel.setForeground(ThemeManager.getTextPrimary());
            }

            boolean visible = ((ModernJTable) table).isRowPasswordVisible(row);
            if (visible) {
                maskLabel.setText(value != null ? value.toString() : "");
                showLabel.setIcon(IconUtils.getIcon("eye-off", 16, grayColor));
            } else {
                maskLabel.setText("••••••••");
                showLabel.setIcon(IconUtils.getIcon("eye", 16, grayColor));
            }
            
            copyLabel.setIcon(IconUtils.getIcon("copy", 16, grayColor));

            return this;
        }
    }

    class UrlCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            String url = (value != null) ? value.toString() : "";
            if (!url.isEmpty()) {
                setText("<html><u>" + url + "</u></html>");
                setForeground(ThemeManager.getBlueAccent());
            } else {
                setText("");
            }
            if (isSelected) {
                setBackground(ThemeManager.getTableSelection());
            } else if (row == ((ModernJTable) table).getHoveredRow()) {
                setBackground(ThemeManager.getTableHover());
            } else {
                setBackground(row % 2 == 0 ? ThemeManager.getCardColor() : ThemeManager.getTableAlternateRow());
            }
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            return this;
        }
    }

    class StrengthCellRenderer extends JPanel implements TableCellRenderer {
        private JLabel label = new JLabel();

        public StrengthCellRenderer() {
            setLayout(new GridBagLayout());
            setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.BOLD, 10));
            label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            add(label);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String strength = (value != null) ? value.toString() : "None";
            label.setText(strength.toUpperCase());

            if (isSelected) {
                setBackground(ThemeManager.getTableSelection());
            } else if (row == ((ModernJTable) table).getHoveredRow()) {
                setBackground(ThemeManager.getTableHover());
            } else {
                setBackground(row % 2 == 0 ? ThemeManager.getCardColor() : ThemeManager.getTableAlternateRow());
            }

            if (ThemeManager.isDark()) {
                switch (strength) {
                    case "Weak":
                        label.setForeground(new Color(0xEF, 0x44, 0x44)); // Destructive Red
                        label.setBackground(new Color(0x45, 0x27, 0x27));
                        break;
                    case "Medium":
                        label.setForeground(new Color(0xFB, 0xBF, 0x24)); // Warning Amber
                        label.setBackground(new Color(0x45, 0x38, 0x23));
                        break;
                    case "Strong":
                        label.setForeground(new Color(0x34, 0xD3, 0x99));
                        label.setBackground(new Color(0x23, 0x45, 0x38));
                        break;
                    case "Very Strong":
                        label.setForeground(new Color(0x34, 0xD3, 0x99));
                        label.setBackground(new Color(0x23, 0x45, 0x38));
                        break;
                    default:
                        label.setForeground(ThemeManager.getTextSecondary());
                        label.setBackground(ThemeManager.getTableHover());
                        break;
                }
            } else {
                switch (strength) {
                    case "Weak":
                        label.setForeground(new Color(0x99, 0x1B, 0x1B));
                        label.setBackground(new Color(0xFE, 0xE2, 0xE2));
                        break;
                    case "Medium":
                        label.setForeground(new Color(0x92, 0x40, 0x0E));
                        label.setBackground(new Color(0xFE, 0xF3, 0xCB));
                        break;
                    case "Strong":
                        label.setForeground(new Color(0x06, 0x5F, 0x46));
                        label.setBackground(new Color(0xD1, 0xFA, 0xE5));
                        break;
                    case "Very Strong":
                        label.setForeground(new Color(0x06, 0x4E, 0x3B));
                        label.setBackground(new Color(0xA7, 0xF3, 0xD0));
                        break;
                    default:
                        label.setForeground(new Color(0x37, 0x41, 0x51));
                        label.setBackground(new Color(0xF3, 0xF4, 0xF6));
                        break;
                }
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(label.getBackground());

            int w = label.getPreferredSize().width + 12;
            int h = 22;
            int x = (getWidth() - w) / 2;
            int y = (getHeight() - h) / 2;
            g2.fillRoundRect(x, y, w, h, 10, 10);
            g2.dispose();

            label.setSize(label.getPreferredSize());
            Graphics gLabel = g.create(x + 6, y + (h - label.getPreferredSize().height) / 2, label.getPreferredSize().width, label.getPreferredSize().height);
            label.paint(gLabel);
            gLabel.dispose();
        }
    }

    public void applyTheme() {
        boolean dark = ThemeManager.isDark();
        
        // 1. Frame and Content Pane
        getContentPane().setBackground(ThemeManager.getBgColor());
        
        // 2. Header Panel
        headerPanel.setBackground(dark ? ThemeManager.DARK_CARD : new Color(0x1F, 0x29, 0x37)); // Charcoal or Dark Slate
        headerTitleLabel.setForeground(Color.WHITE); // Keep white text for high contrast header
        lastUsedLabel.setForeground(dark ? ThemeManager.DARK_TEXT_SECONDARY : new Color(0x9C, 0xA3, 0xAF));
        
        entryCountBadge.setForeground(Color.WHITE);
        entryCountBadge.setBackground(dark ? ThemeManager.DARK_BG : new Color(0x37, 0x41, 0x51));
        entryCountBadge.setIcon(IconUtils.getIcon("archive", 18, Color.WHITE));
        badgePanel.setBackground(dark ? ThemeManager.DARK_BG : new Color(0x37, 0x41, 0x51));
        
        lockButton.setBackground(ThemeManager.getDestructiveRed());
        lockButton.setIcon(IconUtils.getIcon("lock", 18, Color.WHITE));
        
        // 3. Sidebar
        sidebar.setBackground(dark ? ThemeManager.DARK_BG : Color.WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.getBorderColor()),
            BorderFactory.createEmptyBorder(0, 16, 0, 16)
        ));
        
        // Update sidebar section titles
        navTitle.setForeground(ThemeManager.getTextSecondary());
        actionsTitle.setForeground(ThemeManager.getTextSecondary());
        systemTitle.setForeground(ThemeManager.getTextSecondary());
        
        updateSidebarButtons();

        // 4. Vault View & Table Container
        roundedTableContainer.setBorderColor(ThemeManager.getBorderColor());
        roundedTableContainer.setBackground(ThemeManager.getCardColor());
        
        // JTable header
        JTableHeader header = passwordTable.getTableHeader();
        header.setBackground(dark ? ThemeManager.DARK_CARD : new Color(0xF8, 0xF9, 0xFB));
        header.setForeground(ThemeManager.getTextSecondary());
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()));
        
        // 5. Details Panel
        detailsCard.setBorderColor(ThemeManager.getBorderColor());
        detailsCard.setBackground(ThemeManager.getCardColor());
        
        placeholderKeyIcon.setIcon(IconUtils.getIcon("vault", 48, ThemeManager.getTextSecondary()));
        placeholderInfoText.setForeground(ThemeManager.getTextSecondary());
        
        detailsTitleLabel.setForeground(ThemeManager.getTextPrimary());
        
        // Refresh details components
        refreshDetailsComponentsStyle();

        // 6. Generator Panel
        generatorCard.setBorderColor(ThemeManager.getBorderColor());
        generatorCard.setBackground(ThemeManager.getCardColor());
        generatorTitle.setForeground(ThemeManager.getTextPrimary());
        generatorPasswordDisplay.setBackground(dark ? ThemeManager.DARK_BG : new Color(0xF3, 0xF4, 0xF6));
        generatorPasswordDisplay.setForeground(ThemeManager.getTextPrimary());
        generatorPasswordDisplay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        generatorLengthLabel.setForeground(ThemeManager.getTextPrimary());
        generatorRegenBtn.setBackground(dark ? ThemeManager.DARK_BG : new Color(0xF3, 0xF4, 0xF6));
        generatorRegenBtn.setForeground(ThemeManager.getTextPrimary());
        generatorRegenBtn.setIcon(IconUtils.getIcon("wand-sparkles", 16, ThemeManager.getTextPrimary()));
        generatorCopyBtn.setBackground(ThemeManager.getBlueAccent());
        generatorCopyBtn.setForeground(Color.WHITE);
        generatorCopyBtn.setIcon(IconUtils.getIcon("copy", 16, Color.WHITE));
        
        // 7. Settings Panel
        settingsCard.setBorderColor(ThemeManager.getBorderColor());
        settingsCard.setBackground(ThemeManager.getCardColor());
        settingsTitle.setForeground(ThemeManager.getTextPrimary());
        settingsInfo.setForeground(ThemeManager.getTextPrimary());
        settingsRecoveryInfo.setForeground(ThemeManager.getTextPrimary());
        settingsConfigureRecoveryBtn.setBackground(ThemeManager.getBlueAccent());
        settingsConfigureRecoveryBtn.setForeground(Color.WHITE);
        settingsConfigureRecoveryBtn.setIcon(IconUtils.getIcon("key-round", 16, Color.WHITE));
        settingsWarningText.setForeground(ThemeManager.getDestructiveRed());
        settingsResetBtn.setBackground(ThemeManager.getDestructiveRed());
        settingsResetBtn.setForeground(Color.WHITE);
        settingsResetBtn.setIcon(IconUtils.getIcon("trash-2", 16, Color.WHITE));
        settingsThemeTitle.setForeground(ThemeManager.getTextPrimary());
        settingsThemeInfo.setForeground(ThemeManager.getTextPrimary());
        settingsThemeBtn.setBackground(dark ? ThemeManager.DARK_BG : new Color(0xF3, 0xF4, 0xF6));
        settingsThemeBtn.setForeground(ThemeManager.getTextPrimary());
        settingsThemeBtn.setIcon(IconUtils.getIcon(dark ? "sun" : "moon", 16, ThemeManager.getTextPrimary()));
        settingsThemeBtn.setText(dark ? "Switch to Light Theme" : "Switch to Dark Theme");

        // 8. About Panel
        aboutCard.setBorderColor(ThemeManager.getBorderColor());
        aboutCard.setBackground(ThemeManager.getCardColor());
        aboutLogo.setIcon(IconUtils.getIcon("logo", 64, ThemeManager.getBlueAccent()));
        aboutTitle.setForeground(ThemeManager.getTextPrimary());
        aboutVersion.setForeground(ThemeManager.getTextSecondary());
        aboutDesc.setForeground(ThemeManager.getTextPrimary());
        aboutCopyright.setForeground(ThemeManager.getTextSecondary());
        
        // Refresh items
        refreshTable();
        
        // Repaint all components
        revalidate();
        repaint();
    }

    private void updateSidebarButtons() {
        if (sidebar == null) return;
        Component[] components = sidebar.getComponents();
        for (Component comp : components) {
            if (comp instanceof RoundedButton) {
                RoundedButton btn = (RoundedButton) comp;
                String iconName = (String) btn.getClientProperty("iconName");
                if (iconName != null) {
                    if (btn == activeNavButton) {
                        btn.setBackground(ThemeManager.isDark() ? new Color(0x2A, 0x2D, 0x3A) : new Color(0xE5, 0xE7, 0xEB));
                        btn.setForeground(ThemeManager.getBlueAccent());
                        btn.setIcon(IconUtils.getIcon(iconName, 20, ThemeManager.getBlueAccent()));
                    } else {
                        btn.setBackground(ThemeManager.isDark() ? ThemeManager.DARK_BG : Color.WHITE);
                        btn.setForeground(ThemeManager.getTextPrimary());
                        btn.setIcon(IconUtils.getIcon(iconName, 20, ThemeManager.getTextSecondary()));
                    }
                } else {
                    if (btn.getText().equals("Delete Entry")) {
                        btn.setBackground(ThemeManager.isDark() ? ThemeManager.DARK_BG : Color.WHITE);
                        btn.setForeground(ThemeManager.getDestructiveRed());
                        btn.setIcon(IconUtils.getIcon("trash-2", 18, ThemeManager.getDestructiveRed()));
                    } else {
                        btn.setBackground(ThemeManager.isDark() ? ThemeManager.DARK_BG : Color.WHITE);
                        btn.setForeground(ThemeManager.getTextPrimary());
                        String name = getActionIconName(btn.getText());
                        btn.setIcon(IconUtils.getIcon(name, 18, ThemeManager.getTextSecondary()));
                    }
                }
            }
        }
    }

    private String getActionIconName(String text) {
        if (text.startsWith("Add")) return "plus";
        if (text.startsWith("Edit")) return "pencil";
        if (text.startsWith("Delete")) return "trash-2";
        if (text.startsWith("Copy")) return "copy";
        return "info";
    }

    private void refreshDetailsComponentsStyle() {
        if (detailsUsernameField == null || detailsPasswordField == null) return;
        boolean dark = ThemeManager.isDark();
        
        detailsUsernameField.setBackground(ThemeManager.getCardColor());
        detailsUsernameField.setForeground(ThemeManager.getTextPrimary());
        detailsUsernameField.setCaretColor(ThemeManager.getTextPrimary());
        
        detailsPasswordField.setBackground(ThemeManager.getCardColor());
        detailsPasswordField.setForeground(ThemeManager.getTextPrimary());
        detailsPasswordField.setCaretColor(ThemeManager.getTextPrimary());
        
        detailsNotesArea.setBackground(dark ? ThemeManager.getBgColor() : new Color(0xF9, 0xFA, 0xFB));
        detailsNotesArea.setForeground(ThemeManager.getTextPrimary());
        detailsNotesArea.setCaretColor(ThemeManager.getTextPrimary());
        
        btnCopyUser.setBackground(dark ? ThemeManager.getBgColor() : new Color(0xF3, 0xF4, 0xF6));
        btnCopyUser.setIcon(IconUtils.getIcon("copy", 16, dark ? ThemeManager.getTextPrimary() : new Color(0x37, 0x41, 0x51)));
        
        boolean showPass = (detailsPasswordField.getEchoChar() == (char) 0);
        btnTogglePass.setBackground(dark ? ThemeManager.getBgColor() : new Color(0xF3, 0xF4, 0xF6));
        btnTogglePass.setIcon(IconUtils.getIcon(showPass ? "eye-off" : "eye", 16, dark ? ThemeManager.getTextPrimary() : new Color(0x37, 0x41, 0x51)));
        
        btnCopyPass.setBackground(dark ? ThemeManager.getBgColor() : new Color(0xF3, 0xF4, 0xF6));
        btnCopyPass.setIcon(IconUtils.getIcon("copy", 16, dark ? ThemeManager.getTextPrimary() : new Color(0x37, 0x41, 0x51)));
        
        btnOpenUrl.setBackground(dark ? ThemeManager.getBgColor() : new Color(0xF3, 0xF4, 0xF6));
        btnOpenUrl.setIcon(IconUtils.getIcon("external-link", 16, dark ? ThemeManager.getTextPrimary() : new Color(0x37, 0x41, 0x51)));
        
        btnEditDetail.setBackground(ThemeManager.getBlueAccent());
        btnEditDetail.setForeground(Color.WHITE);
        btnEditDetail.setIcon(IconUtils.getIcon("pencil", 16, Color.WHITE));
        
        btnDeleteDetail.setBackground(ThemeManager.getDestructiveRed());
        btnDeleteDetail.setForeground(Color.WHITE);
        btnDeleteDetail.setIcon(IconUtils.getIcon("trash-2", 16, Color.WHITE));
        
        if (detailsContent != null) {
            updateSubcomponentsTheme(detailsContent);
        }
    }

    private void updateSubcomponentsTheme(Container container) {
        boolean dark = ThemeManager.isDark();
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel l = (JLabel) comp;
                if (l == detailsUrlLabel) {
                    l.setForeground(ThemeManager.getBlueAccent());
                } else if (l == detailsStrengthText) {
                    // Handled in updateDetailsPanel
                } else if (l == detailsCreatedLabel || l == detailsModifiedLabel) {
                    l.setForeground(ThemeManager.getTextSecondary());
                    String icon = (l == detailsCreatedLabel) ? "calendar" : "clock";
                    l.setIcon(IconUtils.getIcon(icon, 14, ThemeManager.getTextSecondary()));
                } else {
                    l.setForeground(ThemeManager.getTextSecondary());
                }
            } else if (comp instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) comp;
                sp.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
                sp.getViewport().setBackground(ThemeManager.isDark() ? ThemeManager.getBgColor() : new Color(0xF9, 0xFA, 0xFB));
            } else if (comp instanceof JPanel) {
                JPanel p = (JPanel) comp;
                if (p.getPreferredSize() != null && p.getPreferredSize().height == 1 && p.getPreferredSize().width == 280) {
                    p.setBackground(ThemeManager.getBorderColor());
                } else {
                    updateSubcomponentsTheme(p);
                }
            }
        }
    }
}
