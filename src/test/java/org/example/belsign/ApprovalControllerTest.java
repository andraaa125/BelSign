package org.example.belsign;

import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.example.belsign.be.Product;
import org.example.belsign.gui.controllers.ApprovalController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class ApprovalControllerTest {

    private ApprovalController controller;
    private Product testProduct;


    @BeforeEach
    public void setUp() {
        new JFXPanel(); // JavaFX bootstrap

        controller = new ApprovalController();

        testProduct = new Product();
        testProduct.setProductId(101);
        testProduct.setProductName("Test Product");
        testProduct.setStatus("Pending");

        // Inject dummy JavaFX UI components
        HBox commentSection = new HBox();
        Label commentLabel = new Label();
        GridPane imageGrid = new GridPane();  // ✅ Fix for the NPE

        try {
            Field csField = ApprovalController.class.getDeclaredField("commentSection");
            csField.setAccessible(true);
            csField.set(controller, commentSection);

            Field clField = ApprovalController.class.getDeclaredField("commentLabel");
            clField.setAccessible(true);
            clField.set(controller, commentLabel);

            Field igField = ApprovalController.class.getDeclaredField("imageGrid"); // ✅ Inject imageGrid
            igField.setAccessible(true);
            igField.set(controller, imageGrid);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject JavaFX fields via reflection", e);
        }

        controller.setProduct(testProduct);
    }

    @Test
    public void testOnClickApprove_UpdatesProductStatus() throws Exception {
        // Access and invoke the private method
        Method method = ApprovalController.class.getDeclaredMethod("onClickApprove");
        method.setAccessible(true);
        method.invoke(controller);

        // Assert that the product status was updated
        assertEquals("Approved", testProduct.getStatus(),
                "Product status should be set to 'Approved' after approval.");
    }

    @Test
    public void testOnClickDisapprove_UpdatesProductAndUI() throws Exception {
        // Access and invoke the private method
        Method method = ApprovalController.class.getDeclaredMethod("onClickDisapprove", ActionEvent.class);
        method.setAccessible(true);
        method.invoke(controller, new ActionEvent());

        // Assert that the product status was updated
        assertEquals("Disapproved", testProduct.getStatus(),
                "Product status should be set to 'Disapproved' after disapproval.");

        // Check comment section visibility
        Field csField = ApprovalController.class.getDeclaredField("commentSection");
        csField.setAccessible(true);
        HBox commentSection = (HBox) csField.get(controller);
        assertTrue(commentSection.isVisible(), "Comment section should be visible after disapproval.");

        // Check disapproveMode is set to true
        Field dmField = ApprovalController.class.getDeclaredField("disapproveMode");
        dmField.setAccessible(true);
        boolean disapproveMode = (boolean) dmField.get(controller);
        assertTrue(disapproveMode, "Disapprove mode should be enabled after clicking disapprove.");
    }
}
