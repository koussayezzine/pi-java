package tn.esprit.sirine.utils;

import java.sql.Connection;

/**
 * Singleton class for database connection
 * This is a wrapper around MaConnexion to maintain compatibility with existing code
 */
public class DBconnection {
    private static DBconnection instance;
    private Connection conn;

    private DBconnection() {
        this.conn = MaConnexion.getConnection();
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
