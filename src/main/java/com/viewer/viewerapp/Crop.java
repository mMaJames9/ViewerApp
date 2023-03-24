package com.viewer.viewerapp;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Crop {
    private static final double HANDLE_SIZE = 10;
    private static final double HANDLE_HALF_SIZE = HANDLE_SIZE / 2;
    private static final double MIN_WIDTH = 50;
    private static final double MIN_HEIGHT = 50;
    private static final Color HANDLE_COLOR = Color.RED;
    private static final Color HANDLE_FILL_COLOR = Color.rgb(255, 255, 255, 0.001);

    private static Rectangle cropRect = null;
    private static Group group;

    public static void crop(ImageView imageView) {

        if (cropRect != null) {
            group.getChildren().remove(cropRect);
        }

        Bounds bounds = imageView.getBoundsInParent();
        double x = bounds.getMinX();
        double y = bounds.getMinY();
        double w = bounds.getWidth();
        double h = bounds.getHeight();

        cropRect = new Rectangle(x, y, w, h);
        cropRect.setStroke(HANDLE_COLOR);
        cropRect.setStrokeWidth(2);
        cropRect.setFill(HANDLE_FILL_COLOR);

        group = new Group();
        group.getChildren().add(imageView);
        group.getChildren().add(cropRect);

        addResizeHandles(cropRect);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(group);

        Stage cropStage = new Stage();
        cropStage.initModality(Modality.APPLICATION_MODAL);
        cropStage.setScene(new Scene(borderPane, Color.BLACK));
        cropStage.setResizable(false);
        cropStage.initStyle(StageStyle.UNDECORATED);

        // Add the "validate" and "cancel" buttons to the modal window
        Button validateButton = new Button("Validate Crop");
        validateButton.setOnAction(event -> {
            // When the validate button is clicked, get the cropped image and replace the original image
            Image croppedImage = cropImage(imageView.getImage(), cropRect);

            // Create a new ImageView with the cropped image and replace the old ImageView on the artboard with it
            ImageView croppedImageView = new ImageView(croppedImage);
            croppedImageView.setPreserveRatio(true);
            croppedImageView.fitWidthProperty().bind(imageView.fitWidthProperty());
            croppedImageView.fitHeightProperty().bind(imageView.fitHeightProperty());
            imageView.getParent().getChildrenUnmodifiable().set(imageView.getParent().getChildrenUnmodifiable().indexOf(imageView), croppedImageView);

            cropStage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> {
            // When the cancel button is clicked, close the modal window without making any changes
            cropStage.close();
        });

        HBox buttonBox = new HBox(10, validateButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        borderPane.setBottom(buttonBox);

        cropStage.showAndWait();
    }

    private static void addResizeHandles(Rectangle cropRect) {
        // Add resize handles to the corners of the crop rectangle
        for (CropHandle handle : CropHandle.values()) {
            final Circle circle = new Circle(HANDLE_HALF_SIZE, HANDLE_HALF_SIZE, HANDLE_HALF_SIZE);
            circle.setFill(HANDLE_COLOR);
            handle.attach(circle);

            circle.centerXProperty().addListener((ov, oldX, newX) -> handle.updateX(newX.doubleValue() - HANDLE_HALF_SIZE));

            circle.centerYProperty().addListener((ov, oldY, newY) -> handle.updateY(newY.doubleValue() - HANDLE_HALF_SIZE));

            cropRect.widthProperty().addListener((ov, oldWidth, newWidth) -> handle.updateWidth(newWidth.doubleValue() - HANDLE_SIZE));

            cropRect.heightProperty().addListener((ov, oldHeight, newHeight) -> handle.updateHeight(newHeight.doubleValue() - HANDLE_SIZE));

            handle.updateX(circle.getCenterX() - HANDLE_HALF_SIZE);
            handle.updateY(circle.getCenterY() - HANDLE_HALF_SIZE);

            cropRect.widthProperty().addListener((ov, oldWidth, newWidth) -> handle.updateWidth(newWidth.doubleValue() - HANDLE_SIZE));

            cropRect.heightProperty().addListener((ov, oldHeight, newHeight) -> handle.updateHeight(newHeight.doubleValue() - HANDLE_SIZE));
            group.getChildren().add(circle);
        }
    }

    private static Image cropImage(Image image, Rectangle cropRect) {
        // Crop the image based on the crop rectangle's dimensions and position
        double x = cropRect.getLayoutX() + cropRect.getBoundsInParent().getMinX();
        double y = cropRect.getLayoutY() + cropRect.getBoundsInParent().getMinY();
        double width = cropRect.getBoundsInParent().getWidth();
        double height = cropRect.getBoundsInParent().getHeight();

        PixelReader reader = image.getPixelReader();
        return new WritableImage(reader, (int) x, (int) y, (int) width, (int) height);
    }

    
}