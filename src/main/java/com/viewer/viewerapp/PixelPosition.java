package com.viewer.viewerapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PixelPosition {

    public static void showPixelPosition(Artboard artboard) {
        ImageView imageView = artboard.getImageView();
        Stage pixelPositionStage = createPixelPositionStage(imageView);
        pixelPositionStage.initModality(Modality.APPLICATION_MODAL);
        pixelPositionStage.showAndWait();
    }

    private static Stage createPixelPositionStage(ImageView imageView) {
        Stage pixelPositionStage = new Stage(StageStyle.UTILITY);
        pixelPositionStage.setTitle("Pixel Position");
        pixelPositionStage.setResizable(false);

        ImageView previewImageView = new ImageView(imageView.getImage());
        previewImageView.setPreserveRatio(true);
        previewImageView.setFitWidth(imageView.getFitWidth());
        previewImageView.setFitHeight(imageView.getFitHeight());

        Pane imagePane = new Pane(previewImageView);

        TextField xInput = new TextField();
        TextField yInput = new TextField();

        xInput.setPromptText("Enter x coordinate");
        yInput.setPromptText("Enter y coordinate");

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.getChildren().addAll(new Label("X: "), xInput, new Label("Y: "), yInput);

        Button findPositionButton = new Button("Find Position");
        findPositionButton.setOnAction(event -> {
            try {
                int x = Integer.parseInt(xInput.getText());
                int y = Integer.parseInt(yInput.getText());

                if (x < 0 || x >= imageView.getImage().getWidth() || y < 0 || y >= imageView.getImage().getHeight()) {
                    showAlert("Invalid Coordinates", "The entered coordinates are not on the image.");
                } else {
                    drawPixelPositionLines(imagePane, previewImageView, x, y);
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter integer values for X and Y coordinates.");
            }
        });

        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(imagePane, inputBox, findPositionButton);
        VBox.setMargin(findPositionButton, new Insets(10, 0, 0, 0));

        Scene scene = new Scene(container);
        pixelPositionStage.setScene(scene);

        return pixelPositionStage;
    }

    private static void drawPixelPositionLines(Pane imagePane, ImageView imageView, int x, int y) {
        imagePane.getChildren().removeIf(node -> node instanceof Line); // Remove any existing lines

        double widthRatio = imageView.getFitWidth() / imageView.getImage().getWidth();
        double heightRatio = imageView.getFitHeight() / imageView.getImage().getHeight();
        double scaleFactor = Math.min(widthRatio, heightRatio);

        int scaledImageWidth = (int) Math.round(imageView.getImage().getWidth() * scaleFactor);
        int scaledImageHeight = (int) Math.round(imageView.getImage().getHeight() * scaleFactor);

        int offsetX = (int) Math.round((imageView.getFitWidth() - scaledImageWidth) / 2);
        int offsetY = (int) Math.round((imageView.getFitHeight() - scaledImageHeight) / 2);

        int scaledX = (int) Math.round(x * scaleFactor) + offsetX;
        int scaledY = (int) Math.round(y * scaleFactor) + offsetY;

        Line line1 = new Line(scaledX, offsetY, scaledX, offsetY + scaledImageHeight);
        line1.setStrokeWidth(2);
        line1.setStroke(Color.ORANGE);
        Line line2 = new Line(offsetX, scaledY, offsetX + scaledImageWidth, scaledY);
        line2.setStrokeWidth(2);
        line2.setStroke(Color.ORANGE);
        imagePane.getChildren().addAll(line1, line2);
    }


    private static void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}