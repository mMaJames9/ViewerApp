package com.viewer.viewerapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create image handler to handle loading and displaying images
        ImageHandler imageHandler = new ImageHandler();

        // Create navbar
        Navbar menuBar = new Navbar(imageHandler);

        // Create sidebar
        Sidebar sidebar = new Sidebar();

        // Create artboard container
        HBox artboards = new HBox();
        artboards.getStyleClass().add("artboards");

        // Create and add artboards
        Artboard artboard1 = new Artboard();
        Artboard artboard2 = new Artboard();

        ImageHandler.setArtboard(artboard2);
        ImageHandler.setArtboard(artboard2);

        artboards.getChildren().addAll(artboard1, artboard2);


        // Add text information to the bottom of the content pane
        StackPane textPane = new StackPane();

        Label paneInfo = new Label("Text information");
        textPane.getStyleClass().add("text-pane");
        textPane.getChildren().add(paneInfo);

        // Add artboard container wrapped inside ScrollPane to content
        StackPane content = new StackPane();
        content.getStyleClass().add("stack-pane");
        content.getChildren().add(artboards);

        // Create root layout
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setTop(menuBar);
        root.setLeft(sidebar);
        root.setCenter(content);
        root.setBottom(textPane);

        // Create scene and add stylesheets
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().addAll(Objects.requireNonNull(getClass().getResource("/com/viewer/viewerapp/css/style.css")).toExternalForm());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        // Set stage properties and show stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Image Viewer");
        primaryStage.show();
    }
}