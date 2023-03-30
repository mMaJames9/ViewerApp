package com.viewer.viewerapp;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

        HBox rotateButtonBox = new HBox(5);
        rotateButtonBox.setAlignment(Pos.CENTER);

        FontAwesomeIconView rotateLeftIcon = new FontAwesomeIconView(FontAwesomeIcon.ROTATE_LEFT);
        Button rotateLeftButton = new Button("", rotateLeftIcon);
        rotateLeftButton.setOnAction(e -> previewImageView.setImage(rotateImage(previewImageView.getImage(), -90)));

        FontAwesomeIconView rotateRightIcon = new FontAwesomeIconView(FontAwesomeIcon.ROTATE_RIGHT);
        Button rotateRightButton = new Button("", rotateRightIcon);
        rotateRightButton.setOnAction(e -> previewImageView.setImage(rotateImage(previewImageView.getImage(), 90)));

        rotateButtonBox.getChildren().addAll(rotateLeftButton, rotateRightButton);

        HBox validateBox = new HBox(5);
        validateBox.setAlignment(Pos.CENTER);
        validateBox.setPadding(new Insets(0, 0, 10, 0));

        Button validateButton = new Button("Validate");
        validateButton.setOnAction(e -> {
            imageView.setImage(previewImageView.getImage());
            artboard.setImage(previewImageView.getImage());
            rotateStage.close();
        });

        validateBox.getChildren().add(validateButton);

        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(imageContainer, rotateButtonBox, validateBox);

        Scene scene = new Scene(container);
        rotateStage.setScene(scene);

        return rotateStage;
    }

    private static Image rotateImage(Image image, int angle) {
        ImageView tempImageView = new ImageView(image);
        tempImageView.setRotate(tempImageView.getRotate() + angle);
        return tempImageView.snapshot(null, null);
    }
}
