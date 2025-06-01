package org.example.belsign.be;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Product {
    private String productName;
    private String orderId;
    private String product;
    private String status;
    private int productId;

    private List<Product> products;
    private String qcComment;

    private byte[] imageFront;
    private byte[] imageBack;
    private byte[] imageLeft;
    private byte[] imageRight;
    private byte[] imageTop;
    private byte[] imageBottom;
    private String rejectedImages;

    public Product(String productName, byte[] imageFront, byte[] imageBack, byte[] imageRight, byte[] imageLeft, byte[] imageTop,byte[] imageBottom, String status, int productId) {
        this.productName = productName;
        this.imageFront = imageFront;
        this.imageBack = imageBack;
        this.imageRight = imageRight;
        this.imageLeft = imageLeft;
        this.imageTop = imageTop;
        this.imageBottom = imageBottom;
        this.status = status;
        this.productId = productId;
    }

    private Map<String, byte[]> additionalImages = new HashMap<>();

    public Product() {}

    public Product(String orderId, String productName, String status) {
        this.orderId = orderId;
        this.productName = productName;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public Product(String orderId, String product, byte[] imageFront, byte[] imageBack, byte[] imageRight, byte[] imageLeft, byte[] imageTop, byte[] imageBottom, String status,  int productId) {
        this.orderId = orderId;
        this.product = product;
        this.imageFront = imageFront;
        this.imageBack = imageBack;
        this.imageRight = imageRight;
        this.imageLeft = imageLeft;
        this.imageTop = imageTop;
        this.imageBottom = imageBottom;
        this.status = status;
        this.productId = productId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product, String status,int productId) {
        this.product = product;
        this.status = status;
        this.productId = productId;
    }

    public Product(String orderId, String product, byte[] imageFront, byte[] imageBack, byte[] imageRight, byte[] imageLeft, byte[] imageTop, byte[] imageBottom, int productId, String status) {
        this.orderId = orderId;
        this.product = product;
        this.imageFront = imageFront;
        this.imageBack = imageBack;
        this.imageRight = imageRight;
        this.imageLeft = imageLeft;
        this.imageTop = imageTop;
        this.imageBottom = imageBottom;
        this.productId = productId;
        this.status = status;
    }

    public String getQcComment() {
        return qcComment;
    }

    public String getRejectedImages() {
        return rejectedImages;
    }

    public void setRejectedImages(String rejectedImages) {
        this.rejectedImages = rejectedImages;
    }

    public void setQcComment(String qcComment) {
        this.qcComment = qcComment;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getName() {
        return  productName;
        }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setImageRight(byte[] imageRights) {
        this.imageRight = imageRights;
    }

    public byte[] getImageRight() {
        return imageRight;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "Product{" +
                "orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}