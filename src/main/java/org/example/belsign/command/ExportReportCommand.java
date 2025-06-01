package org.example.belsign.command;

import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.example.belsign.util.PdfExporter;

import java.io.File;
import java.io.IOException;

public class ExportReportCommand implements Command {

    private final Node nodeToExport;
    private final Window ownerWindow;

    public ExportReportCommand(Node nodeToExport, Window ownerWindow) {
        this.nodeToExport = nodeToExport;
        this.ownerWindow = ownerWindow;
    }

    @Override
    public void execute() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Report as PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("QC_Report_" + System.currentTimeMillis() + ".pdf");

            File file = fileChooser.showSaveDialog(ownerWindow);
            if (file != null) {
                PdfExporter.exportNodeToPDF(nodeToExport, file);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error exporting PDF: " + e.getMessage());
        }
    }
}
