package org.example.belsign.be;

public class Product {
    private String productName;
    private String imageFront;
    private String imageBack;
    private String imageRight;
    private String imageLeft;
    private String imageTop;
    private String imageBottom;
    private String orderId;
    private String product;

    public Product(String productName, String imageFront, String imageBack, String imageRight, String imageLeft, String imageTop, String imageBottom) {
        this.productName = productName;
        this.imageFront = imageFront;
        this.imageBack = imageBack;
        this.imageRight = imageRight;
        this.imageLeft = imageLeft;
        this.imageTop = imageTop;
        this.imageBottom = imageBottom;

    }

    public Product(String productName) {
        this.productName = productName;
    }

    public Product(String orderId, String product, String imageFront, String imageBack, String imageRight, String imageLeft, String imageTop, String imageBottom) {
        this.imageFront = imageFront;
        this.imageBack = imageBack;
        this.imageRight = imageRight;
        this.imageLeft = imageLeft;
        this.imageTop = imageTop;
        this.imageBottom = imageBottom;
        this.orderId = orderId;
        this.product = product;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageFront() {
        return imageFront;
    }

    public void setImageFront(String imageFront) {
        this.imageFront = imageFront;
    }

    public String getImageBack() {
        return imageBack;
    }

    public void setImageBack(String imageBack) {
        this.imageBack = imageBack;
    }

    public String getImageRight() {
        return imageRight;
    }

    public void setImageRight(String imageRight) {
        this.imageRight = imageRight;
    }

    public String getImageLeft() {
        return imageLeft;
    }

    public void setImageLeft(String imageLeft) {
        this.imageLeft = imageLeft;
    }

    public String getImageTop() {
        return imageTop;
    }

    public void setImageTop(String imageTop) {
        this.imageTop = imageTop;
    }

    public String getImageBottom() {
        return imageBottom;
    }

    public void setImageBottom(String imageBottom) {
        this.imageBottom = imageBottom;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getName() {
        return productName;
    }
}
