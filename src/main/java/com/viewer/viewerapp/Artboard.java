package com.viewer.viewerapp;

import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.viewer.viewerapp.ImageHandler.artboard2;

public class Artboard extends GridPane {

    private static final List<Artboard> allArtboards = new ArrayList<>();
    private Ruler horizontalRuler;
    private ImageView imageView;
    private Ruler verticalRuler;
    private Grid grid;
    static Canvas canvas;

    public Artboard() {
        allArtboards.add(this);
        setHgap(10);
        setVgap(10);

        // Set column and row constraints
        ColumnConstraints column1 = new ColumnConstraints(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setFillWidth(true);

        RowConstraints row1 = new RowConstraints(30);
        RowConstraints row2 = new RowConstraints();
        row2.setFillHeight(true);

        getColumnConstraints().addAll(column1, column2);
        getRowConstraints().addAll(row1, row2);

        // Add rulers to appropriate cells
        addRulers();

        // Add grid to appropriate cell
        addGrid();


        // Listen to changes in width and height to fit the image
    }

    public static void updateAllArtboards() throws IOException {
        for (Artboard artboard : allArtboards) {
            artboard.setImage(ImageHandler.getImage());
        }
    }

    public void setImage(Image image) {

        // Clear all existing children
        getChildren().clear();

        imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // Add new ImageView to appropriate cell and fit to height
        setConstraints(imageView, 1, 1);

        getChildren().add(imageView);

        // Add rulers
        addRulers();

        // Make sure grid is on top of image
        addGrid();
        grid.toFront();

        // Create canvas with same dimensions as imageview and set to same position
        canvas = new Canvas(imageView.getBoundsInParent().getWidth(), imageView.getBoundsInParent().getHeight());
        setConstraints(canvas, 1, 1);
        canvas.toFront();

        // Add canvas to Artboard
        getChildren().add(canvas);

    }

    void fitImage() {
        imageView = (ImageView) getChildren().stream().filter(node -> node instanceof ImageView).findFirst().orElse(null);
        if (imageView != null) {
            double availableSpace = getHeight() - getInsets().getTop() - getInsets().getBottom() - getRowConstraints().get(0).getPrefHeight() - getVgap();
            double aspectRatio = imageView.getImage().getWidth() / imageView.getImage().getHeight();
            double imageWidth = Math.min(availableSpace, availableSpace * aspectRatio);
            double imageHeight = Math.min(availableSpace, availableSpace / aspectRatio);
            imageView.setFitWidth(imageWidth);
            imageView.setFitHeight(imageHeight);

            canvas.setWidth(imageWidth);
            canvas.setHeight(imageHeight);
            canvas.setLayoutX(imageView.getLayoutX());
            canvas.setLayoutY(imageView.getLayoutY());

            getRowConstraints().get(1).setVgrow(Priority.NEVER);
            getColumnConstraints().get(1).setHgrow(Priority.NEVER);
        }
    }

    public Ruler[] getRulers() {
        return new Ruler[]{horizontalRuler, verticalRuler};
    }

    public void removeRulers() {
        getChildren().removeAll(getRulers());
    }

    public void addRulers() {
        horizontalRuler = new Ruler(Orientation.HORIZONTAL);
        verticalRuler = new Ruler(Orientation.VERTICAL);
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
        getChildren().addAll(getGrid());
    }


    public ImageView getImageView() {
        return imageView;
    }

}
