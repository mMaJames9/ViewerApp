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
        BorderPane root = setupRootLayout();
        Scene scene = setupScene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Image Viewer");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private BorderPane setupRootLayout() {
        HBox artboards = createArtboards();
        Artboard artboard1 = (Artboard) artboards.getChildren().get(0);
        Artboard artboard2 = (Artboard) artboards.getChildren().get(1);

        Navbar menuBar = createNavbar(artboard1, artboard2);
        Sidebar sidebar = createSidebar(artboard2);
        StackPane textPane = createTextPane();
        StackPane content = createContent(artboards);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setTop(menuBar);
        root.setLeft(sidebar);
        root.setCenter(content);
        root.setBottom(textPane);

        return root;
    }

    private Navbar createNavbar(Artboard artboard1, Artboard artboard2) {
        return new Navbar(artboard1, artboard2);
    }

    private Sidebar createSidebar(Artboard artboard2) {
        return new Sidebar(artboard2);
    }

    private HBox createArtboards() {
        HBox artboards = new HBox();
        artboards.getStyleClass().add("artboards");

        Artboard artboard1 = new Artboard();
        Artboard artboard2 = new Artboard();

        artboards.getChildren().addAll(artboard1, artboard2);

        return artboards;
    }

    private StackPane createTextPane() {
        StackPane textPane = new StackPane();

        Label paneInfo = new Label("Text information");
        textPane.getStyleClass().add("text-pane");
        textPane.getChildren().add(paneInfo);

        return textPane;
    }

    private StackPane createContent(HBox artboards) {
        StackPane content = new StackPane();
        content.getStyleClass().add("stack-pane");
        content.getChildren().add(artboards);

        return content;
    }

    private Scene setupScene(BorderPane root) {
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().addAll(Objects.requireNonNull(getClass().getResource("/com/viewer/viewerapp/css/style.css")).toExternalForm());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        return scene;
    }
}