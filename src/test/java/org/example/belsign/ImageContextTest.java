package org.example.belsign;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.example.belsign.gui.model.ImageContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ImageContextTest {
    @Test
    void testAddImageToContext() {
        StackPane pane = new StackPane();
        Image img = new Image("https://via.placeholder.com/150");
        ImageContext.capturedImages.put(pane, img);

        assertTrue(ImageContext.capturedImages.containsKey(pane));
        assertEquals(img, ImageContext.capturedImages.get(pane));
    }
}
