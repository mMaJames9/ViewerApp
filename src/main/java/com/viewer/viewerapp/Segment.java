package com.viewer.viewerapp;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Segment {


    private static ImageView imageView;
    private  static File file;
    private  static Image image;
    private Canvas canvas;
    public static Button segmentButton = new Button("Segment");
    private GraphicsContext gc;
    private  static List<Point> points = new ArrayList<>();
    private static Canvas redCanvas;
    public static void segments(ImageView img) throws IOException {

        image= img.getImage();
        Artboard artboard = new Artboard();
        artboard.fitImage();

        TextField numPointsTextField = new TextField("0");
        segmentButton.setOnAction(event -> {
            try {
                int numPoints = Integer.parseInt(numPointsTextField.getText());
                if (points.size() >= numPoints) {
                    cutImage(numPoints);
                    // Reset the point count and clear the canvas
                    points.clear();
                    redrawCanvas();
                    numPointsTextField.setText("0");
                }
            } catch (NumberFormatException e) {
                // If the user entered an invalid number, do nothing
            }
        });
        // Display the image in a GUI
        imageView = new ImageView(image);

       Artboard.canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            // Update the numPointsTextField with the new count
            int currentNumPoints = Integer.parseInt(numPointsTextField.getText());
            if (currentNumPoints < points.size()) {
                numPointsTextField.setText(String.valueOf(points.size()));
            }
            // Handle the mouse click
            handleMouseClick(event);
        });
    }
    private static void handleMouseClick(MouseEvent event) {

        Point point = new Point((int) event.getX(), (int) event.getY());
        points.add(point);

        // Draw a red dot at the clicked point
        GraphicsContext gc = Artboard.canvas.getGraphicsContext2D();
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillOval(point.x - 3, point.y - 3, 6, 6);

        // Draw red lines between the points
        if (points.size() > 1) {
            Point prevPoint = points.get(points.size() - 2);
            gc.setStroke(javafx.scene.paint.Color.RED);
            gc.setLineWidth(2);
            gc.strokeLine(prevPoint.x, prevPoint.y, point.x, point.y);

        }
    }
    private static List<Point> getSelectedPoints(int numPoints) {
        List<Point> selectedPoints = new ArrayList<>();
        if (points.size() >= numPoints) {
            // Get the last numPoints points that were clicked
            selectedPoints = points.subList(points.size() - numPoints, points.size());
            // Add the first point to the end to close the polygon
            selectedPoints.add(selectedPoints.get(0));
        }
        return selectedPoints;
    }
    private static void cutImage(int numPoints) {
        // Get the selected points
        List<Point> points = getSelectedPoints(numPoints);

        // Find the bounding box of the selected polygon
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Point point : points) {
            minX = Math.min(minX, point.x);

            minY = Math.min(minY, point.y);
            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
        }
        // Create a new image with the size of the bounding box
        int width = maxX - minX;
        int height = maxY - minY;
        WritableImage cutImage = new WritableImage(width, height);
        PixelWriter pixelWriter = cutImage.getPixelWriter();

        // Copy the pixels inside the polygon to the new image
        PixelReader pixelReader = imageView.getImage().getPixelReader();
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                boolean inside = isPixelInsidePath(points, x, y);
                if (inside) {
                    pixelWriter.setColor(x - minX, y - minY, pixelReader.getColor(x, y));
                }
            }
        }

        // Display the cut image
        imageView.setImage(cutImage);
        ImageHandler.artboard2.setImage(cutImage);
    }


    private static boolean isPixelInsidePath(List<Point> points, int x, int y) {
        // Cast a ray from the pixel to a point far outside the image
        int outsideX = (int) (imageView.getImage().getWidth() * 2);
        int outsideY = (int) (imageView.getImage().getHeight() * 2);
        int intersections = 0;
        for (int i = 0; i < points.size(); i++) {
            int j = (i + 1) % points.size();
            int x1 = points.get(i).x;
            int y1 = points.get(i).y;
            int x2 = points.get(j).x;
            int y2 = points.get(j).y;

            if (doLineSegmentsIntersect(x, y, outsideX, outsideY, x1, y1, x2, y2)) {
                intersections++;
            }
        }
        return (intersections % 2 == 1);
    }

    private static boolean doLineSegmentsIntersect(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        double d1 = direction(x3, y3, x4, y4, x1, y1);
        double d2 = direction(x3, y3, x4, y4, x2, y2);
        double d3 = direction(x1, y1, x2, y2, x3, y3);
        double d4 = direction(x1, y1, x2, y2, x4, y4);
        return ((d1 > 0 && d2 < 0 || d1 < 0 && d2 > 0) && (d3 > 0 && d4 < 0 || d3 < 0 && d4 > 0));
    }

    private static double direction(int x1, int y1, int x2, int y2, int x3, int y3) {
        return ((x3 - x1) * (y2 - y1)) - ((x2 - x1) * (y3 - y1));
    }


    private static void redrawCanvas() {
        GraphicsContext gc = Artboard.canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, image.getWidth(), image.getHeight());
    }
}