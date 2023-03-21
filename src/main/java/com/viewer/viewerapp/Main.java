package com.viewer.viewerapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private static Label Xcor = new Label();
    private static Label Ycor = new Label();
    private static Label Cocor = new Label();

    Label XLabel = new Label("X coordinate:");
    Label YLabel = new Label("Y coordinate:");
    Label CoLabel = new Label("pixel color: ");
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

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.addColumn(1, XLabel, Xcor);
        gridPane.addColumn(2, YLabel, Ycor);
        gridPane.addColumn(3, CoLabel, Cocor);

        artboards.getChildren().addAll(artboard1, artboard2);

        artboard1.setOnMouseClicked(event -> {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Color color = ImageHandler.pixelReader.getColor(x, y);

            Xcor.setText(Integer.toString(x));
            Ycor.setText(Integer.toString(y));
            Cocor.setText(String.valueOf(color));
        });


        // Add text information to the bottom of the content pane
        StackPane textPane = new StackPane();

        Label paneInfo = new Label("Text information");
        textPane.getStyleClass().add("text-pane");
        textPane.getChildren().add(paneInfo);

        // Create artboard container wrapped inside ScrollPane to content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setContent(artboards);

        // Add artboard container wrapped inside ScrollPane to content
        StackPane content = new StackPane();
        content.getStyleClass().add("stack-pane");
        content.getChildren().add(scrollPane);

        // Create root layout
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setTop(menuBar);
        root.setLeft(sidebar);
        root.setCenter(content);
        root.setBottom(gridPane);

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