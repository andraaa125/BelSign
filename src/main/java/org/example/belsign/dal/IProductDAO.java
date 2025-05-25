package org.example.belsign.dal;

import org.example.belsign.be.Product;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IProductDAO {
    List<Product> getAllProduct() throws SQLException, IOException;

    void updateProductImage(Product product, String slotType, String imagePath);

    void updateProductStatus(int productId, String newStatus) throws SQLException;
}
