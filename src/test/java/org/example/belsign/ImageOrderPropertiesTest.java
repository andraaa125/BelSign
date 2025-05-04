package org.example.belsign;

import javafx.scene.image.Image;
import org.example.belsign.be.ImageOrder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ImageOrderPropertiesTest {
    @Test
    void testImageOrderProperties() {
        Image dummyImage = new Image("https://via.placeholder.com/150");
        ImageOrder order = new ImageOrder("ORD123", dummyImage, 1, "ORD123_1.png");

        assertEquals("ORD123", order.getOrderId());
        assertEquals(1, order.getSlotNumber());
        assertEquals("ORD123_1.png", order.getFileName());
        assertNotNull(order.getImage());
    }
}
