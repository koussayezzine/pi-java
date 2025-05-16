package tn.esprit.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.demo.util.Maconnexion;

import java.io.IOException;

public class  HelloApplication extends Application {
    private static boolean isFirstLaunch = true;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            System.out.println("Démarrage de l'application...");
            
            if (isFirstLaunch) {
                Maconnexion maconnexion = Maconnexion.getInstance();
                System.out.println("Connexion établie avec succès.");
                isFirstLaunch = false;
            }

            // Charger le dashboard complet
            System.out.println("Chargement du fichier FXML Dashboard...");
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/tn/esprit/demo/Dashboard.fxml"));
            System.out.println("Création de la scène...");
            Scene scene = new Scene(fxmlLoader.load());
            
            // Ajouter le CSS
            System.out.println("Chargement du CSS...");
            String cssPath = getClass().getResource("/tn/esprit/demo/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            System.out.println("Configuration de la fenêtre...");
            stage.setTitle("Gestion des Transports");
            stage.setScene(scene);
            
            // Définir une taille minimale
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            
            // Ajouter un écouteur pour redimensionner les composants de carte
            stage.widthProperty().addListener((obs, oldVal, newVal) -> {
                resizeMapComponents();
            });
            stage.heightProperty().addListener((obs, oldVal, newVal) -> {
                resizeMapComponents();
            });
            
            System.out.println("Affichage de la fenêtre...");
            stage.show();
            System.out.println("Fenêtre affichée avec succès.");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation : " + e.getMessage());
            e.printStackTrace();
            
            // En cas d'erreur, essayer de charger une interface simplifiée
            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/tn/esprit/demo/AfficherBus.fxml"));
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/tn/esprit/demo/style.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.setProperty("javafx.verbose", "true");
        launch(args);
    }

    // Méthode pour redimensionner les composants de carte
    private void resizeMapComponents() {
        // Cette méthode sera appelée quand la fenêtre change de taille
        // Vous pouvez accéder au contrôleur actuel et ajuster la taille de la carte
    }
}
