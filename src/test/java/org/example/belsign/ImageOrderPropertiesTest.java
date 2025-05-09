package org.example.belsign;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.example.belsign.be.ImageOrder;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ImageOrderPropertiesTest {

    @Test
    void testImageOrderProperties() {
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);

        ImageOrder imgOrder = new ImageOrder("WO-123", fxImage, 1, "WO-123_1.png");

        assertEquals("WO-123", imgOrder.getOrderId());
        assertEquals(1, imgOrder.getSlotNumber());
        assertEquals("WO-123_1.png", imgOrder.getFileName());
        assertNotNull(imgOrder.getImage());
    }
}
