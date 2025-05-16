package tn.esprit.sirine.controller;

import tn.esprit.sirine.models.User;
import tn.esprit.sirine.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class UserController {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> prenomColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField mdpField;
    @FXML private TextField roleField;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadUsers();

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, selectedUser) -> {
            if (selectedUser != null) {
                nomField.setText(selectedUser.getNom());
                prenomField.setText(selectedUser.getPrenom());
                emailField.setText(selectedUser.getEmail());
                mdpField.setText(selectedUser.getMotDePasse());
                roleField.setText(selectedUser.getRole());
            }
        });
    }

    private void loadUsers() {
        List<User> users = userService.getAllUsers();
        userTable.getItems().setAll(users);
    }

    @FXML
    private void handleModifier() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            selectedUser.setNom(nomField.getText());
            selectedUser.setPrenom(prenomField.getText());
            selectedUser.setEmail(emailField.getText());
            selectedUser.setMotDePasse(mdpField.getText());
            selectedUser.setRole(roleField.getText());

            userService.updateUser(selectedUser);
            loadUsers();
        }
    }

    @FXML
    private void handleSupprimer() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            userService.deleteUser(selectedUser.getId());
            loadUsers();
        }
    }
}
