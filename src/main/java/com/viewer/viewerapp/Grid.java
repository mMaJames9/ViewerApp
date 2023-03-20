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

    private void drawGrid() {
        // Clear any existing grid lines
        getChildren().clear();

        // Draw the horizontal grid lines
        double y = 0;
        double majorTickStrokeWidth = 1;
        double minorTickStrokeWidth = 0.5;
        while (y < getHeight()) {
            double tickPosition = horizontalRuler.getTickPosition(y);
            Line line = new Line(0, tickPosition, getWidth(), tickPosition);
            line.setStroke(LINE_STROKE);
            if (y % horizontalRuler.getMajorTickUnit() == 0) {
                line.setStrokeWidth(majorTickStrokeWidth);
            } else {
                line.setStrokeWidth(minorTickStrokeWidth);
            }
            getChildren().add(line);
            y += horizontalRuler.getMinorTickUnit();
        }

        // Draw the vertical grid lines
        double x = 0;
        while (x < getWidth()) {
            double tickPosition = verticalRuler.getTickPosition(x);
            Line line = new Line(tickPosition, 0, tickPosition, getHeight());
            line.setStroke(LINE_STROKE);
            if (x % verticalRuler.getMajorTickUnit() == 0) {
                line.setStrokeWidth(majorTickStrokeWidth);
            } else {
                line.setStrokeWidth(minorTickStrokeWidth);
            }
            getChildren().add(line);
            x += verticalRuler.getMinorTickUnit();
        }
    }

}