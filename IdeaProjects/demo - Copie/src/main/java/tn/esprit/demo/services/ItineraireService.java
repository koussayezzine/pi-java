package tn.esprit.demo.services;

import tn.esprit.demo.models.Itineraire;
import tn.esprit.demo.util.Maconnexion;
import tn.esprit.demo.util.DataRefreshManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItineraireService implements IService<Itineraire> {
    private Connection connection;

    public ItineraireService() {
        connection = Maconnexion.getInstance().getConnection();
        createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        try {
            // Vérifier si la table existe déjà
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "itineraire", null);
            
            if (!tables.next()) {
                // La table n'existe pas, la créer
                String createTableSQL = "CREATE TABLE itineraire (" +
                        "idItin INT PRIMARY KEY AUTO_INCREMENT, " +
                        "pointDepart VARCHAR(255) NOT NULL, " +
                        "pointArrivee VARCHAR(255) NOT NULL, " +
                        "distance DOUBLE NOT NULL, " +
                        "duree INT NOT NULL, " +
                        "description TEXT, " +
                        "coordDepart VARCHAR(50), " +
                        "coordArrivee VARCHAR(50), " +
                        "arrets TEXT, " +
                        "dureeEstimee VARCHAR(50)" +
                        ")";
                
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(createTableSQL);
                    System.out.println("Table itineraire créée avec succès");
                    
                    // Insérer des données de test
                    String insertDataSQL = "INSERT INTO itineraire (pointDepart, pointArrivee, distance, duree, description, coordDepart, coordArrivee, arrets, dureeEstimee) VALUES " +
                            "('Tunis', 'Sousse', 140.5, 120, 'Route directe via autoroute A1', '36.8065,10.1815', '35.8245,10.6346', 'Hammamet, Enfidha', '2h00min'), " +
                            "('Tunis', 'Sfax', 270.3, 210, 'Route via autoroute A1', '36.8065,10.1815', '34.7406,10.7603', 'Sousse, Monastir, Mahdia', '3h30min'), " +
                            "('Sousse', 'Monastir', 25.7, 30, 'Route côtière', '35.8245,10.6346', '35.7780,10.8262', 'Skanes', '30min')";
                    stmt.execute(insertDataSQL);
                    System.out.println("Données de test insérées dans la table itineraire");
                }
            } else {
                // La table existe déjà, vérifier sa structure
                ResultSet columns = metaData.getColumns(null, null, "itineraire", null);
                boolean hasPointDepart = false;
                
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    if (columnName.equalsIgnoreCase("pointDepart")) {
                        hasPointDepart = true;
                        break;
                    }
                }
                
                // Si la colonne pointDepart n'existe pas, renommer villeDepart en pointDepart
                if (!hasPointDepart) {
                    try {
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM itineraire LIKE 'villeDepart'");
                        if (rs.next()) {
                            // La colonne villeDepart existe, la renommer en pointDepart
                            stmt.execute("ALTER TABLE itineraire CHANGE COLUMN villeDepart pointDepart VARCHAR(255) NOT NULL");
                            System.out.println("Colonne villeDepart renommée en pointDepart");
                        }
                        
                        rs = stmt.executeQuery("SHOW COLUMNS FROM itineraire LIKE 'villeArrivee'");
                        if (rs.next()) {
                            // La colonne villeArrivee existe, la renommer en pointArrivee
                            stmt.execute("ALTER TABLE itineraire CHANGE COLUMN villeArrivee pointArrivee VARCHAR(255) NOT NULL");
                            System.out.println("Colonne villeArrivee renommée en pointArrivee");
                        }
                    } catch (SQLException e) {
                        System.err.println("Erreur lors de la modification de la structure de la table: " + e.getMessage());
                    }
                }
                
                System.out.println("Table itineraire déjà existante");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification/création de la table itineraire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Itineraire> recuperer() {
        List<Itineraire> itineraires = new ArrayList<>();
        try {
            // Vérifier d'abord la structure de la table
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "itineraire", null);
            
            // Afficher les colonnes pour le débogage
            System.out.println("Structure de la table itineraire:");
            while (columns.next()) {
                System.out.println("Colonne: " + columns.getString("COLUMN_NAME") + 
                                   ", Type: " + columns.getString("TYPE_NAME"));
            }
            
            // Requête SQL
            String req = "SELECT * FROM itineraire";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            
            // Récupérer les métadonnées du ResultSet pour vérifier les colonnes disponibles
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            
            System.out.println("Colonnes dans le ResultSet:");
            for (int i = 1; i <= columnCount; i++) {
                System.out.println(i + ": " + rsMetaData.getColumnName(i));
            }
            
            while (rs.next()) {
                Itineraire itineraire = new Itineraire();
                
                // Utiliser les noms de colonnes exacts de la base de données
                itineraire.setIdItin(rs.getInt("idItin"));
                
                // Vérifier si les colonnes existent avant de les lire
                try {
                    itineraire.setPointDepart(rs.getString("pointDepart"));
                } catch (SQLException e) {
                    try {
                        // Essayer avec un nom alternatif
                        itineraire.setPointDepart(rs.getString("villeDepart"));
                    } catch (SQLException ex) {
                        System.out.println("Colonne pointDepart/villeDepart non trouvée");
                        itineraire.setPointDepart("Non spécifié");
                    }
                }
                
                try {
                    itineraire.setPointArrivee(rs.getString("pointArrivee"));
                } catch (SQLException e) {
                    try {
                        // Essayer avec un nom alternatif
                        itineraire.setPointArrivee(rs.getString("villeArrivee"));
                    } catch (SQLException ex) {
                        System.out.println("Colonne pointArrivee/villeArrivee non trouvée");
                        itineraire.setPointArrivee("Non spécifié");
                    }
                }
                
                try {
                    itineraire.setDescription(rs.getString("description"));
                } catch (SQLException e) {
                    System.out.println("Colonne description non trouvée");
                    itineraire.setDescription("");
                }
                
                try {
                    itineraire.setDistance(rs.getDouble("distance"));
                } catch (SQLException e) {
                    System.out.println("Colonne distance non trouvée");
                    itineraire.setDistance(0);
                }
                
                try {
                    itineraire.setDuree(rs.getInt("duree"));
                } catch (SQLException e) {
                    System.out.println("Colonne duree non trouvée");
                    itineraire.setDuree(0);
                }
                
                try {
                    itineraire.setCoordDepart(rs.getString("coordDepart"));
                } catch (SQLException e) {
                    System.out.println("Colonne coordDepart non trouvée");
                    itineraire.setCoordDepart("");
                }
                
                try {
                    itineraire.setCoordArrivee(rs.getString("coordArrivee"));
                } catch (SQLException e) {
                    System.out.println("Colonne coordArrivee non trouvée");
                    itineraire.setCoordArrivee("");
                }
                
                try {
                    itineraire.setArrets(rs.getString("arrets"));
                } catch (SQLException e) {
                    System.out.println("Colonne arrets non trouvée");
                    itineraire.setArrets("");
                }
                
                try {
                    itineraire.setDureeEstimee(rs.getString("dureeEstimee"));
                } catch (SQLException e) {
                    System.out.println("Colonne dureeEstimee non trouvée");
                    itineraire.setDureeEstimee("");
                }
                
                itineraires.add(itineraire);
            }
            System.out.println("Données récupérées: " + itineraires.size() + " itinéraires");
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des itinéraires: " + ex.getMessage());
            ex.printStackTrace();
        }
        return itineraires;
    }

    @Override
    public void ajouter(Itineraire itineraire) throws SQLException {
        String sql = "INSERT INTO itineraire (pointDepart, pointArrivee, arrets, distance, dureeEstimee) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, itineraire.getPointDepart());
            statement.setString(2, itineraire.getPointArrivee());
            statement.setString(3, itineraire.getArrets());
            statement.setDouble(4, itineraire.getDistance());
            statement.setString(5, itineraire.getDureeEstimee());
            
            statement.executeUpdate();
        }
    }

    @Override
    public void modifier(Itineraire itineraire) throws SQLException {
        String sql = "UPDATE itineraire SET pointDepart = ?, pointArrivee = ?, arrets = ?, distance = ?, dureeEstimee = ? WHERE idItin = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, itineraire.getPointDepart());
            statement.setString(2, itineraire.getPointArrivee());
            statement.setString(3, itineraire.getArrets());
            statement.setDouble(4, itineraire.getDistance());
            statement.setString(5, itineraire.getDureeEstimee());
            statement.setInt(6, itineraire.getIdItin());
            
            statement.executeUpdate();
        }
    }

    @Override
    public void supprimer(int idItin) throws SQLException {
        String sql = "DELETE FROM itineraire WHERE idItin=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idItin);
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("Itinéraire supprimé avec succès: ID=" + idItin);
                // Notifier les écouteurs du changement
                DataRefreshManager.getInstance().notifyDataChanged("itineraire");
            } else {
                System.out.println("Échec de la suppression de l'itinéraire: ID=" + idItin);
            }
        }
    }

    public List<Itineraire> rechercher(String searchTerm) throws SQLException {
        List<Itineraire> itineraires = new ArrayList<>();
        String sql = "SELECT * FROM itineraire WHERE pointDepart LIKE ? OR pointArrivee LIKE ? OR description LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Itineraire itineraire = new Itineraire(
                    rs.getInt("idItin"),
                    rs.getString("pointDepart"),
                    rs.getString("pointArrivee"),
                    rs.getString("description"),
                    rs.getDouble("distance"),
                    rs.getInt("duree"),
                    rs.getString("coordDepart"),
                    rs.getString("coordArrivee"),
                    rs.getString("arrets")
                );
                itineraire.setDureeEstimee(rs.getString("dureeEstimee"));
                itineraires.add(itineraire);
            }
        }
        return itineraires;
    }

    public Itineraire getById(int idItin) throws SQLException {
        String sql = "SELECT * FROM itineraire WHERE idItin = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idItin);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Itineraire itineraire = new Itineraire(
                    rs.getInt("idItin"),
                    rs.getString("pointDepart"),
                    rs.getString("pointArrivee"),
                    rs.getString("description"),
                    rs.getDouble("distance"),
                    rs.getInt("duree"),
                    rs.getString("coordDepart"),
                    rs.getString("coordArrivee"),
                    rs.getString("arrets")
                );
                itineraire.setDureeEstimee(rs.getString("dureeEstimee"));
                return itineraire;
            }
        }
        return null;
    }
}
