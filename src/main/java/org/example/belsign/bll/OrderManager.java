package org.example.belsign.bll;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.example.belsign.be.Order;
import org.example.belsign.be.Product;
import org.example.belsign.dal.IOrderDAO;
import org.example.belsign.dal.db.DBConnection;
import org.example.belsign.dal.db.OrderDAODB;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.belsign.dal.db.DBConnection.getConnection;

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

        String sql = "SELECT OrderID FROM [Order] WHERE Status = 'Done' AND OrderID LIKE ?";

        try (Connection con = getConnection();
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

    public List<Product> getProductsForOrder(String orderId) throws SQLException {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM Product WHERE OrderID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String productName = rs.getString("Product");

                    if (productName != null && !productName.isEmpty()) {
                        products.add(new Product(orderId, productName));

                    }
                }
            }
        }

        return products;
    }

    public Order getOrderById(String orderId) throws SQLException {
        try {
            return orderDAO.getOrderById(orderId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve order", e);
        }
    }


}
