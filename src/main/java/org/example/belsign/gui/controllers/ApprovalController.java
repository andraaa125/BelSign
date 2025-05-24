package org.example.belsign.gui.controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.belsign.be.Order;
import org.example.belsign.be.Product;
import org.example.belsign.bll.OrderManager;
import org.example.belsign.bll.ProductManager;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApprovalController {

    @FXML private GridPane imageGrid;
    @FXML private Button btnApprove;
    @FXML private Button btnDisapprove;
    @FXML private HBox commentSection;
    @FXML private VBox commentBox;
    @FXML private TextArea commentTextArea;
    @FXML private Label commentLabel;

    private Order order;
    private final OrderManager orderManager = new OrderManager();
    private final List<VBox> disapprovedBoxes = new ArrayList<>();
    private boolean disapproveMode = false;
    private final ProductManager productManager = new ProductManager();
    private List<Product> products;

    private Button productButton;

    public void setProductButton(Button productButton) {
        this.productButton = productButton;
    }



    public void setOrder(Order order) {
        this.order = order;
        try {
            this.products = order.getProducts(); // Assuming products are pre-fetched
            if (this.products == null) {
                this.products = productManager.getProductsForOrder(order.getOrderId()); // fallback
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.products = new ArrayList<>();
        }
        loadImagesForApproval();
    }


    private QCDashboardController qcDashboardController;

    public void setQCDashboardController(QCDashboardController controller) {
        this.qcDashboardController = controller;
    }

    private void loadImagesForApproval() {
        imageGrid.getChildren().clear();
        int col = 0, row = 0;

        for (Product product : products) {
            // Add standard images
            String[] columns = {"Image_FRONT", "Image_BACK", "Image_LEFT", "Image_RIGHT", "Image_TOP", "Image_BOTTOM"};
            String[] labels = {"Front", "Back", "Left", "Right", "Top", "Bottom"};

            for (int i = 0; i < columns.length; i++) {
                addImageToGrid(product, columns[i], labels[i], col, row);
                col++;
                if (col > 2) { col = 0; row++; }
            }

            // Add additional images
            for (int i = 1; i <= 20; i++) {
                String colName = "Additional_" + i;
                addImageToGrid(product, colName, "Additional " + i, col, row);
                col++;
                if (col > 2) { col = 0; row++; }
            }
        }
    }


    private void addImageToGrid(Product product, String columnName, String labelText, int col, int row) {
        try {
            byte[] imageData = productManager.getImageData(product, columnName);
            if (imageData != null && imageData.length > 0) {
                Image image = new Image(new ByteArrayInputStream(imageData));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(180);
                imageView.setPreserveRatio(true);

                StackPane borderedPane = new StackPane(imageView);
                borderedPane.setPrefSize(200, 150);
                borderedPane.setStyle("-fx-border-color: #333333; -fx-border-width: 1; -fx-border-radius: 5;");

                Label label = new Label(labelText + " (" + product.getProduct() + ")");
                label.setMaxWidth(Double.MAX_VALUE);
                label.setAlignment(Pos.CENTER);

                VBox vbox = new VBox(5, label, borderedPane);
                vbox.setAlignment(Pos.CENTER);
                vbox.setStyle("-fx-cursor: hand;");

                vbox.setOnMouseClicked(e -> {
                    if (!disapproveMode) return;
                    StackPane imagePane = (StackPane) vbox.getChildren().get(1);

                    if (disapprovedBoxes.contains(vbox)) {
                        disapprovedBoxes.remove(vbox);
                        imagePane.setStyle("-fx-border-color: #333333;");
                    } else {
                        disapprovedBoxes.add(vbox);
                        imagePane.setStyle("-fx-border-color: red;");
                    }
                });

                imageGrid.add(vbox, col, row);
            }
        } catch (Exception e) {
            System.out.println("Could not load image for: " + columnName);
            e.printStackTrace();
        }
    }



    @FXML
    private void onClickCancel(ActionEvent event) {
        Button source = (Button) event.getSource();
        source.getScene().getWindow().hide();
    }

    @FXML
    private void onClickApprove() {
        if (productButton != null) {
            productButton.setStyle("-fx-background-color: #338d71; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15px;");
        }

        commentSection.getChildren().clear();
        Label confirmationLabel = new Label("✅ Product images approved successfully.");
        confirmationLabel.setStyle("-fx-text-fill: #057017; -fx-font-size: 14px; -fx-font-weight: bold;");
        commentSection.getChildren().add(confirmationLabel);
        commentSection.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(7));
        pause.setOnFinished(e -> commentSection.setVisible(false));
        pause.play();
    }



    @FXML
    private void onClickDisapprove(ActionEvent event) {
        disapproveMode = true;
        commentSection.setVisible(true);
        commentLabel.setText("Add Optional Comments\nfor Operator");
        disapprovedBoxes.clear();
    }



    @FXML
    private void onClickSendComment(ActionEvent event) {
        String comment = commentTextArea.getText();
        System.out.println("Disapproved with comment: " + comment);

        // Clear the comment box UI
        commentTextArea.clear();
        commentSection.getChildren().clear();

        // Show confirmation message
        Label confirmationLabel = new Label("✅ Sent successfully to the operator.");
        confirmationLabel.setStyle("-fx-text-fill: #057017; -fx-font-size: 14px; -fx-font-weight: bold;");
        commentSection.getChildren().add(confirmationLabel);
        commentSection.setVisible(true);

        // Hide the message after 10 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(7));
        pause.setOnFinished(e -> commentSection.setVisible(false));
        pause.play();
    }


}
