package com.passvault;

public class PasswordEntry {
    private String id;
    private String name;
    private String username;
    private String password;
    private String url;
    private String notes = "";
    private long createdAt;
    private long lastModifiedAt;

    public PasswordEntry(String name, String username, String password, String url) {
        this(name, username, password, url, "");
    }

    public PasswordEntry(String name, String username, String password, String url, String notes) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.username = username;
        this.password = password;
        this.url = url;
        this.notes = notes != null ? notes : "";
        this.createdAt = System.currentTimeMillis();
        this.lastModifiedAt = System.currentTimeMillis();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.lastModifiedAt = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.lastModifiedAt = System.currentTimeMillis();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.lastModifiedAt = System.currentTimeMillis();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.lastModifiedAt = System.currentTimeMillis();
    }

    public String getNotes() {
        return notes != null ? notes : "";
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes : "";
        this.lastModifiedAt = System.currentTimeMillis();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastModifiedAt() {
        return lastModifiedAt;
    }

    public String getPasswordStrength() {
        if (password == null || password.isEmpty()) {
            return "None";
        }
        
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
}
