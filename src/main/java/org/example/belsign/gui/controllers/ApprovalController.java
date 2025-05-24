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
import org.example.belsign.bll.OrderManager;


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

    private Button productButton;

    public void setProductButton(Button productButton) {
        this.productButton = productButton;
    }



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
                "Front", "Back", "Left",
                "Right", "Top", "Bottom"
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
                vbox.setStyle("-fx-cursor: hand;");

                // 🔴 Click to disapprove (highlight red border)
                vbox.setOnMouseClicked(e -> {
                    if (!disapproveMode) return;

                    StackPane imagePane = (StackPane) vbox.getChildren().get(1);

                    if (disapprovedBoxes.contains(vbox)) {
                        // Already selected: unselect
                        disapprovedBoxes.remove(vbox);
                        imagePane.setStyle("-fx-border-color: #333333; -fx-border-width: 1; -fx-border-radius: 5; -fx-border-style: solid;");
                    } else {
                        // Select
                        disapprovedBoxes.add(vbox);
                        imagePane.setStyle("-fx-border-color: red; -fx-border-width: 2; -fx-border-radius: 5;");
                    }
                });


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
