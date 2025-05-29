package org.example.belsign;

import org.example.belsign.be.Product;
import org.example.belsign.bll.ProductManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        testProduct.setProductId(9999); // Integer product ID
        testProduct.setProduct("Test Product");
        testProduct.setStatus("pending_approval");
    }

    @Test
    public void testSaveFrontImage_WithMockData() throws SQLException {
        byte[] mockData = {1, 2, 3, 4, 5};

        productManager.saveProductImage(testProduct, "Image_FRONT", mockData);

        assertArrayEquals(mockData, testProduct.getImageFront(), "Front image should match the saved byte array.");
    }

    @Test
    public void testSaveAdditionalImage_WithMockData() throws SQLException {
        byte[] mockData = {10, 20, 30};

        productManager.saveProductImage(testProduct, "Additional_7", mockData);

        byte[] actualData = testProduct.getAdditionalImage("Additional_7");
        assertNotNull(actualData, "Additional image should not be null.");
        assertArrayEquals(mockData, actualData, "Additional image should match the saved byte array.");
    }

    @Test
    public void testGetImageData_ReturnsCorrectData() {
        byte[] mockData = {5, 4, 3, 2, 1};
        testProduct.setImageTop(mockData);

        byte[] actual = productManager.getImageData(testProduct, "Image_TOP");

        assertNotNull(actual);
        assertArrayEquals(mockData, actual, "Should retrieve correct image data for TOP slot.");
    }

    @Test
    public void testGetImageData_WithInvalidSlot_ReturnsNull() {
        byte[] result = productManager.getImageData(testProduct, "INVALID_SLOT");
        assertNull(result, "Unknown slot name should return null.");
    }

}
