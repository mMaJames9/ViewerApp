package com.viewer.viewerapp;

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
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

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
        rotateStage.setTitle("Rotate Image");
        rotateStage.setResizable(false);

        double maxDimension = Math.max(imageView.getFitWidth(), imageView.getFitHeight());
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(maxDimension, maxDimension);

        ImageView previewImageView = new ImageView(imageView.getImage());
        previewImageView.setPreserveRatio(true);
        previewImageView.setFitWidth(imageView.getFitWidth());
        previewImageView.setFitHeight(imageView.getFitHeight());

        imageContainer.getChildren().add(previewImageView);

        HBox rotateButtonBox = new HBox(10);
        rotateButtonBox.setAlignment(Pos.CENTER);

        FontIcon rotateLeftIcon = new FontIcon(FontAwesomeSolid.UNDO);
        Button rotateLeftButton = new Button("", rotateLeftIcon);
        rotateLeftButton.getStyleClass().add("button-icon");
        rotateLeftButton.setOnAction(e -> previewImageView.setImage(rotateImage(previewImageView.getImage(), -90)));

        FontIcon rotateRightIcon = new FontIcon(FontAwesomeSolid.REDO);
        Button rotateRightButton = new Button("", rotateRightIcon);
        rotateRightButton.getStyleClass().add("button-icon");
        rotateRightButton.setOnAction(e -> previewImageView.setImage(rotateImage(previewImageView.getImage(), 90)));

        rotateButtonBox.getChildren().addAll(rotateLeftButton, rotateRightButton);

        HBox validateBox = new HBox(5);
        validateBox.setAlignment(Pos.CENTER);
        validateBox.setPadding(new Insets(0, 0, 10, 0));

        Button validateButton = new Button("Validate");
        validateButton.getStyleClass().add("button");

        validateButton.setOnAction(e -> {
            imageView.setImage(previewImageView.getImage());
            artboard.setImage(previewImageView.getImage());
            rotateStage.close();
        });

        validateBox.getChildren().add(validateButton);

        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(imageContainer, rotateButtonBox, validateBox);

        Scene scene = new Scene(container);
        rotateStage.setScene(scene);

        return rotateStage;
    }

    private static Image rotateImage(Image image, int angle) {
        BufferedImage bufferedImage = toBufferedImage(image);
        double radians = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int newWidth = (int) Math.floor(width * cos + height * sin);
        int newHeight = (int) Math.floor(height * cos + width * sin);

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0);
        at.rotate(radians, width / 2.0, height / 2.0);
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

}