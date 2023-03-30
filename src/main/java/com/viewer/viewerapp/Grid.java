package com.viewer.viewerapp;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Grid extends Pane {

    private static final Color LINE_STROKE = Color.LIMEGREEN;
    private final Ruler horizontalRuler;
    private final Ruler verticalRuler;

    public Grid(Ruler horizontalRuler, Ruler verticalRuler) {
        this.horizontalRuler = horizontalRuler;
        this.verticalRuler = verticalRuler;

        // Set a listener to update the grid when the artboard size changes
        widthProperty().addListener(event -> drawGrid());
        heightProperty().addListener(event -> drawGrid());

        // Draw the initial grid
        drawGrid();
    }

    void drawGrid() {
        // Clear any existing grid lines
        getChildren().clear();

        // Draw the horizontal grid lines
        double startY = 0;
        double majorTickStrokeWidth = 1;
        double minorTickStrokeWidth = 0.5;
        while (startY < verticalRuler.getOriginalLength()) {
            double tickPosition = verticalRuler.getTickPosition(startY);
            Line line = new Line(0, tickPosition, getWidth(), tickPosition);
            line.setStroke(LINE_STROKE);
            if (startY % verticalRuler.getMajorTickUnit() == 0) {
                line.setStrokeWidth(majorTickStrokeWidth);
            } else {
                line.setStrokeWidth(minorTickStrokeWidth);
            }
            getChildren().add(line);
            startY += verticalRuler.getMinorTickUnit();
        }

        // Draw the vertical grid lines
        double startX = 0;
        while (startX < horizontalRuler.getOriginalLength()) {
            double tickPosition = horizontalRuler.getTickPosition(startX);
            Line line = new Line(tickPosition, 0, tickPosition, getHeight());
            line.setStroke(LINE_STROKE);
            if (startX % horizontalRuler.getMajorTickUnit() == 0) {
                line.setStrokeWidth(majorTickStrokeWidth);
            } else {
                line.setStrokeWidth(minorTickStrokeWidth);
            }
            getChildren().add(line);
            startX += horizontalRuler.getMinorTickUnit();
        }
    }
}