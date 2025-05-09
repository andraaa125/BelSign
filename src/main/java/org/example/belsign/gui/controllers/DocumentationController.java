package org.example.belsign.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import org.example.belsign.be.ImageOrder;
import org.example.belsign.be.Order;
import org.example.belsign.bll.ImageOrderManager;
import org.example.belsign.gui.model.ImageContext;

import java.io.IOException;
import java.util.List;

public class DocumentationController {

    private Order order;
    private OperatorDashboardController operatorDashboardController;

    @FXML private StackPane stackOne;
    @FXML private StackPane stackTwo;
    @FXML private StackPane stackThree;
    @FXML private StackPane stackFour;
    @FXML private StackPane stackFive;

    @FXML
    public void onClickSend(ActionEvent actionEvent) {
        try {
            ImageOrderManager manager = new ImageOrderManager();
            int slot = 1;
            for (StackPane pane : List.of(stackOne, stackTwo, stackThree, stackFour, stackFive)) {
                Image image = ImageContext.capturedImages.get(pane);
                if (image != null) {
                    String fileName = order.getOrderId() + "_Slot" + slot + ".png";
                    ImageOrder imageOrder = new ImageOrder(order.getOrderId(), image, slot, fileName);
                    manager.saveImage(imageOrder);
                }
                slot++;
            }
            ImageContext.capturedImages.clear();

            if (operatorDashboardController != null) {
                operatorDashboardController.setStatusMessage("Images sent successfully");
            }

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onClickCancel(ActionEvent actionEvent) {
        ImageContext.capturedImages.clear();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setOrder(Order order) {
        this.order = order;
        System.out.println("Order details: " + order);

        try {
            List<ImageOrder> images = new ImageOrderManager().getImagesForOrder(order.getOrderId());
            displayImagesForOrder(images);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOperatorDashboardController(OperatorDashboardController controller) {
        this.operatorDashboardController = controller;
    }

    @FXML
    public void openCameraView(MouseEvent event) throws IOException {
        StackPane clickedPane = (StackPane) event.getSource();
        ImageContext.selectedStackPane = clickedPane;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/belsign/CameraView.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Open Camera");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void displayImagesForOrder(List<ImageOrder> images) {
        for (ImageOrder img : images) {
            switch (img.getSlotNumber()) {
                case 1 -> loadImageIntoStackPane(stackOne, img);
                case 2 -> loadImageIntoStackPane(stackTwo, img);
                case 3 -> loadImageIntoStackPane(stackThree, img);
                case 4 -> loadImageIntoStackPane(stackFour, img);
                case 5 -> loadImageIntoStackPane(stackFive, img);
            }
        }
    }

    private void loadImageIntoStackPane(StackPane pane, ImageOrder imageOrder) {
        ImageView imageView = new ImageView(imageOrder.getImage());
        imageView.setFitWidth(180);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);

        Button deleteBtn = new Button("❌");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 16;");
        deleteBtn.setOnAction(e -> {
            try {
                new ImageOrderManager().deleteImage(imageOrder.getOrderId(), imageOrder.getSlotNumber());
                pane.getChildren().setAll(createAddImageLabel());
                ImageContext.capturedImages.remove(pane);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        VBox vbox = new VBox(5, imageView, deleteBtn);
        vbox.setAlignment(Pos.CENTER);
        pane.getChildren().setAll(vbox);
        ImageContext.capturedImages.put(pane, imageOrder.getImage());
    }

    private Label createAddImageLabel() {
        Label label = new Label("+ Add Image");
        label.setFont(new Font(20));
        return label;
    }
}