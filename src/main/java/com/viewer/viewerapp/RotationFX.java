package com.viewer.viewerapp;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RotationFX {

    public static void rotate(Artboard artboard) {
        ImageView imageView = artboard.getImageView();
        Stage rotationStage = createRotationStage(artboard, imageView);
        rotationStage.initModality(Modality.APPLICATION_MODAL);
        rotationStage.showAndWait();
    }

    private static Stage createRotationStage(Artboard artboard, ImageView imageView) {
        Stage rotationStage = new Stage(StageStyle.UTILITY);
        rotationStage.setResizable(false);

        ImageView previewImageView = new ImageView(imageView.getImage());
        previewImageView.setPreserveRatio(true);

        double maxDimension = Math.max(imageView.getImage().getWidth(), imageView.getImage().getHeight());
        HBox imageContainer = new HBox(previewImageView);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setMinSize(maxDimension, maxDimension);

        HBox rotateButtonBox = new HBox(10);
        rotateButtonBox.setAlignment(Pos.CENTER);

        FontAwesomeIconView rotateLeftIcon = new FontAwesomeIconView(FontAwesomeIcon.ROTATE_LEFT);
        Button rotateLeftButton = new Button("", rotateLeftIcon);
        rotateLeftButton.setOnAction(e -> previewImageView.setImage(rotateImage(previewImageView.getImage(), -90)));

        FontAwesomeIconView rotateRightIcon = new FontAwesomeIconView(FontAwesomeIcon.ROTATE_RIGHT);
        Button rotateRightButton = new Button("", rotateRightIcon);
        rotateRightButton.setOnAction(e -> previewImageView.setImage(rotateImage(previewImageView.getImage(), 90)));

        rotateButtonBox.getChildren().addAll(rotateLeftButton, rotateRightButton);

        Button validateButton = new Button("Validate");
        validateButton.setOnAction(e -> {
            imageView.setImage(previewImageView.getImage());
            artboard.setImage(previewImageView.getImage());
            rotationStage.close();
        });

        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(imageContainer, rotateButtonBox, validateButton);

        Scene scene = new Scene(container);
        rotationStage.setScene(scene);

        rotationStage.setWidth(maxDimension);
        rotationStage.setHeight(maxDimension + rotateButtonBox.getHeight() + validateButton.getHeight() + 60);

        return rotationStage;
    }

    private static Image rotateImage(Image image, int angle) {
        ImageView tempImageView = new ImageView(image);
        tempImageView.setRotate(tempImageView.getRotate() + angle);
        return tempImageView.snapshot(null, null);
    }
}