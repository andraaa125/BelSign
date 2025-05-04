package org.example.belsign.bll;

import org.example.belsign.be.ImageOrder;
import org.example.belsign.dal.IImageOrderDAO;
import org.example.belsign.dal.db.ImageOrderDAODB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ImageOrderManager {
    private final IImageOrderDAO imageDAO;

    public ImageOrderManager() {
        try {
            this.imageDAO = new ImageOrderDAODB();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize ImageOrderDAO", e);
        }
    }

    public void saveImage(ImageOrder image) throws IOException {
        imageDAO.saveImage(image);
    }

    public List<ImageOrder> getAllImages() throws IOException {
        return imageDAO.getAllImages();
    }

    public void saveImages(List<ImageOrder> images) throws IOException {
        for (ImageOrder image : images) {
            saveImage(image);
        }
    }

    // ✅ FIX: Add this
    public List<ImageOrder> getImagesForOrder(String orderId) throws IOException {
        return ((ImageOrderDAODB) imageDAO).getAllImagesForOrder(orderId);
    }
}
