package com.passvault;

import com.passvault.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StorageManager storageManager = new StorageManager();
            AuthService authService = new AuthService(storageManager);
            com.passvault.ui.ThemeManager.initialize(authService);
            LoginFrame loginFrame = new LoginFrame(authService);
            loginFrame.setVisible(true);
        });
    }
}
