package org.example.belsign.bll;

import org.example.belsign.be.Product;
import org.example.belsign.dal.IProductDAO;
import org.example.belsign.dal.db.DBConnection;
import org.example.belsign.dal.db.ProductDAODB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    private final IProductDAO productDAO = new ProductDAODB();

    public void saveProductImage(Product product, String slotType, byte[] imageData) throws SQLException {
        productDAO.updateProductImage(product, slotType, imageData);

        if (slotType.startsWith("Additional_")) {
            product.setAdditionalImage(slotType, imageData);
        } else {
            switch (slotType) {
                case "Image_FRONT" -> product.setImageFront(imageData);
                case "Image_BACK" -> product.setImageBack(imageData);
                case "Image_LEFT" -> product.setImageLeft(imageData);
                case "Image_RIGHT" -> product.setImageRight(imageData);
                case "Image_TOP" -> product.setImageTop(imageData);
                case "Image_BOTTOM" -> product.setImageBottom(imageData);
                default -> throw new IllegalArgumentException("Unsupported image slot: " + slotType);
            }
        }
    }

    public byte[] getImageData(Product product, String columnName) {
        if (columnName.startsWith("Additional_")) {
            return product.getAdditionalImage(columnName);
        }

        return switch (columnName) {
            case "Image_FRONT" -> product.getImageFront();
            case "Image_BACK" -> product.getImageBack();
            case "Image_LEFT" -> product.getImageLeft();
            case "Image_RIGHT" -> product.getImageRight();
            case "Image_TOP" -> product.getImageTop();
            case "Image_BOTTOM" -> product.getImageBottom();
            default -> null;
        };
    }

    public List<Product> getProductsForOrder(String orderId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE OrderID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setOrderId(rs.getString("OrderID"));
                    product.setProductId(rs.getInt("ProductId"));
                    product.setProduct(rs.getString("Product"));
                    product.setStatus(rs.getString("Status"));
                    product.setQcComment(rs.getString("QC_comment"));  // ✅ Load QC comment
                    product.setRejectedImages(rs.getString("Rejected_Images"));  // ✅ Load rejected images


                    product.setImageFront(rs.getBytes("Image_FRONT"));
                    product.setImageBack(rs.getBytes("Image_BACK"));
                    product.setImageLeft(rs.getBytes("Image_LEFT"));
                    product.setImageRight(rs.getBytes("Image_RIGHT"));
                    product.setImageTop(rs.getBytes("Image_TOP"));
                    product.setImageBottom(rs.getBytes("Image_BOTTOM"));

                    for (int i = 1; i <= 20; i++) {
                        String col = "Additional_" + i;
                        product.setAdditionalImage(col, rs.getBytes(col));
                    }

                    products.add(product);
                }
            }
        }

        return products;
    }

    public void updateProductStatus(int productId, String newStatus) throws SQLException {
        productDAO.updateProductStatus(productId, newStatus);
    }

    public void updateQcComment(int productId, String comment) throws SQLException {
        String sql = "UPDATE Product SET QC_comment = ? WHERE ProductId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, comment);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }

    public void updateRejectedImages(int productId, String columns) throws SQLException {
        String sql = "UPDATE Product SET Rejected_Images = ? WHERE ProductId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, columns);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }
}
