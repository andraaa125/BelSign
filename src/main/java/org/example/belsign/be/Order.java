package org.example.belsign.be;

public class Order {
    private String orderId;
    private String operatorFirstName;
    private String operatorLastName;
    private String image_1;
    private String image_2;
    private String image_3;
    private String image_4;
    private String image_5;
    private String status;


    public Order(String orderId, String status, String operatorFirstName, String operatorLastName, String image_1, String image_2, String image_3, String image_4, String image_5) {
        this.orderId = orderId;
        this.status = status;
        this.operatorFirstName = operatorFirstName;
        this.operatorLastName = operatorLastName;
        this.image_1 = image_1;
        this.image_2 = image_2;
        this.image_3 = image_3;
        this.image_4 = image_4;
        this.image_5 = image_5;
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

    public String getImage1() {
        return image_1;
    }

    public void setImage1(String image1) {
        this.image_1 = image1;
    }

    public String getImage2() {
        return image_2;
    }

    public void setImage2(String image2) {
        this.image_2 = image2;
    }

    public String getImage3() {
        return image_3;
    }

    public void setImage3(String image3) {
        this.image_3 = image3;
    }

    public String getImage4() {
        return image_4;
    }

    public void setImage4(String image4) {
        this.image_4 = image4;
    }

    public String getImage5() {
        return image_5;
    }

    public void setImage5(String image5) {
        this.image_5 = image5;
    }

    @Override
    public String toString() {
        return "OrderID: " + orderId + ", Status: " + status;
    }

}
