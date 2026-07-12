package com.passvault;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageManager {
    private static final String DATA_DIR = System.getProperty("user.home") + "/.passvault";
    private static final String VAULT_FILE = DATA_DIR + "/vault.json";
    private static final String CONFIG_FILE = DATA_DIR + "/config.json";

    private Gson gson;

    public StorageManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        createDataDirectory();
    }

    private void createDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMasterPassword(String salt, String hashedPassword, String securityQuestion, String securitySalt, String iv, String recoveryData) throws Exception {
        Map<String, String> config = loadMasterPasswordConfig();
        if (config == null) {
            config = new java.util.HashMap<>();
            config.put("createdAt", String.valueOf(System.currentTimeMillis()));
        }
        config.put("salt", salt);
        config.put("masterPassword", hashedPassword);
        config.put("lastUsedAt", String.valueOf(System.currentTimeMillis()));
        if (securityQuestion != null) {
            config.put("securityQuestion", securityQuestion);
            config.put("securitySalt", securitySalt);
            config.put("iv", iv);
            config.put("recoveryData", recoveryData);
        }

        String json = gson.toJson(config);
        Files.write(Paths.get(CONFIG_FILE), json.getBytes());
    }

    public void saveMasterPassword(String salt, String hashedPassword) throws Exception {
        saveMasterPassword(salt, hashedPassword, null, null, null, null);
    }

    public Map<String, String> loadMasterPasswordConfig() throws Exception {
        Path configPath = Paths.get(CONFIG_FILE);
        if (!Files.exists(configPath)) {
            return null;
        }

        String json = new String(Files.readAllBytes(configPath));
        return gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
    }

    public void saveVault(List<PasswordEntry> entries, SecretKey secretKey) throws Exception {
        String plainJson = gson.toJson(entries);
        EncryptionService.EncryptedData encrypted = EncryptionService.encrypt(plainJson, secretKey);

        Map<String, String> vaultData = new HashMap<>();
        vaultData.put("iv", encrypted.iv);
        vaultData.put("data", encrypted.encryptedText);

        String vaultJson = gson.toJson(vaultData);
        Files.write(Paths.get(VAULT_FILE), vaultJson.getBytes());
    }

    public List<PasswordEntry> loadVault(SecretKey secretKey) throws Exception {
        Path vaultPath = Paths.get(VAULT_FILE);
        if (!Files.exists(vaultPath)) {
            return new ArrayList<>();
        }

        String vaultJson = new String(Files.readAllBytes(vaultPath));
        Map<String, String> vaultData = gson.fromJson(vaultJson, new TypeToken<Map<String, String>>(){}.getType());

        String decrypted = EncryptionService.decrypt(vaultData.get("data"), vaultData.get("iv"), secretKey);
        List<PasswordEntry> entries = gson.fromJson(decrypted, new TypeToken<List<PasswordEntry>>(){}.getType());

        return entries != null ? entries : new ArrayList<>();
    }

    public void updateLastUsedTime() throws Exception {
        Map<String, String> config = loadMasterPasswordConfig();
        if (config != null) {
            config.put("lastUsedAt", String.valueOf(System.currentTimeMillis()));
            String json = gson.toJson(config);
            Files.write(Paths.get(CONFIG_FILE), json.getBytes());
        }
    }
}
