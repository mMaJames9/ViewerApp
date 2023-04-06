package com.viewer.viewerapp;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.UnaryOperator;

public class ImageResize {
    private Artboard artboard;
    private ImageView previewImageView;
    private TextField widthTextField;
    private TextField heightTextField;

    public ImageResize(Artboard artboard) {
        this.artboard = artboard;
        createAndShowModal();
    }

    private void createAndShowModal() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Resize Image");

        previewImageView = new ImageView(artboard.getImageView().getImage());
        previewImageView.setFitWidth(artboard.getImageView().getFitWidth());
        previewImageView.setFitHeight(artboard.getImageView().getFitHeight());
        previewImageView.setPreserveRatio(true);
        previewImageView.setSmooth(true);

        GridPane gridPane = createForm();
        Button resizeButton = createResizeButton();

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(previewImageView, gridPane, resizeButton);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private GridPane createForm() {
        Label widthLabel = new Label("Width:");
        widthTextField = new TextField();
        widthTextField.setTextFormatter(createIntegerFormatter());

        Label heightLabel = new Label("Height:");
        heightTextField = new TextField();
        heightTextField.setTextFormatter(createIntegerFormatter());

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(widthLabel, 0, 0);
        gridPane.add(widthTextField, 1, 0);
        gridPane.add(heightLabel, 0, 1);
        gridPane.add(heightTextField, 1, 1);

        return gridPane;
    }

    private Button createResizeButton() {
        Button resizeButton = new Button("Resize");
        resizeButton.setOnAction(event -> {
            int width = Integer.parseInt(widthTextField.getText());
            int height = Integer.parseInt(heightTextField.getText());
            Image resizedImage = resizeImage(artboard.getImageView().getImage(), width, height);
            artboard.setImage(resizedImage);
        });

        return resizeButton;
    }

    private TextFormatter<String> createIntegerFormatter() {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };
        return new TextFormatter<>(integerFilter);
    }

    private Image resizeImage(Image originalImage, int width, int height) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);
        int type = bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(bufferedImage, 0, 0, width, height, null);
        g.dispose();
        return SwingFXUtils.toFXImage(resizedImage, null);
    }
}
