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
    @FXML
    private FlowPane searchPane;
    @FXML
    private FlowPane productPane;
    @FXML
    private TextField searchField;
    @FXML
    private FlowPane donePane;
    @FXML
    private Button btnDocument;
    @FXML
    private Label userName;
    @FXML
    private Button logoutButton;
    @FXML
    private Label statusLabel;
    @FXML
    private Label incorrectOrderID;
    @FXML
    private Label lblError;
    @FXML
    private Button selectedOrderButton = null;

    private Button selectedButton = null;

    private Order selectedOrder = null;
    private final OrderManager orderManager = new OrderManager();

    private final Map<String, VBox> orderVBoxMap = new HashMap<>();

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        btnDocument.setDisable(true);
    }


    public void onClickDocument(ActionEvent actionEvent) throws IOException {
        if (selectedOrder != null) {
            loadDocumentView(selectedOrder);
        }
    }

    public void setUserName(String firstName, String lastName) {
        userName.setText("Welcome, " + firstName + " " + lastName + "!");
    }

    private void loadDocumentView(Order order) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/belsign/DocumentationView.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Photo Documentation");
        stage.setScene(new Scene(root));

        DocumentationController controller = fxmlLoader.getController();
        controller.setOrder(order);
        //controller.setOperatorDashboardController(this);
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
            System.out.println("Order already displayed: " + searchId);
            return;
        }

        Iterator<Map.Entry<String, VBox>> iterator = orderVBoxMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, VBox> entry = iterator.next();
            VBox box = entry.getValue();
            productPane.getChildren().remove(box);
            iterator.remove();
        }

        try {
            List<Order> allOrders = orderManager.getAllOrders();

            for (Order order : allOrders) {
                if (order.getOrderId().equalsIgnoreCase(searchId)) {
                    displayProductsForOrder(order);
                    return;
                }
            }

            incorrectOrderID.setText("Invalid Order ID");
            System.out.println("Order not found: " + searchId);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void hideOrderVBoxes() {
        Iterator<Map.Entry<String, VBox>> iterator = orderVBoxMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, VBox> entry = iterator.next();
            VBox box = entry.getValue();
            productPane.getChildren().remove(box);
            iterator.remove();
        }

        selectedOrder = null;
        selectedButton = null;

        btnDocument.setDisable(true);
    }

    private void displayProductsForOrder(Order order) {
//        productPane.getChildren().clear();
        try {
            if (order.getProducts() == null || order.getProducts().isEmpty()) {
                List<Product> products = orderManager.getProductsForOrder(order.getOrderId());
                order.setProducts(products);
            }

            VBox orderBox = new VBox(15);
            orderBox.setStyle("-fx-padding: 15; -fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 5;");

            double buttonWidth = 200;
            double buttonHeight = 40;

            // Order button
            Button orderButton = new Button("OrderID: " + order.getOrderId());
            orderButton.setStyle("-fx-font-size: 16px; -fx-background-color: transparent; -fx-border-color: #333535; -fx-padding: 15; -fx-font-weight: bold;");
            orderButton.setUserData(order);
            orderButton.setPrefWidth(buttonWidth);
            orderButton.setPrefHeight(buttonHeight);
            orderButton.setOnAction(e -> handleSelection(orderButton, orderBox, order));
            orderBox.getChildren().add(orderButton);

            for (Product product : order.getProducts()) {
                Button productButton = new Button(product.getName());
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
            btnDocument.setDisable(true);
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

        btnDocument.setDisable(false);
    }
}