package tn.esprit.sirine.models;

import java.sql.Date;
import java.time.LocalDate;

public class Depenses {
    private int idDepense;  // Corrigé pour correspondre à la colonne SQL "idDepense"
    private double montant;
    private Date date;
    private String description;
    private String categorie;
    private String statut;
    private String fournisseur;
    private String justificatif;

    // Constructeur
    public Depenses() {}

    // Getters et Setters
    public int getIdDepense() {
        return idDepense;
    }

    public void setIdDepense(int idDepense) {
        this.idDepense = idDepense;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // Pour les opérations JavaFX avec LocalDate
    public LocalDate getLocalDate() {
        return date != null ? date.toLocalDate() : null;
    }

    public void setDateFromLocalDate(LocalDate localDate) {
        this.date = (localDate != null) ? Date.valueOf(localDate) : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    public String getJustificatif() {
        return justificatif;
    }

    public void setJustificatif(String justificatif) {
        this.justificatif = justificatif;
    }
}
