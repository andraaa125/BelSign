package org.example.belsign.be;

import javafx.scene.image.Image;

public class ImageOrder {
    private String orderId;
    private Image image;
    private int slotNumber;
    private String fileName;

    public ImageOrder(String orderId, Image image, int slotNumber, String fileName) {
        this.orderId = orderId;
        this.image = image;
        this.slotNumber = slotNumber;
        this.fileName = fileName;
    }

    public String getOrderId() { return orderId; }
    public Image getImage() { return image; }
    public int getSlotNumber() { return slotNumber; }
    public String getFileName() { return fileName; }
}
