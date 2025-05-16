package tn.esprit.sirine.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class AdminSignUpController {
    public VBox signupBox;
    @FXML private Label errorLabel;

    @FXML
    private void handleSignup() {
        // Admin registration has been disabled as per requirement
        errorLabel.setText("L'inscription des administrateurs a été désactivée.");
    }

    @FXML
    private void switchToLogin() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/sirine/login.fxml"));
        Stage stage = (Stage) errorLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    public void initialize() {
        // Display the error message immediately when the page loads
        errorLabel.setText("L'inscription des administrateurs a été désactivée.");

        FadeTransition fade = new FadeTransition(Duration.seconds(1), signupBox);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(1), signupBox);
        slide.setFromY(-30);
        slide.setToY(0);

        ParallelTransition animation = new ParallelTransition(fade, slide);
        animation.play();
    }
}
