package tn.esprit.sirine.services;

import tn.esprit.sirine.models.User;
import tn.esprit.sirine.utils.EmailUtil;
import tn.esprit.sirine.utils.MaConnexion;
import tn.esprit.sirine.utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserService {
    private Connection connection;

    public UserService() {
        connection = MaConnexion.getConnection();
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public void updateUser(User user) {
        String sql = "UPDATE users SET nom=?, prenom=?, email=?, mot_de_passe=?, role=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());

            // Hachage du mot de passe avant mise à jour
            String hashedPassword = PasswordUtils.hashPassword(user.getMotDePasse());
            pstmt.setString(4, hashedPassword);

            pstmt.setString(5, user.getRole());
            pstmt.setInt(6, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO users(nom, prenom, email, mot_de_passe, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());

            // Hachage du mot de passe avant insertion
            String hashedPassword = PasswordUtils.hashPassword(user.getMotDePasse());
            pstmt.setString(4, hashedPassword);

            pstmt.setString(5, user.getRole());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User authenticate(String email, String password) {
        String sql = "SELECT * FROM users WHERE email=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("mot_de_passe");

                // Vérifie si le mot de passe correspond au hash stocké
                boolean passwordMatches = false;

                // Vérifie d'abord avec le nouveau format (PBKDF2)
                if (storedHash.contains(":")) {
                    passwordMatches = PasswordUtils.verifyPassword(password, storedHash);
                } else {
                    // Compatibilité avec l'ancien format (SHA-256)
                    String legacyHash = PasswordUtils.legacyHashPassword(password);
                    passwordMatches = storedHash.equals(legacyHash);

                    // Si le mot de passe correspond, mettre à jour vers le nouveau format
                    if (passwordMatches) {
                        User user = new User(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            password, // Mot de passe non hashé pour permettre le rehachage
                            rs.getString("role")
                        );
                        updateUser(user); // Cela va hacher le mot de passe avec le nouveau format
                    }
                }

                if (passwordMatches) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        storedHash, // Garde le hash pour éviter de révéler le mot de passe
                        rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String resetPassword(String email) {
        System.out.println("Attempting to reset password for email: " + email);

        User user = findByEmail(email);
        if (user != null) {
            String newPassword = generateRandomPassword();
            System.out.println("Generated new password for user: " + user.getId());

            // Store the original password before updating
            String originalPassword = user.getMotDePasse();

            try {
                // Set the new password
                user.setMotDePasse(newPassword);

                // Try to send the email first before updating the database
                System.out.println("Attempting to send email with new password");
                EmailUtil.sendResetEmail(email, newPassword);

                // If email is sent successfully, update the user in the database
                System.out.println("Email sent successfully, updating user in database");
                updateUser(user);

                return "Un e-mail avec un nouveau mot de passe a été envoyé.";
            } catch (Exception e) {
                // If there's an error, revert to the original password
                user.setMotDePasse(originalPassword);

                System.err.println("Error during password reset: " + e.getMessage());
                e.printStackTrace();
                return "Erreur lors de l'envoi de l'e-mail: " + e.getMessage();
            }
        } else {
            System.out.println("No user found with email: " + email);
            return "Aucun utilisateur avec cet e-mail.";
        }
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
