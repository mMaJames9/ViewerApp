package com.viewer.viewerapp;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;

public class Crop {
    public static void crop(ImageView imageView) {
        // Create a new Stage to display the cropping interface
        Stage cropStage = new Stage(StageStyle.UTILITY);
        cropStage.setResizable(false);

        // Create a new Pane to hold the ImageView and the crop rectangle
        Pane pane = new Pane();
        pane.setPrefSize(imageView.getFitWidth(), imageView.getFitHeight());

        // Create a new ImageView to display the original image
        ImageView originalImageView = new ImageView(imageView.getImage());
        originalImageView.setPreserveRatio(true);
        originalImageView.setFitWidth(imageView.getFitWidth());
        originalImageView.setFitHeight(imageView.getFitHeight());

        // Create a new Rectangle to darken the area outside the crop rectangle
        Rectangle backgroundRectangle = new Rectangle(pane.getPrefWidth(), pane.getPrefHeight(), Color.BLACK);
        backgroundRectangle.setOpacity(0.5);
        backgroundRectangle.setBlendMode(BlendMode.DIFFERENCE);
        pane.getChildren().add(backgroundRectangle);

        pane.getChildren().add(originalImageView);

        // Create a new Rectangle to define the crop area
        Rectangle cropRectangle = new Rectangle();
        cropRectangle.setFill(Color.TRANSPARENT);
        cropRectangle.setStroke(Color.WHITE);
        cropRectangle.setStrokeWidth(2);
        cropRectangle.setOpacity(1);
        pane.getChildren().add(cropRectangle);

        // Create 8 handle rectangles to allow the user to resize the crop area
        List<Rectangle> handleRectangles = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Rectangle handleRectangle = new Rectangle(8, 8);
            handleRectangle.setFill(Color.WHITE);
            handleRectangle.setStroke(Color.BLACK);
            handleRectangle.setStrokeWidth(1);
            pane.getChildren().add(handleRectangle);
            handleRectangles.add(handleRectangle);
        }

        // Define the initial position and size of the crop rectangle and handle rectangles
        double cropX = imageView.getX();
        double cropY = imageView.getY();
        double cropWidth = imageView.getFitWidth();
        double cropHeight = imageView.getFitHeight();
        updateCropRectangle(cropRectangle, cropX, cropY, cropWidth, cropHeight);
        updateHandleRectangles(handleRectangles, cropX, cropY, cropWidth, cropHeight);

        // Register event handlers to allow the user to interact with the crop area
        DragContext dragContext = new DragContext();

        cropRectangle.setOnMousePressed(event -> {
            dragContext.mouseAnchorX = event.getSceneX();
            dragContext.mouseAnchorY = event.getSceneY();
            dragContext.cropX = cropX;
            dragContext.cropY = cropY;
            dragContext.cropWidth = cropWidth;
            dragContext.cropHeight = cropHeight;
            cropRectangle.setCursor(Cursor.MOVE);
        });


