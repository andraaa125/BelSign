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
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.belsign.be.Order;
import org.example.belsign.bll.OrderManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OperatorDashboardController implements Initializable {
    @FXML private FlowPane pendingPane;
    @FXML private FlowPane inProgressPane;
    @FXML private FlowPane donePane;
    @FXML private Button btnDocument;
    @FXML private Label userName;
    @FXML private Button logoutButton;
    @FXML private Label statusLabel;

    private Button selectedOrderButton = null;
    private Order selectedOrder = null;
    private final OrderManager orderManager = new OrderManager();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Order> orders = orderManager.getAllOrders();
            for (Order order : orders) {
                Button orderButton = new Button(order.getOrderId());
                orderButton.setUserData(order);
                orderButton.setStyle(getStyleForStatus(order.getStatus()));
                orderButton.setOnAction(e -> handleOrderClick(order));
                setupDragEvents(orderButton);

                switch (order.getStatus()) {
                    case "Pending" -> pendingPane.getChildren().add(orderButton);
                    case "InProgress" -> inProgressPane.getChildren().add(orderButton);
                    case "Done" -> donePane.getChildren().add(orderButton);
                }
            }

            setupDropZone(pendingPane, "Pending");
            setupDropZone(inProgressPane, "InProgress");
            setupDropZone(donePane, "Done");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        btnDocument.setDisable(true);
    }

    public void setStatusMessage(String message) {
        statusLabel.setText(message);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> statusLabel.setText(""));
        pause.play();
    }

    private void setupDragEvents(Button orderButton) {
        orderButton.setOnDragDetected(event -> {
            Dragboard db = orderButton.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            Order order = (Order) orderButton.getUserData();
            content.putString(order.getOrderId());
            db.setContent(content);
            db.setDragView(orderButton.snapshot(null, null));
            orderButton.setStyle(orderButton.getStyle() + "; -fx-opacity: 0.6; -fx-border-style: dashed;");
            event.consume();
        });

        orderButton.setOnDragDone(event -> {
            Order order = (Order) orderButton.getUserData();
            orderButton.setStyle(getStyleForStatus(order.getStatus()));
            event.consume();
        });
    }

    private void setupDropZone(FlowPane pane, String status) {
        pane.setOnDragOver(event -> {
            if (event.getGestureSource() != pane && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        pane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            String orderId = db.getString();
            Button draggedButton = findAndRemoveOrderButton(orderId);
            if (draggedButton != null) {
                Order order = (Order) draggedButton.getUserData();
                order.setStatus(status);
                orderManager.updateOrderStatus(order.getOrderId(), status);
                draggedButton.setStyle(getStyleForStatus(status));
                pane.getChildren().add(draggedButton);
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    private Button findAndRemoveOrderButton(String orderId) {
        for (FlowPane pane : List.of(pendingPane, inProgressPane, donePane)) {
            for (Node node : new ArrayList<>(pane.getChildren())) {
                if (node instanceof Button button) {
                    Order order = (Order) button.getUserData();
                    if (order.getOrderId().equals(orderId)) {
                        pane.getChildren().remove(button);
                        return button;
                    }
                }
            }
        }
        return null;
    }

    private String getStyleForStatus(String status) {
        String baseStyle = "-fx-padding: 15; -fx-border-width: 2; -fx-border-radius: 5;";
        return switch (status) {
            case "Pending" -> baseStyle + " -fx-background-color: transparent; -fx-border-color: #575757; -fx-font-size: 15px";
            case "InProgress" -> baseStyle + " -fx-background-color: transparent; -fx-border-color: #DAA520; -fx-font-size: 15px";
            case "Done" -> baseStyle + " -fx-background-color:  transparent; -fx-border-color:  #338d71; -fx-font-size: 15px";
            default -> baseStyle + " -fx-background-color: transparent; -fx-border-color: #575757";
        };
    }

    private void handleOrderClick(Order order) {
        if (!"Done".equals(order.getStatus())) return;

        if (selectedOrderButton != null && selectedOrderButton.getUserData().equals(order)) {
            selectedOrderButton.setStyle(getStyleForStatus(order.getStatus()));
            selectedOrderButton = null;
            selectedOrder = null;
            btnDocument.setDisable(true);
            return;
        }

        if (selectedOrderButton != null) {
            selectedOrderButton.setStyle(getStyleForStatus(((Order) selectedOrderButton.getUserData()).getStatus()));
        }

        Button orderButton = findOrderButtonByOrderId(order.getOrderId());
        orderButton.setStyle("-fx-background-color: #338d71; -fx-text-fill: white; -fx-padding: 15; -fx-font-size: 15px; -fx-border-width: 2; -fx-border-radius: 5;-fx-border-color: #338d71;");
        selectedOrderButton = orderButton;
        selectedOrder = order;
        btnDocument.setDisable(false);
        orderButton.setUserData(order);
    }

    private Button findOrderButtonByOrderId(String orderId) {
        for (FlowPane pane : List.of(pendingPane, inProgressPane, donePane)) {
            for (Node node : pane.getChildren()) {
                if (node instanceof Button button) {
                    Order order = (Order) button.getUserData();
                    if (order != null && order.getOrderId().equals(orderId)) {
                        return button;
                    }
                }
            }
        }
        return null;
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
        controller.setOperatorDashboardController(this);
        stage.show();
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
}
