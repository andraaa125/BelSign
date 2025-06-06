package org.example.belsign.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import org.example.belsign.be.Order;
import org.example.belsign.dal.db.OrderDAODB;

import java.util.List;

public class OrderListViewController {

    private BorderPane mainBorderPane;
    private Node mainView;

    @FXML
    private TableView<Order> orderTable;
    @FXML
    public Button backBtn;
    @FXML
    private TableColumn<Order, String> colOrderID;
    @FXML
    private TableColumn<Order, String> colStatus;
    @FXML
    private TableColumn<Order, String> colFirstName;
    @FXML
    private TableColumn<Order, String> colLastName;

    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    @FXML
    public void initialize() {
        colOrderID.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("operatorFirstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("operatorLastName"));

        loadOrders();
    }

    private void loadOrders() {
        OrderDAODB orderDAO = new OrderDAODB();
        try {
            List<Order> orders = orderDAO.getAllOrders();
            orderTable.getItems().setAll(orders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onClickBack(ActionEvent event) {
        if (mainBorderPane != null && mainView != null) {
            mainBorderPane.setCenter(mainView);
        }
    }

    public void setMainView(Node dashboardMainView) {
        this.mainView = dashboardMainView;
    }
}