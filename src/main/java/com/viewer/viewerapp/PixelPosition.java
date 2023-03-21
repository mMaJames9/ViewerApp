package com.viewer.viewerapp;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;

public class PixelPosition  {
    private static TextField xInput = new TextField();
    private static TextField yInput = new TextField();
    static Line line1;
    static Line line2;

    static void pixelPosition(ImageView imageView) throws IOException {
            Stage stage = new Stage();
            Image image= imageView.getImage();
            // Display the image in a GUI
            imageView = new ImageView(image);
            BorderPane root = new BorderPane();
            root.setTop(imageView);

            HBox hbox = new HBox();
            hbox.setPadding(new Insets(10, 10, 10, 10));
            hbox.setSpacing(10);
            hbox.getChildren().addAll(new Label("X: "), xInput, new Label("Y: "), yInput);
            root.setBottom(hbox);

            Scene scene = new Scene(root, image.getWidth()+100, image.getHeight()+50);
            stage.setScene(scene);
            stage.show();

            xInput.setPromptText("Enter x coordinate");
            yInput.setPromptText("Enter y coordinate");

            // Show the selected pixel on the image
            xInput.setOnAction(event -> {
                int x = Integer.parseInt(xInput.getText());
                int y = Integer.parseInt(yInput.getText());

                line1 = new Line(x, 0, x, image.getHeight());
                line1.setStrokeWidth(1);
                line1.setStroke(Color.BLUE);
                line2 = new Line(0, y, image.getWidth(), y);
                line2.setStrokeWidth(1);
                line2.setStroke(Color.BLUE);
                
            });

            yInput.setOnAction(event -> {
                int x = Integer.parseInt(xInput.getText());
                int y = Integer.parseInt(yInput.getText());

                Line line1 = new Line(x, 0, x, image.getHeight());
                line1.setStrokeWidth(1);
                line1.setStroke(Color.BLUE);
                Line line2 = new Line(0, y, image.getWidth(), y);
                line2.setStrokeWidth(1);
                line2.setStroke(Color.BLUE);
                root.getChildren().addAll(line1, line2);
            });
        }
}
