package tn.esprit.sirine.services;

import tn.esprit.sirine.models.Revenu;
import tn.esprit.sirine.utils.MaConnexion;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class RevenuService implements IService<Revenu> {
    private final Connection connection;

    public RevenuService() {
        this.connection = MaConnexion.getConnection();
    }

    @Override
    public void create(Revenu revenu) throws SQLException {
        String sql = "INSERT INTO revenu (montant, date, source, typeRevenu, statut, client, factureDetails) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, revenu.getMontant());

            if (revenu.getDate() != null) {
                ps.setDate(2, Date.valueOf(revenu.getDate()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            ps.setString(3, revenu.getSource());
            ps.setString(4, revenu.getTypeRevenu());
            ps.setString(5, revenu.getStatut());
            ps.setString(6, revenu.getClient());
            ps.setString(7, revenu.getFactureDetails());

            ps.executeUpdate();
            System.out.println("Revenu ajouté avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du revenu : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Revenu> readAll() throws SQLException {
        List<Revenu> revenusList = new ArrayList<>();
        String query = "SELECT * FROM revenu";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Revenu revenu = new Revenu();
                revenu.setIdRevenu(rs.getInt("idRevenu"));
                revenu.setMontant(rs.getDouble("montant"));
                revenu.setDate(rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null);
                revenu.setSource(rs.getString("source"));
                revenu.setTypeRevenu(rs.getString("typeRevenu"));
                revenu.setStatut(rs.getString("statut"));
                revenu.setClient(rs.getString("client"));
                revenu.setFactureDetails(rs.getString("factureDetails"));
                revenusList.add(revenu);
            }
        }
        return revenusList;
    }

    @Override
    public Revenu readById(int id) throws SQLException {
        String query = "SELECT * FROM revenu WHERE idRevenu = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Revenu revenu = new Revenu();
                revenu.setIdRevenu(rs.getInt("idRevenu"));
                revenu.setMontant(rs.getDouble("montant"));
                revenu.setDate(rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null);
                revenu.setSource(rs.getString("source"));
                revenu.setTypeRevenu(rs.getString("typeRevenu"));
                revenu.setStatut(rs.getString("statut"));
                revenu.setClient(rs.getString("client"));
                revenu.setFactureDetails(rs.getString("factureDetails"));
                return revenu;
            }
        }
        return null;
    }

    @Override
    public void update(Revenu revenu) throws SQLException {
        String sql = "UPDATE revenu SET montant = ?, date = ?, source = ?, typeRevenu = ?, statut = ?, client = ?, factureDetails = ? WHERE idRevenu = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, revenu.getMontant());
            ps.setDate(2, Date.valueOf(revenu.getDate()));
            ps.setString(3, revenu.getSource());
            ps.setString(4, revenu.getTypeRevenu());
            ps.setString(5, revenu.getStatut());
            ps.setString(6, revenu.getClient());
            ps.setString(7, revenu.getFactureDetails());
            ps.setInt(8, revenu.getIdRevenu());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM revenu WHERE idRevenu = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
