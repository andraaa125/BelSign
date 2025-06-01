package org.example.belsign.gui.model;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class ImageContext {

    // Maps product ID → (StackPane → Image)
    public static Map<Integer, Map<StackPane, Image>> productCapturedImages = new HashMap<>();

    // The StackPane currently being updated (when user opens camera)
    public static StackPane selectedStackPane = null;

    // The product ID that the current camera session is targeting
    public static Integer currentProductId = null;

}
