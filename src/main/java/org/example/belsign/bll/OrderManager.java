package org.example.belsign.bll;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.belsign.be.Order;
import org.example.belsign.dal.IOrderDAO;
import org.example.belsign.dal.db.DBConnection;
import org.example.belsign.dal.db.OrderDAODB;

import java.io.IOException;
import java.sql.*;
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

    public ObservableList<String> searchOrders(String query) {
        ObservableList<String> orderID = FXCollections.observableArrayList();

        String sql = "SELECT OrderID FROM [Order] WHERE OrderID LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, query + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orderID.add(rs.getString("OrderID"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderID;
    }
}
