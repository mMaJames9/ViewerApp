package com.viewer.viewerapp;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Crop {
    public static void crop(ImageView imageView) {
        // Create a new Stage to display the cropping interface
        Stage cropStage = new Stage();
        cropStage.setTitle("Crop Image");

        // Create a new Pane to hold the ImageView and the crop rectangle
        Pane pane = new Pane();
        pane.setPrefSize(imageView.getFitWidth(), imageView.getFitHeight());
        pane.getChildren().add(imageView);

        // Create a new Rectangle to define the crop area
        Rectangle cropRectangle = new Rectangle();
        cropRectangle.setFill(Color.rgb(0, 0, 0, 0.5));
        cropRectangle.setStroke(Color.WHITE);
        cropRectangle.setStrokeWidth(2);
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
        });
        cropRectangle.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - dragContext.mouseAnchorX;
            double deltaY = event.getSceneY() - dragContext.mouseAnchorY;
            double newCropX = dragContext.cropX + deltaX;
            double newCropY = dragContext.cropY + deltaY;
            double newCropWidth = dragContext.cropWidth;
            double newCropHeight = dragContext.cropHeight;
            getNewCrop(imageView, cropRectangle, handleRectangles, newCropX, newCropY, newCropWidth, newCropHeight);
        });
        for (Rectangle handleRectangle : handleRectangles) {
            final DragContext handleDragContext = new DragContext();
            handleRectangle.setOnMousePressed(event -> {
                handleDragContext.mouseAnchorX = event.getSceneX();
                handleDragContext.mouseAnchorY = event.getSceneY();
                handleDragContext.cropX = cropX;
                handleDragContext.cropY = cropY;
                handleDragContext.cropWidth = cropWidth;
                handleDragContext.cropHeight = cropHeight;
            });
            handleRectangle.setOnMouseDragged(event -> {
                double deltaX = event.getSceneX() - handleDragContext.mouseAnchorX;
                double deltaY = event.getSceneY() - handleDragContext.mouseAnchorY;
                double newCropX = cropX;
                double newCropY = cropY;
                double newCropWidth = cropWidth;
                double newCropHeight = cropHeight;
                switch (handleRectangles.indexOf(handleRectangle)) {
                    case 0 -> { // Top Left
                        newCropX = handleDragContext.cropX + deltaX;
                        newCropY = handleDragContext.cropY + deltaY;
                        newCropWidth = handleDragContext.cropWidth - deltaX;
                        newCropHeight = handleDragContext.cropHeight - deltaY;
                    }
                    case 1 -> { // Top
                        newCropY = handleDragContext.cropY + deltaY;
                        newCropHeight = handleDragContext.cropHeight - deltaY;
                    }
                    case 2 -> { // Top Right
                        newCropY = handleDragContext.cropY + deltaY;
                        newCropWidth = handleDragContext.cropWidth + deltaX;
                        newCropHeight = handleDragContext.cropHeight - deltaY;
                    }
                    case 3 -> // Right
                            newCropWidth = handleDragContext.cropWidth + deltaX;
                    case 4 -> { // Bottom Right
                        newCropWidth = handleDragContext.cropWidth + deltaX;
                        newCropHeight = handleDragContext.cropHeight + deltaY;
                    }
                    case 5 -> // Bottom
                            newCropHeight = handleDragContext.cropHeight + deltaY;
                    case 6 -> { // Bottom Left
                        newCropX = handleDragContext.cropX + deltaX;
                        newCropWidth = handleDragContext.cropWidth - deltaX;
                        newCropHeight = handleDragContext.cropHeight + deltaY;
                    }
                    case 7 -> { // Left
                        newCropX = handleDragContext.cropX + deltaX;
                        newCropWidth = handleDragContext.cropWidth - deltaX;
                    }
                }
                getNewCrop(imageView, cropRectangle, handleRectangles, newCropX, newCropY, newCropWidth, newCropHeight);
            });
        }
        // Create a new Scene to display the cropping interface
        Scene cropScene = new Scene(pane);

        // Set the scene and show the stage
        cropStage.setScene(cropScene);
        cropStage.showAndWait();
    }

    private static void getNewCrop(ImageView imageView, Rectangle cropRectangle, List<Rectangle> handleRectangles, double newCropX, double newCropY, double newCropWidth, double newCropHeight) {
        double cropX;
        double cropY;
        double cropWidth;
        double cropHeight;
        if (newCropX < 0) {
            newCropWidth += newCropX;
            newCropX = 0;
        }
        if (newCropY < 0) {
            newCropHeight += newCropY;
            newCropY = 0;
        }
        if (newCropX + newCropWidth > imageView.getFitWidth()) {
            newCropWidth = imageView.getFitWidth() - newCropX;
        }
        if (newCropY + newCropHeight > imageView.getFitHeight()) {
            newCropHeight = imageView.getFitHeight() - newCropY;
        }
        cropX = newCropX;
        cropY = newCropY;
        cropWidth = newCropWidth;
        cropHeight = newCropHeight;
        updateCropRectangle(cropRectangle, cropX, cropY, cropWidth, cropHeight);
        updateHandleRectangles(handleRectangles, cropX, cropY, cropWidth, cropHeight);
    }

    /**
     * Updates the position and size of the crop rectangle based on the specified parameters.
     *
     * @param cropRectangle the crop rectangle to update
     * @param cropX         the x-coordinate of the top-left corner of the crop rectangle
     * @param cropY         the y-coordinate of the top-left corner of the crop rectangle
     * @param cropWidth     the width of the crop rectangle
     * @param cropHeight    the height of the crop rectangle
     */
    private static void updateCropRectangle(Rectangle cropRectangle, double cropX, double cropY, double cropWidth, double cropHeight) {
        cropRectangle.setX(cropX);
        cropRectangle.setY(cropY);
        cropRectangle.setWidth(cropWidth);
        cropRectangle.setHeight(cropHeight);
    }

    /**
     * Updates the position and size of the handle rectangles based on the specified parameters.
     *
     * @param handleRectangles the handle rectangles to update
     * @param cropX            the x-coordinate of the top-left corner of the crop rectangle
     * @param cropY            the y-coordinate of the top-left corner of the crop rectangle
     * @param cropWidth        the width of the crop rectangle
     * @param cropHeight       the height of the crop rectangle
     */
    private static void updateHandleRectangles(List<Rectangle> handleRectangles, double cropX, double cropY, double cropWidth, double cropHeight) {
        handleRectangles.get(0).setX(cropX - 4);
        handleRectangles.get(0).setY(cropY - 4);
        handleRectangles.get(1).setX(cropX + cropWidth / 2 - 4);
        handleRectangles.get(1).setY(cropY - 4);
        handleRectangles.get(2).setX(cropX + cropWidth - 4);
        handleRectangles.get(2).setY(cropY - 4);
        handleRectangles.get(3).setX(cropX + cropWidth - 4);
        handleRectangles.get(3).setY(cropY + cropHeight / 2 - 4);
        handleRectangles.get(4).setX(cropX + cropWidth - 4);
        handleRectangles.get(4).setY(cropY + cropHeight - 4);
        handleRectangles.get(5).setX(cropX + cropWidth / 2 - 4);
        handleRectangles.get(5).setY(cropY + cropHeight - 4);
        handleRectangles.get(6).setX(cropX - 4);
        handleRectangles.get(6).setY(cropY + cropHeight - 4);
        handleRectangles.get(7).setX(cropX - 4);
        handleRectangles.get(7).setY(cropY + cropHeight / 2 - 4);
    }

    /**
     * A helper class to store the context of a drag operation.
     */
    private static class DragContext {
        double mouseAnchorX;
        double mouseAnchorY;
        double cropX;
        double cropY;
        double cropWidth;
        double cropHeight;
    }
}
