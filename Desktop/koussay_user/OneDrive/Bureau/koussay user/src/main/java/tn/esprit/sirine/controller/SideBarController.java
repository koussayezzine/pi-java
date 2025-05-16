package tn.esprit.sirine.controller;

import tn.esprit.sirine.HelloApplication;
import tn.esprit.sirine.utils.CurrentSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class SideBarController {
    @FXML
    public Label nameCurrentUser;
    @FXML
    private ToggleButton darkModeToggle;


    @FXML
    public void initialize() {
        if (CurrentSession.getInstance().getUser() != null) {
            nameCurrentUser.setText(CurrentSession.getInstance().getUser().getNom()); // ou getUsername()
        } else {
            nameCurrentUser.setText("Utilisateur inconnu");
        }

        // Default to light mode
        darkModeToggle.setSelected(false);
    }

    @FXML
    private void goToDashboard() throws IOException {
        switchScene("/tn/esprit/sirine/dashboard_user.fxml");
    }

    @FXML
    private void goToRevenue() throws IOException {
        switchScene("/tn/esprit/sirine/Dashboard.fxml");
    }

    @FXML
    private void goToNotifications() throws IOException {}

    @FXML
    private void goToAnalytics() throws IOException {}

    @FXML
    private void goToLikes() throws IOException {}

    @FXML
    private void goToWallets() throws IOException {}

    @FXML
    private void logout() throws IOException {
        switchScene("/tn/esprit/sirine/login.fxml");
    }

    @FXML
    private void toggleDarkMode() {
        boolean isDark = darkModeToggle.isSelected();
        Scene scene = darkModeToggle.getScene();

        // Clear existing stylesheets
        scene.getStylesheets().clear();

        // Apply the appropriate stylesheet
        String cssFile = isDark ? "/tn/esprit/sirine/dark-theme.css" : "/tn/esprit/sirine/light-theme.css";
        try {
            scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/components.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
        } catch (Exception e) {
            System.out.println("Could not load CSS: " + e.getMessage());
        }
    }

    private void switchScene(String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) darkModeToggle.getScene().getWindow();
        Scene scene = new Scene(root);

        // Apply CSS directly
        try {
            boolean isDark = darkModeToggle.isSelected();
            String cssFile = isDark ? "/tn/esprit/sirine/dark-theme.css" : "/tn/esprit/sirine/light-theme.css";
            scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/components.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
        } catch (Exception e) {
            System.out.println("Could not apply CSS: " + e.getMessage());
        }

        stage.setScene(scene);
    }


    @FXML
    public void openEditProfile(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/edit_profile.fxml"));
            Parent root = loader.load();

            EditProfileController controller = loader.getController();
            controller.setUser(CurrentSession.getInstance().getUser()); // ou comme tu stockes l'utilisateur

            // Use the helper method to create a properly styled stage
            Stage stage = HelloApplication.createStage();
            stage.setTitle("Edit Profile");
            Scene scene = new Scene(root);

            // Apply CSS directly
            try {
                boolean isDark = darkModeToggle.isSelected();
                String cssFile = isDark ? "/tn/esprit/sirine/dark-theme.css" : "/tn/esprit/sirine/light-theme.css";
                scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/components.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
            } catch (Exception e) {
                System.out.println("Could not apply CSS: " + e.getMessage());
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
