package com.viewer.viewerapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import javax.swing.*;
import java.util.EventListener;
import java.util.Objects;

import static com.viewer.viewerapp.Artboard.imageView;
import static com.viewer.viewerapp.ImageHandler.pixelReader;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
     static Label Xcor = new Label();
     static Label Ycor = new Label();
     static Label Cocor = new Label();

    Label XLabel = new Label("X coordinate:");
    Label YLabel = new Label("Y coordinate:");
    Label CoLabel = new Label("pixel color: ");

    // Create and add artboards
    Artboard artboard1 = new Artboard();
    Artboard artboard2 = new Artboard();
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


        ImageHandler.setArtboard(artboard2);


        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.addColumn(1, XLabel, Xcor);
        gridPane.addColumn(2, YLabel, Ycor);
        gridPane.addColumn(3, CoLabel, Cocor);

        artboards.getChildren().addAll(artboard1, artboard2);

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
        primaryStage.setTitle("ZEZE Viewer");
        Image icon = new Image("D:\\zeze work\\work\\Viewer Application 2\\Viewer Application\\src\\main\\java\\com\\viewer\\viewerapp\\ZZ_Viewer Logo.jpg");
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }
public static void hangleClick(MouseEvent e){

        int x = (int) e.getX();
        int y = (int) e.getY();
        Color color = pixelReader.getColor(y, x);
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        int intensity = (red + green + blue) / 3;
        Xcor.setText(Integer.toString(x));
        Ycor.setText(Integer.toString(y));
        Cocor.setText(String.valueOf(intensity));


}


}