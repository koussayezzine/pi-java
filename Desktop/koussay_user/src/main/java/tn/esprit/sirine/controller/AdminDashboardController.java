package tn.esprit.sirine.controller;

import tn.esprit.sirine.models.User;
import tn.esprit.sirine.services.UserService;
import tn.esprit.sirine.utils.CurrentSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<User> userTableView;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nomColumn;

    @FXML
    private TableColumn<User, String> prenomColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField searchField;

    private UserService userService;
    private User selectedUser;
    private ObservableList<User> masterData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the user service
        userService = new UserService();

        // Set welcome message with current user's name
        if (CurrentSession.getInstance().getUser() != null) {
            welcomeLabel.setText("Bienvenue, " + CurrentSession.getInstance().getUser().getNom() + "!");
        }

        // Initialize the role combo box
        roleComboBox.setItems(FXCollections.observableArrayList("admin", "user", "chauffeur"));

        // Set up the table columns
        if (userTableView != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
            prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

            // Add selection listener to populate form fields when a user is selected
            userTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedUser = newSelection;
                    populateFormFields(newSelection);
                }
            });

            // Load all users
            loadAllUsers();

            // Set up search functionality
            setupSearch();
        }
    }

    private void loadAllUsers() {
        try {
            // Get all users from the database
            List<User> users = userService.getAllUsers();

            // Update the master data
            masterData = FXCollections.observableArrayList(users);

            // If filtered data is already set up, update its source
            if (filteredData != null) {
                filteredData = new FilteredList<>(masterData, filteredData.getPredicate());
                sortedData = new SortedList<>(filteredData);
                sortedData.comparatorProperty().bind(userTableView.comparatorProperty());
                userTableView.setItems(sortedData);
            } else {
                // Otherwise, set the items directly
                userTableView.setItems(masterData);
            }
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Erreur lors du chargement des utilisateurs");
        }
    }

    private FilteredList<User> filteredData;
    private SortedList<User> sortedData;

    private void setupSearch() {
        // Initialize the filtered list if master data is available
        if (masterData == null) {
            masterData = FXCollections.observableArrayList();
        }

        // Create a filtered list wrapping the observable list
        filteredData = new FilteredList<>(masterData, p -> true);

        // Wrap the filtered list in a sorted list
        sortedData = new SortedList<>(filteredData);

        // Bind the sorted list comparator to the TableView comparator
        sortedData.comparatorProperty().bind(userTableView.comparatorProperty());

        // Add sorted (and filtered) data to the table
        userTableView.setItems(sortedData);

        // Add enter key event handler to search field
        searchField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                applyFilter();
            }
        });
    }

    @FXML
    private void handleSearch() {
        applyFilter();
    }

    private void applyFilter() {
        String searchText = searchField.getText();

        filteredData.setPredicate(user -> {
            // If search text is empty, display all users
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            // Compare search text with user properties (case insensitive)
            String lowerCaseFilter = searchText.toLowerCase();

            try {
                if (user.getNom() != null && user.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches name
                } else if (user.getPrenom() != null && user.getPrenom().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches surname
                } else if (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches email
                } else if (user.getRole() != null && user.getRole().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches role
                }
            } catch (Exception e) {
                // Handle any potential null pointer exceptions
                System.err.println("Error during filtering: " + e.getMessage());
            }
            return false; // No match found
        });

        // Update status label with search results
        int resultCount = sortedData.size();
        if (resultCount == 0 && !searchText.isEmpty()) {
            statusLabel.setText("Aucun résultat trouvé pour '" + searchText + "'");
        } else if (!searchText.isEmpty()) {
            statusLabel.setText(resultCount + " résultat(s) trouvé(s) pour '" + searchText + "'");
        } else {
            statusLabel.setText("");
        }
    }

    private void populateFormFields(User user) {
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        passwordField.setText(""); // For security, don't populate the password field
        roleComboBox.setValue(user.getRole());
    }

    @FXML
    private void handleModifierUser() {
        if (selectedUser == null) {
            statusLabel.setText("Veuillez sélectionner un utilisateur à modifier");
            return;
        }

        // Validate form fields
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || emailField.getText().isEmpty() ||
            roleComboBox.getValue() == null) {
            statusLabel.setText("Tous les champs sont obligatoires sauf le mot de passe");
            return;
        }

        try {
            // Update user object
            selectedUser.setNom(nomField.getText());
            selectedUser.setPrenom(prenomField.getText());
            selectedUser.setEmail(emailField.getText());

            // Only update password if a new one is provided
            if (!passwordField.getText().isEmpty()) {
                selectedUser.setMotDePasse(passwordField.getText());
                // Le mot de passe sera hashé par la méthode updateUser du service
            }

            selectedUser.setRole(roleComboBox.getValue());

            // Update user in database
            userService.updateUser(selectedUser);

            // Refresh table and clear form
            loadAllUsers();
            handleClearFields();
            statusLabel.setText("Utilisateur modifié avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de la modification de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Erreur lors de la modification de l'utilisateur");
        }
    }

    @FXML
    private void handleSupprimerUser() {
        if (selectedUser == null) {
            statusLabel.setText("Veuillez sélectionner un utilisateur à supprimer");
            return;
        }

        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'utilisateur");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer l'utilisateur " + selectedUser.getNom() + " " + selectedUser.getPrenom() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete user from database
            userService.deleteUser(selectedUser.getId());

            // Refresh table and clear form
            loadAllUsers();
            handleClearFields();
            statusLabel.setText("Utilisateur supprimé avec succès");
        }
    }

    @FXML
    private void handleAjouterUser() {
        // Validate form fields
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || emailField.getText().isEmpty() ||
            passwordField.getText().isEmpty() || roleComboBox.getValue() == null) {
            statusLabel.setText("Tous les champs sont obligatoires");
            return;
        }

        try {
            // Check if email already exists
            User existingUser = userService.findByEmail(emailField.getText());
            if (existingUser != null) {
                statusLabel.setText("Un utilisateur avec cet email existe déjà");
                return;
            }

            // Validate password strength
            String password = passwordField.getText();
            if (password.length() < 6) {
                statusLabel.setText("Le mot de passe doit contenir au moins 6 caractères");
                return;
            }

            // Create new user object
            User newUser = new User();
            newUser.setNom(nomField.getText());
            newUser.setPrenom(prenomField.getText());
            newUser.setEmail(emailField.getText());
            newUser.setMotDePasse(password); // Sera hashé par la méthode addUser du service
            newUser.setRole(roleComboBox.getValue());

            // Add user to database
            boolean success = userService.addUser(newUser);

            if (success) {
                // Refresh table and clear form
                loadAllUsers();
                handleClearFields();
                statusLabel.setText("Utilisateur ajouté avec succès");
            } else {
                statusLabel.setText("Erreur lors de l'ajout de l'utilisateur");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Erreur lors de l'ajout de l'utilisateur");
        }
    }

    @FXML
    private void handleClearFields() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);
        selectedUser = null;
        userTableView.getSelectionModel().clearSelection();
        statusLabel.setText("");
    }

    @FXML
    private void refreshUserTable() {
        loadAllUsers();
        searchField.clear();
        statusLabel.setText("");

        // Reset the filter to show all users
        if (filteredData != null) {
            filteredData.setPredicate(user -> true);
        }
    }
}
