package com.viewer.viewerapp;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ISQImageReader extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a File object for the ISQ image file
        File isqFile = new File("C:\\Users\\root\\Downloads\\C0003120 (1).ISQ");

        try {
            // Open a FileInputStream to read the contents of the file
            FileInputStream isqStream = new FileInputStream(isqFile);

            // Create a byte array to hold the contents of the file
            byte[] isqBytes = new byte[(int)isqFile.length()];

            // Read the contents of the file into the byte array
            isqStream.read(isqBytes);

            // Close the FileInputStream
            isqStream.close();

            // Create an InputStream from the byte array
            ByteArrayInputStream bis = new ByteArrayInputStream(isqBytes);

            // Create an Image object from the InputStream
            Image isqImage = new Image(bis);

            // Create an ImageView to display the Image
            ImageView imageView = new ImageView(isqImage);

            // Create a StackPane to hold the ImageView
            StackPane root = new StackPane();
            root.getChildren().add(imageView);

            // Create a Scene with the StackPane as the root node
            Scene scene = new Scene(root, isqImage.getWidth(), isqImage.getHeight());

            // Set the Scene of the Stage and show it
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}

