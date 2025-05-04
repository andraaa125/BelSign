package org.example.belsign.dal;

import org.example.belsign.be.Order;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IOrderDAO {
    List<Order> getAllOrders() throws SQLException, IOException;

    void updateOrderStatus(String orderId, String status) throws IOException;
}
