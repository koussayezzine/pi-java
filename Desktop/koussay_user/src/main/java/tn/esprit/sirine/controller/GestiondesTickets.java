package tn.esprit.sirine.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.sirine.models.EtatTicket;
import tn.esprit.sirine.services.TicketService;
import tn.esprit.sirine.utils.MaConnexion;
import tn.esprit.sirine.models.Ticket;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class GestiondesTickets {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private TableView<Ticket> ticketsTable;
    @FXML private TableColumn<Ticket, String> colNom;
    @FXML private TableColumn<Ticket, LocalDateTime> colDate;
    @FXML private TableColumn<Ticket, Double> colPrix;
    @FXML private TableColumn<Ticket, EtatTicket> colEtat;
    @FXML private TableColumn<Ticket, String> colCodeQR;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button displayButton;
    @FXML private Button btnRetour;
    @FXML private Label errorLabel;
    @FXML private Label noteLabel;
    @FXML private ImageView qrImageView;

    private TicketService ticketService;
    private ObservableList<Ticket> ticketsList;

    public GestiondesTickets() {
        Connection conn = MaConnexion.getConnection();
        ticketService = new TicketService(conn);
        ticketsList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        try {
            System.out.println("Initialisation de GestiondesTickets");
            // Configuration du TableView
            colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNomPassager()));
            colDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDateEmission()));
            colPrix.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrix()).asObject());
            colEtat.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getEtatTicket()));
            colCodeQR.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodeQR()));
            ticketsTable.setItems(ticketsList);
            // Afficher le QR code du ticket sélectionné
            ticketsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    displayQRCode(newSelection);
                } else {
                    qrImageView.setImage(null);
                }
            });
            loadAllTickets();
            System.out.println("TableView initialisé avec succès");
        } catch (Exception e) {
            errorLabel.setText("Erreur lors de l'initialisation du TableView : " + e.getMessage());
            System.err.println("Erreur dans initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAllTickets() {
        try {
            ticketsList.clear();
            List<Ticket> tickets = ticketService.getAll();
            System.out.println("Nombre de tickets chargés : " + tickets.size());
            ticketsList.addAll(tickets);
            noteLabel.setText("Tickets chargés : " + tickets.size());
            errorLabel.setText("");
        } catch (Exception e) {
            errorLabel.setText("Erreur lors du chargement des tickets : " + e.getMessage());
            System.err.println("Erreur dans loadAllTickets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addTicket() {
        try {
            System.out.println("Tentative de chargement de /AjouterTicket.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/AjouterTicket.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Ressource /tn/esprit/sirine/AjouterTicket.fxml introuvable");
            }
            System.out.println("Configuration du ControllerFactory...");
            Connection conn = MaConnexion.getConnection();
            if (conn == null) {
                throw new IllegalStateException("Connexion à la base de données nulle");
            }
            loader.setControllerFactory(clazz -> {
                if (clazz == AjouterTicket.class) {
                    return new AjouterTicket(conn);
                }
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Erreur lors de la création du contrôleur : " + e.getMessage(), e);
                }
            });
            System.out.println("Chargement du FXML...");
            Parent root = loader.load();
            System.out.println("Configuration de la fenêtre...");
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Ticket");
            stage.setOnHidden(event -> loadAllTickets());
            stage.show();
            System.out.println("Interface AjouterTicket ouverte.");
        } catch (Exception e) {
            String errorMessage = "Erreur lors du chargement de l'interface d'ajout : " + e.getMessage();
            errorLabel.setText(errorMessage);
            System.err.println(errorMessage);
            e.printStackTrace();
        }
    }

    @FXML
    private void editTicket() {
        try {
            Ticket selectedTicket = ticketsTable.getSelectionModel().getSelectedItem();
            if (selectedTicket == null) {
                errorLabel.setText("Veuillez sélectionner un ticket à modifier.");
                System.out.println("Aucun ticket sélectionné dans le TableView");
                return;
            }
            System.out.println("Ticket sélectionné : " + selectedTicket.toString());

            System.out.println("Tentative de chargement de /ModifierTicket.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/ModifierTicket.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Ressource /tn/esprit/sirine/ModifierTicket.fxml introuvable");
            }
            System.out.println("Configuration du ControllerFactory...");
            Connection conn = MaConnexion.getConnection();
            if (conn == null) {
                throw new IllegalStateException("Connexion à la base de données nulle");
            }
            loader.setControllerFactory(clazz -> {
                if (clazz == tn.esprit.sirine.controller.ModifierTicket.class) {
                    System.out.println("Création d'une instance de ModifierTicket");
                    return new tn.esprit.sirine.controller.ModifierTicket();
                }
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Erreur lors de la création du contrôleur : " + e.getMessage(), e);
                }
            });
            System.out.println("Chargement du FXML...");
            Parent root = loader.load();
            tn.esprit.sirine.controller.ModifierTicket controller = loader.getController();
            if (controller == null) {
                throw new IllegalStateException("Contrôleur ModifierTicket non trouvé");
            }
            System.out.println("Passage du ticket au contrôleur ModifierTicket");
            controller.setTicket(selectedTicket);
            System.out.println("Configuration de la fenêtre...");
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier un Ticket");
            stage.setOnHidden(event -> loadAllTickets());
            stage.show();
            System.out.println("Interface ModifierTicket ouverte.");
        } catch (Exception e) {
            String errorMessage = "Erreur lors du chargement de l'interface de modification : " + e.getMessage();
            errorLabel.setText(errorMessage);
            System.err.println(errorMessage);
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteTicket() {
        try {
            // Vérifier si un ticket est sélectionné
            Ticket selectedTicket = ticketsTable.getSelectionModel().getSelectedItem();
            if (selectedTicket == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aucun ticket sélectionné");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez sélectionner un ticket à supprimer.");
                alert.showAndWait();
                errorLabel.setText("Veuillez sélectionner un ticket à supprimer.");
                return;
            }
            System.out.println("Ticket sélectionné pour suppression : " + selectedTicket.toString());

            // Charger l'interface SupprimerTicket
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/SupprimerTicket.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Ressource /tn/esprit/sirine/SupprimerTicket.fxml introuvable");
            }
            Parent root = loader.load();

            // Accéder au contrôleur
            SupprimerTicket controller = loader.getController();
            if (controller == null) {
                throw new IllegalStateException("Contrôleur SupprimerTicket non trouvé");
            }

            // Passer le ticket sélectionné
            controller.setSelectedTicket(selectedTicket);

            // Configurer la fenêtre modale
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Supprimer un Ticket");
            stage.initModality(Modality.APPLICATION_MODAL); // Fenêtre modale
            stage.setResizable(false);
            stage.setOnHidden(event -> loadAllTickets()); // Rafraîchir après fermeture
            stage.show();
            System.out.println("Interface SupprimerTicket ouverte.");

        } catch (IOException e) {
            String errorMessage = "Erreur lors du chargement de l'interface de suppression : " + e.getMessage();
            errorLabel.setText(errorMessage);
            showErrorAlert("Erreur de chargement", errorMessage);
            e.printStackTrace();
        } catch (Exception e) {
            String errorMessage = "Erreur inattendue : " + e.getMessage();
            errorLabel.setText(errorMessage);
            showErrorAlert("Erreur", errorMessage);
            e.printStackTrace();
        }
    }

    @FXML
    private void displayTicket() {
        try {
            // Vérifier si un ticket est sélectionné
            Ticket selectedTicket = ticketsTable.getSelectionModel().getSelectedItem();
            if (selectedTicket == null) {
                errorLabel.setText("Veuillez sélectionner un ticket à afficher.");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aucun ticket sélectionné");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez sélectionner un ticket à afficher.");
                alert.showAndWait();
                System.out.println("Aucun ticket sélectionné pour affichage.");
                return;
            }
            System.out.println("Ticket sélectionné pour affichage : " + selectedTicket.toString());

            // Charger l'interface AfficherTicket
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/AfficherTicket.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Ressource /tn/esprit/sirine/AfficherTicket.fxml introuvable");
            }
            Parent root = loader.load();

            // Accéder au contrôleur
            AfficherTicket controller = loader.getController();
            if (controller == null) {
                throw new IllegalStateException("Contrôleur AfficherTicket non trouvé");
            }

            // Passer le ticket sélectionné
            controller.setSelectedTicket(selectedTicket);

            // Configurer la fenêtre modale
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Afficher Détails du Ticket");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();
            System.out.println("Interface AfficherTicket ouverte pour ticket #" + selectedTicket.getIdTicket());
        } catch (IOException e) {
            String errorMessage = "Erreur lors du chargement de l'interface d'affichage : " + e.getMessage();
            errorLabel.setText(errorMessage);
            showErrorAlert("Erreur de chargement", errorMessage);
            System.err.println("Erreur dans displayTicket: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            String errorMessage = "Erreur inattendue : " + e.getMessage();
            errorLabel.setText(errorMessage);
            showErrorAlert("Erreur", errorMessage);
            System.err.println("Erreur dans displayTicket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void searchTickets() {
        String nomPassager = searchField.getText().trim();
        if (nomPassager.isEmpty()) {
            errorLabel.setText("Veuillez entrer un nom de passager.");
            noteLabel.setText("");
            return;
        }
        try {
            ticketsList.clear();
            List<Ticket> tickets = ticketService.getAllByNomPassager(nomPassager);
            System.out.println("Recherche: " + tickets.size() + " tickets trouvés pour " + nomPassager);
            if (tickets.isEmpty()) {
                errorLabel.setText("Aucun ticket trouvé pour ce passager.");
                noteLabel.setText("");
            } else {
                ticketsList.addAll(tickets);
                noteLabel.setText("Tickets trouvés : " + tickets.size());
                errorLabel.setText("");
            }
        } catch (Exception e) {
            errorLabel.setText("Erreur lors de la recherche des tickets : " + e.getMessage());
            System.err.println("Erreur dans searchTickets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayQRCode(Ticket ticket) {
        try {
            String qrCodeFilePath = ticketService.generateQRCode(ticket.getIdTicket());
            if (qrCodeFilePath != null) {
                File qrFile = new File(qrCodeFilePath);
                if (qrFile.exists() && qrFile.isFile()) {
                    Image qrImage = new Image("file:" + qrFile.getAbsolutePath(), true);
                    qrImageView.setImage(qrImage);
                    System.out.println("QR Code affiché pour ticket #" + ticket.getIdTicket());
                } else {
                    qrImageView.setImage(null);
                    errorLabel.setText("Erreur: Fichier QR introuvable (" + qrCodeFilePath + ").");
                }
            } else {
                qrImageView.setImage(null);
                errorLabel.setText("Erreur lors de la génération du QR Code.");
            }
        } catch (Exception e) {
            qrImageView.setImage(null);
            errorLabel.setText("Erreur lors de l'affichage du QR Code : " + e.getMessage());
            System.err.println("Erreur dans displayQRCode: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void retour() {
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.close();
    }
}