package org.example.belsign.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.belsign.be.User;
import org.example.belsign.bll.UserManager;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class LoginController {
    @FXML
    private PasswordField loginPassword;
    @FXML
    private TextField loginUsername;
    @FXML
    private Button loginBtn;
    @FXML
    private TextField visiblePassword;
    @FXML
    private FontIcon eyeIcon;
    @FXML
    private Label lblMessage;

    private boolean passwordVisible = false;

    private UserManager userManager = new UserManager();


    public void onLoginBtnClick(ActionEvent actionEvent) throws IOException {
        String username = loginUsername.getText().trim();
        String password = passwordVisible ? visiblePassword.getText() : loginPassword.getText();

        try {
            User loggedInUser = userManager.authenticate(username, password);
            if (loggedInUser != null) {
                loadDashboard(loggedInUser);
            } else {
                showAlert("Invalid username or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("An error occurred during login.");
        }
    }

    private void showAlert(String s) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText(s);
            alert.showAndWait();
    }

    private void loadDashboard(User user) throws IOException {
        Parent root = null;
        try {
            FXMLLoader fxmlLoader;
            String role = user.getRole();

            switch (role) {
                case "Operator":
                    fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/belsign/OperatorDashboard.fxml"));
                    root = fxmlLoader.load();
                    OperatorDashboardController opController = fxmlLoader.getController();
                    opController.setUserName(user.getFirstName(), user.getLastName());
                    break;

                case "QC":
                    fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/belsign/QCDashboard.fxml"));
                    root = fxmlLoader.load();
                    QCDashboardController qcController = fxmlLoader.getController();
                    qcController.setUserName(user.getFirstName(), user.getLastName());
                    break;

                default:
                    showAlert("Unknown role " + role);
                    return;
            }

            Stage stage = new Stage();
            stage.setTitle(user.getRole() + "Dashboard");
            stage.setScene(new Scene(root));
            stage.show();

            // Close login window
            Stage loginStage = (Stage) loginUsername.getScene().getWindow();
            loginStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("An unexpected error occurred.");
        }
    }


    public void onPasswordVisibility(ActionEvent actionEvent) {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            // Show plain text field
            visiblePassword.setText(loginPassword.getText());
            visiblePassword.setVisible(true);
            visiblePassword.setManaged(true);
            loginPassword.setVisible(false);
            loginPassword.setManaged(false);
            eyeIcon.setIconLiteral("bi-eye-slash");
        } else {
            // Hide plain text field, show PasswordField again
            loginPassword.setText(visiblePassword.getText());
            loginPassword.setVisible(true);
            loginPassword.setManaged(true);
            visiblePassword.setVisible(false);
            visiblePassword.setManaged(false);
            // Change icon back to "eye"
            eyeIcon.setIconLiteral("bi-eye");
        }
    }

    public void onClickForgotPassword(ActionEvent actionEvent) {
        lblMessage.setText("Please contact the IT department!");
    }
}