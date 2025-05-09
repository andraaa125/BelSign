package org.example.belsign.gui.model;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class ImageContext {
    public static Map<StackPane, Image> capturedImages = new HashMap<>();
    public static StackPane selectedStackPane = null;

}
