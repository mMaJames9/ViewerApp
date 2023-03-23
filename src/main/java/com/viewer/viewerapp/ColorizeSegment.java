package com.viewer.viewerapp;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ColorizeSegment {

    private static ImageView imageView;


    static Button segmentButton = new Button("Segment");
    static  ComboBox<String> colorComboBox = new ComboBox<>();
    private  static File file;
    private  static Image image;
    private  static List<Point> points = new ArrayList<>();
    public static void Colorize( ImageView img) throws IOException {

        // Load the image
        image = img.getImage();

        Artboard artboard = new Artboard();
        artboard.fitImage();
        // Initialize the numPointsTextField with a default value of 0
        TextField numPointsTextField = new TextField("0");
        // Initialize the colorComboBox with three color options
        segmentButton.setOnAction(event -> {
            try {
                int numPoints = Integer.parseInt(numPointsTextField.getText());
                if (points.size() >= numPoints) {
                    cutImage(numPoints);
                    // Reset the point count and clear the canvas
                    points.clear();
                    redrawCanvas();
                    // Update the numPointsTextField with the new count
                    numPointsTextField.setText("0");
                }
            } catch (NumberFormatException e) {
                // If the user entered an invalid number, do nothing
            }
        });

        // Display the image in a GUI
        imageView = new ImageView(image);

        // Add a canvas to draw the points and lines

        // Allow the user to select points on the image
        Artboard.canvas .addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
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
        GraphicsContext gc = Artboard.canvas .getGraphicsContext2D();
        gc.setFill(Color.RED);
        gc.fillOval(point.x - 3, point.y - 3, 6, 6);

        // Draw red lines between the points
        if (points.size() > 1) {
            Point prevPoint = points.get(points.size() - 2);
            gc.setStroke(Color.RED);
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
        WritableImage cutImage = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
        PixelWriter pixelWriter = cutImage.getPixelWriter();
// Set the drawing color to the chosen color
        // Copy the pixels inside the polygon to the new image
        PixelReader pixelReader = imageView.getImage().getPixelReader();
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                boolean inside = isPixelInsidePath(points, x, y);
                int argb = pixelReader.getArgb(x, y);
                int grayValue = (int) (0.2989 * ((argb >> 16) & 0xFF) + 0.5870 * ((argb >> 8) & 0xFF) + 0.1140 * (argb & 0xFF));
                if (inside) {
                    Color col = pixelReader.getColor(x, y);
                    int redValue, greenValue, blueValue;
                    // Calculate the gradient color based on the x-coordinate
                    if (grayValue < 135) {
                        redValue = 255;
                        greenValue = (int) (255 * ((100-grayValue) / 85.0));;
                        blueValue = 0;
                    } else if (grayValue < 170) {
                        redValue = (int) (255 * ((170 - grayValue) / 85.0));
                        greenValue = (int) (255 * ((grayValue - 85) / 85.0));
                        blueValue = 0;
                    } else {
                        redValue = 0;
                        greenValue = (int) (255 * ((254-grayValue ) / 85.0));
                        blueValue = 255;
                    }
                    int argbNew =  (0xFF << 24) |(redValue << 16) | (greenValue << 8) | blueValue;
                    pixelWriter.setArgb(x, y, argbNew);


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

    private static void createColorScheme() {
        // Define the color ranges for each cluster
        int[][] colorRanges = {{0, 50, 255, 0, 0}, {50, 100, 255, 165, 0}, {100, 150, 255, 255, 0},
                {150, 180, 0, 255, 0}, {180, 200, 0, 128, 0}, {200, 225, 135, 206, 250}, {225, 255, 0, 0, 255}};

        // Get the dimensions of the original image
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Create a new image with the same dimensions
        WritableImage colorScheme = new WritableImage(width, height);
        PixelWriter pixelWriter = colorScheme.getPixelWriter();

        // Iterate over each pixel in the original image
        PixelReader pixelReader = image.getPixelReader();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the intensity value of the pixel
                int intensity = (int) (pixelReader.getColor(x, y).getRed() * 255);

                // Find the color range that the intensity value belongs to
                int[] color = {0, 0, 0};
                for (int i = 0; i < colorRanges.length; i++) {
                    if (intensity >= colorRanges[i][0] && intensity < colorRanges[i][1]) {
                        color[0] = colorRanges[i][2];
                        color[1] = colorRanges[i][3];
                        color[2] = colorRanges[i][4];
                        break;
                    }
                }

                // Assign the new color to the pixel
                pixelWriter.setColor(x, y, javafx.scene.paint.Color.rgb(color[0], color[1], color[2]));
            }
        }

        // Display the new color scheme
        imageView.setImage(colorScheme);
    }



    private static void redrawCanvas() {
        GraphicsContext gc = Artboard.canvas .getGraphicsContext2D();
        gc.clearRect(0, 0, image.getWidth(), image.getHeight());
    }
}