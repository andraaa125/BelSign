package org.example.belsign.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Window;
import org.example.belsign.be.Order;
import org.example.belsign.be.Product;
import org.example.belsign.command.ExportReportCommand;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ReportPreviewController {

    @FXML
    private VBox rootVBox;
    @FXML
    private FlowPane imageContainer;
    @FXML
    private HBox exportButtons;
    @FXML
    private Button btnSendEmail;
    @FXML
    private Button btnExport;
    @FXML
    private Label lblOrderNumber;
    @FXML
    private Label lblProductName;
    @FXML
    private TextArea txtComments;

    private Product product;
    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        loadFullOrderReport();
    }

    public void setProduct(Product product) {
        this.product = product;
        loadSingleProductReport();
    }

    @FXML
    private void initialize() {
        txtComments.setWrapText(true);

        txtComments.textProperty().addListener((obs, oldText, newText) -> {
            int MAX_CHARACTERS = 150;
            int MAX_LINES = 2;

            if (newText.length() > MAX_CHARACTERS || newText.lines().count() > MAX_LINES) {
                txtComments.setText(oldText);
                return;
            }

            Text text = new Text(newText);
            text.setFont(txtComments.getFont());
            text.setWrappingWidth(txtComments.getWidth() - 10);

            double height = text.getLayoutBounds().getHeight() + 20;
            txtComments.setPrefHeight(height);
        });
    }

    private void loadSingleProductReport() {
        lblOrderNumber.setText(product.getOrderId());
        lblProductName.setText(product.getProductName());
        imageContainer.setPrefWrapLength(480);
        imageContainer.getChildren().clear();

        addImageIfPresent(product.getImageFront());
        addImageIfPresent(product.getImageBack());
        addImageIfPresent(product.getImageLeft());
        addImageIfPresent(product.getImageRight());
        addImageIfPresent(product.getImageTop());
        addImageIfPresent(product.getImageBottom());

        if (product.getAdditionalImages() != null) {
            for (byte[] image : product.getAdditionalImages().values()) {
                addImageIfPresent(image);
            }
        }
    }

    private void loadFullOrderReport() {
        lblOrderNumber.setText(order.getOrderId());
        lblProductName.setText("All Products");
        imageContainer.getChildren().clear();

        for (Product p : order.getProducts()) {
            addImageIfPresent(p.getImageFront());
            addImageIfPresent(p.getImageBack());
            addImageIfPresent(p.getImageLeft());
            addImageIfPresent(p.getImageRight());
            addImageIfPresent(p.getImageTop());
            addImageIfPresent(p.getImageBottom());

            if (p.getAdditionalImages() != null) {
                for (byte[] img : p.getAdditionalImages().values()) {
                    addImageIfPresent(img);
                }
            }
        }
    }

    private void addImageIfPresent(byte[] imageData) {
        if (imageData != null && imageData.length > 0) {
            try {
                Image image = new Image(new ByteArrayInputStream(imageData), 150, 150, true, true);
                imageContainer.getChildren().add(new ImageView(image));
            } catch (Exception e) {
                System.out.println("Failed to load image.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onExportAsPDF() {
        Window window = (rootVBox.getScene() != null) ? rootVBox.getScene().getWindow() : null;
        if (window == null) {
            System.err.println("Scene not initialized. Cannot export.");
            return;
        }

        try {
            exportButtons.setVisible(false);
            exportButtons.setManaged(false);

            rootVBox.requestFocus();
            rootVBox.applyCss();
            rootVBox.layout();

            ExportReportCommand command = new ExportReportCommand(rootVBox, window);
            command.execute();

        } finally {
            exportButtons.setVisible(true);
            exportButtons.setManaged(true);
        }
    }

    @FXML
    private void onSendEmail() {
        try {
            String subject = "QC Report";
            String body = "Please find the attached report.";
            String uriStr = "mailto:?subject=" + URLEncoder.encode(subject, StandardCharsets.UTF_8)
                    + "&body=" + URLEncoder.encode(body, StandardCharsets.UTF_8);
            Desktop.getDesktop().mail(new URI(uriStr));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to open mail client: " + e.getMessage());
        }
    }
}
