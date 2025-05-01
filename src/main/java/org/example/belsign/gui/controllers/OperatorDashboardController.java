package org.example.belsign.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class OperatorDashboardController {

    public void onClickDocument(ActionEvent actionEvent) throws IOException {
        loadDocumentView();
    }

    private void loadDocumentView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/belsign/DocumentationView.fxml"));
        System.out.println(getClass().getResource("OperatorDashboard.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Photo Documentation");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
