package org.example.belsign.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import org.example.belsign.be.Order;
import org.example.belsign.be.Product;

import java.io.FileInputStream;
import java.util.List;

public class ReportPreviewController {

    @FXML private Label lblOrderNumber;
    @FXML private Label lblProductName;
    @FXML private Label lblInspector;
    @FXML private TextArea txtComments;
    @FXML private FlowPane imageContainer;
    @FXML private Button btnSendEmail;
    @FXML private Button btnExport;

    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        loadOrderData();
    }

    private void loadOrderData() {
        if (order == null) return;

        lblOrderNumber.setText(order.getOrderId());
        lblInspector.setText(order.getOperatorFirstName() + " " + order.getOperatorLastName());

        List<Product> products = order.getProducts();

        if (products != null && !products.isEmpty()) {
            StringBuilder productNames = new StringBuilder();
            imageContainer.getChildren().clear();

            for (Product product : products) {
                productNames.append(product.getProduct()).append(" (").append(product.getStatus()).append(")\n");

                // Load and display product images (only front image as example)
                byte[] imageData = product.getImageFront();
                if (imageData != null && imageData.length > 0) {
                    try {
                        Image image = new Image(new java.io.ByteArrayInputStream(imageData), 100, 100, true, true);
                        ImageView imageView = new ImageView(image);
                        imageContainer.getChildren().add(imageView);
                    } catch (Exception e) {
                        System.out.println("Failed to load image for product: " + product.getProductId());
                        e.printStackTrace();
                    }
                }

            }

            lblProductName.setText(productNames.toString().trim());
        } else {
            lblProductName.setText("No products available");
        }
    }

    @FXML
    private void onSendEmail() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Email");
        alert.setHeaderText(null);
        alert.setContentText("Send Email feature is not yet implemented.");
        alert.showAndWait();
    }

    @FXML
    private void onExportAsPDF() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export");
        alert.setHeaderText(null);
        alert.setContentText("PDF Export feature is not yet implemented.");
        alert.showAndWait();
    }
}
