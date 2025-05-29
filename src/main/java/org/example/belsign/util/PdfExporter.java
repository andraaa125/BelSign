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
import javafx.scene.layout.Region;
import javafx.scene.transform.Transform;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfExporter {
    public static void exportNodeToPDF(Node node, File file) throws IOException {
        try {

            if (node instanceof Parent parent) {
                parent.applyCss();
                parent.layout();
            }

            double nodeWidth = node.getBoundsInParent().getWidth();
            double nodeHeight = node.getBoundsInParent().getHeight();

            // PDF page size in points (A4 = 595 x 842 points)
            double maxPdfWidth = PageSize.A4.getWidth() - 40;  // 20pt margin each side
            double maxPdfHeight = PageSize.A4.getHeight() - 40;

            // Determine scale factor to fit content in one page
            double widthScale = maxPdfWidth / nodeWidth;
            double heightScale = maxPdfHeight / nodeHeight;
            double scale = Math.min(widthScale, heightScale);

            int imageWidth = (int) Math.ceil(nodeWidth * scale);
            int imageHeight = (int) Math.ceil(nodeHeight * scale);

            WritableImage fxImage = new WritableImage(imageWidth, imageHeight);
            SnapshotParameters params = new SnapshotParameters();
            params.setTransform(Transform.scale(scale, scale));
            node.snapshot(params, fxImage);

            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);

            Document document = new Document(PageSize.A4, 20, 20, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            com.lowagie.text.Image pdfImage = com.lowagie.text.Image.getInstance(bufferedImage, null);
            pdfImage.scaleToFit((float) maxPdfWidth, (float) maxPdfHeight);
            document.add(pdfImage);

            PdfContentByte cb = writer.getDirectContent();
            cb.beginText();
            cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 12);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "Page 1 of 1", PageSize.A4.getWidth() / 2, 15, 0);
            cb.endText();

            document.close();
            System.out.println("PDF exported successfully to: " + file.getAbsolutePath());

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("PDF export failed: " + ex.getMessage());
        }
    }
}


