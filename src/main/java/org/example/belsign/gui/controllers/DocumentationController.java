package org.example.belsign.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.example.belsign.be.ImageOrder;
import org.example.belsign.be.Order;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import org.example.belsign.be.Order;
import org.example.belsign.bll.ImageOrderManager;
import org.example.belsign.gui.model.ImageContext;

public class DocumentationController {
    private Order order;

    @FXML
    public void onClickSend(ActionEvent actionEvent) {
        try {
            ImageOrderManager manager = new ImageOrderManager();

            for (Image image : ImageContext.capturedImages.values()) {
                ImageOrder imageOrder = new ImageOrder(order.getOrderId(), image);
                manager.saveImage(imageOrder);  // You already have this method
            }

            // Optional: clear map after upload
            ImageContext.capturedImages.clear();

            // Close the documentation window
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void onClickCancel(ActionEvent actionEvent) {
    }


    public void setOrder(Order order) {
        this.order = order;
        // Use this order to display details in the new window
        System.out.println("Order details: " + order);
    }


    @FXML
    public void openCameraView(MouseEvent event) throws IOException {
        StackPane clickedPane = (StackPane) event.getSource();
        ImageContext.selectedStackPane = clickedPane;  // Track it
        loadCameraView();
    }

    public void loadCameraView() throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/belsign/CameraView.fxml"));
            System.out.println(getClass().getResource("CameraView.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Open Camera");
            stage.setScene(new Scene(root));
            stage.show();
    }
}
