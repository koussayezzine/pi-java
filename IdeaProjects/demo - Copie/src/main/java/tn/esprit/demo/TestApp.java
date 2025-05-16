package tn.esprit.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Créer une interface simple sans FXML
            Label label = new Label("Test d'application JavaFX");
            label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            
            Button button = new Button("Cliquez-moi");
            button.setOnAction(e -> System.out.println("Bouton cliqué!"));
            
            VBox root = new VBox(20, label, button);
            root.setStyle("-fx-padding: 20px; -fx-alignment: center;");
            
            Scene scene = new Scene(root, 400, 300);
            
            primaryStage.setTitle("Test JavaFX");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("Fenêtre de test affichée avec succès.");
        } catch (Exception e) {
            System.err.println("Erreur dans TestApp: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}