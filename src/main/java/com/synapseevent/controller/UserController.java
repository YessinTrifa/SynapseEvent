package com.synapseevent.controller;

import com.synapseevent.entities.User;
import com.synapseevent.entities.Role;
import com.synapseevent.entities.Entreprise;
import com.synapseevent.service.UserService;
import com.synapseevent.service.RoleService;
import com.synapseevent.service.EntrepriseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserController {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Long> idColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> prenomColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> entrepriseColumn;

    @FXML private TextField emailField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private ComboBox<Entreprise> entrepriseComboBox;

    private UserService userService = new UserService();
    private RoleService roleService = new RoleService();
    private EntrepriseService entrepriseService = new EntrepriseService();

    @FXML public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().getName()));
        entrepriseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnterprise().getNom()));

        roleComboBox.setItems(FXCollections.observableArrayList(roleService.getAll()));
        entrepriseComboBox.setItems(FXCollections.observableArrayList(entrepriseService.getAll()));

        loadData();
    }

    private void loadData() {
        try {
            userTable.setItems(FXCollections.observableArrayList(userService.readAll()));
        } catch (Exception e) {
            // Handle exception
        }
    }

    @FXML private void addUser() {
        String email = emailField.getText();
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        Role role = roleComboBox.getValue();
        Entreprise entreprise = entrepriseComboBox.getValue();
        if (email != null && !email.isEmpty() && nom != null && !nom.isEmpty() && prenom != null && !prenom.isEmpty() && role != null && entreprise != null) {
            User user = new User(email, nom, prenom, role, entreprise);
            try {
                userService.ajouter(user);
            } catch (Exception e) {
                // Handle exception
            }
            clearFields();
            loadData();
        }
    }

    @FXML private void updateUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setEmail(emailField.getText());
            selected.setNom(nomField.getText());
            selected.setPrenom(prenomField.getText());
            selected.setRole(roleComboBox.getValue());
            selected.setEnterprise(entrepriseComboBox.getValue());
            try {
                userService.modifier(selected);
            } catch (Exception e) {
                // Handle exception
            }
            loadData();
        }
    }

    @FXML private void deleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                userService.supprimer(selected);
            } catch (Exception e) {
                // Handle exception
            }
            loadData();
        }
    }

    private void clearFields() {
        emailField.clear();
        nomField.clear();
        prenomField.clear();
        roleComboBox.setValue(null);
        entrepriseComboBox.setValue(null);
    }
}