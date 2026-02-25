package com.synapseevent.controller;

import com.synapseevent.entities.Entreprise;
import com.synapseevent.entities.Role;
import com.synapseevent.entities.User;
import com.synapseevent.service.EntrepriseService;
import com.synapseevent.service.RoleService;
import com.synapseevent.service.UserService;
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

    private final UserService userService = new UserService();
    private final RoleService roleService = new RoleService();
    private final EntrepriseService entrepriseService = new EntrepriseService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        roleColumn.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            String roleName = (u != null && u.getRole() != null) ? u.getRole().getName() : "";
            return new SimpleStringProperty(roleName);
        });

        entrepriseColumn.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            String entName = (u != null && u.getEnterprise() != null) ? u.getEnterprise().getNom() : "";
            return new SimpleStringProperty(entName);
        });

        try {
            roleComboBox.setItems(FXCollections.observableArrayList(roleService.getAll()));
        } catch (Exception e) {
            roleComboBox.setItems(FXCollections.observableArrayList());
        }

        try {
            entrepriseComboBox.setItems(FXCollections.observableArrayList(entrepriseService.getAll()));
        } catch (Exception e) {
            entrepriseComboBox.setItems(FXCollections.observableArrayList());
        }

        loadData();
    }

    private void loadData() {
        try {
            userTable.setItems(FXCollections.observableArrayList(userService.readAll()));
        } catch (Exception e) {
            userTable.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void selectUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        emailField.setText(selected.getEmail());
        nomField.setText(selected.getNom());
        prenomField.setText(selected.getPrenom());

        if (selected.getRoleId() != null) {
            try {
                Role r = roleService.findbyId(selected.getRoleId());
                roleComboBox.setValue(r);
                selected.setRole(r);
            } catch (Exception e) {
                roleComboBox.setValue(null);
            }
        } else {
            roleComboBox.setValue(null);
        }

        if (selected.getEnterpriseId() != null) {
            try {
                Entreprise ent = entrepriseService.findbyId(selected.getEnterpriseId());
                entrepriseComboBox.setValue(ent);
                selected.setEnterprise(ent);
            } catch (Exception e) {
                entrepriseComboBox.setValue(null);
            }
        } else {
            entrepriseComboBox.setValue(null);
        }
    }

    @FXML
    private void addUser() {
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String nom = nomField.getText() != null ? nomField.getText().trim() : "";
        String prenom = prenomField.getText() != null ? prenomField.getText().trim() : "";

        Role role = roleComboBox.getValue();
        Entreprise entreprise = entrepriseComboBox.getValue();

        if (email.isEmpty() || nom.isEmpty() || prenom.isEmpty() || role == null || entreprise == null) return;

        User user = new User(email, null, nom, prenom, null, null, null, role.getId(), entreprise.getId());
        user.setRole(role);
        user.setEnterprise(entreprise);

        try {
            userService.ajouter(user);
        } catch (Exception e) {
        }

        clearFields();
        loadData();
    }

    @FXML
    private void updateUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Role role = roleComboBox.getValue();
        Entreprise entreprise = entrepriseComboBox.getValue();
        if (role == null || entreprise == null) return;

        selected.setEmail(emailField.getText());
        selected.setNom(nomField.getText());
        selected.setPrenom(prenomField.getText());

        selected.setRoleId(role.getId());
        selected.setEnterpriseId(entreprise.getId());
        selected.setRole(role);
        selected.setEnterprise(entreprise);

        try {
            userService.modifier(selected);
        } catch (Exception e) {
        }

        loadData();
    }

    @FXML
    private void deleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            userService.supprimer(selected);
        } catch (Exception e) {
        }

        loadData();
    }

    private void clearFields() {
        emailField.clear();
        nomField.clear();
        prenomField.clear();
        roleComboBox.setValue(null);
        entrepriseComboBox.setValue(null);
    }
}