package com.viewer.viewerapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GraduatedArrow extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        final Scene scene = new Scene(root, 300, 250);
        Stop[] stops = new Stop[] { new Stop(0, Color.GREEN), new Stop(0.2, Color.GREENYELLOW),
                new Stop(0.4, Color.YELLOW), new Stop(0.6, Color.ORANGE), new Stop(0.8, Color.ORANGERED),
                new Stop(1, Color.RED) };
        LinearGradient linear = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        Rectangle rect = new Rectangle(0, 0, 200, 30);
        rect.setFill(linear);
        rect.setRotate(90);
        rect.setArcWidth(30);
        rect.setArcHeight(30);
        rect.setOpacity(0.5);

        root.getChildren().add(rect);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Faded Progress Bar");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
