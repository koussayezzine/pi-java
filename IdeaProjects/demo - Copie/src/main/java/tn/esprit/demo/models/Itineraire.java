package tn.esprit.demo.models;

public class Itineraire {
    private int idItin;
    private String pointDepart;
    private String pointArrivee;
    private String description;
    private double distance;
    private int duree;
    private String coordDepart;
    private String coordArrivee;
    private String arrets;
    private String dureeEstimee;
    
    // Constructeur par défaut
    public Itineraire() {
    }
    
    // Constructeur avec tous les champs sauf ID
    public Itineraire(String pointDepart, String pointArrivee, String description, 
                     double distance, int duree, String coordDepart, String coordArrivee, String arrets) {
        this.pointDepart = pointDepart;
        this.pointArrivee = pointArrivee;
        this.description = description;
        this.distance = distance;
        this.duree = duree;
        this.coordDepart = coordDepart;
        this.coordArrivee = coordArrivee;
        this.arrets = arrets;
    }
    
    // Constructeur avec tous les champs incluant ID
    public Itineraire(int idItin, String pointDepart, String pointArrivee, String description, 
                     double distance, int duree, String coordDepart, String coordArrivee, String arrets) {
        this.idItin = idItin;
        this.pointDepart = pointDepart;
        this.pointArrivee = pointArrivee;
        this.description = description;
        this.distance = distance;
        this.duree = duree;
        this.coordDepart = coordDepart;
        this.coordArrivee = coordArrivee;
        this.arrets = arrets;
    }
    
    // Getters et Setters
    public int getIdItin() {
        return idItin;
    }
    
    public void setIdItin(int idItin) {
        this.idItin = idItin;
    }
    
    public String getPointDepart() {
        return pointDepart;
    }
    
    public void setPointDepart(String pointDepart) {
        this.pointDepart = pointDepart;
    }
    
    public String getPointArrivee() {
        return pointArrivee;
    }
    
    public void setPointArrivee(String pointArrivee) {
        this.pointArrivee = pointArrivee;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    public int getDuree() {
        return duree;
    }
    
    public void setDuree(int duree) {
        this.duree = duree;
    }
    
    public String getCoordDepart() {
        return coordDepart;
    }
    
    public void setCoordDepart(String coordDepart) {
        this.coordDepart = coordDepart;
    }
    
    public String getCoordArrivee() {
        return coordArrivee;
    }
    
    public void setCoordArrivee(String coordArrivee) {
        this.coordArrivee = coordArrivee;
    }
    
    public String getArrets() {
        return arrets;
    }
    
    public void setArrets(String arrets) {
        this.arrets = arrets;
    }
    
    public String getDureeEstimee() {
        return dureeEstimee;
    }
    
    public void setDureeEstimee(String dureeEstimee) {
        this.dureeEstimee = dureeEstimee;
    }
    
    // Méthodes d'alias pour maintenir la compatibilité avec le code existant
    public String getVilleDepart() {
        return this.pointDepart;
    }
    
    public void setVilleDepart(String villeDepart) {
        this.pointDepart = villeDepart;
    }
    
    public String getVilleArrivee() {
        return this.pointArrivee;
    }
    
    public void setVilleArrivee(String villeArrivee) {
        this.pointArrivee = villeArrivee;
    }
    
    @Override
    public String toString() {
        return "Itineraire{" +
                "idItin=" + idItin +
                ", pointDepart='" + pointDepart + '\'' +
                ", pointArrivee='" + pointArrivee + '\'' +
                ", description='" + description + '\'' +
                ", distance=" + distance +
                ", duree=" + duree +
                ", coordDepart='" + coordDepart + '\'' +
                ", coordArrivee='" + coordArrivee + '\'' +
                ", arrets='" + arrets + '\'' +
                ", dureeEstimee='" + dureeEstimee + '\'' +
                '}';
    }
}
