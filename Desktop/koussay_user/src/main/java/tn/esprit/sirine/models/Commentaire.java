package tn.esprit.sirine.models;


import java.sql.Timestamp;

public class Commentaire {
    public int id;
    public int publicationId;
    public String texte;

    public Commentaire(int publicationId, String texte) {
        this.publicationId = publicationId;
        this.texte = texte;
    }
    public void commentaire(int id){
        this.id=id;
    }

    public Commentaire() {

    }

    public int getPublicationId() { return publicationId; }
    public String getTexte() { return texte; }

    public void setId(int id) {
        this.id=id;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public void setPublicationId(int publicationId) {
        this.publicationId = publicationId;
    }

    public void setDateCommentaire(Timestamp dateCommentaire) {
    }

    public int getId() {
        return id;
    }
}
