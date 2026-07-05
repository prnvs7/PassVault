# PassVault - Quick Start Guide

## 🚀 30-Second Setup

### 1. Get Java 21
```bash
# macOS
brew install java@21

# Windows: Download from java.oracle.com
# Linux: sudo apt install openjdk-21-jdk
```

### 2. Build & Run
```bash
cd PassVault
mvn clean package
java -jar target/PassVault.jar
```

### 3. First Launch
- Create a **strong master password** (8+ characters)
- Click "Create Password"
- Start adding your passwords!

---

## 🔐 What You Get

✅ **Military-Grade Encryption** - AES-256-GCM
✅ **Offline & Local** - No cloud, no servers
✅ **Master Password** - One password to rule them all
✅ **Password Generator** - Create strong passwords
✅ **Password Strength** - Real-time strength indicator
✅ **Clipboard Auto-Clear** - 30-second auto-clear for safety
✅ **Search & Organize** - Find entries instantly
✅ **Last Used Tracking** - See when passwords were modified

---

## 📱 User Interface

```
┌─────────────────────────────────────────────┐
│ 🔒 PassVault  [Created: Jan 01, 2024]       │  ← Topbar
│                Last used: Jan 05, 2024   4  │     (shows metadata)
└─────────────────────────────────────────────┘

┌──────────┬─────────────────────────────────┐
│          │ Search: _____                   │
│ Sidebar  ├─────────────────────────────────┤
│          │ Name │ Username │ Strength      │  ← Main Table
│ ➕ Add   │ GitHub │ dev@... │ Strong       │    (searchable)
│ ✏️ Edit  │ Gmail  │ me@...  │ Very Strong  │
│ 🗑️ Del   │ Netflix│ me@...  │ Medium       │
│ 🔧 Gen   │ Bank   │ user123 │ Weak         │
│ 📋 Copy  │                                 │
│ 🔐 Lock  │                                 │
└──────────┴─────────────────────────────────┘
```

---

## 🎯 Common Tasks

### Add a Password
1. Click **➕ Add Entry** in sidebar
2. Fill in fields:
   - Name: "GitHub"
   - Username: "dev@mail.com"
   - Password: "Type or click 🔧 to generate"
   - URL: "https://github.com" (optional)
3. Click **Save**

### Find a Password
1. Use **Search** field at top
2. Type any part: name, email, or URL
3. Results filter in real-time
4. Click an entry to select it
5. Click **📋 Copy Password** to copy to clipboard
6. (Password auto-clears in 30 seconds)

### Generate a Password
1. Click **🔧 Generate Password**
2. Customize:
   - Length: 8-32 characters (slide)
   - Include: Lowercase, Uppercase, Digits, Symbols (toggle)
3. Click **✓ Use This** to copy
4. Paste into **Add Entry** dialog

### Edit a Password
1. **Double-click** an entry in the table, OR
2. Select it and click **✏️ Edit Entry**
3. Change any fields
4. Click **Save**
5. Timestamp auto-updates

### Delete a Password
1. Select an entry
2. Click **🗑️ Delete Entry**
3. Confirm deletion
4. Entry is permanently deleted

### Lock Your Vault
1. Click **🔐 Lock** button (top-right)
2. Confirm
3. Returns to login screen
4. Master password required to unlock

---

## 🔒 Password Strength Explained

### What Makes a Strong Password?

**Scoring (0-7 points):**
- ✅ 8+ characters = +1 point
- ✅ 12+ characters = +1 point
- ✅ 16+ characters = +1 point
- ✅ Has lowercase (a-z) = +1 point
- ✅ Has UPPERCASE (A-Z) = +1 point
- ✅ Has digits (0-9) = +1 point
- ✅ Has symbols (!@#$) = +1 point

**Ratings:**
- 🔴 **Weak** (0-2 points) - "pass" or "123456"
- 🟠 **Medium** (3-4 points) - "Password123"
- 🟢 **Strong** (5-6 points) - "MyP@ssw0rd2024"
- 🟢 **Very Strong** (7 points) - "Tr0p1cal!Puzz1e#2024"

**Better examples:**
- ❌ Bad: "password", "12345678"
- ✅ Good: "MySecureP@ss123"
- ✅ Best: "Tr0p1c@l_P@ssw0rd!2024"

---

## 🛡️ Security Best Practices

1. **Master Password**
   - Use 16+ characters
   - Mix uppercase, lowercase, numbers, symbols
   - Don't reuse from other sites
   - Write it down OFFLINE (paper in safe)

2. **Password Storage**
   - All data stored in `~/.passvault/`
   - Never share your computer
   - Don't use on public WiFi
   - Use your computer's disk encryption

3. **Clipboard Safety**
   - Passwords auto-clear after 30 seconds
   - Copy only when needed
   - Don't leave passwords in clipboard

4. **Vault Locking**
   - Lock when leaving your computer
   - Lock before sleep/hibernation
   - Lock if multiple users share device

---

## 🐛 Troubleshooting

### "Java not found"
```bash
# Check Java is installed
java -version

# Should show "java 21" or higher
```

**Fix:**
- Download Java 21 from java.oracle.com
- Set JAVA_HOME environment variable
- Restart terminal

### "Cannot build project"
```bash
# Check Maven is installed
mvn -version

# Should show Maven 3.8+
```

**Fix:**
- Install Maven from maven.apache.org
- Add Maven `bin/` folder to PATH
- Restart terminal

### "Password not saving"
1. Check master password is correct
2. Check `~/.passvault/` directory exists
3. Check disk has free space
4. Try creating a new simple entry

### "Forgot master password"
⚠️ **WARNING**: Cannot recover! Options:
1. Delete `~/.passvault/` folder (loses all entries)
2. Restore from backup if you have one
3. Keep master password in secure location

---

## 📊 File Locations

**All data stored in:**
```
~/.passvault/
├── vault.json      (encrypted entries)
└── config.json     (master password hash)
```

**Windows:** `C:\Users\YourName\.passvault\`
**macOS/Linux:** `/Users/YourName/.passvault/`

---

## 🎓 Learning Path

**Day 1:** Add 5-10 passwords, get familiar with UI
**Day 2:** Generate passwords, test strength indicator
**Day 3:** Test lock/unlock, search, edit features
**Day 4:** Copy passwords, clipboard auto-clear
**Day 5:** Regular usage, backup master password somewhere safe

---

## 📞 Need Help?

- **README.md** - Full documentation
- **FEATURES.md** - Complete feature list
- **INSTALL.md** - Installation help
- **Code** - Well-commented Java source code

---

## ✅ Ready to Launch?

```bash
cd PassVault
java -jar target/PassVault.jar
```

**First time?** Create a strong master password (16+ characters with symbols)
**Returning?** Enter your master password to unlock

Enjoy your encrypted vault! 🔐

---

**PassVault v1.0** - Your personal, offline password manager
