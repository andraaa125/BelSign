package org.example.belsign.be;

public class Order {
    private String orderId;
    private String operatorFirstName;
    private String operatorLastName;
    private String image_front;
    private String image_back;
    private String image_right;
    private String image_left;
    private String image_top;
    private String image_bottom;
    private String status;


    public Order(String orderId, String status, String operatorFirstName, String operatorLastName, String image_front, String image_back, String image_right, String image_left, String image_top, String image_bottom) {
        this.orderId = orderId;
        this.status = status;
        this.operatorFirstName = operatorFirstName;
        this.operatorLastName = operatorLastName;
        this.image_front = image_front;
        this.image_back = image_back;
        this.image_right = image_right;
        this.image_left = image_left;
        this.image_top = image_top;
        this.image_bottom = image_bottom;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getImage_front() {
        return image_front;
    }

    public void setImage_front(String image_front) {
        this.image_front = image_front;
    }

    public String getImage_back() {
        return image_back;
    }

    public void setImage_back(String image_back) {
        this.image_back = image_back;
    }

    public String getImage_right() {
        return image_right;
    }

    public void setImage_right(String image_right) {
        this.image_right = image_right;
    }

    public String getImage_left() {
        return image_left;
    }

    public void setImage_left(String image_left) {
        this.image_left = image_left;
    }

    public String getImage_top() {
        return image_top;
    }

    public void setImage_top(String image_top) {
        this.image_top = image_top;
    }

    public String getImage_bottom() {
        return image_bottom;
    }

    public void setImage_bottom(String image_bottom) {
        this.image_bottom = image_bottom;
    }

    @Override
    public String toString() {
        return "OrderID: " + orderId + ", Status: " + status;
    }

}
