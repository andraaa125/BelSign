package org.example.belsign.bll;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.belsign.be.Order;
import org.example.belsign.be.Product;
import org.example.belsign.dal.IOrderDAO;
import org.example.belsign.dal.db.DBConnection;
import org.example.belsign.dal.db.OrderDAODB;

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

    public void updateOrderStatus(String orderId, String pending) {
        try {
            orderDAO.updateOrderStatus(orderId, pending);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update order status", e);
        }
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

        try (Connection conn = getConnection(); // Your DB connection
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Assuming you want to extract 4 products from Product_1 to Product_4
                for (int i = 1; i <= 4; i++) {
                    String productName = rs.getString("Product_" + i);

                    if (productName != null && !productName.isEmpty()) {
                        products.add(new Product(productName, productName)); // Adjust constructor if needed
                    }
                }
            }
        }
        return products;
    }
}
