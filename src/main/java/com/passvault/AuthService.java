package com.passvault;

import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

public class AuthService {
    private StorageManager storageManager;
    private SecretKey currentKey;

    public AuthService(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    public boolean isFirstTime() {
        try {
            return storageManager.loadMasterPasswordConfig() == null;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean setupMasterPassword(String password) {
        return setupMasterPassword(password, null, null);
    }

    public boolean setupMasterPassword(String password, String question, String answer) {
        try {
            String salt = EncryptionService.generateSalt();
            String hashedPassword = hashPassword(password, salt);
            
            String securitySalt = null;
            String iv = null;
            String recoveryData = null;
            
            if (question != null && answer != null && !answer.trim().isEmpty()) {
                securitySalt = EncryptionService.generateSalt();
                SecretKey securityKey = EncryptionService.deriveKeyFromPassword(answer.toLowerCase().trim(), securitySalt);
                EncryptionService.EncryptedData encrypted = EncryptionService.encrypt(password, securityKey);
                iv = encrypted.iv;
                recoveryData = encrypted.encryptedText;
            }

            storageManager.saveMasterPassword(salt, hashedPassword, question, securitySalt, iv, recoveryData);
            this.currentKey = EncryptionService.deriveKeyFromPassword(password, salt);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyMasterPassword(String password) {
        try {
            Map<String, String> config = storageManager.loadMasterPasswordConfig();
            if (config == null) {
                return false;
            }

            String salt = config.get("salt");
            String storedHash = config.get("masterPassword");
            String computedHash = hashPassword(password, salt);

            if (computedHash.equals(storedHash)) {
                this.currentKey = EncryptionService.deriveKeyFromPassword(password, salt);
                storageManager.updateLastUsedTime();
                return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public SecretKey getCurrentKey() {
        return currentKey;
    }

    public String getSecurityQuestion() {
        try {
            Map<String, String> config = storageManager.loadMasterPasswordConfig();
            return config != null ? config.get("securityQuestion") : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String recoverMasterPassword(String answer) {
        try {
            Map<String, String> config = storageManager.loadMasterPasswordConfig();
            if (config == null || !config.containsKey("recoveryData")) {
                return null;
            }

            String salt = config.get("salt");
            String securitySalt = config.get("securitySalt");
            String iv = config.get("iv");
            String recoveryData = config.get("recoveryData");

            SecretKey securityKey = EncryptionService.deriveKeyFromPassword(answer.toLowerCase().trim(), securitySalt);
            String oldPassword = EncryptionService.decrypt(recoveryData, iv, securityKey);
            
            // Set current key so we can access vault entries during re-keying
            this.currentKey = EncryptionService.deriveKeyFromPassword(oldPassword, salt);
            return oldPassword;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean changeMasterPassword(String newPassword, String question, String answer) {
        try {
            if (this.currentKey == null) {
                return false;
            }
            
            // Re-key the vault entries
            VaultService vaultService = new VaultService(storageManager);
            vaultService.loadVault(this.currentKey);
            List<PasswordEntry> entries = vaultService.getEntries();

            // Set up new credentials
            String salt = EncryptionService.generateSalt();
            String hashedPassword = hashPassword(newPassword, salt);
            SecretKey newKey = EncryptionService.deriveKeyFromPassword(newPassword, salt);

            // Re-encrypt the vault file with the new key
            storageManager.saveVault(entries, newKey);

            // Generate new recovery metadata
            String securitySalt = null;
            String iv = null;
            String recoveryData = null;
            if (question != null && answer != null && !answer.trim().isEmpty()) {
                securitySalt = EncryptionService.generateSalt();
                SecretKey securityKey = EncryptionService.deriveKeyFromPassword(answer.toLowerCase().trim(), securitySalt);
                EncryptionService.EncryptedData encrypted = EncryptionService.encrypt(newPassword, securityKey);
                iv = encrypted.iv;
                recoveryData = encrypted.encryptedText;
            }

            storageManager.saveMasterPassword(salt, hashedPassword, question, securitySalt, iv, recoveryData);
            this.currentKey = newKey;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSecurityQuestion(String question, String answer, String currentPassword) {
        try {
            if (!verifyMasterPassword(currentPassword)) {
                return false;
            }

            String securitySalt = EncryptionService.generateSalt();
            SecretKey securityKey = EncryptionService.deriveKeyFromPassword(answer.toLowerCase().trim(), securitySalt);
            EncryptionService.EncryptedData encrypted = EncryptionService.encrypt(currentPassword, securityKey);

            Map<String, String> config = storageManager.loadMasterPasswordConfig();
            String salt = config.get("salt");
            String hashedPassword = config.get("masterPassword");

            storageManager.saveMasterPassword(salt, hashedPassword, question, securitySalt, encrypted.iv, encrypted.encryptedText);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hashPassword(String password, String salt) throws Exception {
        String combined = password + salt;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(combined.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public Map<String, String> getPasswordMetadata() {
        try {
            return storageManager.loadMasterPasswordConfig();
        } catch (Exception e) {
            return null;
        }
    }
}
