package org.example.belsign.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.belsign.be.ImageOrder;
import org.example.belsign.be.Order;
import org.example.belsign.bll.ImageOrderManager;
import org.example.belsign.gui.model.ImageContext;

import java.io.IOException;
import java.util.List;

public class DocumentationController {

    private Order order;

    @FXML private StackPane stackOne;
    @FXML private StackPane stackTwo;
    @FXML private StackPane stackThree;
    @FXML private StackPane stackFour;
    @FXML private StackPane stackFive;

    @FXML private Button btnSend;
    @FXML private Button btnCancel;

    @FXML
    public void onClickSend(ActionEvent actionEvent) {
        try {
            ImageOrderManager manager = new ImageOrderManager();

            // Pane list with fixed slot order
            StackPane[] panes = {stackOne, stackTwo, stackThree, stackFour, stackFive};

            for (int i = 0; i < panes.length; i++) {
                StackPane pane = panes[i];
                Image image = ImageContext.capturedImages.get(pane);
                if (image != null) {
                    int slotNumber = i + 1;
                    String fileName = order.getOrderId() + "_" + slotNumber + ".png";

                    ImageOrder imgOrder = new ImageOrder(order.getOrderId(), image, slotNumber, fileName);
                    manager.saveImage(imgOrder);
                }
            }

            ImageContext.capturedImages.clear();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onClickCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setOrder(Order order) {
        this.order = order;
        System.out.println("Loaded order: " + order.getOrderId());

        try {
            ImageOrderManager manager = new ImageOrderManager();
            List<ImageOrder> images = manager.getImagesForOrder(order.getOrderId());

            StackPane[] panes = {stackOne, stackTwo, stackThree, stackFour, stackFive};

            for (ImageOrder img : images) {
                int slot = img.getSlotNumber();
                if (slot >= 1 && slot <= 5) {
                    StackPane pane = panes[slot - 1];
                    ImageView iv = new ImageView(img.getImage());
                    iv.setPreserveRatio(true);
                    iv.setFitWidth(180);
                    iv.setFitHeight(130);
                    pane.getChildren().setAll(iv);

                    ImageContext.capturedImages.put(pane, img.getImage());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openCameraView(MouseEvent event) throws IOException {
        StackPane clickedPane = (StackPane) event.getSource();
        ImageContext.selectedStackPane = clickedPane;
        loadCameraView();
    }

    public void loadCameraView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/belsign/CameraView.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Open Camera");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
