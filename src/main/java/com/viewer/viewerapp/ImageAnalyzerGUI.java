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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ImageAnalyzerGUI {

    private final ImageView imageView;
    private final Label maxPixelValueLabel;
    private final Label minPixelValueLabel;
    private final Label meanPixelValueLabel;
    private final Label imageAreaLabel;

    public ImageAnalyzerGUI(Artboard artboard) {
        ImageView originalImageView = artboard.getImageView();
        this.imageView = new ImageView(originalImageView.getImage());
        this.imageView.setFitWidth(originalImageView.getFitWidth());
        this.imageView.setFitHeight(originalImageView.getFitHeight());
        this.imageView.setPreserveRatio(true);
        this.maxPixelValueLabel = new Label();
        this.minPixelValueLabel = new Label();
        this.meanPixelValueLabel = new Label();
        this.imageAreaLabel = new Label();
    }

    public void show() {
        Stage analyzetStage = new Stage(StageStyle.UTILITY);
        analyzetStage.setResizable(false);
        analyzetStage.initModality(Modality.APPLICATION_MODAL);
        analyzetStage.setTitle("Image Analyzer");

        Image image = imageView.getImage();

        analyzeImage(image);

        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setAlignment(Pos.CENTER);

        HBox analysisBoxes = new HBox(10);
        analysisBoxes.setAlignment(Pos.CENTER);
        analysisBoxes.setPadding(new Insets(10));
        analysisBoxes.getChildren().addAll(createAnalysisBox("Maximum pixel value:", maxPixelValueLabel), createAnalysisBox("Minimum pixel value:", minPixelValueLabel), createAnalysisBox("Mean pixel value:", meanPixelValueLabel), createAnalysisBox("Image area:", imageAreaLabel));

        Button exportButton = new Button("Export");
        exportButton.setOnAction(event -> exportAnalysisData());

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> analyzetStage.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(exportButton, cancelButton);

        container.getChildren().addAll(imageView, analysisBoxes, buttonBox);

        Scene scene = new Scene(container);
        analyzetStage.setScene(scene);
        analyzetStage.showAndWait();
    }

    private VBox createAnalysisBox(String labelText, Label valueLabel) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        box.getChildren().addAll(label, valueLabel);
        return box;
    }

    // Rest of the code remains the same, except the exportAnalysisData() method

    private void exportAnalysisData() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Analysis Data");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showSaveDialog(imageView.getScene().getWindow());

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("Analysis Data\n\n");
            fileWriter.write(String.format("%-20s|%-20s|%-20s|%-20s%n", "Max Pixel Value", "Min Pixel Value", "Mean Pixel Value", "Image Area"));
            fileWriter.write("--------------------------------------------------------------------\n");
            fileWriter.write(String.format("%-20s|%-20s|%-20s|%-20s%n", maxPixelValueLabel.getText(), minPixelValueLabel.getText(), meanPixelValueLabel.getText(), imageAreaLabel.getText()));
            fileWriter.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Analysis Data Saved");
            alert.setHeaderText(null);
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Saving Analysis Data");
            alert.setHeaderText(null);
            alert.setContentText("There was an error saving the analysis data. Please try again.");
            alert.showAndWait();
        }
    }

    private void analyzeImage(Image image) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int pixelCount = imageWidth * imageHeight;

        int maxPixelValue = 0;
        int minPixelValue = 255;
        int sumPixelValues = 0;

        for (int x = 0; x < imageWidth; x++) {
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
