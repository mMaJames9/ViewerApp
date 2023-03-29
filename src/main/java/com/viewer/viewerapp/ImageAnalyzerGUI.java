package com.viewer.viewerapp;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ImageAnalyzerGUI {
    private static Label maxPixelValueLabel = new Label();
    private static Label minPixelValueLabel = new Label();
    private static Label meanPixelValueLabel = new Label();
    private static Label imageAreaLabel = new Label();
    private static ImageView imageView = new ImageView();


    public static void Measure( ) throws IOException {
        Stage stage = new Stage();
        BufferedImage buffer = ImageIO.read(ImageHandler.file);
        Image image = SwingFXUtils.toFXImage(buffer,null);
        imageView = new ImageView(image);
        // Create a label and button for selecting an image file
        Label imageLabel = new Label("Select an image file:");
        Button imageButton = new Button("Measure");
        imageButton.setOnAction(event -> {
            try {
                analyzeImage(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Create image view for displaying the selected image

        // Create labels for displaying the analysis results
        Label maxLabel = new Label("Maximum pixel value:");
        Label minLabel = new Label("Minimum pixel value:");
        Label meanLabel = new Label("Mean pixel value:");
        Label areaLabel = new Label("Image area:");

        // Add labels to a grid pane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.addRow(0, imageLabel, imageButton);
        gridPane.addRow(1, imageView);
        gridPane.addRow(2, maxLabel, maxPixelValueLabel);
        gridPane.addRow(3, minLabel, minPixelValueLabel);
        gridPane.addRow(4, meanLabel, meanPixelValueLabel);
        gridPane.addRow(5, areaLabel, imageAreaLabel);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            try {
                // Open a file chooser to select the file to save the labels to
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Labels");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
                File f = fileChooser.showSaveDialog(stage);

                // Save the label values to the selected file as text
                FileWriter fileWriter = new FileWriter(f);
                fileWriter.write("MEASUREMENT OF : " + imageView + "\n\n");
                fileWriter.write("Maximum pixel value: " + maxPixelValueLabel.getText() + "\n");
                fileWriter.write("Minimum pixel value: " + minPixelValueLabel.getText() + "\n");
                fileWriter.write("Mean pixel value: " + meanPixelValueLabel.getText() + "\n");
                fileWriter.write("Image area: " + imageAreaLabel.getText() + "\n");
                fileWriter.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Analysis Saved");
                alert.setHeaderText(null);
                alert.setContentText("The Analysis has been saved to " + f.getAbsolutePath());
                alert.showAndWait();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        gridPane.getChildren().add(saveButton);
        gridPane.setAlignment(Pos.BOTTOM_LEFT);

        // Create a scene and set it on the stage
        Scene analscene = new Scene(gridPane, 400, 500);
        stage.setScene(analscene);
        stage.setTitle("Image Analyzer");
        stage.show();
    }
    private static void analyzeImage(ImageView imageFile) throws IOException {
        // Read the image file into a BufferedImage object
        Image image= imageFile.getImage();

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(300);
        imageView.setFitHeight(300);
        // Display the image in the image view
        imageView.setImage(image);

        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int pixelCount = imageWidth * imageHeight;

        int maxPixelValue = 0;
        int minPixelValue = 255;
        int sumPixelValues = 0;

        for(int x = 0;x < imageWidth;x++){
            for (int y = 0; y < imageHeight; y++) {
                int pixelValue = bufferedImage.getRaster().getSample(x, y, 0);
                if (pixelValue > maxPixelValue) {
                    maxPixelValue = pixelValue;
                }

                if (pixelValue < minPixelValue) {
                    minPixelValue = pixelValue;
                }

                sumPixelValues += pixelValue;
            }
        }

        double meanPixelValue = (double) sumPixelValues / pixelCount;
        int imageArea = imageWidth * imageHeight;

        // Display the analysis results in the corresponding labels
        maxPixelValueLabel.setText(Integer.toString(maxPixelValue));
        minPixelValueLabel.setText(Integer.toString(minPixelValue));
        meanPixelValueLabel.setText(String.format("%.2f", meanPixelValue));
        imageAreaLabel.setText(Integer.toString(imageArea));
    }
}