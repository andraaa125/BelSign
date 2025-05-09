package org.example.belsign.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.belsign.bll.OrderManager;

import java.io.IOException;

public class QCDashboardController {
    @FXML
    private Label userName;
    @FXML
    private Button logoutButton;
    @FXML
    private ComboBox<String> searchComboBox;

    private OrderManager orderManager = new OrderManager();

    private final ObservableList<String> searchResults = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        searchComboBox.setEditable(true); // Allow text input
        searchComboBox.setPromptText("Search Order Number...");
        searchComboBox.setVisibleRowCount(8);

        searchComboBox.setItems(searchResults);

        // Add listener for input changes
        searchComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 1) { //search with 1 digit
                updateSearchResults(newVal);
            } else {
                searchResults.clear();
            }
        });
    }

    private void updateSearchResults(String query) {
        searchResults.clear();
        try {
            ObservableList<String> results = searchOrders(query);
            searchResults.addAll(results);
            searchComboBox.show(); // Show dropdown as you type
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSearchKeyReleased() {
        String query = searchComboBox.getEditor().getText().trim();
        if (!query.isEmpty()) {
            searchOrders(query);
        }
    }

    private ObservableList<String> searchOrders(String query) {
        try {
            return FXCollections.observableArrayList(orderManager.searchOrders(query));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("An error occurred while fetching orders.");
            return FXCollections.observableArrayList(); // Return empty list to avoid null
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onClickLogout(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) logoutButton.getScene().getWindow();
        currentStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/belsign/LoginView.fxml"));
        Parent root = fxmlLoader.load();
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(root));

        loginStage.show();
    }

    public void setUserName(String firstName, String lastName) {
        userName.setText("Welcome, " + firstName + " " + lastName + "!");
    }
}
