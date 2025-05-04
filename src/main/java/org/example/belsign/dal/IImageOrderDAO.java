package org.example.belsign.dal;

import org.example.belsign.be.ImageOrder;

import java.io.IOException;
import java.util.List;

public interface IImageOrderDAO {
    void saveImage(ImageOrder image) throws IOException;
    List<ImageOrder> getAllImages() throws IOException;
    List<ImageOrder> getAllImagesForOrder(String orderId) throws IOException;
}
