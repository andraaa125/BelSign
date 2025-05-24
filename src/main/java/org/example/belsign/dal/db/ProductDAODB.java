package org.example.belsign.dal.db;

import org.example.belsign.be.Product;
import org.example.belsign.dal.IProductDAO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAODB implements IProductDAO {
    private final DBConnection con = new DBConnection();

    @Override
    public List<Product> getAllProduct() throws SQLException, IOException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM [Product]";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product product = new Product();
                product.setOrderId(rs.getString("OrderID"));
                product.setProductId(rs.getString("ProductId"));
                product.setProduct(rs.getString("Product"));
                product.setStatus(rs.getString("Status"));

                product.setImageFront(rs.getBytes("Image_FRONT"));
                product.setImageBack(rs.getBytes("Image_BACK"));
                product.setImageRight(rs.getBytes("Image_RIGHT"));
                product.setImageLeft(rs.getBytes("Image_LEFT"));
                product.setImageTop(rs.getBytes("Image_TOP"));
                product.setImageBottom(rs.getBytes("Image_BOTTOM"));

                // Load all additional images dynamically
                for (int i = 1; i <= 20; i++) {
                    String col = "Additional_" + i;
                    byte[] data = rs.getBytes(col);
                    product.setAdditionalImage(col, data);
                }

                products.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch products", e);
        }

        return products;
    }

    @Override
    public void updateProductImage(Product product, String columnName, byte[] imageData) {
        // Accepts full column name like "Image_FRONT" or "Additional_5"
        String sql = "UPDATE [Product] SET " + columnName + " = ? WHERE OrderID = ? AND ProductId = ?";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (imageData != null) {
                ps.setBytes(1, imageData);
            } else {
                ps.setNull(1, Types.VARBINARY);
            }

            ps.setString(2, product.getOrderId());
            ps.setString(3, product.getProductId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update image in column: " + columnName, e);
        }
    }
}
