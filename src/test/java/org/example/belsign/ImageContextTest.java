package org.example.belsign;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import org.example.belsign.gui.model.ImageContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ImageContextTest {

    @AfterEach
    void clearContext() {
        ImageContext.productCapturedImages.clear();
        ImageContext.selectedStackPane = null;
        ImageContext.currentProductId = null;
    }

    @Test
    void testAddImageToContextForProduct() {
        int testProductId = 101;
        StackPane imageSlotPane = new StackPane();
        Image capturedImage = new WritableImage(10, 10); // create test image

        // Add image to context
        ImageContext.productCapturedImages.putIfAbsent(testProductId, new java.util.HashMap<>());
        ImageContext.productCapturedImages.get(testProductId).put(imageSlotPane, capturedImage);

        // Verify
        Map<StackPane, Image> imageMap = ImageContext.productCapturedImages.get(testProductId);
        assertNotNull(imageMap, "Image map should exist for the given product ID.");
        assertTrue(imageMap.containsKey(imageSlotPane), "Image slot pane should be present in the map.");
        assertSame(capturedImage, imageMap.get(imageSlotPane), "Stored image should match the added image.");
    }

    @Test
    void testSelectedStackPaneAndCurrentProductId() {
        StackPane slotPane = new StackPane();
        int selectedProductId = 42;

        ImageContext.selectedStackPane = slotPane;
        ImageContext.currentProductId = selectedProductId;

        assertSame(slotPane, ImageContext.selectedStackPane, "Selected stack pane should match the assigned pane.");
        assertEquals(selectedProductId, ImageContext.currentProductId, "Current product ID should match the assigned ID.");
    }
}
