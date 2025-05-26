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
                    Product product = new Product();

                    product.setOrderId(rs.getString("OrderID"));
                    product.setProductId(rs.getInt("ProductId"));
                    product.setProduct(rs.getString("Product"));
                    product.setStatus(rs.getString("Status"));

                    product.setImageFront(rs.getBytes("Image_FRONT"));
                    product.setImageBack(rs.getBytes("Image_BACK"));
                    product.setImageLeft(rs.getBytes("Image_LEFT"));
                    product.setImageRight(rs.getBytes("Image_RIGHT"));
                    product.setImageTop(rs.getBytes("Image_TOP"));
                    product.setImageBottom(rs.getBytes("Image_BOTTOM"));

                    for (int i = 1; i <= 20; i++) {
                        String colName = "Additional_" + i;
                        product.setAdditionalImage(colName, rs.getBytes(colName));
                    }


                    products.add(product);
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


    public void updateProductStatus(int productId, String newStatus) throws SQLException {
        String sql = "UPDATE Product SET status = ? WHERE productId = ?";
        try (Connection conn =  getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }

    public byte[] getImageData(String orderId, String columnName) throws IOException {
        return orderDAO.getImageData(orderId, columnName);
    }


//    public List<Order> getOrdersFromProducts() throws SQLException {
//        return orderDAO.getOrdersFromProducts();
//    }


}
