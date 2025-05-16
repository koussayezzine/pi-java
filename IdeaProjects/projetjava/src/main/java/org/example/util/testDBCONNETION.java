package org.example.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class testDBCONNETION {

    public static void main(String[] args) {
        // Obtenez l'instance de la connexion à la base de données
        DBconnection dbConnection = DBconnection.getInstance();

        // Vérifiez si la connexion est fonctionnelle
        if (dbConnection.getConn() != null) {
            System.out.println("Connexion à la base de données réussie !");

            // Test de la connexion en exécutant une requête simple
            try (Statement stmt = dbConnection.getConn().createStatement()) {
                // Simple requête pour tester la connexion (ici, une requête SELECT 1)
                String query = "SELECT 1";
                ResultSet rs = stmt.executeQuery(query);

                // Vérifiez que le résultat contient bien une ligne
                if (rs.next()) {
                    System.out.println("Requête exécutée avec succès, base de données fonctionnelle !");
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'exécution de la requête : " + e.getMessage());
            }
        } else {
            System.out.println("Erreur : Impossible de se connecter à la base de données.");
        }
    }
}
