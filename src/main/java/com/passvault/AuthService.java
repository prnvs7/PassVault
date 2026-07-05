package com.passvault;

import javax.crypto.SecretKey;
import java.security.MessageDigest;
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
        try {
            String salt = EncryptionService.generateSalt();
            String hashedPassword = hashPassword(password, salt);
            storageManager.saveMasterPassword(salt, hashedPassword);
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
