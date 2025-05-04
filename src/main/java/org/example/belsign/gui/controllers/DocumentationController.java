package org.example.belsign.gui.controllers;

import javafx.event.ActionEvent;
import org.example.belsign.be.Order;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import org.example.belsign.be.Order;

public class DocumentationController {
    private Order order;

    public void onClickSend(ActionEvent actionEvent) {
    }

    public void onClickCancel(ActionEvent actionEvent) {
    }


    public void setOrder(Order order) {
        this.order = order;
        // Use this order to display details in the new window
        System.out.println("Order details: " + order);
    }


    public void openCameraView(MouseEvent mouseEvent) throws IOException{
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
