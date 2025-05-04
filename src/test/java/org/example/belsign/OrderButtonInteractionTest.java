package org.example.belsign;

import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderButtonInteractionTest {
    @Test
    void testAddRemoveButtonFromPane() {
        FlowPane pane = new FlowPane();
        Button button = new Button("Order 1");

        pane.getChildren().add(button);
        assertTrue(pane.getChildren().contains(button));

        pane.getChildren().remove(button);
        assertFalse(pane.getChildren().contains(button));
    }
}
