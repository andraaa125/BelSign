package org.example.belsign.util;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;

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

            double scaleFactor = 3.0; // Increase for higher DPI
            double width = node.getBoundsInParent().getWidth();
            double height = node.getBoundsInParent().getHeight();

            int scaledWidth = (int) Math.ceil(width * scaleFactor);
            int scaledHeight = (int) Math.ceil(height * scaleFactor);

            WritableImage fxImage = new WritableImage(scaledWidth, scaledHeight);
            SnapshotParameters params = new SnapshotParameters();
            params.setTransform(javafx.scene.transform.Transform.scale(scaleFactor, scaleFactor));

            node.snapshot(params, fxImage);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);

            // PDF export
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            com.lowagie.text.Image pdfImage = com.lowagie.text.Image.getInstance(bufferedImage, null);
            pdfImage.scaleToFit(PageSize.A4.getWidth() - 40, PageSize.A4.getHeight() - 40);
            document.add(pdfImage);

            document.close();
            System.out.println("PDF exported to: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("PDF export failed: " + e.getMessage());
        }
    }


}
