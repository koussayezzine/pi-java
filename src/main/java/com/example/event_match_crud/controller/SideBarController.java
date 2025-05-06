package com.example.event_match_crud.controller;

import com.example.event_match_crud.utlis.CurrentSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.awt.event.ActionEvent;
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
    }

    @FXML
    private void goToDashboard() throws IOException {
        switchScene("/com/example/event_match_crud/dashboard_user.fxml");
    }

    @FXML
    private void goToRevenue() throws IOException {
        // redirige vers Revenue.fxml (à créer)
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
        switchScene("/com/example/event_match_crud/login.fxml");
    }

    @FXML
    private void toggleDarkMode() {
        boolean isDark = darkModeToggle.isSelected();
        String stylesheet = isDark ? "dark-theme.css" : "light-theme.css";
        Stage stage = (Stage) darkModeToggle.getScene().getWindow();
        Scene scene = stage.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/com/example/event_match_crud/" + stylesheet).toExternalForm());
    }

    private void switchScene(String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) darkModeToggle.getScene().getWindow();
        stage.setScene(new Scene(root));
    }


    @FXML
    public void openEditProfile(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/event_match_crud/edit_profile.fxml"));
            Parent root = loader.load();

            EditProfileController controller = loader.getController();
            controller.setUser(CurrentSession.getInstance().getUser()); // ou comme tu stockes l'utilisateur

            Stage stage = new Stage();
            stage.setTitle("Edit Profile");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
