package org.example.belsign;

import org.example.belsign.be.Product;
import org.example.belsign.dal.db.ProductDAODB;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductDAODBTest {

    private ProductDAODB dao;
    private static final int TEST_PRODUCT_ID = 1;
    private static final String TEST_ORDER_ID = "077-50900";

    @BeforeEach
    public void setUp() {
        dao = new ProductDAODB();
    }

    @Test
    @Order(1)
    public void testGetAllProduct_ShouldReturnList() throws SQLException, IOException {
        List<Product> products = dao.getAllProduct();

        assertNotNull(products, "Product list should not be null");
        assertFalse(products.isEmpty(), "Product list should not be empty if test data exists");
    }

    @Test
    @Order(2)
    public void testUpdateProductStatus_ShouldUpdateStatusInDB() throws SQLException, IOException {
        dao.updateProductStatus(TEST_PRODUCT_ID, "Approved");

        // Re-fetch and assert
        List<Product> products = dao.getAllProduct();
        Product updated = products.stream()
                .filter(p -> p.getProductId() == TEST_PRODUCT_ID)
                .findFirst()
                .orElse(null);

        assertNotNull(updated, "Updated product should exist in database");
        assertEquals("Approved", updated.getStatus(), "Status should be updated to 'Approved'");
    }

    @Test
    @Order(3)
    public void testUpdateProductImage_ShouldUpdateColumnInDB() throws SQLException, IOException {
        byte[] imageBytes = {1, 2, 3, 4, 5};

        Product testProduct = new Product();
        testProduct.setOrderId(TEST_ORDER_ID);
        testProduct.setProductId(TEST_PRODUCT_ID);

        dao.updateProductImage(testProduct, "Image_FRONT", imageBytes);

        // Fetch product and verify image was set
        List<Product> products = dao.getAllProduct();
        Product fetched = products.stream()
                .filter(p -> p.getProductId() == TEST_PRODUCT_ID)
                .findFirst()
                .orElse(null);

        assertNotNull(fetched, "Fetched product should not be null");
        assertNotNull(fetched.getImageFront(), "Front image should not be null after update");
        assertArrayEquals(imageBytes, fetched.getImageFront(), "Image data should match what was saved");
    }
}
