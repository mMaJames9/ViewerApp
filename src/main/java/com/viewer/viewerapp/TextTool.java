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

public class TextTool {

    static GridPane gridPane = new GridPane();
    static Group textGroup = new Group();
    private static TextField textField;
    private static Font font;
    private static Color textColor;
    private static Text selectedText = new Text();
    public static File file;
    public static Image image;
    private static double mouseX;
    private static double mouseY;


    public static void addTextTool(ImageView imageView) throws IOException {
        image = imageView.getImage();

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
                        selectedText = (Text) event2.getSource();
                        // Set the textField to the text of the clicked text object
                        textField.setText(selectedText.getText());
                        // Set the font and textColor variables to the font and text color of the clicked text object
                        font = selectedText.getFont();
                        textColor = (Color) selectedText.getFill();
                    }
                });

                // Add the new text object to the group
                textGroup.getChildren().add(newText);
                selectedText = newText;
                textField.setText("");
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
        selectedText.setOnMousePressed(event2 -> {
            // Get the mouse coordinates when the text object is clicked
            mouseX = event2.getSceneX();
            mouseY = event2.getSceneY();
        });
        selectedText.setOnMouseDragged(event2 -> {
            if (selectedText != null) {
                // Get the distance the mouse has moved since the text object was clicked
                double deltaX = event2.getSceneX() - mouseX;
                double deltaY = event2.getSceneY() - mouseY;

                // Move the selected text object by the distance the mouse has moved
                selectedText.setX(selectedText.getX() + deltaX);
                selectedText.setY(selectedText.getY() + deltaY);

                // Update the mouse coordinates
                mouseX = event2.getSceneX();
                mouseY = event2.getSceneY();
            }
        });

        // Add the controls to the grid pane
        gridPane.add(new Label("Text:"), 0, 0);
        gridPane.add(textField, 1, 0);
        gridPane.add(new Label("Font size:"), 0, 3);
        gridPane.add(createfontSizeSlider(), 1, 3);
        gridPane.add(new Label("Font:"), 0, 1);
        gridPane.add(createFontComboBox(), 1, 1);
        gridPane.add(new Label("Text Color:"), 0, 2);
        gridPane.add(createColorPicker(), 1, 2);

        // Create the stage and scene
        Stage textToolStage = new Stage();
        Scene textToolScene = new Scene(gridPane, 250, 100);

        // Set the stage properties
        textToolStage.setTitle("Text Tool");
        textToolStage.setScene(textToolScene);
        textToolStage.setX(imageView.getScene().getWindow().getX() + imageView.getLayoutX() + imageView.getBoundsInParent().getMinX());
        textToolStage.setY(imageView.getScene().getWindow().getY() + imageView.getLayoutY() + imageView.getBoundsInParent().getMinY() + imageView.getBoundsInParent().getHeight());

        // Show the stage
        textToolStage.show();
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
