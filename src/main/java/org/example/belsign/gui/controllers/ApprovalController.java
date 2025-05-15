package org.example.belsign.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.belsign.be.Order;
import org.example.belsign.bll.OrderManager;
import org.example.belsign.factory.ReportPreviewFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ApprovalController {

    @FXML private GridPane imageGrid;
    @FXML private Button btnApprove;
    @FXML private Button btnDecline;
    @FXML private HBox commentSection;
    @FXML private VBox commentBox;
    @FXML private TextArea commentTextArea;
    @FXML private Label commentLabel;

    private Order order;
    private final OrderManager orderManager = new OrderManager();

    public void setOrder(Order order) {
        this.order = order;
        loadImagesForApproval();
    }

    private QCDashboardController qcDashboardController;

    public void setQCDashboardController(QCDashboardController controller) {
        this.qcDashboardController = controller;
    }

    private void loadImagesForApproval() {
        imageGrid.getChildren().clear();

        String[] defaultColumns = {
                "Image_FRONT", "Image_BACK", "Image_LEFT",
                "Image_RIGHT", "Image_TOP", "Image_BOTTOM"
        };

        String[] defaultLabels = {
                "Front Image", "Back Image", "Left Image",
                "Right Image", "Top Image", "Bottom Image"
        };

        int col = 0, row = 0;

        // Load default images
        for (int i = 0; i < defaultColumns.length; i++) {
            addImageToGrid(defaultColumns[i], defaultLabels[i], col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }

        // Load additional images
        for (int i = 1; i <= 20; i++) {
            String column = "Additional_" + i;
            addImageToGrid(column, "Additional Image", col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }

    private void addImageToGrid(String column, String labelText, int col, int row) {
        try {
            byte[] data = orderManager.getImageData(order.getOrderId(), column);
            if (data != null && data.length > 0) {
                ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(data)));
                imageView.setFitWidth(180);
                imageView.setPreserveRatio(true);

                StackPane borderedPane = new StackPane(imageView);
                borderedPane.setPrefSize(200, 150);
                borderedPane.setStyle("-fx-border-color: #333333; -fx-border-width: 1; -fx-border-radius: 5; -fx-border-style: solid;");

                Label label = new Label(labelText);
                label.setStyle("-fx-text-fill: #333333; -fx-font-size: 13px;");
                label.setMaxWidth(Double.MAX_VALUE);
                label.setAlignment(Pos.CENTER);

                VBox vbox = new VBox(5, label, borderedPane);
                vbox.setAlignment(Pos.CENTER);

                imageGrid.add(vbox, col, row);
            }
        } catch (IOException e) {
            System.out.println("Could not load image from: " + column);
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
        ReportPreviewFactory.showReportWindow(order);
    }

    @FXML
    private void onClickDecline(ActionEvent event) {
        commentSection.setVisible(true);
        commentLabel.setText("Add Optional Comments\nfor Operator");
    }

    @FXML
    private void onClickSendComment(ActionEvent event) {
        String comment = commentTextArea.getText();
        System.out.println("Declined with comment: " + comment);
        commentBox.setVisible(false);
        commentTextArea.clear();
    }
}
