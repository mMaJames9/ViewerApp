package com.viewer.viewerapp;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.viewer.viewerapp.ImageHandler.artboard2;

public class Crop {
    private static double xStart, yStart, xEnd, yEnd;

    private static Image image;
    static Button cropButton = new Button("Save");
    private static Rectangle2D clip;

    private static boolean showCrop;
    private static GraphicsContext gc;

    public static void crop(ImageView imageView) throws IOException {

        image= Sidebar.newview.getImage();
        gc = Artboard.canvas.getGraphicsContext2D();

        Artboard.canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, Crop::onMousePressed);
        Artboard.canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, Crop::onMouseDragged);
        Artboard.canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, Crop::onMouseReleased);

        cropButton.setOnAction(event -> {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            BufferedImage croppedImage = bufferedImage.getSubimage((int) clip.getMinX(), (int) clip.getMinY(),
                    (int) clip.getWidth(), (int) clip.getHeight());
            redrawCanvas();
             image= SwingFXUtils.toFXImage(croppedImage, null);
            artboard2.setImage(image);
        });
    }

    private static void onMousePressed(MouseEvent e) {
        gc.clearRect(0, 0, Artboard.canvas.getWidth(), Artboard.canvas.getHeight());
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);

        double x = e.getX();
        double y = e.getY();

        gc.beginPath();
        gc.moveTo(x, y);
        gc.lineTo(x, y);
        gc.stroke();

        clip = new Rectangle2D(x, y, 0, 0);
    }

    private static void onMouseDragged(MouseEvent e) {
        gc.clearRect(0, 0, Artboard.canvas.getWidth(), Artboard.canvas.getHeight());
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);

        double x = e.getX();
        double y = e.getY();

        gc.beginPath();
        gc.moveTo(clip.getMinX(), clip.getMinY());
        gc.lineTo(x, clip.getMinY());
        gc.lineTo(x, y);
        gc.lineTo(clip.getMinX(), y);
        gc.lineTo(clip.getMinX(), clip.getMinY());
        gc.stroke();

        double width = Math.abs(x - clip.getMinX());
        double height = Math.abs(y - clip.getMinY());
        clip = new Rectangle2D(clip.getMinX(), clip.getMinY(), width, height);
    }

    private static void onMouseReleased(MouseEvent e) {
        gc.clearRect(0, 0, Artboard.canvas.getWidth(), Artboard.canvas.getHeight());

        gc.setStroke(Color.RED);
        gc.setLineWidth(2);

        double x = e.getX();
        double y = e.getY();

        double width = Math.abs(x - clip.getMinX());
        double height = Math.abs(y - clip.getMinY());
        clip = new Rectangle2D(clip.getMinX(), clip.getMinY(), width, height);

        gc.beginPath();
        gc.moveTo(clip.getMinX(), clip.getMinY());
        gc.lineTo(clip.getMaxX(), clip.getMinY());
        gc.lineTo(clip.getMaxX(), clip.getMaxY());
        gc.lineTo(clip.getMinX(), clip.getMaxY());
        gc.lineTo(clip.getMinX(), clip.getMinY());
        gc.stroke();

        showCrop = true;

    }

    static void redrawCanvas() {
        GraphicsContext gc = Artboard.canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, image.getWidth(), image.getHeight());
    }
}

