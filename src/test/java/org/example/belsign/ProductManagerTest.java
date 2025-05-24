package org.example.belsign;

import org.example.belsign.be.Product;
import org.example.belsign.bll.ProductManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ProductManagerTest {

    private ProductManager productManager;
    private Product testProduct;

    @BeforeEach
    public void setup() {
        productManager = new ProductManager();

        testProduct = new Product();
        testProduct.setOrderId("TEST_ORDER");
        testProduct.setProductId("TEST_PRODUCT");
        testProduct.setProduct("Test Product");
        testProduct.setStatus("pending_approval");
    }

    @Test
    public void testSaveFrontImage() throws SQLException, IOException {
        byte[] imageData = Files.readAllBytes(Paths.get("images/test_front.jpg"));
        productManager.saveProductImage(testProduct, "Image_FRONT", imageData);

        assertArrayEquals(imageData, testProduct.getImageFront());
    }

    @Test
    public void testSaveAdditionalImage() throws SQLException, IOException {
        byte[] imageData = Files.readAllBytes(Paths.get("images/additional_7.jpg"));
        productManager.saveProductImage(testProduct, "Additional_7", imageData);

        assertArrayEquals(imageData, testProduct.getAdditionalImage("Additional_7"));
    }
}
