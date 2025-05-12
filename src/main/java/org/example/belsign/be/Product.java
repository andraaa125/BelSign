package org.example.belsign.be;

public class Product {
    private String productName;
    private String productImage;

    public Product(String productName, String productImage) {
        this.productName = productName;
        this.productImage = productImage;
    }


    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getProductImage() {
        return productImage;
    }
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getName() {
        return productName;
    }
}
