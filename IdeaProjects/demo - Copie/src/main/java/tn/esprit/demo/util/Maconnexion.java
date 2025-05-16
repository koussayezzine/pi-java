package tn.esprit.demo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Maconnexion {

    private final String URL = "jdbc:mysql://localhost:3306/3b3"; // Remplacez 'your_database' par le nom de votre base de données
    private final String USER = "root";  // Remplacez par votre utilisateur
    private final String PASSWORD = "";  // Remplacez par votre mot de passe
    private Connection connection;
    private static Maconnexion instance;

    // Constructeur privé pour éviter l'instanciation directe
    private Maconnexion() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion établie avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
    }

    // Méthode pour récupérer l'instance unique de la connexion
    public static Maconnexion getInstance() {
        if (instance == null) {
            instance = new Maconnexion();
        }
        return instance;
    }

    // Méthode pour récupérer la connexion
    public Connection getConnection() {
        return connection;
    }

    // Optionnel : Méthode pour fermer la connexion si nécessaire
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
