package org.example.belsign.bll;

import org.example.belsign.be.Order;
import org.example.belsign.dal.IOrderDAO;
import org.example.belsign.dal.db.OrderDAODB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OrderManager {
    private final IOrderDAO orderDAO = new OrderDAODB();

    public List<Order> getAllOrders() throws IOException, SQLException {
        return orderDAO.getAllOrders();
    }

    public void updateOrderStatus(String orderId, String pending) {
        try {
            orderDAO.updateOrderStatus(orderId, pending);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update order status", e);
        }
    }
}
