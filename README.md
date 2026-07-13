# PassVault - Encrypted Password Manager

A secure, desktop-based password manager built with Java Swing that uses military-grade AES-256-GCM encryption.

## Features

✅ **Master Password Protection** - Create a strong master password on first use
✅ **AES-256-GCM Encryption** - Military-grade encryption for all stored passwords
✅ **PBKDF2 Key Derivation** - Secure password-to-encryption-key conversion
✅ **Password Strength Analysis** - Real-time strength indicator (Weak/Medium/Strong/Very Strong)
✅ **Password Generator** - Generate random strong passwords with customizable parameters
✅ **Secure Clipboard** - Passwords auto-clear from clipboard after 30 seconds
✅ **Search & Filter** - Quickly find entries by name, username, or URL
✅ **Password Metadata** - Track when passwords were created and last modified
✅ **Clean UI** - Modern dashboard with sidebar, topbar, and organized tables

## System Requirements

- Java 21 or higher
- 50 MB free disk space
- Windows, macOS, or Linux

## Installation

### From JAR File
```bash
java -jar PassVault.jar
```

### Building from Source
1. Install Java 21 and Maven
2. Navigate to the project directory
3. Run:
```bash
mvn clean install
mvn package
```
4. The executable JAR will be in `target/PassVault.jar`

## First Launch

1. **Setup Master Password**: On first launch, you'll be prompted to create a master password
   - Minimum 8 characters
   - Strength indicator shows quality in real-time
   - This password encrypts all your stored passwords

2. **Main Dashboard**: After setup, you'll see:
   - **Sidebar**: Quick action buttons (Add, Edit, Delete, Generate, Copy, Lock)
   - **Topbar**: Vault status, entry count, last used date, lock button
   - **Main Table**: All stored password entries with strength indicators

## Usage

### Adding a Password Entry
1. Click **➕ Add Entry** in the sidebar
2. Fill in: Name, Username/Email, Password, URL (optional)
3. Password strength is shown in real-time
4. Click **Generate** to create a strong random password
5. Click **Save** to encrypt and store

### Editing an Entry
1. Select an entry in the table
2. Click **✏️ Edit Entry** or double-click the row
3. Modify fields as needed
4. Click **Save**

### Deleting an Entry
1. Select an entry
2. Click **🗑️ Delete Entry**
3. Confirm deletion

### Generating Passwords
1. Click **🔧 Generate Password**
2. Customize length (8-32 characters)
3. Toggle character types: lowercase, uppercase, digits, symbols
4. Click **✓ Use This** to copy to clipboard

### Copying Passwords
1. Select an entry
2. Click **📋 Copy Password**
3. Password copies to clipboard (auto-clears after 30 seconds)

### Searching
1. Use the search bar at the top
2. Enter a name, username, or URL
3. Results filter in real-time

### Locking the Vault
1. Click **🔐 Lock** button in top-right
2. This returns you to login screen
3. Master password required to unlock

## Technical Details

### Encryption
- **Algorithm**: AES-256 in GCM mode (authenticated encryption)
- **Key Derivation**: PBKDF2-SHA256 with 65,536 iterations
- **IV**: 12-byte random nonce per entry
- **Authentication Tag**: 128-bit GCM tag

### Password Strength Calculation
- Length ≥8 chars: +1
- Length ≥12 chars: +1
- Length ≥16 chars: +1
- Contains lowercase: +1
- Contains uppercase: +1
- Contains digits: +1
- Contains symbols: +1

**Scoring**:
- 0-2 points = Weak (red)
- 3-4 points = Medium (orange)
- 5-6 points = Strong (green)
- 7+ points = Very Strong (dark green)

### Data Storage
- All data stored in `~/.passvault/` directory
- `vault.json` - Encrypted password entries
- `config.json` - Master password hash and metadata
- No data sent to cloud or external servers

## Security Notes

⚠️ **Important**:
1. Your master password is never stored - only a hash
2. All passwords are encrypted with AES-256-GCM
3. Encryption keys are derived only when needed and cleared from memory
4. The application is fully offline - no network calls
5. Data persists only on your local machine
6. If you forget your master password, there is a provision with one security question.

## Keyboard Shortcuts

| Action | Shortcut |
|--------|----------|
| Login/Unlock | Enter |
| Generate Password | Click button |
| Copy Password | Click button |
| Edit Entry | Double-click row |

## Troubleshooting

**"Cannot find JDK"**
- Install Java 21 from java.oracle.com
- Set JAVA_HOME environment variable

**"JAR won't run"**
- Ensure Java 21: `java -version`
- Try: `java -jar PassVault.jar`

**"Forgot master password"**
- Delete `~/.passvault/config.json` to reset
- WARNING: This destroys your vault!

## File Structure

```
PassVault/
├── src/main/java/com/passvault/
│   ├── Main.java                  # Entry point
│   ├── AuthService.java           # Master password auth
│   ├── VaultService.java          # Password entry management
│   ├── StorageManager.java        # File I/O & encryption
│   ├── EncryptionService.java     # AES-256-GCM crypto
│   ├── PasswordEntry.java         # Password data model
│   ├── PasswordGenerator.java     # Random password gen
│   └── ui/
│       ├── LoginFrame.java        # Login/setup screen
│       ├── VaultFrame.java        # Main dashboard
│       ├── AddEditDialog.java     # Entry editor modal
│       └── PasswordGeneratorDialog.java  # Password gen dialog
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

## Dependencies

- **Gson** (2.10.1) - JSON serialization
- **Java Swing** - UI framework (built-in)
- **Java Cryptography** - AES, PBKDF2 (built-in)

## License

Free software for personal use. © 2024

## Support

For issues or feature requests, visit: https://github.com/yourusername/PassVault

---

**Version**: 1.0  
**Last Updated**: 2026  
**Status**: Production Ready
