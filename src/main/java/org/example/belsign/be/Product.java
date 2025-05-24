package org.example.belsign.be;

import java.util.HashMap;
import java.util.Map;

public class Product {
    private String orderId;
    private String productId;
    private String product;
    private String status;

    // Now use byte[] for images instead of String paths
    private byte[] imageFront;
    private byte[] imageBack;
    private byte[] imageLeft;
    private byte[] imageRight;
    private byte[] imageTop;
    private byte[] imageBottom;

    private Map<String, byte[]> additionalImages = new HashMap<>();

    public Product() {}

    public Product(String orderId, String product) {
        this.orderId = orderId;
        this.product = product;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getImageFront() {
        return imageFront;
    }

    public void setImageFront(byte[] imageFront) {
        this.imageFront = imageFront;
    }

    public byte[] getImageBack() {
        return imageBack;
    }

    public void setImageBack(byte[] imageBack) {
        this.imageBack = imageBack;
    }

    public byte[] getImageLeft() {
        return imageLeft;
    }

    public void setImageLeft(byte[] imageLeft) {
        this.imageLeft = imageLeft;
    }

    public byte[] getImageRight() {
        return imageRight;
    }

    public void setImageRight(byte[] imageRight) {
        this.imageRight = imageRight;
    }

    public byte[] getImageTop() {
        return imageTop;
    }

    public void setImageTop(byte[] imageTop) {
        this.imageTop = imageTop;
    }

    public byte[] getImageBottom() {
        return imageBottom;
    }

    public void setImageBottom(byte[] imageBottom) {
        this.imageBottom = imageBottom;
    }

    public Map<String, byte[]> getAdditionalImages() {
        return additionalImages;
    }

    public void setAdditionalImages(Map<String, byte[]> additionalImages) {
        this.additionalImages = additionalImages;
    }

    public void setAdditionalImage(String key, byte[] value) {
        this.additionalImages.put(key, value);
    }

    public byte[] getAdditionalImage(String key) {
        return this.additionalImages.get(key);
    }

    @Override
    public String toString() {
        return "Product{" +
                "orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", product='" + product + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
