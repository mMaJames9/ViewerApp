package com.viewer.viewerapp;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class RotationFX {

    public static void rotate(Artboard artboard) {
        ImageView imageView = artboard.getImageView();
        Stage rotateStage = createRotateStage(imageView, artboard);
        rotateStage.initModality(Modality.APPLICATION_MODAL);
        rotateStage.showAndWait();
    }

    private static Stage createRotateStage(ImageView imageView, Artboard artboard) {
        Stage rotateStage = new Stage(StageStyle.UTILITY);
        rotateStage.setResizable(false);

        double diagonal = Math.sqrt(Math.pow(imageView.getFitWidth(), 2) + Math.pow(imageView.getFitHeight(), 2));
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(diagonal, diagonal);

        ImageView previewImageView = new ImageView(imageView.getImage());
        previewImageView.setPreserveRatio(true);
        previewImageView.setFitWidth(imageView.getFitWidth());
        previewImageView.setFitHeight(imageView.getFitHeight());

        imageContainer.getChildren().add(previewImageView);
        imageContainer.setClip(new Rectangle(diagonal, diagonal));

        TextField angleInput = new TextField();
        angleInput.setPromptText("Enter rotation angle");

        ComboBox<String> directionComboBox = new ComboBox<>(FXCollections.observableArrayList("Left", "Right"));
        directionComboBox.getSelectionModel().selectFirst();

        Button validateButton = new Button("Validate");
        validateButton.setOnAction(event -> {
            try {
                int angle = Integer.parseInt(angleInput.getText());
                String direction = directionComboBox.getValue();
                int finalAngle = direction.equals("Left") ? -angle : angle;
                previewImageView.setImage(rotateImage(previewImageView.getImage(), finalAngle));
            } catch (NumberFormatException e) {
                showAlert();
            }
        });

        HBox rotateInputBox = new HBox(5);
        rotateInputBox.setAlignment(Pos.CENTER);
        rotateInputBox.getChildren().addAll(angleInput, directionComboBox, validateButton);

        Button applyButton = new Button("Apply");
        applyButton.setOnAction(e -> {
            imageView.setImage(previewImageView.getImage());
            artboard.setImage(previewImageView.getImage());
            rotateStage.close();
        });

        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10));
        container.getChildren().addAll(imageContainer, rotateInputBox, applyButton);

        Scene scene = new Scene(container);
        rotateStage.setScene(scene);

        return rotateStage;
    }

    private static Image rotateImage(Image image, int angle) {
        BufferedImage bufferedImage = toBufferedImage(image);
        double radians = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int newWidth = (int) Math.floor(width * cos + height * sin);
        int newHeight = (int) Math.floor(height * cos + width * sin);

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0);
        at.rotate(radians, width / 2.0, height / 2.0);
        AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(bufferedImage, rotatedImage);

        return toJavaFXImage(rotatedImage);
    }

    private static BufferedImage toBufferedImage(Image image) {
        BufferedImage bufferedImage = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        javafx.embed.swing.SwingFXUtils.fromFXImage(image, bufferedImage);
        return bufferedImage;
    }

    private static Image toJavaFXImage(BufferedImage bufferedImage) {
        return javafx.embed.swing.SwingFXUtils.toFXImage(bufferedImage, null);
    }

    private static void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText("Please enter a valid rotation angle.");
        alert.showAndWait();
    }
}