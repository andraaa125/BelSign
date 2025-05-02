/*
 * This file is based on WebcamFX by Houari Zegai
 * Original source: https://github.com/HouariZegai/WebcamFX
 * Licensed under the MIT License
 *
 * Modifications made by Andra Danielevici, may 2025
 *
 */


package org.example.belsign.gui.controllers;

import com.github.sarxos.webcam.Webcam;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.control.Button;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CameraController implements Initializable {
    public static Webcam webcam; // For taking pictures
    public static boolean isCapture = false; // For stop & resume thread of camera

    @FXML
    private StackPane imgContainer;

    @FXML
    private ImageView imageView;

    @FXML
    private Button cancelButton;

    private FileChooser fileChooser; // For select path of saving picture captured

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /* Bind views */
        imageView.fitWidthProperty().bind(imgContainer.widthProperty());
        imageView.fitHeightProperty().bind(imgContainer.heightProperty());

        /* Init camera */
        webcam = Webcam.getDefault();
        if(webcam == null) {
            System.out.println("Camera not found !");
            System.exit(-1);
        }
        webcam.setViewSize(new Dimension(640, 480));
        webcam.open();

        new VideoTacker().start(); // Start camera capture

        /* Init save file chooser */
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images (*.png)", "*.png"));
        fileChooser.setTitle("Save Image");
    }

    @FXML
    private void btnCapture() { // Stop camera & taking picture
        isCapture = true;
    }

    @FXML
    private void btnReload() { // Resume camera capture
        isCapture = false;
        webcam.open();
        new VideoTacker().start();

    }

    @FXML
    private void btnSave() {
        isCapture = true; // Stop taking pictures
        webcam.close();

        File file = fileChooser.showSaveDialog(imageView.getScene().getWindow());
        if (file != null)
            try { // Save picture with .png extension
                ImageIO.write(SwingFXUtils.fromFXImage(imageView.getImage(), null), "PNG", file);
            } catch (IOException ex) {
                ex.printStackTrace(); // Can't save picture
            }
    }

    @FXML
    public void btnCancel() {
        webcam.close();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

    }

    class VideoTacker extends Thread {
        @Override
        public void run() {
            while (!isCapture) { // For each 30 millisecond take picture and set it in image view
                try {
                    imageView.setImage(SwingFXUtils.toFXImage(webcam.getImage(), null));
                    sleep(30);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CameraController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
