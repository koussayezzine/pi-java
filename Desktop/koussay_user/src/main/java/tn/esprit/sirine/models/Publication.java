package tn.esprit.sirine.models;


import java.sql.Timestamp;

public class Publication {
    public int id;
    public String texte;

    public Publication(String texte) {
        this.texte = texte;
    }

    public Publication(int id, String texte) {
        this.id = id;
        this.texte = texte;
    }

    public Publication() {
        // Empty constructor
    }

    public int getId() { return id; }
    public String getTexte() { return texte; }

    public void setId(int id) {
        this.id=id;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public void setDateCreation(Timestamp dateCreation) {
    }
}
