package org.example.service;

import org.example.models.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements IReservationService {
    private Connection conn;

    public ReservationService(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Reservation> readAll() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM Reservation";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("idReservation"),
                        rs.getString("nomUtilisateur"),
                        rs.getString("email"),
                        rs.getString("lieuDepart"),
                        rs.getString("lieuArrive"),
                        rs.getString("statut")  // Retirer heureReservation
                );
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la lecture des réservations : " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    @Override
    public void add(Reservation reservation) {
        String SQL = "INSERT INTO Reservation (nomUtilisateur, email, lieuDepart, lieuArrive, statut) " +
                "VALUES (?, ?, ?, ?, ?)"; // Retirer heureReservation

        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setString(1, reservation.getNomUtilisateur());
            stmt.setString(2, reservation.getEmail());
            stmt.setString(3, reservation.getLieuDepart());
            stmt.setString(4, reservation.getLieuArrive());
            stmt.setString(5, reservation.getStatut());  // Retirer heureReservation
            stmt.executeUpdate();
            System.out.println("Réservation ajoutée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la réservation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Reservation reservation) {
        String SQL = "UPDATE Reservation SET nomUtilisateur = ?, email = ?, lieuDepart = ?, lieuArrive = ?, statut = ? " +
                "WHERE idReservation = ?";  // Retirer heureReservation

        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setString(1, reservation.getNomUtilisateur());
            stmt.setString(2, reservation.getEmail());
            stmt.setString(3, reservation.getLieuDepart());
            stmt.setString(4, reservation.getLieuArrive());
            stmt.setString(5, reservation.getStatut());  // Retirer heureReservation
            stmt.setInt(6, reservation.getIdReservation());
            stmt.executeUpdate();
            System.out.println("Réservation mise à jour avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la réservation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Reservation getByNom(String nom) {
        String sql = "SELECT * FROM reservation WHERE nomUtilisateur = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Reservation(
                        rs.getInt("idReservation"),    // Assure-toi du nom exact de la colonne ID
                        rs.getString("nomUtilisateur"),            // ← colonne réelle en base
                        rs.getString("email"),
                        rs.getString("lieuDepart"),
                        rs.getString("lieuArrive"),
                        rs.getString("statut")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void delete(Reservation reservation) {
        String SQL = "DELETE FROM Reservation WHERE idReservation = ?";

        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, reservation.getIdReservation());
            stmt.executeUpdate();
            System.out.println("Réservation supprimée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la réservation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Reservation> getAll() {
        List<Reservation> reservations = new ArrayList<>();
        String SQL = "SELECT * FROM Reservation";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("idReservation"),
                        rs.getString("nomUtilisateur"),
                        rs.getString("email"),
                        rs.getString("lieuDepart"),
                        rs.getString("lieuArrive"),
                        rs.getString("statut")  // Retirer heureReservation
                );
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des réservations : " + e.getMessage());
            e.printStackTrace();
        }
        return reservations;
    }

    @Override
    public Reservation getById(int id) {
        String SQL = "SELECT * FROM Reservation WHERE idReservation = ?";
        Reservation reservation = null;

        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                reservation = new Reservation(
                        rs.getInt("idReservation"),
                        rs.getString("nomUtilisateur"),
                        rs.getString("email"),
                        rs.getString("lieuDepart"),
                        rs.getString("lieuArrive"),
                        rs.getString("statut")  // Retirer heureReservation
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la réservation : " + e.getMessage());
            e.printStackTrace();
        }

        return reservation;
    }

    @Override
    public boolean confirmReservation(int id) {
        String SQL = "UPDATE Reservation SET statut = 'Confirmée' WHERE idReservation = ?";

        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la confirmation de la réservation : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean cancelReservation(int id) {
        String SQL = "UPDATE Reservation SET statut = 'Annulée' WHERE idReservation = ?";

        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'annulation de la réservation : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
