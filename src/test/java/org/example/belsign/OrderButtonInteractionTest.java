package org.example.belsign;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderButtonInteractionTest {

    @Test
    void testAddRemoveButtonName() {
        List<String> mockPane = new ArrayList<>();
        String buttonName = "Order 1";

        // Simulate adding a button
        mockPane.add(buttonName);
        assertTrue(mockPane.contains(buttonName));

        // Simulate removing a button
        mockPane.remove(buttonName);
        assertFalse(mockPane.contains(buttonName));
    }
}
