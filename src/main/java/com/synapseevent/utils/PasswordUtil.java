package com.synapseevent.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {
    private static final int COST = 12; // Cost factor for BCrypt

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray());
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        // If the stored password is not a BCrypt hash (doesn't start with $2), 
        // treat it as plain text and compare directly
        if (hashedPassword == null || !hashedPassword.startsWith("$2")) {
            return password.equals(hashedPassword);
        }
        // Otherwise, verify using BCrypt
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
        return result.verified;
    }
}