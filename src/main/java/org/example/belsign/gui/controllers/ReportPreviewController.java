package org.example.belsign.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.example.belsign.be.Order;
import org.example.belsign.be.Product;
import org.example.belsign.bll.OrderManager;
import org.example.belsign.bll.ProductManager;
import org.example.belsign.util.PdfExporter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    private ProductManager productManager = new ProductManager();
    private Product product;

    public void setProduct(Product product) {
        this.product = product;
        loadReportData();
    }

    @FXML
    private void initialize() {
        txtComments.setWrapText(true); // Make sure text wraps

        txtComments.textProperty().addListener((obs, oldText, newText) -> {
            Text text = new Text(newText);
            text.setFont(txtComments.getFont());
            text.setWrappingWidth(txtComments.getWidth() - 10); // account for padding

            double height = text.getLayoutBounds().getHeight() + 20; // some padding
            txtComments.setPrefHeight(height);
        });

        int MAX_CHARACTERS = 150;
        int MAX_LINES = 2;

        txtComments.setWrapText(true);

        txtComments.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > MAX_CHARACTERS) {
                txtComments.setText(oldText);
                return;
            }

            long lineCount = newText.chars().filter(ch -> ch == '\n').count() + 1;
            if (lineCount > MAX_LINES) {
                txtComments.setText(oldText);
            }
        });
    }

    private void loadReportData() {
        lblOrderNumber.setText(product.getOrderId());

        String productInfo = product.getProductName();
        lblProductName.setText(productInfo);

        imageContainer.setPrefWrapLength(480);
        imageContainer.getChildren().clear();

        addImageIfPresent(product.getImageFront());
        addImageIfPresent(product.getImageBack());
        addImageIfPresent(product.getImageLeft());
        addImageIfPresent(product.getImageRight());
        addImageIfPresent(product.getImageTop());
        addImageIfPresent(product.getImageBottom());

        if (product.getAdditionalImages() != null) {
            for (byte[] additionalImage : product.getAdditionalImages().values()) {
                addImageIfPresent(additionalImage);
            }
        }
    }

    private void addImageIfPresent(byte[] imageData) {
        if (imageData != null && imageData.length > 0) {
            try {
                Image image = new Image(new java.io.ByteArrayInputStream(imageData), 150, 150, true, true);
                ImageView imageView = new ImageView(image);
                imageContainer.getChildren().add(imageView);
            } catch (Exception e) {
                System.out.println("Failed to load image.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onExportAsPDF() throws IOException {
        try {
            exportButtons.setVisible(false);
            exportButtons.setManaged(false);

            rootVBox.requestFocus();
            rootVBox.applyCss();
            rootVBox.layout();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save QC Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("QC_Report_" + System.currentTimeMillis() + ".pdf");

            Window window = rootVBox.getScene().getWindow();
            File selectedFile = fileChooser.showSaveDialog(window);

            if (selectedFile != null) {
                PdfExporter.exportNodeToPDF(rootVBox, selectedFile);
            }
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
