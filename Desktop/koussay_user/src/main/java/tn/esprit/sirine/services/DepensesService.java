package tn.esprit.sirine.services;

import tn.esprit.sirine.models.Depenses;
import tn.esprit.sirine.utils.MaConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepensesService implements IService<Depenses> {

    private final Connection connection;

    public DepensesService() {
        this.connection = MaConnexion.getConnection();
    }

    @Override
    public void create(Depenses depense) throws SQLException {
        String sql = "INSERT INTO depenses (montant, date, categorie, statut, fournisseur, description, justificatif) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, depense.getMontant());

            if (depense.getDate() != null) {
                ps.setDate(2, depense.getDate());
            } else {
                ps.setNull(2, Types.DATE);
            }

            ps.setString(3, depense.getCategorie());
            ps.setString(4, depense.getStatut());
            ps.setString(5, depense.getFournisseur());
            ps.setString(6, depense.getDescription());
            ps.setString(7, depense.getJustificatif());

            ps.executeUpdate();
            System.out.println("Dépense ajoutée avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la dépense : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Depenses> readAll() throws SQLException {
        List<Depenses> list = new ArrayList<>();
        String req = "SELECT * FROM depenses";

        try (PreparedStatement ps = connection.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Depenses depense = new Depenses();
                depense.setIdDepense(rs.getInt("idDepense")); // ✅ id correct
                depense.setMontant(rs.getDouble("montant"));
                depense.setDate(rs.getDate("date"));
                depense.setDescription(rs.getString("description"));
                depense.setCategorie(rs.getString("categorie"));
                depense.setJustificatif(rs.getString("justificatif"));
                depense.setStatut(rs.getString("statut"));
                depense.setFournisseur(rs.getString("fournisseur"));
                list.add(depense);
            }
        }
        return list;
    }

    @Override
    public Depenses readById(int id) throws SQLException {
        String sql = "SELECT * FROM depenses WHERE idDepense = ?"; // ✅ corrigé avec le bon nom de colonne

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Depenses depense = new Depenses();
                    depense.setIdDepense(rs.getInt("idDepense")); // ✅ bien le bon id
                    depense.setMontant(rs.getDouble("montant"));
                    depense.setDate(rs.getDate("date"));
                    depense.setCategorie(rs.getString("categorie"));
                    depense.setStatut(rs.getString("statut"));
                    depense.setFournisseur(rs.getString("fournisseur"));
                    depense.setDescription(rs.getString("description"));
                    depense.setJustificatif(rs.getString("justificatif"));
                    return depense;
                }
            }
        }

        return null;
    }

    @Override
    public void update(Depenses depense) throws SQLException {
        String sql = "UPDATE depenses SET montant = ?, date = ?, categorie = ?, statut = ?, fournisseur = ?, " +
                "description = ?, justificatif = ? WHERE idDepense = ?"; // ✅ corrigé le nom de colonne

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, depense.getMontant());
            ps.setDate(2, depense.getDate());
            ps.setString(3, depense.getCategorie());
            ps.setString(4, depense.getStatut());
            ps.setString(5, depense.getFournisseur());
            ps.setString(6, depense.getDescription());
            ps.setString(7, depense.getJustificatif());
            ps.setInt(8, depense.getIdDepense()); // ✅ corrigé le getter
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int idDepense) throws SQLException {
        String sql = "DELETE FROM depenses WHERE idDepense = ?"; // ✅ corrigé le nom de colonne

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1,idDepense);
            ps.executeUpdate();
        }
    }
}
