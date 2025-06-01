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
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.belsign.be.Order;
import org.example.belsign.be.Product;
import org.example.belsign.bll.OrderManager;
import org.example.belsign.bll.ProductManager;
import org.example.belsign.factory.ImageSlotFactory;
import org.example.belsign.gui.model.ImageContext;
import org.example.belsign.util.ImageColumn;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class DocumentationController {

    private Order order;
    private Product product;
    private OperatorDashboardController operatorDashboardController;

    @FXML
    private StackPane stackOne, stackTwo, stackThree, stackFour, stackFive, stackSix;
    @FXML
    private GridPane imageGrid;
    @FXML
    private HBox qcCommentSection;
    @FXML
    private TextArea qcCommentArea;

    private int nextSlotColumn = 0;
    private int nextSlotRow = 3;
    private int additionalImageCount = 1;

    private final OrderManager orderManager = new OrderManager();
    private final ProductManager productManager = new ProductManager();

    private final List<String> columnNames = List.of(
            "Image_FRONT", "Image_BACK", "Image_LEFT", "Image_RIGHT", "Image_TOP", "Image_BOTTOM"
    );

    public void setOperatorDashboardController(OperatorDashboardController controller) {
        this.operatorDashboardController = controller;
    }

    @FXML
    public void onClickAddImage(ActionEvent event) {
        if (additionalImageCount > 20) return;
        String label = "Additional " + additionalImageCount;
        StackPane newSlot = createInteractiveSlot(label);
        addSlotToGrid(newSlot);
        getImageMap().put(newSlot, null);
        additionalImageCount++;
    }

    @FXML
    public void onClickSend(ActionEvent event) {
        try {
            Map<StackPane, Image> imageMap = getImageMap();
            for (int i = 0; i < 6; i++) {
                StackPane slot = getDefaultSlots().get(i);
                Image img = imageMap.get(slot);
                if (img != null) {
                    productManager.saveProductImage(product, columnNames.get(i), convertToBytes(img));
                }
            }

            int addIdx = 1;
            for (Node node : imageGrid.getChildren()) {
                if (node instanceof VBox vbox && vbox.getChildren().get(0) instanceof StackPane pane &&
                        !getDefaultSlots().contains(pane)) {
                    Image img = imageMap.get(pane);
                    if (img != null) {
                        productManager.saveProductImage(product, "Additional_" + addIdx++, convertToBytes(img));
                    }
                }
            }

            ImageContext.productCapturedImages.remove(product.getProductId());
            productManager.updateProductStatus(product.getProductId(), "Pending approval");
            orderManager.updateOrderStatus(order.getOrderId(), "Done");

            if (operatorDashboardController != null) {
                operatorDashboardController.markProductAsSent(product);
            }

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onClickCancel(ActionEvent event) {
        ImageContext.productCapturedImages.remove(product.getProductId());
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setOrder(Order order) {
        this.order = order;
        if (order.getProducts() != null && !order.getProducts().isEmpty()) {
            setProduct(order.getProducts().get(0));
        }
    }

    public void setProduct(Product product) {
        this.product = product;
        ImageContext.currentProductId = product.getProductId();
        getImageMap().clear();

        Set<String> rejectedSet = new HashSet<>();
        if (product.getRejectedImages() != null && !product.getRejectedImages().isBlank()) {
            rejectedSet.addAll(Arrays.asList(product.getRejectedImages().split(",")));
        }

        imageGrid.getChildren().removeIf(node -> {
            if (node instanceof VBox vbox && vbox.getChildren().get(0) instanceof StackPane slot) {
                return !getDefaultSlots().contains(slot);
            }
            return false;
        });

        nextSlotColumn = 0;
        nextSlotRow = 3;
        additionalImageCount = 1;

        List<StackPane> slots = getDefaultSlots();
        for (int i = 0; i < slots.size(); i++) {
            StackPane slot = slots.get(i);
            String column = columnNames.get(i);
            byte[] data = productManager.getImageData(product, column);
            boolean isRejected = rejectedSet.contains(column);
            slot.getChildren().removeIf(n -> n instanceof ImageView || n instanceof Button);

            if (data != null) {
                Image img = new Image(new ByteArrayInputStream(data));
                loadImageIntoSlot(slot, img, column.replace("Image_", ""), isRejected);
                getImageMap().put(slot, img);
                slot.setOnMouseClicked(null);
            } else {
                slot.setOnMouseClicked(this::openCameraView);
            }
        }

        for (int i = 1; i <= 20; i++) {
            String col = "Additional_" + i;
            byte[] data = productManager.getImageData(product, col);
            if (data == null) break;

            Image img = new Image(new ByteArrayInputStream(data));
            boolean isRejected = rejectedSet.contains(col);
            StackPane slot = createInteractiveSlot("Additional " + i);
            loadImageIntoSlot(slot, img, "Additional " + i, isRejected);
            addSlotToGrid(slot);
            getImageMap().put(slot, img);
            additionalImageCount++;
        }

        if ("Disapproved".equalsIgnoreCase(product.getStatus()) &&
                product.getQcComment() != null &&
                !product.getQcComment().isBlank()) {

            qcCommentArea.setText(product.getQcComment());
            qcCommentSection.setVisible(true);
            qcCommentSection.setManaged(true);
        } else {
            qcCommentArea.clear();
            qcCommentSection.setVisible(false);
            qcCommentSection.setManaged(false);
        }
    }

    private StackPane createInteractiveSlot(String labelText) {
        StackPane slot = ImageSlotFactory.createSlot("+ " + labelText, s -> {
            imageGrid.getChildren().remove(((Node) s.getParent()));
            getImageMap().remove(s);
        });
        slot.setOnMouseClicked(this::openCameraView);
        return slot;
    }

    private void addSlotToGrid(StackPane slot) {
        VBox container = new VBox(slot);
        container.setAlignment(Pos.CENTER);
        imageGrid.add(container, nextSlotColumn, nextSlotRow);
        if (++nextSlotColumn > 2) {
            nextSlotColumn = 0;
            nextSlotRow++;
        }
    }

    private void loadImageIntoSlot(StackPane slot, Image image, String label, boolean isRejected) {
        ImageView iv = new ImageView(image);
        iv.setFitWidth(180);
        iv.setFitHeight(120);
        iv.setPreserveRatio(true);

        Button deleteBtn = new Button("✖");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
        StackPane.setAlignment(deleteBtn, Pos.TOP_RIGHT);

        deleteBtn.setOnAction(e -> {
            try {
                String column = ImageColumn.isAdditionalColumn(label)
                        ? ImageColumn.getAdditionalColumnName(Integer.parseInt(label.replaceAll("[^0-9]", "")))
                        : ImageColumn.getDefaultColumnName(label);
                productManager.saveProductImage(product, column, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            slot.getChildren().removeIf(n -> n instanceof ImageView || n instanceof Button);
            getImageMap().remove(slot);
            slot.setOnMouseClicked(this::openCameraView);
            slot.setStyle("-fx-border-color: #333535; -fx-border-width: 2; -fx-border-radius: 5; -fx-border-style: dashed;");
        });

        slot.getChildren().removeIf(n -> n instanceof ImageView || n instanceof Button);
        slot.getChildren().addAll(iv, deleteBtn);
        slot.setOnMouseClicked(null);

        if (isRejected) {
            slot.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-border-radius: 5;");
        } else {
            slot.setStyle("-fx-border-color: #333535; -fx-border-width: 2; -fx-border-radius: 5; -fx-border-style: dashed;");
        }
    }

    @FXML
    public void openCameraView(MouseEvent event) {
        try {
            StackPane clickedPane = (StackPane) event.getSource();
            ImageContext.selectedStackPane = clickedPane;
            ImageContext.currentProductId = product.getProductId();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/belsign/CameraView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Open Camera");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<StackPane> getDefaultSlots() {
        return List.of(stackOne, stackTwo, stackThree, stackFour, stackFive, stackSix);
    }

    private Map<StackPane, Image> getImageMap() {
        return ImageContext.productCapturedImages
                .computeIfAbsent(product.getProductId(), k -> new HashMap<>());
    }

    private byte[] convertToBytes(Image fxImage) throws IOException {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        return baos.toByteArray();
    }
}