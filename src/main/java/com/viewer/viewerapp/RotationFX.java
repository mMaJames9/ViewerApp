package com.viewer.viewerapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class RotationFX {

    public static void rotate(Artboard artboard) {
        ImageView imageView = artboard.getImageView();
        Stage rotateStage = createRotateStage(imageView, artboard);
        rotateStage.initModality(Modality.APPLICATION_MODAL);
        rotateStage.showAndWait();
    }

    private static Stage createRotateStage(ImageView imageView, Artboard artboard) {
        Stage rotateStage = new Stage(StageStyle.UTILITY);
        rotateStage.setResizable(false);

        double maxDimension = Math.max(imageView.getFitWidth(), imageView.getFitHeight());
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(maxDimension, maxDimension);

        ImageView previewImageView = new ImageView(imageView.getImage());
        previewImageView.setPreserveRatio(true);
        previewImageView.setFitWidth(imageView.getFitWidth());
        previewImageView.setFitHeight(imageView.getFitHeight());

        imageContainer.getChildren().add(previewImageView);

        TextField angleInput = new TextField();
        angleInput.setPromptText("Enter rotation angle");

        ChoiceBox<String> directionChoiceBox = new ChoiceBox<>();
        directionChoiceBox.getItems().addAll("Left", "Right");
        directionChoiceBox.setValue("Left");

        Button applyRotationButton = new Button("Apply Rotation");
        applyRotationButton.setOnAction(e -> {
            try {
                int angle = Integer.parseInt(angleInput.getText());
                if (directionChoiceBox.getValue().equals("Right")) {
                    angle = -angle;
                }
                Image rotatedImage = rotateImage(previewImageView.getImage(), angle, true);
                previewImageView.setImage(rotatedImage);
                adjustPreviewImageViewDimensions(previewImageView, rotatedImage);
            } catch (NumberFormatException ex) {
                showAlert();
            }
        });


        HBox rotateInputBox = new HBox(5);
        rotateInputBox.setAlignment(Pos.CENTER);
        rotateInputBox.getChildren().addAll(angleInput, new Label("Direction: "), directionChoiceBox, applyRotationButton);

        Button validateButton = new Button("Validate");
        validateButton.getStyleClass().add("button");
        validateButton.setOnAction(e -> {
            imageView.setImage(previewImageView.getImage());
            artboard.setImage(previewImageView.getImage());
            rotateStage.close();
        });

        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(imageContainer, rotateInputBox, validateButton);

        Scene scene = new Scene(container);
        rotateStage.setScene(scene);

        return rotateStage;
    }

    private static Image rotateImage(Image image, int angle) {
        System.gc();

        BufferedImage bufferedImage = toBufferedImage(image);
        double radians = Math.toRadians(angle);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        // Calculate the center point of the original image
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        // Create a new AffineTransform and apply the translation and rotation
        AffineTransform at = new AffineTransform();
        at.translate(centerX, centerY);
        at.rotate(radians);
        at.translate(-centerX, -centerY);

        // Create a new image with the same dimensions as the original image
        BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(bufferedImage, rotatedImage);

        return toJavaFXImage(rotatedImage);
    }

    private static BufferedImage toBufferedImage(Image image) {
        BufferedImage bufferedImage = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        javafx.embed.swing.SwingFXUtils.fromFXImage(image, bufferedImage);
        return bufferedImage;
    }

    private static Image toJavaFXImage(BufferedImage bufferedImage) {
        return javafx.embed.swing.SwingFXUtils.toFXImage(bufferedImage, null);
    }

    private static void adjustPreviewImageViewDimensions(ImageView previewImageView, Image rotatedImage) {
        double containerWidth = previewImageView.getFitWidth();
        double containerHeight = previewImageView.getFitHeight();

        double rotatedImageWidth = rotatedImage.getWidth();
        double rotatedImageHeight = rotatedImage.getHeight();

        double widthRatio = containerWidth / rotatedImageWidth;
        double heightRatio = containerHeight / rotatedImageHeight;
        double scaleFactor = Math.min(widthRatio, heightRatio);

        previewImageView.setFitWidth(rotatedImageWidth * scaleFactor);
        previewImageView.setFitHeight(rotatedImageHeight * scaleFactor);
    }

    private static void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText("Please enter a valid rotation angle.");
        alert.showAndWait();
    }
}