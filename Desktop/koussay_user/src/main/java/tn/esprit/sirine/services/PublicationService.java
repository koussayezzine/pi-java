package tn.esprit.sirine.services;


import tn.esprit.sirine.models.Publication;
import tn.esprit.sirine.models.Commentaire;
import tn.esprit.sirine.utils.MaConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PublicationService {
    public Commentaire getCommentaireById(int id) {
        String sql = "SELECT * FROM commentaire WHERE id = ?";
        try (Connection conn = MaConnexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Commentaire c = new Commentaire();
                c.setId(rs.getInt("id"));
                c.setPublicationId(rs.getInt("publication_id"));
                c.setTexte(rs.getString("texte"));
                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void ajouterPublication(Publication publication) {
        String sql = "INSERT INTO publication (texte) VALUES (?)";
        try (Connection conn = MaConnexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, publication.getTexte());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                publication.getClass().getDeclaredField("id").setAccessible(true);
                publication.getClass().getDeclaredField("id").set(publication, rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ajouterCommentaire(Commentaire commentaire) {
        String sql = "INSERT INTO commentaire (publication_id, texte) VALUES (?, ?)";

        try (Connection conn = MaConnexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, commentaire.getPublicationId());
            stmt.setString(2, commentaire.getTexte());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    commentaire.setId(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<Publication> getAllPublications() {
        List<Publication> publications = new ArrayList<>();
        String sql = "SELECT * FROM publication ORDER BY date_creation DESC";

        try (Connection conn = MaConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Publication p = new Publication();
                p.setId(rs.getInt("id"));
                p.setTexte(rs.getString("texte"));
                p.setDateCreation(rs.getTimestamp("date_creation"));
                publications.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return publications;
    }

    public List<Commentaire> getCommentairesParPublication(int publicationId) {
        List<Commentaire> commentaires = new ArrayList<>();
        String sql = "SELECT * FROM commentaire WHERE publication_id = ? ORDER BY date_commentaire ASC";

        try (Connection conn = MaConnexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, publicationId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Commentaire c = new Commentaire();
                c.setId(rs.getInt("id"));
                c.setTexte(rs.getString("texte"));
                c.setPublicationId(rs.getInt("publication_id"));

                commentaires.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commentaires;
    }
    public void supprimerPublication(int publicationId) {
        String sql = "DELETE FROM publication WHERE id = ?";
        try (Connection conn = MaConnexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, publicationId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void supprimerCommentaire(int commentaireId) {
        String sql = "DELETE FROM commentaire WHERE id = ?";
        try (Connection conn = MaConnexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentaireId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
