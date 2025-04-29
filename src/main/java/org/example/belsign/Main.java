package org.example.belsign;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("BelSign");
        stage.setScene(scene);
        stage.show();
    }
// hello from Andra&Haliimo
    //my super cool code
    //123
    public static void main(String[] args) {
        launch();
    }
}