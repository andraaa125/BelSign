package org.example.belsign.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.belsign.be.User;
import org.example.belsign.dal.db.UserDAODB;
import org.example.belsign.util.PasswordUtil;

import java.io.IOException;

public class AdminDashboardController {
    @FXML
    private Label userName;
    @FXML
    private Button logoutBtn;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;
    @FXML
    private ComboBox<String> role;
    @FXML
    private Label lblMessage;

    private Node dashboardMainView;

    @FXML
    public void initialize() {
        role.getItems().addAll("Admin", "QC", "Operator");
        dashboardMainView = mainBorderPane.getCenter();
    }

    public void onClickOrder(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/belsign/OrderListView.fxml"));
            Node orderListView = loader.load();

            OrderListViewController orderListController = loader.getController();
            orderListController.setMainBorderPane(mainBorderPane);
            orderListController.setMainView(dashboardMainView);

            mainBorderPane.setCenter(orderListView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickUser(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/belsign/UserListView.fxml"));
            Node userListView = loader.load();

            UserListViewController userListController = loader.getController();

            userListController.setMainBorderPane(mainBorderPane);
            userListController.setMainView(dashboardMainView);

            mainBorderPane.setCenter(userListView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickLogout(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
        currentStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/belsign/LoginView.fxml"));
        Parent root = fxmlLoader.load();
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(root));

        loginStage.show();
    }

    public void onClickSave(ActionEvent actionEvent) {
        try {
            lblMessage.setText("");

            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String username = txtUsername.getText().trim();
            String hashedPassword = PasswordUtil.hashPassword(txtPassword.getText());
            String selectedRole = role.getSelectionModel().getSelectedItem();

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()
                    || hashedPassword.isEmpty() || selectedRole == null || selectedRole.isEmpty()) {
                lblMessage.setStyle("-fx-text-fill:  #880000;");
                lblMessage.setText("Please fill out all fields");
                return;
            }

            User newUser = new User(firstName, lastName, username, hashedPassword, selectedRole);

            UserDAODB userDAO = new UserDAODB();

            if (userDAO.usernameExists(username)) {
                lblMessage.setStyle("-fx-text-fill:  #880000;");
                lblMessage.setText("Username already exists!");
                return;
            }

            userDAO.insertUser(newUser);

            txtFirstName.clear();
            txtLastName.clear();
            txtUsername.clear();
            txtPassword.clear();
            role.getSelectionModel().clearSelection();


            lblMessage.setStyle("-fx-text-fill: #338d71;");
            lblMessage.setText("User saved successfully!");

        } catch (Exception e) {
            e.printStackTrace();

            lblMessage.setStyle("-fx-text-fill: #880000;");
            lblMessage.setText("Error: " + e.getMessage());

            System.out.println("User saved successfully!");
        }
    }

    public void setUserName(String firstName, String lastName) {
        userName.setText("Welcome, " + firstName + " " + lastName + "!");
        userName.setStyle("-fx-font-size: 16");
    }

    public void onClickCancel(ActionEvent actionEvent) {
        txtFirstName.clear();
        txtLastName.clear();
        txtUsername.clear();
        txtPassword.clear();
        role.getSelectionModel().clearSelection();

        lblMessage.setText("");

    }
}