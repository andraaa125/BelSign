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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        lblInspector.setText(order.getOperatorFirstName() + " " + order.getOperatorLastName());

        try {
            if (order.getProducts() == null || order.getProducts().isEmpty()) {
                order.setProducts(orderManager.getProductsForOrder(order.getOrderId()));
            }

            StringBuilder productNames = new StringBuilder();
            for (Product product : order.getProducts()) {
                productNames.append(product.getProduct()).append("\n");
            }
            lblProductName.setText(productNames.toString().trim());
        } catch (Exception e) {
            lblProductName.setText("N/A");
            e.printStackTrace();
        }

        imageContainer.setPrefWrapLength(480); // Ensure 3 images/row
        imageContainer.getChildren().clear();

        String[] defaultCols = {
                "Image_FRONT", "Image_BACK", "Image_LEFT",
                "Image_RIGHT", "Image_TOP", "Image_BOTTOM"
        };
        String[] defaultLabels = {
                "Front", "Back", "Left", "Right", "Top", "Bottom"
        };

        try {
            for (int i = 0; i < defaultCols.length; i++) {
                byte[] data = orderManager.getImageData(order.getOrderId(), defaultCols[i]);
                if (data != null && data.length > 0) {
                    addImageWithLabel(defaultLabels[i] + " Image", data);
                }
            }

            for (int i = 1; i <= 20; i++) {
                String column = "Additional_" + i;
                byte[] data = orderManager.getImageData(order.getOrderId(), column);
                if (data != null && data.length > 0) {
                    addImageWithLabel("Additional Image", data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addImageWithLabel(String labelText, byte[] imageData) {
        Image image = new Image(new ByteArrayInputStream(imageData));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(120);
        imageView.setPreserveRatio(true);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");
        label.setMaxWidth(120);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);

        VBox imageBox = new VBox(5, label, imageView);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPadding(new Insets(5));
        imageBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");
        imageContainer.getChildren().add(imageBox);
    }

    @FXML
    private void onExportAsPDF() {
        try {
            exportButtons.setVisible(false);
            exportButtons.setManaged(false);

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
