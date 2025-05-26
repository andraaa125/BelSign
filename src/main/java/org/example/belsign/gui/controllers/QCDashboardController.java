package org.example.belsign.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
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
    @FXML
    private Label userName;
    @FXML
    private Button btnGenerateReport;
    @FXML
    private Button logoutButton;
    @FXML
    private ComboBox<String> searchComboBox;
    @FXML
    private FlowPane pendingPane;
    @FXML
    private FlowPane productPane;
    @FXML
    private Button selectedOrderButton;
    @FXML
    private Order selectedOrder;
    @FXML
    private Button selectedButton = null;


    private final Map<String, VBox> orderVBoxMap = new HashMap<>();

    private final OrderManager orderManager = new OrderManager();

    private final ObservableList<String> searchResults = FXCollections.observableArrayList();



    @FXML
    private void onOpenApproval(ActionEvent event) {
        if (selectedOrder == null) {
            showAlert("Please select an order first.");
            return;
        }

        try {
            // Optionally re-fetch from DB to ensure latest data
            Order fullOrder = orderManager.getOrderById(selectedOrder.getOrderId());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/belsign/ApprovalView.fxml"));
            Parent root = loader.load();

            ApprovalController controller = loader.getController();
            controller.setOrder(fullOrder); // Pass the order with the correct ID

            Stage stage = new Stage();
            stage.setTitle("Approval Panel");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to open report preview.");
        }
    }

    @FXML
    private void initialize() {
        btnGenerateReport.setDisable(true);
        searchComboBox.setEditable(true); // Allow text input
        searchComboBox.setPromptText("Search Order Number...");
        searchComboBox.setVisibleRowCount(8);

        searchComboBox.setItems(searchResults);
        searchComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 1) { //search with 1 digit
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
            if (boxToRemove != null) {
                productPane.getChildren().remove(boxToRemove);
            }
            btnGenerateReport.setDisable(true);
            System.out.println("Order unselected: " + order.getOrderId());
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
        btnGenerateReport.setDisable(true);
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

    private void displayProductsForOrder(Order order) {
        if (orderVBoxMap.containsKey(order.getOrderId())) return;

        try {
            if (order.getProducts() == null || order.getProducts().isEmpty()) {
                order.setProducts(orderManager.getProductsForOrder(order.getOrderId()));
            }

            VBox orderBox = new VBox(15);
            orderBox.setUserData(order);
            orderBox.setStyle("-fx-padding: 15; -fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 5;");

            double buttonWidth = 200;
            double buttonHeight = 40;

            Label operatorLabel = new Label("Operator: " + order.getOperatorFirstName() + " " + order.getOperatorLastName());
            operatorLabel.setStyle("-fx-font-style: italic; -fx-font-size: 14px; -fx-text-fill: #333;");
            orderBox.getChildren().add(operatorLabel);

            Button orderButton = new Button("OrderID: " + order.getOrderId());
            orderButton.setPrefWidth(200);
            orderButton.setPrefHeight(40);
            orderButton.setStyle("-fx-font-size: 16px; -fx-background-color: transparent; -fx-border-color: #333535; -fx-padding: 15; -fx-font-weight: bold;");
            orderButton.setUserData(order);
            orderButton.setOnAction(e -> handleSelection(orderButton, orderBox, order));
            orderBox.getChildren().add(orderButton);

            orderButton.setOnAction(e -> handleSelection(orderButton, orderBox, order));
            orderBox.getChildren().add(orderButton);

            for (Product product : order.getProducts()) {
                //Button productButton = new Button(product.getProductName());
                Button productButton = new Button(product.getProduct());
                productButton.setPrefWidth(200);
                productButton.setPrefHeight(40);
                productButton.setStyle("-fx-border-color: #333535; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px;");
                productButton.setUserData(product);
                productButton.setPrefWidth(buttonWidth);
                productButton.setPrefHeight(buttonHeight);

                // Apply status color:
                String baseStyle = "-fx-padding: 15px; -fx-font-size: 16px; -fx-border-color: #333535;";
                switch (product.getStatus()) {
                    case "Approved" -> productButton.setStyle(baseStyle + " -fx-background-color: #338d71; -fx-text-fill: white;");
                    case "Disapproved" -> productButton.setStyle(baseStyle + " -fx-background-color: #880000; -fx-text-fill: white;");
                    default -> productButton.setStyle(baseStyle + " -fx-background-color: transparent;");
                }

                productButton.setOnAction(e -> handleSelection(productButton, orderBox, order));
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
        if (clickedButton.getText().startsWith("OrderID:")) {
            return;
        }

        if (clickedButton.equals(selectedButton)) {
            resetButtonStyles(orderBox);
            selectedButton = null;
            selectedOrder = null;
            btnGenerateReport.setDisable(true);
            return;
        }

        resetButtonStyles(orderBox);

        clickedButton.setStyle("-fx-border-color: #9d9d9d; -fx-background-color: #9d9d9d; -fx-text-fill: white; -fx-padding: 15px; -fx-font-size: 16px;");
        selectedButton = clickedButton;
        selectedOrder = order;
    }

    private void resetButtonStyles(VBox orderBox) {
        for (Node node : orderBox.getChildren()) {
            if (node instanceof Button button) {
                if (button.getText().startsWith("OrderID:")) {
                    button.setStyle("-fx-border-color: #9d9d9d; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px; -fx-font-weight: bold;");
                } else {
                    button.setStyle("-fx-border-color: #9d9d9d; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px;");
                }
            }
        }

        btnGenerateReport.setDisable(false);
    }
    private String getDefaultProductStyle(Button button) {
        if (button.getText().startsWith("OrderID:")) {
            return "-fx-border-color: #333535; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px; -fx-font-weight: bold;";
        } else {
            return "-fx-border-color: #333535; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px;";
        }
    }

    private ObservableList<String> searchOrders(String query) {
        try {
            return FXCollections.observableArrayList(orderManager.searchOrders(query));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("An error occurred while fetching orders.");
            return FXCollections.observableArrayList();
        }
    }

    private void openApprovalView(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/belsign/ApprovalView.fxml"));
            Parent root = loader.load();
            ApprovalController controller = loader.getController();
            controller.setOrder(order);

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

    public void updateProductColor(Product product) {
        VBox box = orderVBoxMap.get(product.getOrderId());
        if (box == null) return;

        for (Node node : box.getChildren()) {
            if (node instanceof Button btn && btn.getUserData() instanceof Product p) {
                if (p.getProductName().equals(product.getProductName())) {
                    String baseStyle = "-fx-padding: 15px; -fx-font-size: 16px; -fx-border-color: #333535;";
                    switch (product.getStatus()) {
                        case "Approved" -> btn.setStyle(baseStyle + " -fx-background-color: #338d71; -fx-text-fill: white;");
                        case "Disapproved" -> btn.setStyle(baseStyle + " -fx-background-color: #880000; -fx-text-fill: white;");
                        default -> btn.setStyle(baseStyle + " -fx-background-color: transparent;");
                    }
                }
            }
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
        userName.setStyle("-fx-font-size: 20");
    }

    public void onClickGenerateReport(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/belsign/ReportPreview.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Report");
        stage.setScene(new Scene(root));

        stage.show();
    }
}
