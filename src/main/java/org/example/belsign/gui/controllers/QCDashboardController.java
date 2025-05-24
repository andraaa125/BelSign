package org.example.belsign.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.belsign.be.Order;
import org.example.belsign.be.Product;
import org.example.belsign.bll.OrderManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QCDashboardController {

    @FXML private Label userName;
    @FXML private Button approvalButton;
    @FXML private Button logoutButton;
    @FXML private ComboBox<String> searchComboBox;
    @FXML private FlowPane pendingPane;
    @FXML private FlowPane productPane;
    @FXML private Button selectedOrderButton;
    @FXML private Button selectedButton = null;

    private Order selectedOrder;
    private final OrderManager orderManager = new OrderManager();
    private final Map<String, VBox> orderVBoxMap = new HashMap<>();
    private final ObservableList<String> searchResults = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        approvalButton.setDisable(false);
        searchComboBox.setEditable(true);
        searchComboBox.setPromptText("Search Order Number...");
        searchComboBox.setVisibleRowCount(8);

        searchComboBox.setItems(searchResults);
        searchComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 1) {
                updateSearchResults(newVal);
            } else {
                searchResults.clear();
            }
        });

        try {
            List<Order> orders = orderManager.getAllOrders();
            for (Order order : orders) {
                if ("Done".equals(order.getStatus())) {
                    Button orderButton = new Button(order.getOrderId());
                    orderButton.setUserData(order);
                    orderButton.setStyle(getStyleForStatus(order.getStatus()));
                    orderButton.setOnAction(e -> handleOrderClick(order));
                    pendingPane.getChildren().add(orderButton);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("An error occurred while loading orders.");
        }
    }

    private void handleOrderClick(Order order) {
        if (!"Done".equals(order.getStatus())) return;

        if (selectedOrderButton != null && selectedOrderButton.getUserData().equals(order)) {
            selectedOrderButton.setStyle(getStyleForStatus(order.getStatus()));
            selectedOrderButton = null;
            selectedOrder = null;
            VBox boxToRemove = orderVBoxMap.remove(order.getOrderId());
            if (boxToRemove != null) productPane.getChildren().remove(boxToRemove);
            approvalButton.setDisable(true);
            return;
        }

        if (selectedOrderButton != null) {
            selectedOrderButton.setStyle(getStyleForStatus(((Order) selectedOrderButton.getUserData()).getStatus()));
            VBox boxToRemove = orderVBoxMap.remove(((Order) selectedOrderButton.getUserData()).getOrderId());
            if (boxToRemove != null) productPane.getChildren().remove(boxToRemove);
        }

        Button orderButton = findOrderButtonByOrderId(order.getOrderId());
        orderButton.setStyle("-fx-background-color: #9d9d9d; -fx-text-fill: white; -fx-padding: 15; -fx-font-size: 15px; -fx-border-width: 2; -fx-border-radius: 5; -fx-border-color: #9d9d9d;");
        selectedOrderButton = orderButton;
        selectedOrder = order;

        displayProductsForOrder(order);
        approvalButton.setDisable(true);
    }

    private Button findOrderButtonByOrderId(String orderId) {
        for (Node node : pendingPane.getChildren()) {
            if (node instanceof Button button) {
                Order order = (Order) button.getUserData();
                if (order != null && order.getOrderId().equals(orderId)) return button;
            }
        }
        return null;
    }

    private void displayProductsForOrder(Order order) {
        if (orderVBoxMap.containsKey(order.getOrderId())) return;

        try {
            if (order.getProducts() == null || order.getProducts().isEmpty()) {
                order.setProducts(orderManager.getProductsForOrder(order.getOrderId()));
            }

            VBox orderBox = new VBox(15);
            orderBox.setUserData(order);
            orderBox.setStyle("-fx-padding: 15; -fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 5;");

            Label operatorLabel = new Label("Operator: " + order.getOperatorFirstName() + " " + order.getOperatorLastName());
            operatorLabel.setStyle("-fx-font-style: italic; -fx-font-size: 14px; -fx-text-fill: #333;");
            orderBox.getChildren().add(operatorLabel);

            Button orderBtn = new Button("OrderID: " + order.getOrderId());
            orderBtn.setPrefWidth(200);
            orderBtn.setPrefHeight(40);
            orderBtn.setStyle("-fx-font-size: 16px; -fx-background-color: transparent; -fx-border-color: #333535; -fx-padding: 15; -fx-font-weight: bold;");
            orderBtn.setUserData(order);
            orderBtn.setOnAction(e -> handleSelection(orderBtn, orderBox, order));
            orderBox.getChildren().add(orderBtn);

            for (Product product : order.getProducts()) {
                Button productButton = new Button(product.getName());
                productButton.setPrefWidth(200);
                productButton.setPrefHeight(40);
                productButton.setStyle("-fx-border-color: #333535; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px;");
                productButton.setUserData(product);

                productButton.setOnMouseClicked(e -> {
                    handleSelection(productButton, orderBox, order);
                    if (e.getClickCount() == 2) {
                        openApprovalView(order);
                    }
                });

                orderBox.getChildren().add(productButton);
            }

            productPane.getChildren().add(orderBox);
            orderVBoxMap.put(order.getOrderId(), orderBox);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading products for order.");
        }
    }

    private void handleSelection(Button clickedButton, VBox orderBox, Order order) {
        // Skip if the clicked button is the OrderID button
        if (clickedButton.getText().startsWith("OrderID:")) {
            return; // Do nothing for OrderID button
        }

        // If clicking the same button again → toggle off
        if (clickedButton.equals(selectedButton)) {
            resetButtonStyles(orderBox);
            selectedButton = null;
            selectedOrder = null;
            return;
        }

        // Unhighlight all buttons in this order box
        resetButtonStyles(orderBox);

        // Highlight clicked button
        clickedButton.setStyle("-fx-border-color: #9d9d9d; -fx-background-color: #9d9d9d; -fx-text-fill: white; -fx-padding: 15px; -fx-font-size: 16px;");

        selectedButton = clickedButton;
        selectedOrder = order;
    }

    private void resetButtonStyles(VBox orderBox) {
        for (Node node : orderBox.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if (button.getText().startsWith("OrderID:")) {
                    button.setStyle("-fx-border-color: #9d9d9d; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px; -fx-font-weight: bold;");
                } else {
                    button.setStyle("-fx-border-color: #9d9d9d; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px;");
                }
            }
        }
    }


    private String getDefaultProductStyle(Button button) {
        if (button.getText().startsWith("OrderID:")) {
            return "-fx-border-color: #333535; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px; -fx-font-weight: bold;";
        } else {
            return "-fx-border-color: #333535; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px;";
        }
    }

    private void openApprovalView(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/belsign/ApprovalView.fxml"));
            Parent root = loader.load();
            ApprovalController controller = loader.getController();
            controller.setOrder(order);

            // Pass selected button
            if (selectedButton != null && !selectedButton.getText().startsWith("OrderID:")) {
                controller.setProductButton(selectedButton);
            }

            Stage stage = new Stage();
            stage.setTitle("Approval Panel");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Failed to open Approval View.");
        }
    }


    private void updateSearchResults(String query) {
        searchResults.clear();
        try {
            ObservableList<String> results = FXCollections.observableArrayList(orderManager.searchOrders(query));
            searchResults.addAll(results);
            searchComboBox.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStyleForStatus(String status) {
        return "-fx-padding: 15; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-color: transparent; -fx-border-color: #575757; -fx-font-size: 15px;";
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/belsign/LoginView.fxml"));
        Parent root = loader.load();
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(root));
        loginStage.show();
    }

    public void setUserName(String firstName, String lastName) {
        userName.setText("Welcome, " + firstName + " " + lastName + "!");
    }

    @FXML
    private void onOpenApproval(ActionEvent event) {
        if (selectedOrder == null) {
            showAlert("Please select an order first.");
            return;
        }
        openApprovalView(selectedOrder);
    }
}
