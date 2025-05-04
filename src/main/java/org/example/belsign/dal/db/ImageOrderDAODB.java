package org.example.belsign.dal.db;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.example.belsign.be.ImageOrder;
import org.example.belsign.dal.IImageOrderDAO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImageOrderDAODB implements IImageOrderDAO {
    private final Connection connection;

    public ImageOrderDAODB() throws SQLException {
        this.connection = new DBConnection().getConnection();
    }

    @Override
    public void saveImage(ImageOrder imageOrder) throws IOException {
        String sql = "INSERT INTO ImageOrder (orderId, imageData, slotNumber, fileName) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageOrder.getImage(), null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            ps.setString(1, imageOrder.getOrderId());
            ps.setBytes(2, imageBytes);
            ps.setInt(3, imageOrder.getSlotNumber());
            ps.setString(4, imageOrder.getFileName());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Failed to save image", e);
        }
    }

    @Override
    public List<ImageOrder> getAllImages() {
        throw new UnsupportedOperationException();
    }

    public List<ImageOrder> getAllImagesForOrder(String orderId) throws IOException {
        List<ImageOrder> images = new ArrayList<>();
        String sql = "SELECT imageData, slotNumber, fileName FROM ImageOrder WHERE orderId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    byte[] bytes = rs.getBytes("imageData");
                    Image image = new Image(new ByteArrayInputStream(bytes));
                    int slot = rs.getInt("slotNumber");
                    String fileName = rs.getString("fileName");
                    images.add(new ImageOrder(orderId, image, slot, fileName));
                }
            }
        } catch (SQLException e) {
            throw new IOException("Failed to load images", e);
        }

        return images;
    }
}
