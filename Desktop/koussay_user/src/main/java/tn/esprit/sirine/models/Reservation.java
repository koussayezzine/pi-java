package tn.esprit.sirine.models;

public class Reservation {
    private int idReservation;
    private String nomUtilisateur;
    private String email;
    private String lieuDepart;
    private String lieuArrive;
    private String statut;

    public Reservation() {
        // Constructeur vide
    }

    public Reservation(int idReservation, String nomUtilisateur, String email, String lieuDepart, String lieuArrive, String statut) {
        this.idReservation = idReservation;
        this.nomUtilisateur = nomUtilisateur;
        this.email = email;
        this.lieuDepart = lieuDepart;
        this.lieuArrive = lieuArrive;
        this.statut = statut;
    }

    public Reservation(String nomUtilisateur, String email, String lieuDepart, String lieuArrive, String statut) {
        this.nomUtilisateur = nomUtilisateur;
        this.email = email;
        this.lieuDepart = lieuDepart;
        this.lieuArrive = lieuArrive;
        this.statut = statut;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLieuDepart() {
        return lieuDepart;
    }

    public void setLieuDepart(String lieuDepart) {
        this.lieuDepart = lieuDepart;
    }

    public String getLieuArrive() {
        return lieuArrive;
    }

    public void setLieuArrive(String lieuArrive) {
        this.lieuArrive = lieuArrive;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
