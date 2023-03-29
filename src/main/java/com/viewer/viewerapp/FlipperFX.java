package com.viewer.viewerapp;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

public class FlipperFX {

    public static void flipImage(Artboard artboard, boolean horizontal) {
        ImageView imageView = artboard.getImageView();
        Image image = imageView.getImage();

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        int[] pixels = new int[width * height];
        image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

        int[] flippedPixels = new int[width * height];

        if (horizontal) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    flippedPixels[y * width + (width - x - 1)] = pixels[y * width + x];
                }
            }
        } else {
            for (int y = 0; y < height; y++) {
                System.arraycopy(pixels, y * width, flippedPixels, (height - y - 1) * width, width);
            }
        }

        WritableImage flippedImage = new WritableImage(width, height);
        flippedImage.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), flippedPixels, 0, width);
        artboard.setImage(flippedImage);
    }

}