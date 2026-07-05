package com.passvault;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:',.<>?/";

    public static String generatePassword(int length, boolean useLower, boolean useUpper, 
                                          boolean useDigits, boolean useSymbols) {
        StringBuilder chars = new StringBuilder();

        if (useLower) chars.append(LOWERCASE);
        if (useUpper) chars.append(UPPERCASE);
        if (useDigits) chars.append(DIGITS);
        if (useSymbols) chars.append(SYMBOLS);

        if (chars.length() == 0) {
            chars.append(LOWERCASE);
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    public static String generatePassword(int length) {
        return generatePassword(length, true, true, true, true);
    }
}
