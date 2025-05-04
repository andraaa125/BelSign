package org.example.belsign.dal.db;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.example.belsign.be.ImageOrder;
import org.example.belsign.dal.IImageOrderDAO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;

public class ImageOrderDAODB implements IImageOrderDAO {

    private final Connection connection;

    public ImageOrderDAODB() throws SQLException {
        this.connection = new DBConnection().getConnection();
        initializeImageOrderTable();
    }

    @Override
    public void saveImage(ImageOrder imageOrder) throws IOException {
        String sql = "INSERT INTO ImageOrder (orderId, imageData) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, imageOrder.getOrderId());

            BufferedImage bImage = SwingFXUtils.fromFXImage(imageOrder.getImage(), null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            ps.setBytes(2, imageBytes);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Failed to save image to SQL Server", e);
        }
    }

    private void initializeImageOrderTable() throws SQLException {
        String sql = """
            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ImageOrder' AND xtype='U')
            CREATE TABLE ImageOrder (
                id INT IDENTITY(1,1) PRIMARY KEY,
                orderId NVARCHAR(50) NOT NULL,
                imageData VARBINARY(MAX) NOT NULL
            );
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public java.util.List<ImageOrder> getAllImages() throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
