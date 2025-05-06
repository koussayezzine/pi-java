package com.example.event_match_crud;

import com.example.event_match_crud.utlis.MaConnexion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class    HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Test de connexion à la base de données
        Connection conn = MaConnexion.getConnection();
        if (conn != null) {
            System.out.println("Connexion réussie dans HelloApplication !");
        } else {
            System.out.println("Échec de la connexion.");
        }




        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/event_match_crud/signup.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Gestion des événements");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}