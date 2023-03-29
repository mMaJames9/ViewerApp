package com.viewer.viewerapp;

import javafx.geometry.Orientation;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class Artboard extends GridPane {
    private Ruler horizontalRuler;
    private ImageView imageView;
    private Ruler verticalRuler;
    private Grid grid;
    private double originalImageWidth;
    private double originalImageHeight;

    public Artboard() {
        setHgap(10);
        setVgap(10);

        ColumnConstraints column1 = new ColumnConstraints(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setFillWidth(true);

        RowConstraints row1 = new RowConstraints(30);
        RowConstraints row2 = new RowConstraints();
        row2.setFillHeight(true);

        getColumnConstraints().addAll(column1, column2);
        getRowConstraints().addAll(row1, row2);

        addRulers();
        addGrid();

        widthProperty().addListener((observable, oldWidth, newWidth) -> fitImage());
        heightProperty().addListener((observable, oldHeight, newHeight) -> fitImage());
    }

    public void setImage(Image image) {
        getChildren().clear();

        imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        originalImageWidth = image.getWidth();
        originalImageHeight = image.getHeight();

        setConstraints(imageView, 1, 1);
        getChildren().add(imageView);

        addRulers();
        addGrid();
        grid.toFront();

        fitImage();
    }

    void fitImage() {
        imageView = (ImageView) getChildren().stream().filter(node -> node instanceof ImageView).findFirst().orElse(null);
        if (imageView != null) {
            double availableSpace = getHeight() - getInsets().getTop() - getInsets().getBottom() - getRowConstraints().get(0).getPrefHeight() - getVgap();
            double aspectRatio = originalImageWidth / originalImageHeight;
            double imageWidth = Math.min(availableSpace, availableSpace * aspectRatio);
            double imageHeight = Math.min(availableSpace, availableSpace / aspectRatio);
            imageView.setFitWidth(imageWidth);
            imageView.setFitHeight(imageHeight);
            getRowConstraints().get(1).setVgrow(Priority.NEVER);
            getColumnConstraints().get(1).setHgrow(Priority.NEVER);

            imageView.fitWidthProperty().addListener((observable, oldWidth, newWidth) -> {
                horizontalRuler.setRulerWidth(newWidth.doubleValue());
                grid.drawGrid();
            });

            imageView.fitHeightProperty().addListener((observable, oldHeight, newHeight) -> {
                verticalRuler.setRulerHeight(newHeight.doubleValue());
                grid.drawGrid();
            });
        }
    }

    public Ruler[] getRulers() {
        return new Ruler[]{horizontalRuler, verticalRuler};
    }

    public void removeRulers() {
        getChildren().removeAll(getRulers());
    }

    public void addRulers() {
        horizontalRuler = new Ruler(Orientation.HORIZONTAL, getImageWidth());
        verticalRuler = new Ruler(Orientation.VERTICAL, getImageHeight());
        setConstraints(horizontalRuler, 1, 0);
        setConstraints(verticalRuler, 0, 1);
        getChildren().addAll(getRulers());
    }

    public Grid getGrid() {
        return grid;
    }

    public void removeGrid() {
        getChildren().remove(getGrid());
    }

    public void addGrid() {
        grid = new Grid(horizontalRuler, verticalRuler);
        setConstraints(grid, 1, 1);
        getChildren().add(grid);
        grid.toFront();
    }

    public double getImageWidth() {
        return originalImageWidth;
    }

    public double getImageHeight() {
        return originalImageHeight;
    }

    public ImageView getImageView() {
        return imageView;
    }
}