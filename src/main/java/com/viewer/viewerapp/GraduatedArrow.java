package com.viewer.viewerapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class GraduatedArrow extends Application {
    private Polygon arrow1= new Polygon();
    private Rectangle rect;
    private Text text1;
    private TextField idfTextField;
    private Label idfLabel;
    private Polygon arrow2 = new Polygon();;

    private Text text2;

    private TextField edfTextField;

    private Label edfLabel;

    double percentage1;
    double percentage2;


    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();

        //setting a padding to the root
        root.setPadding(new Insets(25));

        //setting the size of the scene
        final Scene scene = new Scene(root);
        scene.setFill(Color.SKYBLUE);

        //adding the colors
        Stop[] stops = new Stop[]{new Stop(0, Color.GREEN), new Stop(0.2, Color.GREENYELLOW),
                new Stop(0.4, Color.YELLOW), new Stop(0.6, Color.ORANGE), new Stop(0.8, Color.ORANGERED),
                new Stop(1, Color.RED)};
        LinearGradient linear = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        // creating a progress bar of name "rect"
        rect = new Rectangle(0, 0, 200, 30);
        rect.setFill(linear);
        rect.setArcWidth(30);
        rect.setArcHeight(30);

        // creating an arrow
        arrow2.getPoints().addAll(new Double[]{
                5.0, 10.0,
                10.0, 0.0,
                0.0, 0.0});
        arrow2.setFill(Color.GREEN);

        arrow1.getPoints().addAll(new Double[]{
                5.0, 10.0,
                10.0, 0.0,
                0.0, 0.0});
        arrow1.setFill(Color.WHITE);

        // creating labels and text fields for IDF and EDF percentages
        idfLabel = new Label("IDF Percentage:");
        idfTextField = new TextField();

        edfLabel = new Label("EDF Percentage:");
        edfTextField = new TextField();

        // creating a button to execute the code and show the arrows
        Button executeButton = new Button("Execute");
        executeButton.setOnAction(event -> {
             percentage1 = Double.parseDouble(idfTextField.getText());
            // setting the x and y coordinate of the arrows
            double arrowHeight = arrow1.getBoundsInLocal().getHeight();

            double x1 = rect.getWidth() * percentage1 / 100.0 - arrowHeight / 2.0;
            double y1 = rect.getHeight() - percentage1 * rect.getHeight() / percentage1;
            arrow1.setLayoutX(x1);
            arrow1.setLayoutY(y1 - arrowHeight);

            percentage2 = Double.parseDouble(edfTextField.getText());

            double x2 = rect.getWidth() * percentage2 / 100.0 - arrowHeight / 2.0;
            double y2 = rect.getHeight() - percentage2 * rect.getHeight() / percentage2;
            arrow2.setLayoutX(x2);
            arrow2.setLayoutY(y2 - arrowHeight);

            // updating the percentage text on the arrows
            text1.setText(String.format("IDF : " +"%.1f%%", percentage1));
            text2.setText(String.format("EDF : " +"%.1f%%", percentage2));

            text1.setX(arrow1.getLayoutX() - 20);
            text1.setY(arrow1.getLayoutY() - 10);
            text2.setX(arrow2.getLayoutX() - 20);
            text2.setY(arrow2.getLayoutY() - 10);
        });

// creating a HBox to hold the IDF percentage label and text field
        VBox idfBox = new VBox(10, idfLabel, idfTextField);
        idfBox.setRotate(90);

        // creating a HBox to hold the EDF percentage label and text field
        VBox edfBox = new VBox(10, edfLabel, edfTextField);
        edfBox.setRotate(90);

        // setting the font for the percentage text
        Font font = new Font("Arial", 12);
        // creating a HBox to hold the execute button
        VBox executeBox = new VBox(executeButton);
        executeBox.setAlignment(Pos.CENTER);
        executeButton.setRotate(90);

        // creating the percentage text for arrow 1
        text1 = new Text(String.format("%.1f%%", percentage1));
        text1.setFont(font);
        text1.setRotate(90);
        text1.setFill(Color.WHITE);

        // creating the percentage text for arrow 2
        text2 = new Text(String.format("%.1f%%", percentage1));
        text2.setFont(font);
        text2.setRotate(90);
        text2.setFill(Color.WHITE);
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(edfBox, executeBox, idfBox);
        Pane pane = new Pane(rect, arrow1, text1,arrow2, text2);
        GridPane gridPane = new GridPane();


        GridPane.setRowIndex(pane, 0);
        GridPane.setColumnIndex(pane, 0);
        GridPane.setColumnSpan(pane, 2);
        GridPane.setRowIndex(hBox, 1);
        GridPane.setColumnIndex(hBox, 0);
        GridPane.setColumnSpan(hBox, 2);

        root.getChildren().addAll(pane,hBox);
        root.setAlignment(Pos.CENTER);
        root.setBackground(Background.fill(Color.SKYBLUE));
        root.setRotate(-90);

        // Add a listener to update the arrow's position when the progress changes
        primaryStage.setTitle("Graduated Arrow");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(250);
        primaryStage.setMaxWidth(250);
        primaryStage.setMinHeight(260);
        primaryStage.setMaxHeight(260);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

