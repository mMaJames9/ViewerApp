package com.viewer.viewerapp;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageGraph  {

    private static final int AXIS_MARGIN = 50;
    static Image IMG;

    public static void graphimg(ImageView imageView) throws IOException {

        // Load the image
        Stage primaryStage = new Stage();
        IMG= imageView.getImage();

        BufferedImage image = SwingFXUtils.fromFXImage(IMG, null);
        int w = image.getWidth();
        int h = image.getHeight();
        // Create a canvas with the size of the graph plus the axis margins
        Canvas canvas = new Canvas(w + AXIS_MARGIN * 2, h + AXIS_MARGIN * 2);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw the x and y axis
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        gc.strokeLine(AXIS_MARGIN, h + AXIS_MARGIN, w + AXIS_MARGIN, h + AXIS_MARGIN); // x axis
        gc.strokeLine(AXIS_MARGIN, AXIS_MARGIN, AXIS_MARGIN, h + AXIS_MARGIN); // y axis

        // Draw the image on the canvas with its pixel values
        for (int i = 0; i < w ; i++) {
            for (int j = 0; j < h; j++) {
                int pixel = image.getRGB(i, j);
                Paint color = Color.rgb((pixel >> 16) & 0xff, (pixel >> 8) & 0xff, pixel & 0xff);
                gc.setFill(color);
                gc.fillRect(i + AXIS_MARGIN, j + AXIS_MARGIN, 5, 5);
            }
        }

        // Draw the ticks and labels on the x axis
        double tickSize = w / 10.0;
        double x = AXIS_MARGIN;
        double y = h + AXIS_MARGIN + 20;
        for (int i = 0; i <= 10; i++) {
            gc.strokeLine(x, AXIS_MARGIN, x, h + AXIS_MARGIN + 5);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.strokeText(String.format("%.0f", x - AXIS_MARGIN), x, y);
            x += tickSize;
        }

        // Draw the ticks and labels on the y axis
        tickSize = h / 10.0;
        x = AXIS_MARGIN - 20;
        y = AXIS_MARGIN;
        for (int i = 0; i <= 10; i++) {
            gc.strokeLine(AXIS_MARGIN - 5, y, w +AXIS_MARGIN, y); // horizontal tick line
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.strokeText(String.format("%.0f", y - AXIS_MARGIN), x, y); // <-- modified line
            y += tickSize; // <-- modified line
        }

        // Create a stack pane to hold the canvas
        StackPane root = new StackPane();
        root.getChildren().add(canvas);


        // Create the scene and show the stage
        Scene scene = new Scene(root, w + AXIS_MARGIN * 2, h + AXIS_MARGIN * 2);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
