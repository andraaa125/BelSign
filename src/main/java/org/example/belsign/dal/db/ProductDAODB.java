package org.example.belsign.dal.db;

import org.example.belsign.be.Product;
import org.example.belsign.dal.IProductDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAODB implements IProductDAO {
    private final DBConnection con = new DBConnection();

    @Override
    public List<Product> getAllProduct() throws SQLException, IOException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT OrderID, Product, Image_FRONT, Image_BACK, Image_RIGHT, Image_LEFT, Image_TOP, Image_BOTTOM FROM [Product]";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String orderId = rs.getString("OrderID");
                String product = rs.getString("Product");
                String image_front = rs.getString("Image_FRONT");
                String image_back = rs.getString("Image_BACK");
                String image_right = rs.getString("Image_RIGHT");
                String image_left = rs.getString("Image_LEFT");
                String image_top = rs.getString("Image_TOP");
                String image_bottom = rs.getString("Image_BOTTOM");

                System.out.println("Fetched order ID: " + orderId + ", Product: " + product);

                products.add(new Product(orderId, product, image_front, image_back, image_right, image_left, image_top, image_bottom));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch orders", e);
        }

        return products;
    }

    public void updateProductImage(Product product, String slotType, String imagePath) {
        String columnName = switch (slotType) {
            case "FRONT" -> "Image_FRONT";
            case "BACK" -> "Image_BACK";
            case "LEFT" -> "Image_LEFT";
            case "RIGHT" -> "Image_RIGHT";
            case "TOP" -> "Image_TOP";
            case "BOTTOM" -> "Image_BOTTOM";
            default -> throw new IllegalArgumentException("Invalid slot type: " + slotType);
        };

        String sql = "UPDATE [Product] SET " + columnName + " = ? WHERE OrderID = ? AND Product = ?";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, imagePath);
            ps.setString(2, product.getOrderId());
            ps.setString(3, product.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
