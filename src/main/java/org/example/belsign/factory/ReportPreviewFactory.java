package org.example.belsign.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.belsign.be.Order;
import org.example.belsign.gui.controllers.ReportPreviewController;

public class ReportPreviewFactory {
    public static void showReportWindow(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(ReportPreviewFactory.class.getResource("/org/example/belsign/ReportPreview.fxml"));
            Parent root = loader.load();

            ReportPreviewController controller = loader.getController();
            controller.setOrder(order);  // Inject the order

            Stage stage = new Stage();
            stage.setTitle("Quality Control Report");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to open Report Preview: " + e.getMessage());
        }
    }
}
