package com.viewer.viewerapp;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageResizeGUI {
    public static Label widthLabel = new Label("Width:");
    public static TextField widthTextField = new TextField();

    public static Label heightLabel = new Label("Height:");
    public static TextField heightTextField = new TextField();
    public static Image image;
    public static Button resizeButton = new Button("Resize");
    public static ImageView resultView = new ImageView();
    public static void resize(ImageView imageView) {

        resizeButton.setOnAction(event -> {
            image= imageView.getImage();
            int width = 0;
            int height = 0;
            try {
                width = Integer.parseInt(widthTextField.getText());
                height = Integer.parseInt(heightTextField.getText());
            } catch (NumberFormatException ex) {
                // Show an error dialog if the input is not a number
                JOptionPane.showMessageDialog(null, "Width and Height must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BufferedImage originalImage = SwingFXUtils.fromFXImage(image, null);
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizedImage = new BufferedImage(width, height, type);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, width, height, null);
            g.dispose();

            // Show the resized image in the result view
            Image resultImage = SwingFXUtils.toFXImage(resizedImage, null);

            ImageHandler.artboard2.setImage(resultImage);
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            try {
                saveImage(resultView.getImage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void saveImage(Image image ) throws IOException {

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("TIFF",  "*.tiff"));

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {

            ImageIO.write(bufferedImage, "png", file);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Image Saved");
            alert.setHeaderText(null);
            alert.setContentText("The image has been saved to " + file.getAbsolutePath());
            alert.showAndWait();
        }
    }

}
