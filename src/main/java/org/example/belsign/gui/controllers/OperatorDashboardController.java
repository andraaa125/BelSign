package org.example.belsign.gui.controllers;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.belsign.be.Order;
import org.example.belsign.be.Product;
import org.example.belsign.bll.OrderManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class OperatorDashboardController implements Initializable {
    @FXML private FlowPane searchPane;
    @FXML private FlowPane productPane;
    @FXML private FlowPane inProgressPane;
    @FXML private FlowPane donePane;
    @FXML private Label userName;
    @FXML private Button logoutButton;
    @FXML private Label statusLabel;
    @FXML private Label incorrectOrderID;
    @FXML private Label lblError;
    @FXML private Button selectedOrderButton = null;
    @FXML private TextField searchField;

    private Button selectedButton = null;
    private Order selectedOrder = null;
    private final OrderManager orderManager = new OrderManager();
    private final Map<String, VBox> orderVBoxMap = new HashMap<>();

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        refreshDonePane();
    }

    private void displayOrderInDonePane(Order order) {
        // Avoid duplicate buttons
        for (Node node : donePane.getChildren()) {
            if (node instanceof Button btn && btn.getUserData() instanceof Order o && o.getOrderId().equals(order.getOrderId())) {
                return;
            }
        }

        Button orderButton = new Button(order.getOrderId());
        orderButton.setStyle(getStyleForStatus(order.getStatus())); // Default style
        orderButton.setPrefWidth(200);
        orderButton.setPrefHeight(40);
        orderButton.setUserData(order);

        orderButton.setOnAction(e -> {
            if (selectedButton == orderButton) {
                // Deselect if already selected
                orderButton.setStyle(getStyleForStatus(order.getStatus()));
                selectedButton = null;
                hideOrderVBoxes();
            } else {
                // Reset styles on all buttons
                for (Node node : donePane.getChildren()) {
                    if (node instanceof Button btn) {
                        btn.setStyle(getStyleForStatus(((Order) btn.getUserData()).getStatus()));
                    }
                }

                // Highlight current button
                orderButton.setStyle(
                        "-fx-background-color: #d3d3d3;" +  // Light grey
                                "-fx-text-fill: black;" +
                                "-fx-padding: 15;" +
                                "-fx-font-size: 16px;" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 5;" +
                                "-fx-border-color: #575757;" +
                                "-fx-font-weight: bold;"
                );

                // Clear previous and display new
                hideOrderVBoxes();
                try {
                    displayProductsForOrder(order);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                selectedButton = orderButton;
            }
        });


        donePane.getChildren().add(orderButton);
    }



    public void refreshDonePane() {
        donePane.getChildren().clear();
        try {
            List<Order> doneOrders = orderManager.getOrdersWithDisapprovedProducts();
            for (Order order : doneOrders) {
                order.setProducts(orderManager.getProductsForOrder(order.getOrderId()));
                displayOrderInDonePane(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setUserName(String firstName, String lastName) {
        userName.setText("Welcome, " + firstName + " " + lastName + "!");
        userName.setStyle("-fx-font-size: 20");
    }

    private void loadDocumentView(Order order, Product product) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/belsign/DocumentationView.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Photo Documentation");
        stage.setScene(new Scene(root));

        DocumentationController controller = fxmlLoader.getController();
        controller.setOrder(order);
        controller.setProduct(product);
        controller.setOperatorDashboardController(this);
        stage.show();
    }

    public void setStatusMessage(String message) {
        statusLabel.setText(message);
        PauseTransition pause = new PauseTransition(Duration.seconds(7));
        pause.setOnFinished(e -> statusLabel.setText(""));
        pause.play();
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

    public void onClickSearch(ActionEvent actionEvent) {
        lblError.setText("");
        incorrectOrderID.setText("");

        String searchId = searchField.getText().trim();
        if (searchId.isEmpty()) {
            hideOrderVBoxes();
            lblError.setText("Please enter Order ID!");
            return;
        }

        if (orderVBoxMap.containsKey(searchId)) {
            lblError.setText(" Order already displayed!");
            return;
        }

        hideOrderVBoxes();

        try {
            List<Order> allOrders = orderManager.getAllOrders();
            for (Order order : allOrders) {
                if (order.getOrderId().equalsIgnoreCase(searchId)) {
                    displayProductsForOrder(order);
                    return;
                }
            }

            incorrectOrderID.setText("Invalid Order ID");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void hideOrderVBoxes() {
        for (VBox box : orderVBoxMap.values()) {
            productPane.getChildren().remove(box);
        }
        orderVBoxMap.clear();
        selectedOrder = null;
        selectedButton = null;
    }

    private void displayProductsForOrder(Order order) throws SQLException {
        try {
            // ✅ Remove previous order's products if any
            hideOrderVBoxes();

            if (order.getProducts() == null || order.getProducts().isEmpty()) {
                List<Product> products = orderManager.getProductsForOrder(order.getOrderId());
                order.setProducts(products);
            }

            VBox orderBox = new VBox(15);
            orderBox.setStyle("-fx-padding: 15; -fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 5;");

            Button orderButton = new Button("OrderID: " + order.getOrderId());
            orderButton.setStyle("-fx-font-size: 16px; -fx-background-color: transparent; -fx-border-color: #333535; -fx-padding: 15; -fx-font-weight: bold;");
            orderButton.setUserData(order);
            orderButton.setPrefWidth(200);
            orderButton.setPrefHeight(40);
            orderButton.setOnAction(e -> handleSelection(orderButton, orderBox, order));
            orderBox.getChildren().add(orderButton);

            for (Product product : order.getProducts()) {
                Button productButton = new Button(product.getName());

                String borderColor = switch (product.getStatus().toLowerCase()) {
                    case "pending_approval", "approved" -> "#338d71";
                    case "disapproved" -> "#880000";
                    default -> "#9ca3af";
                };

                productButton.setStyle("-fx-border-color: " + borderColor + "; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px;");
                productButton.setUserData(product);
                productButton.setPrefWidth(200);
                productButton.setPrefHeight(40);
                productButton.setOnAction(e -> {
                    handleSelection(productButton, orderBox, order);
                    try {
                        loadDocumentView(order, product);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                orderBox.getChildren().add(productButton);
            }

            productPane.getChildren().add(orderBox);
            orderVBoxMap.clear(); // Since we're only showing one order at a time
            orderVBoxMap.put(order.getOrderId(), orderBox);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void handleSelection(Button clickedButton, VBox orderBox, Order order) {
        for (Node node : orderBox.getChildren()) {
            if (node instanceof Button btn) {
                boolean isOrderButton = btn.getText().startsWith("OrderID:");
                btn.setStyle("-fx-border-color: #9d9d9d; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px;" + (isOrderButton ? " -fx-font-weight: bold;" : ""));
            }
        }

        clickedButton.setStyle("-fx-border-color: #9d9d9d; -fx-background-color: #9d9d9d; -fx-text-fill: white; -fx-padding: 15px; -fx-font-size: 16px;");
        selectedButton = clickedButton;
        selectedOrder = order;
    }

    public void onNumberClick(ActionEvent actionEvent) {
        Button clickedButton = (Button) actionEvent.getSource();
        searchField.appendText(clickedButton.getText());
    }

    public void onClickClear(ActionEvent actionEvent) {
        searchField.clear();
    }

    public void onClickBackspace(ActionEvent actionEvent) {
        String currentText = searchField.getText();
        if (!currentText.isEmpty()) {
            searchField.setText(currentText.substring(0, currentText.length() - 1));
        }
    }

    public void markProductAsSent(Product product) {
        VBox orderBox = orderVBoxMap.get(product.getOrderId());
        if (orderBox == null) return;

        for (Node node : orderBox.getChildren()) {
            if (node instanceof Button button && button.getUserData() instanceof Product p) {
                if (p.getProductId() == product.getProductId()) {
                    product.setStatus("Pending approval");
                    button.setText(product.getName() + " (" + product.getStatus() + ")");
                    button.setStyle("-fx-border-color: #338d71; -fx-padding: 15px; -fx-background-color: transparent; -fx-font-size: 16px;");
                    break;
                }
            }
        }
    }

    private String getStyleForStatus(String status) {
        return "-fx-padding: 15;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 5;" +
                "-fx-background-color: transparent;" +
                "-fx-border-color: #575757;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #333;";
    }


}
