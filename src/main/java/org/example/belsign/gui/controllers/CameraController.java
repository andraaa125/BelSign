package org.example.belsign.gui.controllers;

import com.github.sarxos.webcam.Webcam;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.belsign.gui.model.ImageContext;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CameraController implements Initializable {
    public static Webcam webcam;
    public static boolean isCapture = false;

    @FXML private StackPane imgContainer;
    @FXML private ImageView imageView;
    @FXML private Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageView.fitWidthProperty().bind(imgContainer.widthProperty());
        imageView.fitHeightProperty().bind(imgContainer.heightProperty());

        webcam = Webcam.getDefault();
        if (webcam == null) {
            System.out.println("No webcam found.");
            System.exit(-1);
        }

        webcam.setViewSize(new Dimension(640, 480));
        webcam.open();
        new VideoTacker().start();
    }

    @FXML
    private void btnCapture() {
        isCapture = true;
    }

    @FXML
    private void btnReload() {
        isCapture = false;
        webcam.open();
        new VideoTacker().start();
    }

    @FXML
    private void btnSave() {
        isCapture = true;
        webcam.close();

        javafx.scene.image.Image fxImage = imageView.getImage();

        if (ImageContext.selectedStackPane != null) {
            ImageView iv = new ImageView(fxImage);
            iv.setPreserveRatio(true);
            iv.setFitWidth(180);
            iv.setFitHeight(130);

            // Replace label and store image
            ImageContext.selectedStackPane.getChildren().setAll(iv);
            ImageContext.capturedImages.put(ImageContext.selectedStackPane, fxImage);
        }

        // ✅ Close the camera window immediately
        Stage stage = (Stage) imageView.getScene().getWindow();
        stage.close();
    }


    @FXML
    public void btnCancel() {
        webcam.close();
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    class VideoTacker extends Thread {
        @Override
        public void run() {
            while (!isCapture) {
                try {
                    java.awt.image.BufferedImage bimg = webcam.getImage();
                    if (bimg != null) {
                        imageView.setImage(SwingFXUtils.toFXImage(bimg, null));
                    }
                    sleep(30);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CameraController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
