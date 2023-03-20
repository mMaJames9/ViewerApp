package com.viewer.viewerapp;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Crop {

    private final ImageView imageView;
    private final Pane cropPane;
    private final Rectangle cropRect;
    private final Group cropGroup;

    private double mouseX, mouseY;
    private Point2D rectStartPoint;
    private Point2D rectEndPoint;

    public Crop(ImageView imageView) {
        this.imageView = imageView;
        cropPane = new Pane();
        cropPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-background-radius: 0;");
        cropPane.setPickOnBounds(false);

        cropGroup = new Group();
        cropPane.getChildren().add(cropGroup);

        cropRect = new Rectangle(0, 0, 0, 0);
        cropRect.setStroke(Color.RED);
        cropRect.setStrokeWidth(2);
        cropRect.setFill(Color.TRANSPARENT);

        cropGroup.getChildren().add(cropRect);

        imageView.setPickOnBounds(false);

        addListeners();
    }

    private void addListeners() {
        imageView.setOnMousePressed(event -> {
            rectStartPoint = new Point2D(event.getX(), event.getY());
            rectEndPoint = rectStartPoint;
            updateView();
        });

        imageView.setOnMouseDragged(event -> {
            rectEndPoint = new Point2D(event.getX(), event.getY());
            updateView();
        });

        imageView.setOnMouseReleased(event -> {
            rectEndPoint = new Point2D(event.getX(), event.getY());
            updateView();
            imageView.setCursor(Cursor.DEFAULT);
        });

        cropRect.widthProperty().addListener((observable, oldValue, newValue) -> updateCrop());

        cropRect.heightProperty().addListener((observable, oldValue, newValue) -> updateCrop());
    }

    private void updateView() {
        double x = Math.min(rectStartPoint.getX(), rectEndPoint.getX());
        double y = Math.min(rectStartPoint.getY(), rectEndPoint.getY());
        double width = Math.abs(rectStartPoint.getX() - rectEndPoint.getX());
        double height = Math.abs(rectStartPoint.getY() - rectEndPoint.getY());
        cropRect.setX(x);
        cropRect.setY(y);
        cropRect.setWidth(width);
        cropRect.setHeight(height);

        cropGroup.getChildren().clear();
        cropGroup.getChildren().add(cropRect);

        Bounds bounds = imageView.getBoundsInLocal();
        double scale = imageView.getScaleX();
        double imageX = (bounds.getWidth() - imageView.getImage().getWidth() * scale) / 2;
        double imageY = (bounds.getHeight() - imageView.getImage().getHeight() * scale) / 2;

        double x1 = (x - imageX) / scale;
        double y1 = (y - imageY) / scale;
        double w1 = width / scale;
        double h1 = height / scale;

        imageView.setViewport(new javafx.geometry.Rectangle2D(x1, y1, w1, h1));
    }

    private void updateCrop() {
        double x = cropRect.getX();
        double y = cropRect.getY();
        double width = cropRect.getWidth();
        double height = cropRect.getHeight();

        double scaleX = imageView.getFitWidth() / imageView.getImage().getWidth();
        double scaleY = imageView.getFitHeight() / imageView.getImage().getHeight();

        double imageX = -imageView.getLayoutX() / scaleX;
        double imageY = -imageView.getLayoutY() / scaleY;

        double cropX = x / scaleX + imageX;
        double cropY = y / scaleY + imageY;
        double cropWidth = width / scaleX;
        double cropHeight = height / scaleY;

        imageView.setViewport(new javafx.geometry.Rectangle2D(cropX, cropY, cropWidth, cropHeight));
    }

    public Pane getCropPane() {
        return cropPane;
    }

    public void show() {
        imageView.setCursor(Cursor.CROSSHAIR);
        imageView.getParent().getChildrenUnmodifiable().add(cropPane);
    }

    public void hide() {
        imageView.setCursor(Cursor.DEFAULT);
        imageView.getParent().getChildrenUnmodifiable().remove(cropPane);
    }
}