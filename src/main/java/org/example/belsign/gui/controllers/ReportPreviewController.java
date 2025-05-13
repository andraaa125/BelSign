package org.example.belsign.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.example.belsign.be.Order;
import org.example.belsign.command.Command;
import org.example.belsign.command.ExportReportCommand;
import org.example.belsign.util.PdfExporter;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ReportPreviewController {

    @FXML private VBox rootVBox;
    @FXML private Button btnSendEmail;
    @FXML private Button btnExport;

    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        loadReportData();
    }

    private void loadReportData() {
        // populate fields and images
    }

    @FXML
    private void onExportAsPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Quality Control Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("QC_Report_" + System.currentTimeMillis() + ".pdf");

        Window window = rootVBox.getScene().getWindow();
        File selectedFile = fileChooser.showSaveDialog(window);

        if (selectedFile != null) {
            // 👇 Temporarily hide export and email buttons
            btnExport.setVisible(false);
            btnSendEmail.setVisible(false);

            // Call PDF exporter
            PdfExporter.exportNodeToPDF(rootVBox, selectedFile);

            // 👇 Show them again
            btnExport.setVisible(true);
            btnSendEmail.setVisible(true);
        }
    }


    @FXML
    private void onSendEmail() {
        try {
            String subject = "QC Report";
            String body = "Please find the attached report.";

            // Proper URI escaping for spaces and symbols
            String uriStr = "mailto:?subject=" + URLEncoder.encode(subject, StandardCharsets.UTF_8)
                    + "&body=" + URLEncoder.encode(body, StandardCharsets.UTF_8);

            Desktop.getDesktop().mail(new URI(uriStr));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to open mail client: " + e.getMessage());
        }
    }



}
