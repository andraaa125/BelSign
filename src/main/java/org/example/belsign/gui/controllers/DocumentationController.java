package org.example.belsign.gui.controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.belsign.be.Order;
import org.example.belsign.be.Product;
import org.example.belsign.bll.OrderManager;
import org.example.belsign.bll.ProductManager;
import org.example.belsign.gui.model.ImageContext;
import org.example.belsign.util.ImageColumn;
import org.example.belsign.factory.ImageSlotFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DocumentationController {

    private Order order;
    private OperatorDashboardController operatorDashboardController;

    @FXML
    private StackPane stackOne, stackTwo, stackThree, stackFour, stackFive, stackSix;
    @FXML
    private GridPane imageGrid;
    @FXML
    private Button btnAddImage;

    private Product product;

    private int nextSlotColumn = 0;
    private int nextSlotRow = 3;
    private int additionalImageCount = 1;

    private final List<String> columnNames = List.of(
            "Image_FRONT", "Image_BACK", "Image_LEFT", "Image_RIGHT", "Image_TOP", "Image_BOTTOM"
    );

    private final OrderManager orderManager = new OrderManager();
    private final ProductManager productManager = new ProductManager();

    @FXML
    public void onClickAddImage(ActionEvent actionEvent) {
        if (additionalImageCount > 20) {
            operatorDashboardController.setStatusMessage("You can only add up to 20 additional images.");
            return;
        }

        String labelText = "Additional " + additionalImageCount;
        StackPane newSlot = createInteractiveSlot(labelText);
        addSlotToGrid(newSlot);
        ImageContext.capturedImages.put(newSlot, null);
        additionalImageCount++;
    }

    @FXML
    public void onClickSend(ActionEvent actionEvent) {
        try {
            List<StackPane> slots = getDefaultSlots();
            for (int i = 0; i < slots.size(); i++) {
                StackPane pane = slots.get(i);
                Image image = ImageContext.capturedImages.get(pane);
                if (image != null) {
                    String column = columnNames.get(i);
                    productManager.saveProductImage(product, column, convertToBytes(image));
                }
            }

            int additionalIndex = 1;
            for (Node node : imageGrid.getChildren()) {
                if (node instanceof VBox vbox && vbox.getChildren().get(0) instanceof StackPane pane &&
                        !getDefaultSlots().contains(pane)) {

                    if (additionalIndex > 20) break;

                    Image image = ImageContext.capturedImages.get(pane);
                    if (image != null) {
                        String column = "Additional_" + additionalIndex;
                        productManager.saveProductImage(product, column, convertToBytes(image));
                        additionalIndex++;
                    }
                }
            }

            ImageContext.capturedImages.clear();

            product.setStatus("pending_approval");
            productManager.updateProductStatus(product.getProductId(), "Pending approval");


            if (operatorDashboardController != null) {
                operatorDashboardController.setStatusMessage("Images sent successfully to QC!");
            }

//            orderManager.updateProductStatus(product.getProductId(), "Pending approval");


            ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onClickCancel(ActionEvent actionEvent) {
        ImageContext.capturedImages.clear();
        ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
    }

    public void setProduct(Product product) {
        this.product = product;

        List<StackPane> slots = getDefaultSlots();
        for (int i = 0; i < slots.size(); i++) {
            String column = columnNames.get(i);
            byte[] data = productManager.getImageData(product, column);
            if (data != null) {
                Image image = new Image(new java.io.ByteArrayInputStream(data));
                StackPane slot = slots.get(i);
                loadImageIntoSlot(slot, image, column.replace("Image_", ""));
                ImageContext.capturedImages.put(slot, image);
            }
        }

        int additionalIndex = 1;
        while (additionalIndex <= 20) {
            String column = "Additional_" + additionalIndex;
            byte[] data = productManager.getImageData(product, column);
            if (data == null) break;

            Image image = new Image(new java.io.ByteArrayInputStream(data));
            String label = "Additional " + additionalIndex;
            StackPane newSlot = createInteractiveSlot(label);
            loadImageIntoSlot(newSlot, image, label);

            addSlotToGrid(newSlot);
            ImageContext.capturedImages.put(newSlot, image);

            additionalIndex++;
            additionalImageCount++;
        }
    }

    private void loadImageIntoSlot(StackPane slot, Image image, String label) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(180);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        Button deleteButton = new Button("✖");
        deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
        deleteButton.setOnAction(e -> {
            try {
                String column = ImageColumn.isAdditionalColumn(label)
                        ? ImageColumn.getAdditionalColumnName(Integer.parseInt(label.replaceAll("[^0-9]", "")))
                        : ImageColumn.getDefaultColumnName(label);

                productManager.saveProductImage(product, column, null);
                System.out.println("Deleted from DB: " + column);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            StackPane replacementSlot = createInteractiveSlot(label);
            VBox container = new VBox(replacementSlot);
            container.setAlignment(Pos.CENTER);

            int col = GridPane.getColumnIndex(slot.getParent());
            int row = GridPane.getRowIndex(slot.getParent());

            imageGrid.getChildren().remove(slot.getParent());
            imageGrid.add(container, col, row);

            ImageContext.capturedImages.remove(slot);
        });

        StackPane.setAlignment(deleteButton, Pos.TOP_RIGHT);
        slot.getChildren().clear();
        slot.getChildren().addAll(imageView, deleteButton);
    }

    private StackPane createInteractiveSlot(String labelText) {
        StackPane slot = ImageSlotFactory.createSlot("+ " + labelText, s -> {
            imageGrid.getChildren().remove(((Node) s.getParent()));
            ImageContext.capturedImages.remove(s);
        });

        slot.setOnMouseClicked(event -> {
            try {
                openCameraView(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return slot;
    }

    private void addSlotToGrid(StackPane slot) {
        VBox container = new VBox(slot);
        container.setAlignment(Pos.CENTER);
        imageGrid.add(container, nextSlotColumn, nextSlotRow);
        nextSlotColumn++;
        if (nextSlotColumn > 2) {
            nextSlotColumn = 0;
            nextSlotRow++;
        }
    }

    public void setOperatorDashboardController(OperatorDashboardController controller) {
        this.operatorDashboardController = controller;
    }

    @FXML
    public void openCameraView(MouseEvent event) throws IOException {
        StackPane clickedPane = (StackPane) event.getSource();
        ImageContext.selectedStackPane = clickedPane;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/belsign/CameraView.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Open Camera");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private byte[] convertToBytes(Image fxImage) throws IOException {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);
        if (bufferedImage == null) throw new IOException("BufferedImage is null");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos); // Write directly to memory
        baos.flush(); // Ensure all data is pushed
        return baos.toByteArray(); // Return clean PNG bytes
    }

    private List<StackPane> getDefaultSlots() {
        return List.of(stackOne, stackTwo, stackThree, stackFour, stackFive, stackSix);
    }

    public void setOrder(Order order) {
        this.order = order;
        try {
            // Load default slots
            List<StackPane> slots = getDefaultSlots();
            for (int i = 0; i < slots.size(); i++) {
                String column = columnNames.get(i);
                byte[] imageBytes = orderManager.getImageData(order.getOrderId(), column);
                if (imageBytes != null) {
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    StackPane slot = slots.get(i);
                    loadImageIntoSlot(slot, image, column.replace("Image_", ""));
                    ImageContext.capturedImages.put(slot, image);
                }
            }

            // Load additional images
            int additionalIndex = 1;
            while (additionalIndex <= ImageColumn.MAX_ADDITIONAL_IMAGES) {
                String column = ImageColumn.getAdditionalColumnName(additionalIndex);
                byte[] imageBytes = orderManager.getImageData(order.getOrderId(), column);
                if (imageBytes == null) break;

                Image image = new Image(new ByteArrayInputStream(imageBytes));
                String label = "Additional " + additionalIndex;
                StackPane newSlot = createInteractiveSlot(label);
                loadImageIntoSlot(newSlot, image, label);

                addSlotToGrid(newSlot);
                ImageContext.capturedImages.put(newSlot, image);

                additionalIndex++;
                additionalImageCount++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
