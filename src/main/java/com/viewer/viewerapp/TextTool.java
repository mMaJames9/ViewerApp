package com.viewer.viewerapp;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static com.viewer.viewerapp.Sidebar.newview;
import static javafx.scene.layout.Region.USE_PREF_SIZE;


public class TextTool {

    static GridPane gridPane = new GridPane();
    static Group textGroup = new Group();
    private static TextField textField;
    private static Font font;
    static ImageView imageView;
    static Image image ;

    private static Color textColor;
    private static Text selectedText = new Text();
    public static File file;

    private static double mouseX;
    private static double mouseY;


    public static void addTextTool(ImageView imView) throws IOException {

        Stage primaryStage =new Stage();

        image =imView.getImage();
        imageView = new ImageView(image);

        // Create the group for the text objects
        textGroup = new Group();

        // Add event handler for the image view
       imageView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) { // Only handle left click
                // Get the position of the mouse click relative to the image view
                double x = event.getX();
                double y = event.getY();

                // Create a new Text object with the entered text
                Text newText = new Text(textField.getText());
                newText.setFont(font);
                newText.setFill(textColor);

                // Set the position of the new text object
                newText.setX(x);
                newText.setY(y);

                // Add an event handler to the new text object
                newText.setOnMouseClicked(event2 -> {
                    if (event2.getButton() == MouseButton.PRIMARY) { // Only handle left click
                        // Set the selectedText variable to the clicked text object
                        selectedText = newText;
                        // Set the textField to the text of the clicked text object
                        textField.setText(selectedText.getText());
                        // Set the font and textColor variables to the font and text color of the clicked text object
                        font = selectedText.getFont();
                        textColor = (Color) selectedText.getFill();
                    }
                });

                // Add the new text object to the group
                textGroup.getChildren().add(newText);
            }
        });

        // Create the controls for the text tool
        textField = new TextField("Enter text here");
        textField.setOnAction(event -> {
            if (selectedText != null) {
                // Update the selected text object's text property
                selectedText.setText(textField.getText());
                selectedText.setFont(font);
                selectedText.setFill(textColor);
            }
        });

        // Add event handlers for moving the selected text object
        textGroup.setOnMousePressed(event -> {
            if (selectedText != null && event.getButton() == MouseButton.PRIMARY) {
                mouseX = event.getX();
                mouseY = event.getY();
            }
        });

        textGroup.setOnMouseDragged(event -> {
            if (selectedText != null && event.getButton() == MouseButton.PRIMARY) {
                double deltaX = event.getX() - mouseX;
                double deltaY = event.getY() - mouseY;
                selectedText.setX(selectedText.getX() + deltaX);
                selectedText.setY(selectedText.getY() + deltaY);
                mouseX = event.getX();
                mouseY = event.getY();
            }
        });

        // Create the grid pane to hold the controls
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        // Add the image view and the text group to the grid
        gridPane.add(imageView, 0, 0);
        gridPane.add(textGroup, 0, 0);

        // Create the controls pane
        GridPane controlsPane = new GridPane();
        controlsPane.setHgap(10);
        controlsPane.setVgap(10);
        controlsPane.setAlignment(Pos.CENTER);

        // Add the controls to the controls pane
        controlsPane.add(new Label("Text:"), 0, 0);
        controlsPane.add(textField, 1, 0);
        controlsPane.add(new Label("Font size:"), 0, 1);
        controlsPane.add(createfontSizeSlider(), 1, 1);
        controlsPane.add(new Label("Font:"), 0, 2);
        controlsPane.add(createFontComboBox(), 1, 2);
        controlsPane.add(new Label("Text color:"), 0, 3);
        controlsPane.add(createColorPicker(), 1, 3);

        // Create the controls scene
        Scene controlsScene = new Scene(controlsPane);

        // Create the controls stage
        Stage controlsStage = new Stage();
        controlsStage.setTitle("Text Tool Controls");
        controlsStage.setScene(controlsScene);


        Group rootGroup = new Group(imageView, textGroup, gridPane);

        // Create the scene
        Scene scene = new Scene(rootGroup);
// Show the controls stage
        controlsStage.show();
        // Set the stage properties
        primaryStage.setTitle("Text Tool");
        Image icon = new Image("D:\\zeze work\\work\\Viewer Application 2\\Viewer Application\\src\\main\\java\\com\\viewer\\viewerapp\\ZZ_Viewer Logo.jpg");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static Slider createfontSizeSlider() {
        Slider fontSizeSlider = new Slider(0, 100, 12);
        fontSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            font = Font.font(font.getName(), newValue.doubleValue());
            if (selectedText != null) {
                selectedText.setFont(font);
            }
        });
        return fontSizeSlider;
    }

    private static ComboBox<String> createFontComboBox() {
        ComboBox<String> fontComboBox = new ComboBox<>();
        fontComboBox.getItems().addAll(Font.getFontNames());
        fontComboBox.setOnAction(event -> {
            Font selectedFont = Font.font(fontComboBox.getSelectionModel().getSelectedItem());
            font = Font.font(selectedFont.getName(), createfontSizeSlider().getValue());
        });
        return fontComboBox;
    }

    private static ColorPicker createColorPicker() {
        // Create a color picker with the initial color set to black
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setOnAction(event -> {
            if (selectedText != null) {
                // Update the text color of the selected text object
                textColor = colorPicker.getValue();
                selectedText.setFill(textColor);
            }
        });
        return colorPicker;
    }
}
