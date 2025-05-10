package org.example.belsign.bll;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.example.belsign.be.Order;
import org.example.belsign.dal.IOrderDAO;
import org.example.belsign.dal.db.DBConnection;
import org.example.belsign.dal.db.OrderDAODB;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class OrderManager {

    private final IOrderDAO orderDAO = new OrderDAODB();

    public List<Order> getAllOrders() throws IOException, SQLException {
        return orderDAO.getAllOrders();
    }

    public void updateOrderStatus(String orderId, String newStatus) {
        try {
            orderDAO.updateOrderStatus(orderId, newStatus);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    public void saveImageToColumn(String orderId, String columnName, byte[] imageData) {
        try {
            if (!orderDAO.doesColumnExist(columnName)) {
                orderDAO.addColumnToOrderTable(columnName);
            }
            orderDAO.updateOrderImageColumn(orderId, columnName, imageData);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image to column: " + columnName, e);
        }
    }

    public Image getImageFromColumn(String orderId, String columnName) throws IOException {
        byte[] imageData = orderDAO.getImageData(orderId, columnName);
        if (imageData != null && imageData.length > 0) {
            return new Image(new ByteArrayInputStream(imageData));
        }
        return null;
    }
    public byte[] getImageData(String orderId, String columnName) throws IOException {
        return orderDAO.getImageData(orderId, columnName);
    }
    public void deleteImageFromColumn(String orderId, String columnName) throws IOException {
        orderDAO.deleteImageData(orderId, columnName);
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
