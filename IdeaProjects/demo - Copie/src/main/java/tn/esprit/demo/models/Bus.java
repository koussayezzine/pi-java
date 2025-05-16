package tn.esprit.demo.models;

public class Bus {
    private int idBus;
    private String modele;
    private int capacite;
    private String plaqueImmat;
    private int idItineraire;
    private String type;
    private double tarif;
    private String status;

    public Bus() {
    }

    public Bus(int idBus, String modele, int capacite, String plaqueImmat, int idItineraire, String type, double tarif) {
        this.idBus = idBus;
        this.modele = modele;
        this.capacite = capacite;
        this.plaqueImmat = plaqueImmat;
        this.idItineraire = idItineraire;
        this.type = type;
        this.tarif = tarif;
    }

    public Bus(String modele, int capacite, String plaqueImmat, int idItineraire, String type, double tarif) {
        this.modele = modele;
        this.capacite = capacite;
        this.plaqueImmat = plaqueImmat;
        this.idItineraire = idItineraire;
        this.type = type;
        this.tarif = tarif;
    }
    
    // Nouveau constructeur avec le paramètre status
    public Bus(int idBus, String modele, int capacite, String plaqueImmat, int idItineraire, String type, double tarif, String status) {
        this.idBus = idBus;
        this.modele = modele;
        this.capacite = capacite;
        this.plaqueImmat = plaqueImmat;
        this.idItineraire = idItineraire;
        this.type = type;
        this.tarif = tarif;
        this.status = status;
    }

    // Getters and Setters
    public int getIdBus() {
        return idBus;
    }

    public void setIdBus(int idBus) {
        this.idBus = idBus;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getPlaqueImmat() {
        return plaqueImmat;
    }

    public void setPlaqueImmat(String plaqueImmat) {
        this.plaqueImmat = plaqueImmat;
    }

    public int getIdItineraire() {
        return idItineraire;
    }

    public void setIdItineraire(int idItineraire) {
        this.idItineraire = idItineraire;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getTarif() {
        return tarif;
    }

    public void setTarif(double tarif) {
        this.tarif = tarif;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "idBus=" + idBus +
                ", modele='" + modele + '\'' +
                ", capacite=" + capacite +
                ", plaqueImmat='" + plaqueImmat + '\'' +
                ", idItineraire=" + idItineraire +
                ", type='" + type + '\'' +
                ", tarif=" + tarif +
                ", status='" + status + '\'' +
                '}';
    }
}
