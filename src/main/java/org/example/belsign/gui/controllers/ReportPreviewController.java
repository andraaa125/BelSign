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
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.example.belsign.be.Order;
import org.example.belsign.be.Product;
import org.example.belsign.bll.OrderManager;
import org.example.belsign.util.PdfExporter;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ReportPreviewController {

    @FXML private VBox rootVBox;
    @FXML private FlowPane imageContainer;
    @FXML private HBox exportButtons;
    @FXML private Button btnSendEmail;
    @FXML private Button btnExport;
    @FXML private Label lblOrderNumber;
    @FXML private Label lblProductName;
    @FXML private Label lblInspector;
    @FXML private TextArea txtComments;

    private final OrderManager orderManager = new OrderManager();
    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        loadReportData();
    }

    private void loadReportData() {
        lblOrderNumber.setText(order.getOrderId());

        try {
            if (order.getProducts() == null || order.getProducts().isEmpty()) {
                order.setProducts(orderManager.getProductsForOrder(order.getOrderId()));
            }

            StringBuilder productNames = new StringBuilder();
            imageContainer.setPrefWrapLength(480); // Ensure 3 images/row
            imageContainer.getChildren().clear();

            for (Product product : order.getProducts()) {
                productNames.append(product.getProduct()).append(" (").append(product.getStatus()).append(")\n");

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
        } catch (Exception e) {
            lblProductName.setText("N/A");
            e.printStackTrace();
        }
    }

    @FXML
    private void onExportAsPDF() {
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
