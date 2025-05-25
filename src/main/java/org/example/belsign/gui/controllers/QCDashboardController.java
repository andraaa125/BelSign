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

    private OrderManager orderManager = new OrderManager();

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
            if (boxToRemove != null) {
                productPane.getChildren().remove(boxToRemove);
            }
        }

        Button orderButton = findOrderButtonByOrderId(order.getOrderId());
        orderButton.setStyle("-fx-background-color: #9d9d9d; -fx-text-fill: white; " +
                "-fx-padding: 15; -fx-font-size: 15px; -fx-border-width: 2;" +
                "-fx-border-radius: 5; -fx-border-color: #9d9d9d;");

        selectedOrderButton = orderButton;
        selectedOrder = order;

        displayProductsForOrder(order);
        btnGenerateReport.setDisable(true);
    }

    private Button findOrderButtonByOrderId(String orderId) {
        for (FlowPane pane : List.of(pendingPane)) {
            for (Node node : pane.getChildren()) {
                if (node instanceof Button) {
                    Button button = (Button) node;
                    Order order = (Order) button.getUserData();
                    if (order != null && order.getOrderId().equals(orderId)) {
                        return button;
                    }
                }
            }
        }
        return null;
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

    private String getStyleForStatus(String status) {
        switch (status) {
            case "Pending":
                return "-fx-padding: 15; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-color: transparent; -fx-border-color: #575757; -fx-font-size: 15px";
                default:
                    return "-fx-padding: 15; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-color: transparent; -fx-border-color: #575757; -fx-font-size: 15px";
        }
    }

    private void displayProductsForOrder(Order order) {
        if (productPane.getChildren().stream().anyMatch(node -> node.getUserData() != null && node.getUserData().equals(order))) {
            System.out.println("Order already displayed: " + order.getOrderId());
            return;
        }

        try {
            if (order.getProducts() == null || order.getProducts().isEmpty()) {
                List<Product> products = orderManager.getProductsForOrder(order.getOrderId());
                order.setProducts(products);
            }

            VBox orderBox = new VBox(15);
            orderBox.setUserData(order);
            orderBox.setStyle("-fx-padding: 15; -fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 5;");

            double buttonWidth = 200;
            double buttonHeight = 40;

            Label operatorLabel = new Label("Operator: " + order.getOperatorFirstName() + " " + order.getOperatorLastName());
            operatorLabel.setStyle("-fx-font-style: italic; -fx-font-size: 14px; -fx-text-fill: #333;");
            orderBox.getChildren().add(operatorLabel);

            // Order Button
            Button orderButton = new Button("OrderID: " + order.getOrderId());
            orderButton.setStyle("-fx-font-size: 16px; -fx-background-color: transparent; -fx-border-color: #333535; -fx-padding: 15; -fx-font-weight: bold");
            orderButton.setPrefWidth(buttonWidth);
            orderButton.setPrefHeight(buttonHeight);
            orderButton.setUserData(order);

            orderButton.setOnAction(e -> handleSelection(orderButton, orderBox, order));
            orderBox.getChildren().add(orderButton);

            // Product Buttons
            for (Product product : order.getProducts()) {
                Button productButton = new Button(product.getProductName());
                productButton.setStyle("-fx-border-color: #333535; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px;");
                productButton.setUserData(product);
                productButton.setPrefWidth(buttonWidth);
                productButton.setPrefHeight(buttonHeight);

                productButton.setOnAction(e -> handleSelection(productButton, orderBox, order));
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
        if (clickedButton.equals(selectedButton)) {
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
            selectedButton = null;
            selectedOrder = null;
            btnGenerateReport.setDisable(true);
            return;
        }
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
        clickedButton.setStyle("-fx-border-color: #9d9d9d; -fx-background-color: #9d9d9d; -fx-text-fill: white; -fx-padding: 15px; -fx-font-size: 16px;");

        selectedButton = clickedButton;
        selectedOrder = order;

        btnGenerateReport.setDisable(false);
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
}
