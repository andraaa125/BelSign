package org.example.belsign.dal.db;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.example.belsign.be.Order;
import org.example.belsign.be.Product;
import org.example.belsign.dal.IOrderDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAODB implements IOrderDAO {
    private final DBConnection con = new DBConnection();

    @Override
    public void updateOrderStatus(String orderId, String newStatus) throws IOException {
        String sql = "UPDATE [Order] SET Status = ? WHERE OrderID = ?"; // Use your actual table and column names
        System.out.println("Updating OrderID: " + orderId + " with new Status: " + newStatus); // Debug log
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, orderId);
            int affectedRows = ps.executeUpdate();
            System.out.println("Affected rows: " + affectedRows); // Debug log for affected rows
            if (affectedRows == 0) {
                System.out.println("No rows were updated, check if OrderID exists.");
            }
        } catch (SQLException e) {
            System.out.println("SQLException occurred: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    @Override
    public List<Order> getAllOrders() throws IOException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT OrderID, Status, Operator_First_Name, Operator_Last_Name, Image_FRONT, Image_BACK, Image_RIGHT, Image_LEFT, Image_TOP, Image_BOTTOM FROM [Order]";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String orderId = rs.getString("OrderID");
                String status = rs.getString("Status");
                String operator_first_name = rs.getString("Operator_First_Name");
                String operator_last_name = rs.getString("Operator_Last_Name");
                String image_front = rs.getString("Image_FRONT");
                String image_back = rs.getString("Image_BACK");
                String image_right = rs.getString("Image_RIGHT");
                String image_left = rs.getString("Image_LEFT");
                String image_top = rs.getString("Image_TOP");
                String image_bottom = rs.getString("Image_BOTTOM");

                // Debugging output
                System.out.println("Fetched order ID: " + orderId + ", Status: " + status);

                // If status is null, set a default value
                if (status == null) {
                    status = "Unknown";
                }

                // Create Order object with more fields, including images
                orders.add(new Order(orderId, status, operator_first_name, operator_last_name, image_front, image_back, image_right, image_left, image_top, image_bottom));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch orders", e);
        }

        return orders;
    }

    @Override
    public void saveDefaultImage(String orderId, String columnName, byte[] imageData) throws IOException {
        String sql = "UPDATE [Order] SET " + columnName + " = ? WHERE OrderID = ?";
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBytes(1, imageData);
            ps.setString(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Failed to save default image: " + columnName, e);
        }
    }

    @Override
    public void saveAdditionalImageColumn(String columnName) throws IOException {
        String checkColumn = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Order' AND COLUMN_NAME = ?";
        String alterTable = "ALTER TABLE [Order] ADD " + columnName + " VARBINARY(MAX)";
        try (Connection connection = con.getConnection();
             PreparedStatement check = connection.prepareStatement(checkColumn)) {
            check.setString(1, columnName);
            ResultSet rs = check.executeQuery();
            if (!rs.next()) {
                try (PreparedStatement alter = connection.prepareStatement(alterTable)) {
                    alter.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new IOException("Failed to create column: " + columnName, e);
        }
    }

    @Override
    public void saveAdditionalImageData(String orderId, String columnName, byte[] imageData) throws IOException {
        String sql = "UPDATE [Order] SET " + columnName + " = ? WHERE OrderID = ?";
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBytes(1, imageData);
            ps.setString(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Failed to save additional image in column: " + columnName, e);
        }
    }

    @Override
    public void addColumnToOrderTable(String columnName) throws IOException {
        String sql = "ALTER TABLE [Order] ADD [" + columnName + "] VARBINARY(MAX)";
        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            // Optional: only ignore if it's a "duplicate column" error
            if (!e.getMessage().toLowerCase().contains("duplicate")) {
                throw new IOException("Failed to add column to table: " + columnName, e);
            }
        }
    }

    @Override
    public boolean doesColumnExist(String columnName) throws IOException {
        String query = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Order' AND COLUMN_NAME = ?";
        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();  // true if column exists
            }
        } catch (SQLException e) {
            throw new IOException("Failed to check column existence: " + columnName, e);
        }
    }

    @Override
    public void updateOrderImageColumn(String orderId, String columnName, byte[] imageData) throws IOException {
        // Reuse your existing logic
        saveAdditionalImageData(orderId, columnName, imageData);
    }

    @Override
    public byte[] getImageData(String orderId, String columnName) throws IOException {
        String sql = "SELECT " + columnName + " FROM [Order] WHERE OrderID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBytes(columnName);
            }
        } catch (SQLException e) {
            throw new IOException("Failed to load image from DB", e);
        }
        return null;
    }

    public void deleteImageData(String orderId, String columnName) throws IOException {
        String sql = "UPDATE [Order] SET " + columnName + " = NULL WHERE OrderId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Failed to delete image from column: " + columnName, e);
        }
    }

    @Override
    public Order getOrderById(String orderId) throws IOException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT OrderID, Status, Operator_First_Name, Operator_Last_Name, " +
                             "Image_FRONT, Image_BACK, Image_RIGHT, Image_LEFT, Image_TOP, Image_BOTTOM " +
                             "FROM [Order] WHERE OrderID = ?")) {

            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Order(
                        rs.getString("OrderID"),
                        rs.getString("Status"),
                        rs.getString("Operator_First_Name"),
                        rs.getString("Operator_Last_Name"),
                        rs.getString("Image_FRONT"),
                        rs.getString("Image_BACK"),
                        rs.getString("Image_RIGHT"),
                        rs.getString("Image_LEFT"),
                        rs.getString("Image_TOP"),
                        rs.getString("Image_BOTTOM")
                );
            }
        } catch (SQLException e) {
            throw new IOException("Failed to fetch order by ID", e);
        }
        return null;
    }
}
//
//    public List<Order> getOrdersFromProducts() {
//        List<Order> orders = new ArrayList<>();
//        String sql = "SELECT DISTINCT OrderID FROM Product WHERE Status = 'Pending approval'";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql);
//             ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                String orderId = rs.getString("OrderID");
//                Order order = getOrderById(orderId);
//                if (order != null) {
//                    List<Product> products = getProductsForOrder(orderId);
//                    order.setProducts(products);
//                    order.setStatus("Pending approval");
//                    orders.add(order);
//                }
//            }
//        } catch (IOException | SQLServerException e) {
//            throw new RuntimeException(e);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return orders;
//    }
//
//    private List<Product> getProductsForOrder(String orderId) throws  SQLException {
//        List<Product> products = new ArrayList<>();
//        String sql = "SELECT OrderID, Product, Image_FRONT, Image_BACK, Image_RIGHT, Image_LEFT, Image_TOP, Image_BOTTOM, ProductId, Status " +
//                "FROM [Product] WHERE OrderID = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, orderId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    int productId = rs.getInt("ProductId");
//                    String status = rs.getString("Status");
//                    String orderID = rs.getString("OrderID");
//                    String productName = rs.getString("Product");
//                    String image_front = rs.getString("Image_FRONT");
//                    String image_back = rs.getString("Image_BACK");
//                    String image_right = rs.getString("Image_RIGHT");
//                    String image_left = rs.getString("Image_LEFT");
//                    String image_top = rs.getString("Image_TOP");
//                    String image_bottom = rs.getString("Image_BOTTOM");
//
//                    Product product = new Product(orderID, productName, image_front, image_back, image_right, image_left, image_top, image_bottom, productId, status);
//                    products.add(product);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return products;
//    }


