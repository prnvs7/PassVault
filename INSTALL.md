# PassVault Installation Guide

## Prerequisites
- Java 21 or higher (JDK, not JRE)
- Maven 3.8+ (for building)

## Download and Install Java 21

### Windows
1. Visit https://www.oracle.com/java/technologies/downloads/#java21
2. Download "Windows x64 Installer"
3. Run the installer and follow instructions
4. Verify: Open Command Prompt and run `java -version`

### macOS
```bash
brew install java@21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
java -version
```

### Linux
```bash
sudo apt update
sudo apt install openjdk-21-jdk
java -version
```

## Build PassVault

### Option 1: Using Maven (Recommended)
1. Install Maven: https://maven.apache.org/download.cgi
2. Extract PassVault folder
3. Open terminal in PassVault directory
4. Run:
```bash
mvn clean package
```
5. The JAR will be at: `target/PassVault.jar`
6. Run it:
```bash
java -jar target/PassVault.jar
```

### Option 2: Manual Compilation
1. Open terminal in PassVault directory
2. Compile:
```bash
javac -d bin -cp "src:lib/*" $(find src -name "*.java")
```
3. Create JAR:
```bash
jar cvfe PassVault.jar com.passvault.Main -C bin .
```
4. Run:
```bash
java -jar PassVault.jar
```

## Troubleshooting

**Error: "javac: command not found"**
- Make sure JDK (not JRE) is installed
- Check JAVA_HOME is set: `echo $JAVA_HOME`

**Error: "mvn: command not found"**
- Install Maven from maven.apache.org
- Add Maven bin folder to PATH

**Cannot connect to localhost:5555**
- This is expected - PassVault is fully offline
- Check firewall isn't blocking Java

## First Run
1. Run: `java -jar PassVault.jar`
2. Create master password (8+ characters)
3. Start adding your passwords!

Need help? Check README.md
