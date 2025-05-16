package tn.esprit.sirine;

import tn.esprit.sirine.utils.MaConnexion;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {

    // Application constants
    public static final String APP_NAME = "Transport Management System";
    public static final String APP_VERSION = "1.0.0";

    @Override
    public void start(Stage stage) throws IOException {
        // Test de connexion à la base de données
        Connection conn = MaConnexion.getConnection();
        if (conn != null) {
            System.out.println("Connexion réussie dans HelloApplication !");
        } else {
            System.out.println("Échec de la connexion.");
        }

        // Set application icon - commented out until icon is available
        /*
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/tn/esprit/sirine/images/app_icon.png"))));
        } catch (Exception e) {
            System.out.println("Could not load application icon: " + e.getMessage());
        }
        */

        // Configure the stage for a desktop-like appearance
        stage.setTitle(APP_NAME);
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        // Load the login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/login.fxml"));
        Scene scene = new Scene(loader.load());

        // Apply CSS directly
        try {
            scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/login.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Could not apply CSS: " + e.getMessage());
            e.printStackTrace();
        }

        stage.setScene(scene);
        stage.show();

        // Set up application close handler
        Platform.setImplicitExit(true);
        stage.setOnCloseRequest(event -> Platform.exit());
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * Creates a new stage with desktop application styling
     * @return A configured Stage
     */
    public static Stage createStage() {
        Stage stage = new Stage();
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setTitle(APP_NAME);
        return stage;
    }
}
