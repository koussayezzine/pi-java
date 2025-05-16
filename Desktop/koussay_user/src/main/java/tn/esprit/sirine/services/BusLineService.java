package tn.esprit.sirine.services;

import tn.esprit.sirine.models.BusLine;
import tn.esprit.sirine.utils.MaConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusLineService {
    private Connection connection;

    public BusLineService() {
        connection = MaConnexion.getConnection();
    }

    public List<BusLine> getAllBusLines() {
        List<BusLine> busLines = new ArrayList<>();
        String sql = "SELECT * FROM bus_lines";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                BusLine busLine = new BusLine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("start_point"),
                        rs.getString("end_point"),
                        rs.getInt("chauffeur_id")
                );
                busLines.add(busLine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return busLines;
    }

    public List<BusLine> getBusLinesByChauffeurId(int chauffeurId) {
        List<BusLine> busLines = new ArrayList<>();
        String sql = "SELECT * FROM bus_lines WHERE chauffeur_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, chauffeurId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BusLine busLine = new BusLine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("start_point"),
                        rs.getString("end_point"),
                        rs.getInt("chauffeur_id")
                );
                busLines.add(busLine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return busLines;
    }

    public boolean addBusLine(BusLine busLine) {
        String sql = "INSERT INTO bus_lines(name, start_point, end_point, chauffeur_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, busLine.getName());
            pstmt.setString(2, busLine.getStartPoint());
            pstmt.setString(3, busLine.getEndPoint());
            pstmt.setInt(4, busLine.getChauffeurId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateBusLine(BusLine busLine) {
        String sql = "UPDATE bus_lines SET name=?, start_point=?, end_point=?, chauffeur_id=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, busLine.getName());
            pstmt.setString(2, busLine.getStartPoint());
            pstmt.setString(3, busLine.getEndPoint());
            pstmt.setInt(4, busLine.getChauffeurId());
            pstmt.setInt(5, busLine.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBusLine(int id) {
        String sql = "DELETE FROM bus_lines WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create the bus_lines table if it doesn't exist
    public void createBusLinesTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS bus_lines (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "start_point VARCHAR(100) NOT NULL," +
                "end_point VARCHAR(100) NOT NULL," +
                "chauffeur_id INT," +
                "FOREIGN KEY (chauffeur_id) REFERENCES users(id)" +
                ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Bus lines table created or already exists");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}