package tn.esprit.sirine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import tn.esprit.sirine.models.User;
import tn.esprit.sirine.services.UserService;

public class EditProfileController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
        usernameField.setText(user.getNom());
        emailField.setText(user.getEmail());
    }

    @FXML
    private void handleSave() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        currentUser.setNom(username);
        currentUser.setEmail(email);
        if (!password.isEmpty()) {
            currentUser.setMotDePasse(password); // Hash if necessary
        }

        new UserService().updateUser(currentUser);

        showAlert("Success", "Profile updated successfully");
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
