package org.example.belsign.command;

import javafx.scene.Node;
import javafx.stage.Window;
import org.example.belsign.command.Command;

public class ExportReportCommand implements Command {

    private final Node nodeToExport;
    private final Window ownerWindow;

    public ExportReportCommand(Node nodeToExport, Window ownerWindow) {
        this.nodeToExport = nodeToExport;
        this.ownerWindow = ownerWindow;
    }


    @Override
    public void execute() {
        //  your export to PDF logic here
        System.out.println("Exporting report to PDF...");
    }
}
