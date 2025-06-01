package org.example.belsign.be;

import java.util.List;

public class Order {
    private String orderId;
    private String status;
    private String operatorFirstName;
    private String operatorLastName;
    private List<Product> products;

    public Order() {
        // For DAO or serialization
    }

    public Order(String orderId, String status, String operatorFirstName, String operatorLastName) {
        this.orderId = orderId;
        this.status = status;
        this.operatorFirstName = operatorFirstName;
        this.operatorLastName = operatorLastName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperatorFirstName() {
        return operatorFirstName;
    }

    public void setOperatorFirstName(String operatorFirstName) {
        this.operatorFirstName = operatorFirstName;
    }

    public String getOperatorLastName() {
        return operatorLastName;
    }

    public void setOperatorLastName(String operatorLastName) {
        this.operatorLastName = operatorLastName;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "OrderID: " + orderId + ", Status: " + status;
    }
}
