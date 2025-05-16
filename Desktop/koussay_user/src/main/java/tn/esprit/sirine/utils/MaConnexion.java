package tn.esprit.sirine.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MaConnexion {
    private static Connection connection;

    // Constructeur privé pour empêcher l'instanciation
    private MaConnexion() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Chargement du driver JDBC (facultatif avec les versions récentes)
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Informations de connexion
                String url = "jdbc:mysql://localhost:3306/application?useSSL=false&serverTimezone=UTC";
                String user = "root";
                String password = ""; // Mets ton mot de passe ici si tu en as défini un

                // Établir la connexion
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connexion à la base de données réussie !");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver JDBC non trouvé : " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            }
        }
        return connection;
    }
}
