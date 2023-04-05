package com.viewer.viewerapp;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class Segment {

    private final ImageView imageView;
    private final Artboard artboard;
    private final List<Point> points = new ArrayList<>();
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final CheckBox colorizeCheckBox = new CheckBox("Apply Colorization");
    private final CheckBox segmentCheckBox = new CheckBox("Segment Image");
    private boolean selectionClosed = false;

    private Segment(Artboard artboard) {
        this.artboard = artboard;
        ImageView originalImageView = artboard.getImageView();
        this.imageView = new ImageView(originalImageView.getImage());
        this.imageView.setFitWidth(originalImageView.getFitWidth());
        this.imageView.setFitHeight(originalImageView.getFitHeight());
        this.imageView.setPreserveRatio(true);
        this.canvas = new Canvas(imageView.getFitWidth(), imageView.getFitHeight());
        this.gc = canvas.getGraphicsContext2D();
        setupCanvasEventHandlers();
    }

    public static void showSegmentTool(Artboard artboard) {
        Segment segment = new Segment(artboard);
        segment.showSegmentStage();
    }

    private void showSegmentStage() {
        Stage segmentStage = new Stage(StageStyle.UTILITY);
        segmentStage.setResizable(false);
        segmentStage.initModality(Modality.APPLICATION_MODAL);
        segmentStage.setTitle("Segment Image");

        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setAlignment(Pos.CENTER);

        Button validateButton = new Button("Validate");
        validateButton.setOnAction(event -> {
            if (points.size() > 2) {
                if (!colorizeCheckBox.isSelected() && !segmentCheckBox.isSelected()) {
                    // Show an alert if neither checkbox is selected
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select at least one option (Apply Colorization or Segment Image) before validating.");
                    alert.showAndWait();
                    return;
                }

                closeAndCut();

                Image resultImage = imageView.getImage();

                if (colorizeCheckBox.isSelected()) {
                    // Apply colorization to the selected area
                    resultImage = colorizeImage();
                }

                if (segmentCheckBox.isSelected()) {
                    // Cut the image and set it to the artboard
                    resultImage = cutImage();
                }

                // Set the result image to the artboard
                artboard.setImage(resultImage);

                // Clear the points and close the stage
                points.clear();
                Platform.runLater(segmentStage::close);
            }
        });

        VBox checkBox = new VBox(10);
        checkBox.setPadding(new Insets(10, 0, 0, 0));
        checkBox.getChildren().addAll(colorizeCheckBox, segmentCheckBox);

        HBox inputBox = new HBox(10);
        inputBox.setPadding(new Insets(10, 0, 0, 0));
        inputBox.setAlignment(Pos.CENTER);
        inputBox.getChildren().addAll(validateButton);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(imageView, canvas);

        container.getChildren().addAll(stackPane, checkBox, inputBox);

        Scene scene = new Scene(container);
        segmentStage.setScene(scene);
        segmentStage.showAndWait();
    }

    private void setupCanvasEventHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMouseClick);

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (!points.isEmpty()) {
                Point firstPoint = points.get(0);
                if (isMouseCloseToFirstPoint(event.getX(), event.getY(), firstPoint.x, firstPoint.y)) {
                    gc.setFill(Color.YELLOW);
                    gc.fillOval(firstPoint.x - 3, firstPoint.y - 3, 6, 6);
                } else {
                    redrawCanvas();
                }
            }
        });
    }

    private void handleMouseClick(MouseEvent event) {
        Point point = new Point((int) event.getX(), (int) event.getY());

        if (!points.isEmpty()) {
            Point firstPoint = points.get(0);
            if (isMouseCloseToFirstPoint(event.getX(), event.getY(), firstPoint.x, firstPoint.y)) {
                closeAndCut();
                selectionClosed = true;
                redrawCanvas(); // Redraw the canvas after closing the polygon
                return;
            }
        }

        if (selectionClosed) {
            selectionClosed = false; // Set selectionClosed to false before clearing points and redrawing
            points.clear();
            redrawCanvas();
        }

        points.add(point);

        // Draw a red dot at the clicked point
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

    private boolean isMouseCloseToFirstPoint(double mouseX, double mouseY, int firstPointX, int firstPointY) {
        double distance = Math.sqrt(Math.pow(mouseX - firstPointX, 2) + Math.pow(mouseY - firstPointY, 2));
        return distance <= 10;
    }

    private void closeAndCut() {
        // Close the polygon by connecting the last point to the first point
        Point firstPoint = points.get(0);
        Point lastPoint = points.get(points.size() - 1);
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeLine(lastPoint.x, lastPoint.y, firstPoint.x, firstPoint.y);
    }

    private Image cutImage() {
        // Create a segmented image with transparency outside the selected area
        return ImageSegmentationUtil.createSegmentedImage(imageView.getImage(), points, imageView.getFitWidth(), imageView.getFitHeight());
    }

    private Image colorizeImage() {

        // Call the utility method to create a colorized image
        return ImageSegmentationUtil.createColorizedImage(imageView.getImage(), points, imageView.getFitWidth(), imageView.getFitHeight());

    }


    private void redrawCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawPointsAndLines();

        if (selectionClosed) {
            closeAndCut();
        }
    }

    private void drawPointsAndLines() {
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);

            // Draw a red dot at the point
            gc.setFill(Color.RED);
            gc.fillOval(point.x - 3, point.y - 3, 6, 6);

            // Draw red lines between the points
            if (i > 0) {
                Point prevPoint = points.get(i - 1);
                gc.setStroke(Color.RED);
                gc.setLineWidth(2);
                gc.strokeLine(prevPoint.x, prevPoint.y, point.x, point.y);
            }
        }
    }
}

