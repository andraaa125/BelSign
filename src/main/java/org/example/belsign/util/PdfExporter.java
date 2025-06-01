package org.example.belsign.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class PdfExporter {

    // Higher scaling for higher quality (3.0 = approx. 216 DPI)
    private static final double DPI_SCALE = 3.0;

    public static void exportNodeToPDF(Node node, File file) throws IOException {
        if (node instanceof Parent parent) {
            parent.applyCss();
            parent.layout();
        }

        double nodeWidth = node.getBoundsInParent().getWidth();
        double nodeHeight = node.getBoundsInParent().getHeight();

        double scaledWidth = nodeWidth * DPI_SCALE;
        double scaledHeight = nodeHeight * DPI_SCALE;

        WritableImage fxImage = new WritableImage((int) scaledWidth, (int) scaledHeight);
        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(Transform.scale(DPI_SCALE, DPI_SCALE));
        WritableImage snapshot = node.snapshot(params, fxImage);

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

        // Save the image as lossless PNG to a temp file
        File tempImageFile = File.createTempFile("snapshot", ".png");
        ImageIO.write(bufferedImage, "png", tempImageFile);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            Document document = new Document(PageSize.A4, 20, 20, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();

            Image pdfImage = Image.getInstance(tempImageFile.getAbsolutePath());
            pdfImage.scaleToFit(PageSize.A4.getWidth() - 40, PageSize.A4.getHeight() - 40);
            pdfImage.setAlignment(Image.ALIGN_CENTER);
            document.add(pdfImage);

            PdfContentByte cb = writer.getDirectContent();
            cb.beginText();
            cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 12);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "Page 1 of 1", PageSize.A4.getWidth() / 2, 15, 0);
            cb.endText();

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to generate PDF", e);
        } finally {
            tempImageFile.delete(); // Clean up temp image
        }

        System.out.println("✅ PDF exported in high resolution to: " + file.getAbsolutePath());
    }
}
