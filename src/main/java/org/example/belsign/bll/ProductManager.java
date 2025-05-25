package org.example.belsign.bll;

import org.example.belsign.be.Product;
import org.example.belsign.dal.IProductDAO;
import org.example.belsign.dal.db.ProductDAODB;

import java.sql.SQLException;
import java.util.Map;

public class ProductManager {
    private Map<String, Product> products;
    private final IProductDAO productDAODB = new ProductDAODB();

    public void saveProductImage(Product product, String slotType, String imagePath) throws SQLException {
        productDAODB.updateProductImage(product, slotType, imagePath);
    }
    public void updateProductStatus(int productId, String newStatus) throws SQLException {
        productDAODB.updateProductStatus(productId, newStatus);
    }

}
