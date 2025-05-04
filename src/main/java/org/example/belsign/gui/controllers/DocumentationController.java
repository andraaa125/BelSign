package org.example.belsign.gui.controllers;

import javafx.event.ActionEvent;
import org.example.belsign.be.Order;

public class DocumentationController {
    public void onClickSend(ActionEvent actionEvent) {
    }

    public void onClickCancel(ActionEvent actionEvent) {
    }

    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        // Use this order to display details in the new window
        System.out.println("Order details: " + order);
    }
}
