package org.example.belsign.be;

import javafx.scene.image.Image;

public class ImageOrder {
    private String orderId;
    private Image image;

    public ImageOrder(String orderId, Image image) {
        this.orderId = orderId;
        this.image = image;
    }

    public String getOrderId() {
        return orderId;
    }

    public Image getImage() {
        return image;
    }
}
