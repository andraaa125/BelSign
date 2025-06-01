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
import org.example.belsign.factory.ReportPreviewFactory;

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
    private FlowPane donePane;
    @FXML
    private Button selectedButton = null;


    private final Map<String, VBox> orderVBoxMap = new HashMap<>();

    private final OrderManager orderManager = new OrderManager();

    private final ObservableList<String> searchResults = FXCollections.observableArrayList();





    @FXML
    private void initialize() {
        btnGenerateReport.setDisable(true);
        searchComboBox.setEditable(true); // Allow text input
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
            // Load DONE status orders to pendingPane
            List<Order> doneOrders = orderManager.getAllOrders();
            for (Order order : doneOrders) {
                if ("Done".equals(order.getStatus())) {
                    Button orderButton = new Button(order.getOrderId());
                    orderButton.setUserData(order);
                    orderButton.setStyle(getStyleForStatus(order.getStatus()));
                    orderButton.setPrefWidth(200);
                    orderButton.setPrefHeight(40);

                    orderButton.setOnAction(e -> handleOrderClick(order));
                    pendingPane.getChildren().add(orderButton);
                }
            }

            // Load APPROVED orders (all products are approved) into donePane
            List<Order> approvedOrders = orderManager.getFullyApprovedOrders();
            for (Order order : approvedOrders) {
                Button orderButton = new Button(order.getOrderId());
                orderButton.setUserData(order);
                orderButton.setStyle(getStyleForStatus("Approved"));
                orderButton.setPrefWidth(200);
                orderButton.setPrefHeight(40);

                orderButton.setOnAction(e -> handleOrderClick(order));
                donePane.getChildren().add(orderButton);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("An error occurred while loading orders.");
        }
    }


    private void handleOrderClick(Order order) {
        if (!"Done".equals(order.getStatus()) && !"Approved".equals(order.getStatus())) return;


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
        for (Node node : donePane.getChildren()) {
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


            for (Product product : order.getProducts()) {
                //Button productButton = new Button(product.getProductName());
                Button productButton = new Button(product.getName());
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

                productButton.setOnMouseClicked(e -> {
                    handleSelection(productButton, orderBox, order);
                    if (e.getClickCount() == 2) {
                        openApprovalView(order, product.getProductName()); // not getName()
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
        boolean isOrderButton = clickedButton.getText().startsWith("OrderID:");

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

        if (isOrderButton) {
            try {
                List<Product> products = orderManager.getProductsForOrder(order.getOrderId());
                boolean allApproved = products.stream().allMatch(p -> "Approved".equals(p.getStatus()));

                if (allApproved) {
                    btnGenerateReport.setDisable(false);
                    // Optionally store the products in selectedOrder for preview use
                    order.setProducts(products);
                    selectedOrder = order;
                } else {
                    btnGenerateReport.setDisable(true);
                    showAlert("All products in this order must be approved to generate the report.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Failed to load product data for this order.");
            }

        } else {
            Product product = (Product) clickedButton.getUserData();
            boolean isApproved = "Approved".equals(product.getStatus());
            btnGenerateReport.setDisable(!isApproved);
        }
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

    // New overloaded method that accepts the product button
    private void openApprovalView(Order order, String productName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/belsign/ApprovalView.fxml"));
            Parent root = loader.load();
            ApprovalController controller = loader.getController();

            controller.setOrder(order);

            // 🔍 Find the product by name (match against productName)
            Product matchedProduct = order.getProducts()
                    .stream()
                    .filter(p -> productName.equals(p.getProductName()))
                    .findFirst()
                    .orElse(null);

            if (matchedProduct == null) {
                showAlert("Product '" + productName + "' not found in this order.");
                return;
            }

            controller.setProduct(matchedProduct); // ✅ Ensure the product is passed
            controller.setQCDashboardController(this); // Pass dashboard ref if needed

            Stage stage = new Stage();
            stage.setTitle("Approval Panel");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Failed to open Approval View.");
        }
    }


    public void moveOrderToDonePane(Order order) {
        // Remove from pendingPane
        pendingPane.getChildren().removeIf(node ->
                node instanceof Button btn && btn.getUserData() instanceof Order o && o.getOrderId().equals(order.getOrderId())
        );

        // Create new button
        Button approvedButton = new Button(order.getOrderId());
        approvedButton.setUserData(order);
        approvedButton.setPrefWidth(200);
        approvedButton.setPrefHeight(40);
        approvedButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 15px; -fx-padding: 15;");

        approvedButton.setOnAction(e -> handleOrderClick(order));

        // Add to donePane
        donePane.getChildren().add(approvedButton);
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

    public void onClickGenerateReport(ActionEvent actionEvent) {
        if (selectedButton == null) {
            showAlert("Please select an approved product or order.");
            return;
        }

        if (selectedButton.getText().startsWith("OrderID:")) {
            if (selectedOrder == null || selectedOrder.getProducts() == null || selectedOrder.getProducts().isEmpty()) {
                showAlert("Order is not fully loaded.");
                return;
            }

            ReportPreviewFactory.showReportWindow(selectedOrder);

        } else if (selectedButton.getUserData() instanceof Product selectedProduct) {
            if (!"Approved".equals(selectedProduct.getStatus())) {
                showAlert("Only approved products can generate reports.");
                return;
            }

            ReportPreviewFactory.showReportWindow(selectedProduct);
        }
    }


}
