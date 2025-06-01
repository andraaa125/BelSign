package org.example.belsign.factory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import java.util.function.Consumer;

public class ImageSlotFactory {

    private static final double PREF_WIDTH = 200.0;
    private static final double PREF_HEIGHT = 150.0;

    public static StackPane createSlot(String labelText, Consumer<StackPane> onDelete) {
        StackPane slot = createBaseSlot();

        Label label = new Label(labelText);
        label.setFont(new Font(16));
        StackPane.setAlignment(label, Pos.CENTER);

        Button deleteBtn = createDeleteButton(onDelete, slot);

        slot.getChildren().addAll(label, deleteBtn);
        return slot;
    }

    public static StackPane createSlot(String labelText) {
        return createSlot(labelText, s -> {});
    }

    public static StackPane createImageSlot(ImageView imageView, String labelText, Consumer<StackPane> onDelete) {
        StackPane slot = createBaseSlot();

        Label label = new Label(labelText);
        label.setFont(new Font(16));
        StackPane.setAlignment(label, Pos.CENTER);

        Button deleteBtn = createDeleteButton(onDelete, slot);

        slot.getChildren().addAll(imageView, label, deleteBtn);
        return slot;
    }

    private static StackPane createBaseSlot() {
        StackPane slot = new StackPane();
        slot.setPrefSize(PREF_WIDTH, PREF_HEIGHT);
        slot.setMinSize(PREF_WIDTH, PREF_HEIGHT);
        slot.setMaxSize(PREF_WIDTH, PREF_HEIGHT);
        slot.setStyle("-fx-border-color: #333535; -fx-border-width: 2; -fx-border-radius: 5; -fx-border-style: dashed;");
        return slot;
    }

    private static Button createDeleteButton(Consumer<StackPane> onDelete, StackPane slot) {
        Button deleteBtn = new Button("✖");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 12;");
        deleteBtn.setOnAction(e -> onDelete.accept(slot));
        StackPane.setAlignment(deleteBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(deleteBtn, new Insets(5, 5, 0, 0));
        return deleteBtn;
    }
}