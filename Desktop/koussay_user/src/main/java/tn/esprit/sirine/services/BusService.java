package tn.esprit.sirine.services;

import tn.esprit.sirine.models.Bus;
import tn.esprit.sirine.utils.MaConnexion;
import tn.esprit.sirine.utils.DataRefreshManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusService implements tn.esprit.sirine.services.IServicee<Bus> {
    private Connection connection;
    private PreparedStatement pst;

    public BusService() {
        connection = MaConnexion.getConnection();
        // Si vous voulez recréer la table à chaque démarrage (attention, cela efface les données)
        // createTableIfNotExists();
        
        // Si vous voulez conserver les données existantes et juste ajouter la colonne manquante
        addStatusColumnIfNotExists();
    }

    private void createTableIfNotExists() {
        try {
            // Supprimer la table si elle existe
            String dropTable = "DROP TABLE IF EXISTS bus";
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(dropTable);
            }

            // Créer la table avec la bonne structure
            String sql = "CREATE TABLE bus (" +
                    "idBus INT PRIMARY KEY AUTO_INCREMENT," +
                    "modele VARCHAR(50) NOT NULL," +
                    "capacite INT NOT NULL," +
                    "plaqueImmat VARCHAR(20) NOT NULL," +
                    "idItineraire INT NOT NULL," +
                    "type VARCHAR(20) NOT NULL," +
                    "tarif DOUBLE NOT NULL," +
                    "status VARCHAR(20) DEFAULT 'Disponible'," +
                    "FOREIGN KEY (idItineraire) REFERENCES itineraire(idItin)" +
                    ")";
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
                // Insérer les données de test
                insertTestData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertTestData() {
        try (Statement statement = connection.createStatement()) {
            // Utiliser des guillemets doubles pour la requête et échapper l'apostrophe
            String sql = "INSERT INTO bus (modele, capacite, plaqueImmat, idItineraire, type, tarif, status) VALUES "
                    + "('Mens Coach', 45, '345 TUN 678', 3, 'Premium', 45.0, 'Disponible'),"
                    + "('Scania Touring', 40, '901 TUN 234', 2, 'Standard', 38.5, 'En service'),"
                    + "('Mercedes Tourismo', 50, '567 TUN 890', 1, 'Premium', 42.0, 'Disponible'),"
                    + "('Volvo 9700', 48, '123 TUN 456', 4, 'Standard', 40.0, 'En maintenance'),"
                    + "('Iveco Magelys', 35, '789 TUN 012', 5, 'Économique', 35.0, 'Disponible')";
            
            statement.executeUpdate(sql);
            System.out.println("Données de test insérées avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion des données de test : " + e.getMessage());
            // Continuer malgré l'erreur, car les données pourraient déjà exister
        }
    }

    private void addStatusColumnIfNotExists() {
        try {
            // Vérifier si la colonne status existe déjà
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "bus", "status");
            
            if (!rs.next()) {
                // La colonne n'existe pas, l'ajouter
                String sql = "ALTER TABLE bus ADD COLUMN status VARCHAR(20) DEFAULT 'Disponible'";
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("Colonne 'status' ajoutée à la table 'bus'");
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification/ajout de la colonne 'status' : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void ajouter(Bus bus) throws SQLException {
        // Vérifier d'abord si l'itinéraire existe
        String checkItineraire = "SELECT COUNT(*) FROM itineraire WHERE idItin = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkItineraire)) {
            checkStmt.setInt(1, bus.getIdItineraire());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new SQLException("L'itinéraire avec ID " + bus.getIdItineraire() + " n'existe pas");
            }
        }
        
        // Procéder à l'ajout du bus
        String req = "INSERT INTO bus (modele, capacite, plaqueImmat, idItineraire, type, tarif, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bus.getModele());
            ps.setInt(2, bus.getCapacite());
            ps.setString(3, bus.getPlaqueImmat());
            ps.setInt(4, bus.getIdItineraire());
            ps.setString(5, bus.getType());
            ps.setDouble(6, bus.getTarif());
            ps.setString(7, bus.getStatus() != null ? bus.getStatus() : "Disponible");
            
            int result = ps.executeUpdate();
            if (result > 0) {
                System.out.println("Bus ajouté avec succès!");
                
                // Récupérer l'ID généré
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        bus.setIdBus(generatedKeys.getInt(1));
                    }
                }
                
                // Notifier les écouteurs du changement
                if (DataRefreshManager.getInstance() != null) {
                    DataRefreshManager.getInstance().notifyChange("bus", null);
                }
            } else {
                System.out.println("Échec de l'ajout du bus.");
            }
        }
    }

    @Override
    public void modifier(Bus bus) throws SQLException {
        String sql = "UPDATE bus SET modele=?, capacite=?, plaqueImmat=?, idItineraire=?, type=?, tarif=?, status=? WHERE idBus=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bus.getModele());
            pstmt.setInt(2, bus.getCapacite());
            pstmt.setString(3, bus.getPlaqueImmat());
            pstmt.setInt(4, bus.getIdItineraire());
            pstmt.setString(5, bus.getType());
            pstmt.setDouble(6, bus.getTarif());
            pstmt.setString(7, bus.getStatus());
            pstmt.setInt(8, bus.getIdBus());
            
            pstmt.executeUpdate();
        }
    }

    @Override
    public void supprimer(int idBus) throws SQLException {
        String sql = "DELETE FROM bus WHERE idBus=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idBus);
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("Bus supprimé avec succès: ID=" + idBus);
                // Notifier les écouteurs du changement
                if (DataRefreshManager.getInstance() != null) {
                    DataRefreshManager.getInstance().notifyChange("bus", null);
                }
            } else {
                System.out.println("Échec de la suppression du bus: ID=" + idBus);
            }
        }
    }

    @Override
    public List<Bus> recuperer() throws SQLException {
        List<Bus> list = new ArrayList<>();
        String req = "SELECT * FROM bus";
        try (PreparedStatement st = connection.prepareStatement(req);
             ResultSet rs = st.executeQuery()) {
            
            while (rs.next()) {
                Bus bus =  new Bus(
                    rs.getInt("idBus"),
                    rs.getString("modele"),
                    rs.getInt("capacite"),
                    rs.getString("plaqueImmat"),
                    rs.getInt("idItineraire"),
                    rs.getString("type"),
                    rs.getDouble("tarif"),
                    rs.getString("status")
                );
                list.add(bus);
            }
        }
        return list;
    }

    public Bus getById(int idBus) throws SQLException {
        String sql = "SELECT * FROM bus WHERE idBus = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idBus);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Bus(
                        rs.getInt("idBus"),
                        rs.getString("modele"),
                        rs.getInt("capacite"),
                        rs.getString("plaqueImmat"),
                        rs.getInt("idItineraire"),
                        rs.getString("type"),
                        rs.getDouble("tarif"),
                        rs.getString("status")
                    );
                }
            }
        }
        return null;
    }

    public List<Bus> rechercherParType(String type) throws SQLException {
        List<Bus> buses = new ArrayList<>();
        String sql = "SELECT * FROM bus WHERE type = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bus bus = new Bus(
                        rs.getInt("idBus"),
                        rs.getString("modele"),
                        rs.getInt("capacite"),
                        rs.getString("plaqueImmat"),
                        rs.getInt("idItineraire"),
                        rs.getString("type"),
                        rs.getDouble("tarif"),
                        rs.getString("status")
                    );
                    buses.add(bus);
                }
            }
        }
        return buses;
    }

    public List<Bus> rechercherParItineraire(int idItineraire) throws SQLException {
        List<Bus> buses = new ArrayList<>();
        String sql = "SELECT * FROM bus WHERE idItineraire = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idItineraire);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bus bus = new Bus(
                        rs.getInt("idBus"),
                        rs.getString("modele"),
                        rs.getInt("capacite"),
                        rs.getString("plaqueImmat"),
                        rs.getInt("idItineraire"),
                        rs.getString("type"),
                        rs.getDouble("tarif"),
                        rs.getString("status")
                    );
                    buses.add(bus);
                }
            }
        }
        return buses;
    }
}
