package com.viewer.viewerapp;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

public class TextEditor {

    public static void editText(Artboard artboard) {
        ImageView imageView = artboard.getImageView();
        Stage textEditorStage = createTextEditorStage(imageView, artboard);
        textEditorStage.initModality(Modality.APPLICATION_MODAL);
        textEditorStage.showAndWait();
    }

    private static Stage createTextEditorStage(ImageView imageView, Artboard artboard) {
        Stage textEditorStage = new Stage(StageStyle.UTILITY);
        textEditorStage.setTitle("Text Editor");

        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10));

        // Create a new ImageView for the preview
        ImageView previewImageView = new ImageView(imageView.getImage());
        previewImageView.setPreserveRatio(true);
        previewImageView.setFitWidth(imageView.getFitWidth());
        previewImageView.setFitHeight(imageView.getFitHeight());
        Pane previewPane = new Pane(previewImageView);
        previewPane.setMaxWidth(previewImageView.getFitWidth());
        previewPane.setMinWidth(previewImageView.getFitWidth());
        previewPane.setMaxHeight(previewImageView.getFitHeight());
        previewPane.setMinHeight(previewImageView.getFitHeight());

        // Add text entry tools and previewImageView to container
        HBox textEntryTools = createTextEntryTools(previewImageView);
        textEntryTools.setMaxWidth(previewImageView.getFitWidth());
        textEntryTools.setMinWidth(previewImageView.getFitWidth());

        container.getChildren().addAll(textEntryTools, previewPane);

        // Add Validate button
        Button validateButton = new Button("Validate");
        validateButton.setOnAction(e -> {
            // Set the edited image with text on top to the artboard
            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setFill(Color.TRANSPARENT);
            Image snapshot = previewPane.snapshot(snapshotParameters, null);
            artboard.setImage(snapshot);
            textEditorStage.close();
        });
        container.getChildren().add(validateButton);

        Scene scene = new Scene(container);
        textEditorStage.setScene(scene);

        return textEditorStage;
    }

    private static HBox createTextEntryTools(ImageView previewImageView) {
        HBox textEntryTools = new HBox(10);
        textEntryTools.setAlignment(Pos.CENTER);

        // Add TextField for entering text
        TextField textField = new TextField();
        textField.setPromptText("Enter text");

        // Add ColorPicker for choosing text color
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);

        // Add ComboBox for choosing font family
        List<String> fontFamilies = Font.getFamilies();
        ComboBox<String> fontFamilyComboBox = new ComboBox<>(FXCollections.observableArrayList(fontFamilies));
        fontFamilyComboBox.getSelectionModel().select("System");

        // Add Slider for adjusting font size
        Slider fontSizeSlider = new Slider(10, 72, 24);

        // Add Text to previewImageView
        textField.setOnAction(e -> {
            String inputText = textField.getText();
            if (!inputText.isEmpty()) {
                addTextToPreview(previewImageView, inputText, colorPicker.getValue(), fontFamilyComboBox.getValue(), fontSizeSlider.getValue());
                textField.clear();
            }
        });

        textEntryTools.getChildren().addAll(textField, colorPicker, fontFamilyComboBox, fontSizeSlider);
        return textEntryTools;
    }

    private static void addTextToPreview(ImageView previewImageView, String inputText, Color color, String fontFamily, double fontSize) {
        Text text = new Text(inputText);
        text.setFont(Font.font(fontFamily, fontSize));
        text.setFill(color);
        text.setCursor(Cursor.HAND);

        Pane parent = (Pane) previewImageView.getParent();
        parent.getChildren().add(text);

        // Add event handlers for moving text
        text.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                text.setUserData(new double[]{event.getSceneX() - text.getLayoutX(), event.getSceneY() - text.getLayoutY()});
            }
        });

        text.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                double[] userData = (double[]) text.getUserData();
                double newX = event.getSceneX() - userData[0];
                double newY = event.getSceneY() - userData[1];

                // Constrain text movement within image area
                newX = Math.max(0, Math.min(newX, previewImageView.getFitWidth() - text.getBoundsInLocal().getWidth()));
                newY = Math.max(0, Math.min(newY, previewImageView.getFitHeight() - text.getBoundsInLocal().getHeight()));

                text.setLayoutX(newX);
                text.setLayoutY(newY);
            }
        });

        // Add event handlers for modifying text
        text.setOnMouseClicked(event -> {
            if (event.isSecondaryButtonDown()) {
                // Get the current values
                String currentFontFamily = text.getFont().getFamily();
                double currentFontSize = text.getFont().getSize();
                Color currentColor = (Color) text.getFill();

                // Create a context menu for modifying text
                ContextMenu contextMenu = new ContextMenu();

                // Create color picker for modifying text color
                ColorPicker colorPicker = new ColorPicker(currentColor);
                colorPicker.setOnAction(e -> {
                    text.setFill(colorPicker.getValue());
                    contextMenu.hide();
                });

                // Create ComboBox for choosing font family
                List<String> fontFamilies = Font.getFamilies();
                ComboBox<String> fontFamilyComboBox = new ComboBox<>(FXCollections.observableArrayList(fontFamilies));
                fontFamilyComboBox.getSelectionModel().select(currentFontFamily);
                fontFamilyComboBox.setOnAction(e -> {
                    text.setFont(Font.font(fontFamilyComboBox.getValue(), currentFontSize));
                    contextMenu.hide();
                });

                // Create Slider for adjusting font size
                Slider fontSizeSlider = new Slider(10, 72, currentFontSize);
                fontSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    text.setFont(Font.font(currentFontFamily, newValue.doubleValue()));
                    contextMenu.hide();
                });

                // Add the color picker, font family ComboBox, and font size Slider to the context menu
                contextMenu.getItems().addAll(new CustomMenuItem(colorPicker, false), new CustomMenuItem(fontFamilyComboBox, false), new CustomMenuItem(fontSizeSlider, false));

                // Show the context menu
                contextMenu.show(text, event.getScreenX(), event.getScreenY());
            }
        });

    }
}