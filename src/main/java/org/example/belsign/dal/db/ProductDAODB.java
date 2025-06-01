package org.example.belsign.dal.db;

import org.example.belsign.be.Product;
import org.example.belsign.dal.IProductDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                product.setProductId(rs.getInt("ProductId"));
                product.setProduct(rs.getString("Product"));
                product.setStatus(rs.getString("Status"));

                product.setImageFront(rs.getBytes("Image_FRONT"));
                product.setImageBack(rs.getBytes("Image_BACK"));
                product.setImageRight(rs.getBytes("Image_RIGHT"));
                product.setImageLeft(rs.getBytes("Image_LEFT"));
                product.setImageTop(rs.getBytes("Image_TOP"));
                product.setImageBottom(rs.getBytes("Image_BOTTOM"));

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
        String sql = "UPDATE [Product] SET " + columnName + " = ? WHERE OrderID = ? AND ProductId = ?";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (imageData != null) {
                ps.setBytes(1, imageData);
            } else {
                ps.setNull(1, Types.VARBINARY);
            }

            ps.setString(2, product.getOrderId());
            ps.setInt(3, product.getProductId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update image in column: " + columnName, e);
        }
    }

    @Override
    public void updateProductStatus(int productId, String newStatus) throws SQLException {
        String sql = "UPDATE Product SET status = ? WHERE productId = ?";
        try (Connection conn =  con.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }
}