package tn.esprit.sirine.controller;

import tn.esprit.sirine.controller.EditProfileController;
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
    private void goToReservations() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/GestionReservation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Réservations");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de GestionReservation.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToTickets() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/GestionTicket.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Tickets");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de GestionTicket.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToDashboardW() throws IOException {
        try {
            switchScene("/tn/esprit/sirine/DashboardW.fxml");
            System.out.println("Navigation vers DashboardW.fxml");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de DashboardW.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToPublication() throws IOException {
        try {
            switchScene("/tn/esprit/sirine/hello-viewP.fxml");
            System.out.println("Navigation vers hello-viewP.fxml");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de hello-viewP.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() throws IOException {
        switchScene("/tn/esprit/sirine/login.fxml");
    }

    @FXML
    private void toggleDarkMode() {
        boolean isDark = darkModeToggle.isSelected();
        String stylesheet = isDark ? "dark-theme.css" : "light-theme.css";
        Stage stage = (Stage) darkModeToggle.getScene().getWindow();
        Scene scene = stage.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/" + stylesheet).toExternalForm());
    }

    private void switchScene(String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) darkModeToggle.getScene().getWindow();
        stage.setScene(new Scene(root));
    }


    @FXML
    public void openEditProfile(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/edit_profile.fxml"));
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
