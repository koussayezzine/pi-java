package org.example.models;

import java.time.LocalDateTime;

public class Ticket {
    private int idTicket;
    private LocalDateTime dateEmission;
    private String codeQR;
    private double prix;
    private EtatTicket etatTicket;
    private String nomPassager;

    public Ticket(LocalDateTime dateEmission, String codeQR, double prix, EtatTicket etatTicket, String nomPassager) {
        this.dateEmission = dateEmission;
        this.codeQR = codeQR;
        this.prix = prix;
        this.etatTicket = etatTicket;
        this.nomPassager = nomPassager;
    }

    // Getters and setters
    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public LocalDateTime getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(LocalDateTime dateEmission) {
        this.dateEmission = dateEmission;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public EtatTicket getEtatTicket() {
        return etatTicket;
    }

    public void setEtatTicket(EtatTicket etatTicket) {
        this.etatTicket = etatTicket;
    }

    public String getNomPassager() {
        return nomPassager;
    }

    public void setNomPassager(String nomPassager) {
        this.nomPassager = nomPassager;
    }
}