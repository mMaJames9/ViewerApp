// Ruler.java
package com.viewer.viewerapp;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Ruler extends Region {
    private static final double MAJOR_TICK_HEIGHT = 20;
    private static final double MINOR_TICK_HEIGHT = 5;
    private static final double CENTER_TICK_HEIGHT = 15;
    private static final Color TICK_COLOR = Color.LIMEGREEN;
    private static final Color LABEL_COLOR = Color.LIMEGREEN;
    private static final double LABEL_OFFSET = 5;

    private final Orientation orientation;
    private final double majorTickUnit = 100;
    private final double minorTickUnit = 10;
    private final double originalLength;
    private double width = 0;
    private double height = 0;

    public Ruler(Orientation orientation, double originalLength) {
        this.orientation = orientation;
        this.originalLength = originalLength;
    }

    public double getMajorTickUnit() {
        return majorTickUnit;
    }

    public double getMinorTickUnit() {
        return minorTickUnit;
    }

    public void setRulerWidth(double width) {
        super.setWidth(width);
    }

    public void setRulerHeight(double height) {
        super.setHeight(height);
    }

    @Override
    protected void layoutChildren() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        width = getWidth();
        height = getHeight();
        draw();
    }

    private void draw() {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(TICK_COLOR);
        gc.setStroke(TICK_COLOR);
        gc.setLineWidth(0.7);
        double tickStart;
        double tickEnd;

        if (orientation == Orientation.HORIZONTAL) {
            double canvasHeight = height - getInsets().getTop() - getInsets().getBottom();
            gc.strokeLine(0, canvasHeight, width, canvasHeight);
            tickStart = Math.ceil(getInsets().getLeft() / minorTickUnit) * minorTickUnit;
            tickEnd = Math.floor((originalLength - getInsets().getRight()) / minorTickUnit) * minorTickUnit;

            for (double i = tickStart; i <= tickEnd; i += minorTickUnit) {
                double tickHeight = MINOR_TICK_HEIGHT;
                if (i % majorTickUnit == 0) {
                    tickHeight = MAJOR_TICK_HEIGHT;
                    gc.setFill(LABEL_COLOR);
                    String label = String.valueOf((int) i);
                    Text text = new Text(label);
                    double labelX = getTickPosition(i) - (text.getBoundsInLocal().getWidth() / 2) + (LABEL_OFFSET * 3);
                    double labelY = canvasHeight - text.getBoundsInLocal().getHeight();
                    gc.fillText(label, labelX, labelY);
                } else if (i % (majorTickUnit / 2) == 0) {
                    tickHeight = CENTER_TICK_HEIGHT;
                }
                double tickPos = getTickPosition(i);
                gc.strokeLine(tickPos, canvasHeight - tickHeight, tickPos, canvasHeight);
            }

            setCanvasAlignment(canvas, Pos.BOTTOM_LEFT);
        } else {
            double canvasWidth = width - getInsets().getLeft() - getInsets().getRight();
            gc.strokeLine(canvasWidth, 0, canvasWidth, height);
            tickStart = Math.ceil(getInsets().getTop() / minorTickUnit) * minorTickUnit;
            tickEnd = Math.floor((originalLength - getInsets().getBottom()) / minorTickUnit) * minorTickUnit;

            for (double i = tickStart; i <= tickEnd; i += minorTickUnit) {
                double tickHeight = MINOR_TICK_HEIGHT;
                if (i % majorTickUnit == 0) {
                    tickHeight = MAJOR_TICK_HEIGHT;
                    gc.setFill(LABEL_COLOR);
                    String label = String.valueOf((int) i);
                    Text text = new Text(label);
                    double labelX = canvasWidth - tickHeight - text.getBoundsInLocal().getWidth();
                    double labelY = getTickPosition(i) + (text.getBoundsInLocal().getHeight() / 2) + (LABEL_OFFSET * 2);
                    gc.fillText(label, labelX, labelY);
                } else if (i % (majorTickUnit / 2) == 0) {
                    tickHeight = CENTER_TICK_HEIGHT;
                }
                double tickPos = getTickPosition(i);
                gc.strokeLine(canvasWidth - tickHeight, tickPos, canvasWidth, tickPos);
            }

            setCanvasAlignment(canvas, Pos.TOP_RIGHT);
        }

        getChildren().clear();
        getChildren().add(canvas);
    }

    private void setCanvasAlignment(Canvas canvas, Pos position) {
        StackPane.setAlignment(canvas, position);
        StackPane.setMargin(canvas, new Insets(0, 0, 0, 0));
    }

    public double getTickPosition(double value) {
        return value * (getRulerLength() / originalLength);
    }

    public double getRulerLength() {
        if (orientation == Orientation.HORIZONTAL) {
            return getWidth();
        } else {
            return getHeight();
        }
    }

}

