package com.viewer.viewerapp;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

public class FlipperFX {

    private ImageView imageView;
    private static ImageView flippedImageView;
    private String filePath;
    private Image srcImage, dstImage;

public static void VertFlip( ImageView imageView ) throws IOException {

    Image image = imageView.getImage();
    flippedImageView = new ImageView();
    flippedImageView.setPreserveRatio(true);
    flippedImageView.setFitWidth(USE_PREF_SIZE);
    flippedImageView.setFitHeight(USE_PREF_SIZE);

    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    int[] pixels = new int[width * height];
    // Get the pixels from the source image
    image.getPixelReader().getPixels(0, 0, width, height, javafx.scene.image.PixelFormat.getIntArgbInstance(), pixels, 0, width);

    // Create a new int array to store the flipped image pixels
    int[] flippedPixels = new int[width * height];

    // Flip the image vertically by copying the pixels from the bottom row to the top row and vice versa
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            flippedPixels[(height - y - 1) * width + x] = pixels[y * width + x];
        }
    }

    // Create a WritableImage object and set the flipped pixels
    javafx.scene.image.WritableImage flippedImage = new javafx.scene.image.WritableImage(width, height);
    flippedImage.getPixelWriter().setPixels(0, 0, width, height, javafx.scene.image.PixelFormat.getIntArgbInstance(), flippedPixels, 0, width);
    // Update the flipped image view
    flippedImageView.setImage(flippedImage);
    ImageHandler.artboard2.setImage(flippedImage);
}

    public static void Horfrip(ImageView imageView ) throws IOException {

        Image image = imageView.getImage();

        flippedImageView = new ImageView();
        flippedImageView.setPreserveRatio(true);
        flippedImageView.setFitWidth(USE_PREF_SIZE);
        flippedImageView.setFitHeight(USE_PREF_SIZE);

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        int[] pixels = new int[width * height];

        // Get the pixels from the source image
        image.getPixelReader().getPixels(0, 0, width, height, javafx.scene.image.PixelFormat.getIntArgbInstance(), pixels, 0, width);

        // Create a new int array to store the flipped image pixels
        int[] flippedPixels = new int[width * height];

        // Flip the image horizontally by copying the pixels from the right column to the left column and vice versa
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                flippedPixels[y * width + (width - x - 1)] = pixels[y * width + x];
            }

        }
        // Create a WritableImage object and set the flipped pixels
        javafx.scene.image.WritableImage flippedImage = new javafx.scene.image.WritableImage(width, height);
        flippedImage.getPixelWriter().setPixels(0, 0, width, height, javafx.scene.image.PixelFormat.getIntArgbInstance(), flippedPixels, 0, width);

        // Update the flipped image view
        flippedImageView.setImage(flippedImage);
        ImageHandler.artboard2.setImage(flippedImage);
    }

}
