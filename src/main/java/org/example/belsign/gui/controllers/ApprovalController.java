package org.example.belsign.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.belsign.be.Order;
import org.example.belsign.bll.OrderManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

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
        List<String> imageColumns = List.of(
                "Image_FRONT", "Image_BACK", "Image_LEFT",
                "Image_RIGHT", "Image_TOP", "Image_BOTTOM"
                // optionally loop for "Additional_1" to "Additional_20"
        );

        int col = 0, row = 0;
        for (String column : imageColumns) {
            try {
                byte[] data = orderManager.getImageData(order.getOrderId(), column);
                if (data != null) {
                    ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(data)));
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                    imageGrid.add(new StackPane(imageView), col, row);

                    col++;
                    if (col > 2) {
                        col = 0;
                        row++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onClickCancel(ActionEvent event) {
        // Close the current window
        Button source = (Button) event.getSource();
        source.getScene().getWindow().hide();
    }


    @FXML
    private void onClickApprove() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/belsign/ReportPreview.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Quality Control Report");
            stage.setScene(new Scene(root));
            stage.show();

            // Optional: Close this window
            btnApprove.getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load ReportPreview.fxml");
        }
    }



    @FXML
    private void onClickDecline(ActionEvent event) {
        commentSection.setVisible(true); // Shows the section ONLY after clicking Decline
        commentLabel.setText("Add Optional Comments\nfor Operator");

    }


    @FXML
    private void onClickSendComment(ActionEvent event) {
        String comment = commentTextArea.getText();
        System.out.println("Declined with comment: " + comment);

        // Hide comment box and optionally clear it
        commentBox.setVisible(false);
        commentTextArea.clear();

        // You can also update the database here or notify QC backend
        // Example: orderManager.markAsDeclined(orderId, comment);
    }

}