class ImageSegmentationUtil {

    public static Image createSegmentedImage(Image image, List<Point> points, double fitWidth, double fitHeight) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader pixelReader = image.getPixelReader();
        WritableImage segmented = new WritableImage(width, height);
        PixelWriter pixelWriter = segmented.getPixelWriter();

        double scaleX = width / fitWidth;
        double scaleY = height / fitHeight;

        Path2D.Double path = new Path2D.Double();
        path.moveTo(points.get(0).x * scaleX, points.get(0).y * scaleY);

        for (int i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i).x * scaleX, points.get(i).y * scaleY);
        }

        path.closePath();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (path.contains(x, y)) {
                    pixelWriter.setArgb(x, y, pixelReader.getArgb(x, y));
                } else {
                    // Set the pixel to transparent
                    pixelWriter.setArgb(x, y, 0);
                }
            }
        }
        return segmented;
    }

    public static Image createColorizedImage(Image image, List<Point> points, double fitWidth, double fitHeight) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader pixelReader = image.getPixelReader();
        WritableImage colorizedImage = new WritableImage(width, height);
        PixelWriter pixelWriter = colorizedImage.getPixelWriter();

        double scaleX = width / fitWidth;
        double scaleY = height / fitHeight;

        Path2D.Double path = new Path2D.Double();
        path.moveTo(points.get(0).x * scaleX, points.get(0).y * scaleY);

        for (int i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i).x * scaleX, points.get(i).y * scaleY);
        }

        path.closePath();

        // Calculate the meanPixelValue
        double meanPixelValue = calculateMeanPixelValue(image);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (path.contains(x, y)) {
                    Color originalColor = pixelReader.getColor(x, y);
                    // Pass the meanPixelValue to calculateColorizedColor method
                    Color colorizedColor = calculateColorizedColor(originalColor, meanPixelValue);
                    pixelWriter.setColor(x, y, colorizedColor);
                } else {
                    pixelWriter.setArgb(x, y, pixelReader.getArgb(x, y));
                }
            }
        }
        return colorizedImage;
    }

    private static double calculateMeanPixelValue(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        PixelReader pixelReader = image.getPixelReader();
        long totalPixelValue = 0;
        long pixelCount = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = pixelReader.getColor(x, y);
                int grayValue = (int) (0.2989 * (color.getRed() * 255) + 0.5870 * (color.getGreen() * 255) + 0.1140 * (color.getBlue() * 255));
                totalPixelValue += grayValue;
                pixelCount++;
            }
        }

        return (double) totalPixelValue / pixelCount;

    }

    private static Color calculateColorizedColor(Color originalColor, double meanPixelValue) {
        int redValue = 0, greenValue = 0, blueValue = 0;
        int grayValue = (int) (0.2989 * (originalColor.getRed() * 255) + 0.5870 * (originalColor.getGreen() * 255) + 0.1140 * (originalColor.getBlue() * 255));

        int[][] thresholds = {{95, 120, 145}, {130, 150, 170}, {140, 155, 170}};

        double[][] factors = {{85.0, 85.0, 85.0}, {85.0, 85.0, 85.0}, {85.5, 85.0, 85.5}};

        int[] meanLimits = {100, 150};
        int meanIndex = (meanPixelValue < meanLimits[0]) ? 0 : (meanPixelValue < meanLimits[1]) ? 1 : 2;

        for (int i = 0; i < thresholds[meanIndex].length; i++) {
            if (grayValue <= thresholds[meanIndex][i]) {
                redValue = (i == 0) ? 255 : (int) (255 * ((thresholds[meanIndex][i] - grayValue) / factors[meanIndex][i]));
                greenValue = (int) (255 * ((grayValue - (i == 0 ? 0 : thresholds[meanIndex][i - 1])) / factors[meanIndex][i]));
                break;
            }
        }

        if (meanIndex == 0 && grayValue > thresholds[meanIndex][2]) {
            greenValue = (int) (255 * ((210 - grayValue) / 85.0));
            blueValue = 255;
        } else if (meanIndex > 0 && grayValue > thresholds[meanIndex][2]) {
            greenValue = (int) (255 * ((254 - grayValue) / 85.5));
            blueValue = 255;
        }

        // Clamp color values within the range 0-255
        redValue = Math.min(Math.max(redValue, 0), 255);
        greenValue = Math.min(Math.max(greenValue, 0), 255);

        return Color.rgb(redValue, greenValue, blueValue);
    }


}