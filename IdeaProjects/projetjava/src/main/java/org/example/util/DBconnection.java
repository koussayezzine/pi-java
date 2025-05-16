package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    private final String url = "jdbc:mysql://localhost:3306/bustracker";
    private final String user = "root";
    private final String password = "";
    private Connection conn;
    private static DBconnection instance;

    private DBconnection() {
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion à la base de données établie !");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static DBconnection getInstance() {
        if (instance == null) {
            instance = new DBconnection();
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }
}
