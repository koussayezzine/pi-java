
package tn.esprit.sirine.models;

import java.sql.Date;
import java.time.LocalDate;

public class Revenu {
    private int idRevenu;
    private double montant;
    private LocalDate date;
    private String source;
    private String typeRevenu;
    private String statut;
    private String client;
    private String factureDetails;
    public Revenu() {
        // Constructeur par défaut requis pour certaines opérations (ex: instanciation dynamique)
    }


    // Constructeur avec 7 arguments
    public Revenu(double montant, LocalDate date, String source, String typeRevenu,
                  String statut, String client, String factureDetails) {
        this.montant = montant;
        this.date = date;
        this.source = source;
        this.typeRevenu = typeRevenu;
        this.statut = statut;
        this.client = client;
        this.factureDetails = factureDetails;
    }

    // Getters et Setters
    public int getIdRevenu() {
        return idRevenu;
    }

    public void setIdRevenu(int idRevenu) {
        this.idRevenu = idRevenu;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTypeRevenu() {
        return typeRevenu;
    }

    public void setTypeRevenu(String typeRevenu) {
        this.typeRevenu = typeRevenu;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getFactureDetails() {
        return factureDetails;
    }

    public void setFactureDetails(String factureDetails) {
        this.factureDetails = factureDetails;
    }
}

