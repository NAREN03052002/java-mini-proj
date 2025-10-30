package com.app.rating.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtil {
    // WARNING: This is a simplistic utility. Use a library like jBCrypt in production!

    public static String hashPassword(String plaintextPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plaintextPassword.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean checkPassword(String plaintextPassword, String storedHash) {
        String hashedAttempt = hashPassword(plaintextPassword);
        return hashedAttempt.equals(storedHash);
    }
}
