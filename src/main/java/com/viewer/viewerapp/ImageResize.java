package com.viewer.viewerapp;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.UnaryOperator;

public class ImageResize {
    private final Artboard artboard;
    private final double originalAspectRatio;
    private TextField widthTextField;
    private TextField heightTextField;
    private CheckBox preserveRatioCheckBox;

    public ImageResize(Artboard artboard) {
        this.artboard = artboard;
        this.originalAspectRatio = artboard.getImageView().getImage().getWidth() / artboard.getImageView().getImage().getHeight();
        createAndShowModal();
    }

    private void createAndShowModal() {
        Stage resizeStage = new Stage(StageStyle.UTILITY);
        resizeStage.initModality(Modality.APPLICATION_MODAL);
        resizeStage.setResizable(false);
        resizeStage.setTitle("Resize Image");

        ImageView previewImageView = new ImageView(artboard.getImageView().getImage());
        previewImageView.setFitWidth(artboard.getImageView().getFitWidth());
        previewImageView.setFitHeight(artboard.getImageView().getFitHeight());
        previewImageView.setPreserveRatio(true);
        previewImageView.setSmooth(true);

        VBox form = createForm();
        form.setPadding(new Insets(10));
        form.setAlignment(Pos.CENTER);

        preserveRatioCheckBox = new CheckBox("Preserve aspect ratio");
        preserveRatioCheckBox.setSelected(true);
        preserveRatioCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                adjustHeight();
            }
        });

        widthTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (preserveRatioCheckBox.isSelected()) {
                adjustHeight();
            }
        });

        HBox inputBox = new HBox(20);
        inputBox.getChildren().addAll(form, preserveRatioCheckBox);
        inputBox.setAlignment(Pos.CENTER);


        Button resizeButton = createResizeButton();

        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(previewImageView, inputBox, resizeButton);

        Scene scene = new Scene(container);
        resizeStage.setScene(scene);
        resizeStage.showAndWait();
    }

    private VBox createForm() {
        Label widthLabel = new Label("Width:");
        widthTextField = new TextField();
        widthTextField.setTextFormatter(createIntegerFormatter());

        HBox widthBox = new HBox(5);
        widthBox.getChildren().addAll(widthLabel, widthTextField);

        Label heightLabel = new Label("Height:");
        heightTextField = new TextField();
        heightTextField.setTextFormatter(createIntegerFormatter());

        HBox heightBox = new HBox(5);
        heightBox.getChildren().addAll(heightLabel, heightTextField);

        VBox form = new VBox(10);
        form.getChildren().addAll(widthBox, heightBox);
        form.setAlignment(Pos.CENTER_LEFT);

        return form;
    }

    private Button createResizeButton() {
        Button resizeButton = new Button("Resize");
        resizeButton.setOnAction(event -> {
            int width = Integer.parseInt(widthTextField.getText());
            int height = Integer.parseInt(heightTextField.getText());
            Image resizedImage = resizeImage(artboard.getImageView().getImage(), width, height);
            artboard.setImage(resizedImage);
            ((Stage) resizeButton.getScene().getWindow()).close();
        });

        return resizeButton;
    }

    private void adjustHeight() {
        try {
            int newWidth = Integer.parseInt(widthTextField.getText());
            int newHeight = (int) Math.round(newWidth / originalAspectRatio);
            heightTextField.setText(String.valueOf(newHeight));
        } catch (NumberFormatException e) {
            // Ignore if the width field contains non-numeric values
        }
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
