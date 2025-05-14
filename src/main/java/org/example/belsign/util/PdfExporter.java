package org.example.belsign.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class PdfExporter {

    public static void exportNodeToPDF(Node node, File file) {
        try {
            if (node instanceof Parent parent) {
                parent.applyCss();
                parent.layout();
            }

            double scale = 2.0; // High quality
            double width = node.getBoundsInParent().getWidth();
            double height = node.getBoundsInParent().getHeight();

            int imageWidth = (int) Math.ceil(width * scale);
            int imageHeight = (int) Math.ceil(height * scale);

            WritableImage fxImage = new WritableImage(imageWidth, imageHeight);
            SnapshotParameters params = new SnapshotParameters();
            params.setTransform(Transform.scale(scale, scale));
            node.snapshot(params, fxImage);

            BufferedImage fullImage = SwingFXUtils.fromFXImage(fxImage, null);

            Document document = new Document(PageSize.A4, 20, 20, 20, 20);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            float pdfWidth = PageSize.A4.getWidth() - 40;
            float pdfHeight = PageSize.A4.getHeight() - 40;

            int y = 0;
            while (y < fullImage.getHeight()) {
                int remainingHeight = fullImage.getHeight() - y;
                int sliceHeight = Math.min(remainingHeight, (int) (pdfHeight));

                BufferedImage slice = fullImage.getSubimage(0, y, fullImage.getWidth(), sliceHeight);
                com.lowagie.text.Image pdfImage = com.lowagie.text.Image.getInstance(slice, null);
                pdfImage.scaleToFit(pdfWidth, pdfHeight);
                document.add(pdfImage);
                document.newPage();

                y += sliceHeight;
            }

            document.close();
            System.out.println("PDF exported successfully to: " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("PDF export failed: " + e.getMessage());
        }
    }
}
