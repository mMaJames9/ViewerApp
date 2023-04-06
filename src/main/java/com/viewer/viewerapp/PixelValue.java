package com.viewer.viewerapp;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class PixelValue {
    private final Artboard artboard;

    public PixelValue(Artboard artboard) {
        this.artboard = artboard;
    }

    public void handleClick(MouseEvent event) {
        ImageView imageView = artboard.getImageView();
        Image image = imageView.getImage();

        if (image != null) {
            // Get original image dimensions from the Artboard
            double originalImageWidth = artboard.getImageWidth();
            double originalImageHeight = artboard.getImageHeight();

            // Calculate the scaling factor
            double scaleX = originalImageWidth / imageView.getFitWidth();
            double scaleY = originalImageHeight / imageView.getFitHeight();

            // Calculate the coordinates based on the scaling factor
            int x = (int) (event.getX() * scaleX);
            int y = (int) (event.getY() * scaleY);

            if (x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight()) {
                int argb = image.getPixelReader().getArgb(x, y);

                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                double intensity = (0.2989 * r) + (0.5870 * g) + (0.1140 * b);

                String pixelInfo = String.format("X: %d, Y: %d, Intensity: %.2f, Color: rgb(%d, %d, %d)", x, y, intensity, r, g, b);
                Main.getPaneInfo().setText(pixelInfo);
            }
        }
    }
}