        cropRectangle.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - dragContext.mouseAnchorX;
            double deltaY = event.getSceneY() - dragContext.mouseAnchorY;
            double newCropX = dragContext.cropX + deltaX;
            double newCropY = dragContext.cropY + deltaY;
            double newCropWidth = dragContext.cropWidth;
            double newCropHeight = dragContext.cropHeight;
            getNewCrop(originalImageView, cropRectangle, handleRectangles, newCropX, newCropY, newCropWidth, newCropHeight);
        });

        // Set the cursor back to default when the user releases the crop rectangle
        cropRectangle.setOnMouseReleased(event -> cropRectangle.setCursor(Cursor.DEFAULT));

        // Register event handlers for the handle rectangles to allow the user to resize the crop area
        registerHandleEvents(handleRectangles, originalImageView, cropRectangle);

        // Add the pane to the stage and show the stage
        Scene scene = new Scene(pane);
        cropStage.setScene(scene);
        cropStage.show();

        // Add an event filter for the enter key to validate the crop
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Get the cropped image
                WritableImage croppedImage = getCroppedImage(originalImageView, cropRectangle);

                // Set the cropped image in the original ImageView
                imageView.setImage(croppedImage);

                // Close the crop stage
                cropStage.close();

                // Consume the event to prevent further processing
                event.consume();
            }
        });

    }

    private static void updateCropRectangle(Rectangle cropRectangle, double x, double y, double width, double height) {
        cropRectangle.setX(x);
        cropRectangle.setY(y);
        cropRectangle.setWidth(width);
        cropRectangle.setHeight(height);
    }

    private static void updateHandleRectangles(List<Rectangle> handleRectangles, double x, double y, double width, double height) {
        double handleSize = handleRectangles.get(0).getWidth();
        handleRectangles.get(0).setX(x - handleSize / 2);
        handleRectangles.get(0).setY(y - handleSize / 2);
        handleRectangles.get(1).setX(x + width / 2 - handleSize / 2);
        handleRectangles.get(1).setY(y - handleSize / 2);
        handleRectangles.get(2).setX(x + width - handleSize / 2);
        handleRectangles.get(2).setY(y - handleSize / 2);
        handleRectangles.get(3).setX(x + width - handleSize / 2);
        handleRectangles.get(3).setY(y + height / 2 - handleSize / 2);
        handleRectangles.get(4).setX(x + width - handleSize / 2);
        handleRectangles.get(4).setY(y + height - handleSize / 2);
        handleRectangles.get(5).setX(x + width / 2 - handleSize / 2);
        handleRectangles.get(5).setY(y + height - handleSize / 2);
        handleRectangles.get(6).setX(x - handleSize / 2);
        handleRectangles.get(6).setY(y + height - handleSize / 2);
        handleRectangles.get(7).setX(x - handleSize / 2);
        handleRectangles.get(7).setY(y + height / 2 - handleSize / 2);
    }

    private static void registerHandleEvents(List<Rectangle> handleRectangles, ImageView imageView, Rectangle cropRectangle) {
        for (int i = 0; i < handleRectangles.size(); i++) {
            int finalI = i;
            Rectangle handleRectangle = handleRectangles.get(i);
            DragContext dragContext = new DragContext();
            handleRectangle.setOnMousePressed(event -> {
                dragContext.mouseAnchorX = event.getSceneX();
                dragContext.mouseAnchorY = event.getSceneY();
                dragContext.cropX = cropRectangle.getX();
                dragContext.cropY = cropRectangle.getY();
                dragContext.cropWidth = cropRectangle.getWidth();
                dragContext.cropHeight = cropRectangle.getHeight();
                dragContext.handleIndex = finalI;
                handleRectangle.setCursor(getCursorForHandle(finalI));
            });
            handleRectangle.setOnMouseDragged(event -> {
                double deltaX = event.getSceneX() - handleRectangle.getWidth() / 2 - dragContext.mouseAnchorX;
                double deltaY = event.getSceneY() - handleRectangle.getHeight() / 2 - dragContext.mouseAnchorY;
                double newCropX = dragContext.cropX;
                double newCropY = dragContext.cropY;
                double newCropWidth = dragContext.cropWidth;
                double newCropHeight = dragContext.cropHeight;

                // Update the crop rectangle and handle rectangles based on which handle is being dragged
                switch (dragContext.handleIndex) {
                    case 0 -> {
                        newCropX += deltaX;
                        newCropY += deltaY;
                        newCropWidth -= deltaX;
                        newCropHeight -= deltaY;
                    }
                    case 1 -> {
                        newCropY += deltaY;
                        newCropHeight -= deltaY;
                    }
                    case 2 -> {
                        newCropY += deltaY;
                        newCropWidth += deltaX;
                        newCropHeight -= deltaY;
                    }
                    case 3 -> newCropWidth += deltaX;
                    case 4 -> {
                        newCropWidth += deltaX;
                        newCropHeight += deltaY;
                    }
                    case 5 -> newCropHeight += deltaY;
                    case 6 -> {
                        newCropX += deltaX;
                        newCropWidth -= deltaX;
                        newCropHeight += deltaY;
                    }
                    case 7 -> {
                        newCropX += deltaX;
                        newCropWidth -= deltaX;
                    }
                }

                // Make sure the crop rectangle stays within the image bounds
                if (newCropX < 0) {
                    newCropX = 0;
                }
                if (newCropY < 0) {
                    newCropY = 0;
                }
                if (newCropX + newCropWidth > imageView.getFitWidth()) {
                    newCropX = imageView.getFitWidth() - newCropWidth;
                }
                if (newCropY + newCropHeight > imageView.getFitHeight()) {
                    newCropY = imageView.getFitHeight() - newCropHeight;
                }

                updateCropRectangle(cropRectangle, newCropX, newCropY, newCropWidth, newCropHeight);
                updateHandleRectangles(handleRectangles, newCropX, newCropY, newCropWidth, newCropHeight);
            });
            handleRectangle.setOnMouseReleased(event -> handleRectangle.setCursor(getCursorForHandle(finalI)));
        }

    }

    private static void getNewCrop(ImageView imageView, Rectangle cropRectangle, List<Rectangle> handleRectangles, double cropX, double cropY, double cropWidth, double cropHeight) {
        double imageWidth = imageView.getImage().getWidth();
        double imageHeight = imageView.getImage().getHeight();

        // Ensure that the crop rectangle stays within the bounds of the image
        cropX = Math.max(0, cropX);
        cropY = Math.max(0, cropY);
        cropWidth = Math.min(imageWidth - cropX, cropWidth);
        cropHeight = Math.min(imageHeight - cropY, cropHeight);

        // Update the crop rectangle and handle rectangles
        updateCropRectangle(cropRectangle, cropX, cropY, cropWidth, cropHeight);
        updateHandleRectangles(handleRectangles, cropX, cropY, cropWidth, cropHeight);
    }

    private static Cursor getCursorForHandle(int handleIndex) {
        return switch (handleIndex) {
            case 0, 4 -> Cursor.NW_RESIZE;
            case 1, 5 -> Cursor.N_RESIZE;
            case 2, 6 -> Cursor.NE_RESIZE;
            case 3, 7 -> Cursor.E_RESIZE;
            default -> Cursor.DEFAULT;
        };
    }

    private static WritableImage getCroppedImage(ImageView imageView, Rectangle cropRectangle) {
        // Create a new snapshot parameters object
        SnapshotParameters snapshotParams = new SnapshotParameters();

        // Set the viewport of the snapshot parameters to the bounds of the crop rectangle
        Bounds bounds = cropRectangle.getBoundsInParent();
        snapshotParams.setViewport(new Rectangle2D(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));

        // Return a snapshot of the ImageView with the specified snapshot parameters
        return imageView.snapshot(snapshotParams, null);
    }

    private static class DragContext {
        double mouseAnchorX;
        double mouseAnchorY;
        double cropX;
        double cropY;
        double cropWidth;
        double cropHeight;
        int handleIndex;
    }

}