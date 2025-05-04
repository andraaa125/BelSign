package org.example.belsign;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import org.example.belsign.gui.model.ImageContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ImageContextTest {
    @Test
    void testAddImageToContext() {
        StackPane dummyPane = new StackPane();
        Image dummyImage = new WritableImage(10, 10); // creates a safe in-memory image

        ImageContext.capturedImages.put(dummyPane, dummyImage);
        assertTrue(ImageContext.capturedImages.containsKey(dummyPane));
    }

}
