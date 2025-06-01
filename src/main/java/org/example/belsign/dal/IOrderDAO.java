// IOrderDAO.java
package org.example.belsign.dal;

import org.example.belsign.be.Order;

import java.io.IOException;
import java.util.List;

public interface IOrderDAO {
    void updateOrderStatus(String orderId, String newStatus) throws IOException;

    List<Order> getAllOrders() throws IOException;

    byte[] getImageData(String orderId, String columnName) throws IOException;

    void saveDefaultImage(String orderId, String columnName, byte[] imageData) throws IOException;

    void saveAdditionalImageColumn(String columnName) throws IOException;

    void saveAdditionalImageData(String orderId, String columnName, byte[] imageData) throws IOException;

    void deleteImageData(String orderId, String columnName) throws IOException;

    void addColumnToOrderTable(String columnName) throws IOException;

    boolean doesColumnExist(String columnName) throws IOException;

    void updateOrderImageColumn(String orderId, String columnName, byte[] imageData) throws IOException;

    Order getOrderById(String orderId) throws IOException;
}

