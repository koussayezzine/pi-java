package tn.esprit.sirine.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class PasswordUtils {

    // Paramètres de l'algorithme PBKDF2
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    // Séparateur pour stocker le sel et le hash ensemble
    private static final String SEPARATOR = ":";

    /**
     * Hache un mot de passe en utilisant PBKDF2 avec HMAC SHA-256
     * @param password Le mot de passe à hacher
     * @return Une chaîne contenant le sel et le hash, séparés par ':'
     */
    public static String hashPassword(String password) {
        try {
            // Génération d'un sel aléatoire
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Hachage du mot de passe avec PBKDF2
            byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

            // Encodage en Base64 du sel et du hash
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            // Retourne le sel et le hash séparés par ':'
            return saltBase64 + SEPARATOR + hashBase64;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.err.println("Erreur de hachage : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Vérifie si un mot de passe correspond à un hash stocké
     * @param password Le mot de passe à vérifier
     * @param storedHash Le hash stocké (sel:hash)
     * @return true si le mot de passe correspond, false sinon
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Séparation du sel et du hash
            String[] parts = storedHash.split(SEPARATOR);
            if (parts.length != 2) {
                return false; // Format invalide
            }

            // Décodage du sel et du hash
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);

            // Calcul du hash du mot de passe fourni avec le même sel
            byte[] testHash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

            // Comparaison des hash (temps constant pour éviter les attaques temporelles)
            return Arrays.equals(hash, testHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            System.err.println("Erreur de vérification : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Implémentation de l'algorithme PBKDF2
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Méthode de compatibilité pour l'ancien système de hachage (SHA-256 simple)
     * À utiliser uniquement pour la migration des anciens mots de passe
     */
    public static String legacyHashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
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
