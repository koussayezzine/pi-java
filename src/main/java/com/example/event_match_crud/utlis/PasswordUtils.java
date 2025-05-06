package com.example.event_match_crud.utlis;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b)); // conversion en hex
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Erreur de hachage : " + e.getMessage());
            return null;
        }
    }
}
