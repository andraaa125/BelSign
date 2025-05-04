package org.example.belsign.dal.db;

import org.example.belsign.be.Order;
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
        String sql = "SELECT OrderID, Status, Operator_First_Name, Operator_Last_Name, Image_1, Image_2, Image_3, Image_4, Image_5 FROM [Order]";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String orderId = rs.getString("OrderID");
                String status = rs.getString("Status");
                String operator_first_name = rs.getString("Operator_First_Name");
                String operator_last_name = rs.getString("Operator_Last_Name");
                String image_1 = rs.getString("Image_1");
                String image_2 = rs.getString("Image_2");
                String image_3 = rs.getString("Image_3");
                String image_4 = rs.getString("Image_4");
                String image_5 = rs.getString("Image_5");

                // Debugging output
                System.out.println("Fetched order ID: " + orderId + ", Status: " + status);

                // If status is null, set a default value
                if (status == null) {
                    status = "Unknown";
                }

                // Create Order object with more fields, including images
                orders.add(new Order(orderId, status, operator_first_name, operator_last_name, image_1, image_2, image_3, image_4, image_5));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch orders", e);
        }

        return orders;
    }


}


