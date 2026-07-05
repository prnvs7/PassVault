package com.passvault;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VaultService {
    private StorageManager storageManager;
    private List<PasswordEntry> entries;
    private SecretKey secretKey;

    public VaultService(StorageManager storageManager) {
        this.storageManager = storageManager;
        this.entries = new ArrayList<>();
    }

    public void loadVault(SecretKey secretKey) throws Exception {
        this.secretKey = secretKey;
        this.entries = storageManager.loadVault(secretKey);
    }

    public void addEntry(PasswordEntry entry) throws Exception {
        entries.add(entry);
        saveVault();
    }

    public void updateEntry(PasswordEntry entry) throws Exception {
        entries = entries.stream()
                .map(e -> e.getId().equals(entry.getId()) ? entry : e)
                .collect(Collectors.toList());
        saveVault();
    }

    public void deleteEntry(String entryId) throws Exception {
        entries = entries.stream()
                .filter(e -> !e.getId().equals(entryId))
                .collect(Collectors.toList());
        saveVault();
    }

    public List<PasswordEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public PasswordEntry getEntryById(String id) {
        return entries.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<PasswordEntry> searchEntries(String query) {
        String lowerQuery = query.toLowerCase();
        return entries.stream()
                .filter(e -> e.getName().toLowerCase().contains(lowerQuery) ||
                        e.getUsername().toLowerCase().contains(lowerQuery) ||
                        e.getUrl().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    private void saveVault() throws Exception {
        storageManager.saveVault(entries, secretKey);
    }

    public void clear() {
        entries.clear();
        secretKey = null;
    }
}
