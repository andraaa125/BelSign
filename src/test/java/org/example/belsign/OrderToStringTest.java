package org.example.belsign;

import org.example.belsign.be.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderToStringTest {
    @Test
    void testToString() {
        Order order = new Order();
        order.setOrderId("ORD001");
        order.setStatus("Done");

        String expected = "OrderID: ORD001, Status: Done";
        assertEquals(expected, order.toString());
    }
}