package tn.esprit.sirine.controller;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.sirine.models.Commentaire;
import tn.esprit.sirine.models.Publication;
import tn.esprit.sirine.services.PublicationService;
import tn.esprit.sirine.utils.LanguageDetectionUtil;

import java.io.IOException;
import java.util.List;

// import static java.io.IO.print; // This import is incorrect

public class HelloController {

    @FXML
    private TextArea publicationField;
    @FXML
    private VBox publicationList;
    @FXML
    private Label statusMessage;
    @FXML
    private TextField searchField;

    private final PublicationService service = new PublicationService();

    @FXML
    public void initialize() {
        // Afficher un message de bienvenue
        statusMessage.setText("Bienvenue dans votre réseau social!");
        statusMessage.getStyleClass().setAll("status-info");

        // Charger les publications existantes
        chargerPublications();

        // Ajouter un listener pour effacer le message de statut quand l'utilisateur commence à écrire
        publicationField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                statusMessage.setText("");
            }
        });
    }

    @FXML
    public void publier() {
        String texte = publicationField.getText().trim();
        if (texte.isEmpty()) {
            statusMessage.setText("Le champ de publication est vide.");
            statusMessage.getStyleClass().setAll("status-error");
            return;
        }

        Publication p = new Publication(texte);
        service.ajouterPublication(p);
        publicationField.clear();

        // Afficher un message de succès
        statusMessage.setText("Publication ajoutée avec succès!");
        statusMessage.getStyleClass().setAll("status-success");

        // Afficher la nouvelle publication
        afficherPublication(p);

        // Effacer le message après 3 secondes
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> statusMessage.setText(""));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void chargerPublications() {
        publicationList.getChildren().clear();
        List<Publication> publications = service.getAllPublications();
        for (Publication p : publications) {
            afficherPublication(p);
        }
    }
    private void afficherPublication(Publication p) {
        VBox pubBox = new VBox(10);
        pubBox.setPadding(new Insets(15));
        pubBox.getStyleClass().add("publication-box");

        // Publication text with better styling
        String textePublication = (p.getTexte() != null) ? p.getTexte() : "[Contenu vide]"; // Fallback text

        // Use TextFlow instead of Label for better text rendering
        javafx.scene.text.Text pubText = new javafx.scene.text.Text(textePublication);
        pubText.getStyleClass().add("publication-text");
        pubText.setWrappingWidth(500); // Set a specific wrapping width

        // Create a TextFlow to contain the text
        javafx.scene.text.TextFlow textFlow = new javafx.scene.text.TextFlow(pubText);
        textFlow.setMaxWidth(Double.MAX_VALUE);
        textFlow.setMinHeight(30);
        textFlow.setPadding(new Insets(5));
        textFlow.getStyleClass().add("text-flow-container");

        // 🔍 Détection de la langue via API
        String langue = LanguageDetectionUtil.detectLanguage(p.getTexte());
        Label langueLabel = new Label("Langue détectée : " + langue);
        langueLabel.getStyleClass().add("language-label");

        // Styled delete button
        Button deletePubBtn = new Button("Supprimer");
        deletePubBtn.getStyleClass().add("delete-button");
        deletePubBtn.setOnAction(e -> {
            service.supprimerPublication(p.getId());
            publicationList.getChildren().remove(pubBox);
        });

        // Create a VBox for the publication text to ensure it's displayed properly
        VBox textContainer = new VBox(textFlow);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textContainer, javafx.scene.layout.Priority.ALWAYS);

        HBox header = new HBox(10, textContainer, deletePubBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(5, 0, 10, 0));

        // Separator
        Separator separator = new Separator();

        // Zone de commentaire avec titre
        Label commentTitle = new Label("Commentaires");
        commentTitle.getStyleClass().add("comments-title");

        // Create a border for the comments section to make it more visible
        VBox commentSection = new VBox(5);
        commentSection.getStyleClass().add("comment-section");
        commentSection.setPadding(new Insets(10));

        // Add the title to the comment section
        commentSection.getChildren().add(commentTitle);

        // Create the comments box
        VBox commentairesBox = new VBox(8);
        commentairesBox.setPadding(new Insets(10, 5, 10, 5));
        commentairesBox.getStyleClass().add("comments-box");

        // Get comments for this publication
        List<Commentaire> commentaires = service.getCommentairesParPublication(p.getId());


        if (commentaires.isEmpty()) {
            Label emptyLabel = new Label("Aucun commentaire pour l'instant");
            emptyLabel.getStyleClass().add("empty-comments");
            commentairesBox.getChildren().add(emptyLabel);
        } else {
            for (Commentaire c : commentaires) {
                addCommentToBox(c, commentairesBox);
            }
        }

        // Add the comments box to the comment section
        commentSection.getChildren().add(commentairesBox);

        // Zone d'ajout de commentaire
        TextField newCommentField = new TextField();
        newCommentField.setPromptText("Ajouter un commentaire...");
        newCommentField.getStyleClass().add("comment-field");
        HBox.setHgrow(newCommentField, javafx.scene.layout.Priority.ALWAYS);

        Button ajouterCommentBtn = new Button("Commenter");
        ajouterCommentBtn.getStyleClass().add("comment-button");
        ajouterCommentBtn.setOnAction(e -> {
            String texte = newCommentField.getText().trim();
            if (!texte.isEmpty()) {
                Commentaire c = new Commentaire(p.getId(), texte);
                service.ajouterCommentaire(c);

                // Supprimer le message "Aucun commentaire" s'il existe
                commentairesBox.getChildren().removeIf(node ->
                    node instanceof Label && ((Label) node).getText().equals("Aucun commentaire pour l'instant"));

                // Ajouter le nouveau commentaire
                addCommentToBox(c, commentairesBox);
                newCommentField.clear();

                // Afficher un message de confirmation
                statusMessage.setText("Commentaire ajouté avec succès!");
                statusMessage.getStyleClass().setAll("status-success");

                // Effacer le message après 2 secondes
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> statusMessage.setText(""));
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        });

        HBox commentInput = new HBox(10, newCommentField, ajouterCommentBtn);
        commentInput.setAlignment(Pos.CENTER_LEFT);
        commentInput.setPadding(new Insets(5, 0, 0, 0));

        // Add the comment input to the comment section
        commentSection.getChildren().add(commentInput);

        pubBox.getChildren().addAll(
            header,
            langueLabel,
            separator,
            commentSection
        );
        publicationList.getChildren().add(0, pubBox); // ajout en haut
    }

    /**
     * Helper method to add a comment to the comments box
     */
    private void addCommentToBox(Commentaire c, VBox commentairesBox) {
        HBox ligne = new HBox(8);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.getStyleClass().add("comment-line");

        // Create a TextFlow for better text rendering
        javafx.scene.text.Text commentText = new javafx.scene.text.Text(c.getTexte());
        commentText.getStyleClass().add("comment-text");

        javafx.scene.text.TextFlow commentFlow = new javafx.scene.text.TextFlow(commentText);
        commentFlow.setMaxWidth(Double.MAX_VALUE);
        commentFlow.setPadding(new Insets(5));
        HBox.setHgrow(commentFlow, javafx.scene.layout.Priority.ALWAYS);

        Button deleteCommentBtn = new Button("×");
        deleteCommentBtn.getStyleClass().add("comment-delete");
        deleteCommentBtn.setOnAction(e -> {
            service.supprimerCommentaire(c.getId());
            commentairesBox.getChildren().remove(ligne);

            // Si c'était le dernier commentaire, afficher le message "Aucun commentaire"
            if (commentairesBox.getChildren().isEmpty()) {
                Label emptyLabel = new Label("Aucun commentaire pour l'instant");
                emptyLabel.getStyleClass().add("empty-comments");
                commentairesBox.getChildren().add(emptyLabel);
            }
        });

        ligne.getChildren().addAll(commentFlow, deleteCommentBtn);
        commentairesBox.getChildren().add(ligne);
    }

    @FXML
    public void rechercherPublication() {
        String motCle = searchField.getText().trim().toLowerCase();
        publicationList.getChildren().clear();

        // Afficher un message de statut pour la recherche
        if (!motCle.isEmpty()) {
            statusMessage.setText("Recherche en cours pour : '" + motCle + "'");
            statusMessage.getStyleClass().setAll("status-info");
        } else {
            statusMessage.setText("");
        }

        if (motCle.isEmpty()) {
            chargerPublications(); // recharge tout si champ vide
            return;
        }

        List<Publication> publications = service.getAllPublications();
        int resultCount = 0;

        for (Publication p : publications) {
            if (p.getTexte().toLowerCase().contains(motCle)) {
                afficherPublication(p);
                resultCount++;
            }
        }

        // Mettre à jour le message de statut avec le nombre de résultats
        if (resultCount == 0) {
            statusMessage.setText("Aucun résultat trouvé pour : '" + motCle + "'");
            statusMessage.getStyleClass().setAll("status-error");
        } else {
            statusMessage.setText(resultCount + " résultat(s) trouvé(s) pour : '" + motCle + "'");
            statusMessage.getStyleClass().setAll("status-success");
        }
    }

    @FXML
    public void handleRetour() {
        try {
            // Retourner à la page précédente (Dashboard principal)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/Dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) publicationField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            System.out.println("Retour à la page précédente");
        } catch (IOException e) {
            System.err.println("Erreur lors du retour à la page précédente: " + e.getMessage());
            e.printStackTrace();

            // Afficher un message d'erreur à l'utilisateur
            statusMessage.setText("Erreur lors du retour à la page précédente");
            statusMessage.getStyleClass().setAll("status-error");
        }
    }
}
