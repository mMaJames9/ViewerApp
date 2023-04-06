package com.viewer.viewerapp;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
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

        setupEventFilter();
    }

    private void setupEventFilter() {
        addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Node target = getParent().lookup("#imageView");
            if (target != null) {
                MouseEvent newEvent = new MouseEvent(
                        MouseEvent.MOUSE_CLICKED,
                        event.getX(), event.getY(),
                        event.getScreenX(), event.getScreenY(),
                        event.getButton(), event.getClickCount(),
                        event.isShiftDown(), event.isControlDown(),
                        event.isAltDown(), event.isMetaDown(),
                        event.isPrimaryButtonDown(), event.isMiddleButtonDown(),
                        event.isSecondaryButtonDown(), event.isSynthesized(),
                        event.isPopupTrigger(), event.isStillSincePress(),
                        event.getPickResult()
                );
                target.fireEvent(newEvent);
                event.consume();
            }
        });
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