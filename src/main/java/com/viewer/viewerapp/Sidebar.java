package com.viewer.viewerapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class Sidebar extends VBox {

    private final Artboard artboard;

    public Sidebar(Artboard artboard) {
        super();
        this.artboard = artboard;
        setSpacing(10);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));
        getStyleClass().add("sidebar");

        Button selectButton = createButton("Select", FontAwesomeSolid.MOUSE_POINTER);
        Button zoomButton = createButton("Zoom Tool", FontAwesomeSolid.SEARCH);
        Button cropButton = createButton("Crop Tool", FontAwesomeSolid.CROP);
        Button flipHButton = createButton("Horizontal Flip Tool", FontAwesomeSolid.ARROWS_ALT_H);
        Button flipVButton = createButton("Vertical Flip Tool", FontAwesomeSolid.ARROWS_ALT_V);
        Button rotateButton = createButton("Rotate Tool", FontAwesomeSolid.SYNC_ALT);
        Button rSelectorButton = createButton("Region Selector Tool", FontAwesomeSolid.PENCIL_ALT);
        Button analysisButton = createButton("Analysis Tool", FontAwesomeSolid.CHART_LINE);
        Button resizeButton = createButton("Resize Tool", FontAwesomeSolid.EXPAND_ALT);
        Button pixelButton = createButton("Pixel position Tool", FontAwesomeSolid.CROSSHAIRS);
        Button textButton = createButton("Text Tool", FontAwesomeSolid.TEXT_WIDTH);

        // Create buttons and add them to the sidebar
        getChildren().addAll(selectButton, zoomButton, cropButton, flipHButton, flipVButton, rotateButton, rSelectorButton, analysisButton, resizeButton, pixelButton, textButton);

        setupButtonActions(selectButton, zoomButton, cropButton, flipHButton, flipVButton, rotateButton, rSelectorButton, analysisButton, resizeButton, pixelButton, textButton);
    }

    private Button createButton(String tooltipText, FontAwesomeSolid iconCode) {
        Button button = new Button();
        button.getStyleClass().add("button-icon");
        FontIcon icon = new FontIcon(iconCode);
        icon.getStyleClass().add("icon");
        icon.setIconSize(18);
        button.setGraphic(icon);
        button.setTooltip(new Tooltip(tooltipText));
        return button;
    }

    private void setupButtonActions(Button selectButton, Button zoomButton, Button cropButton, Button flipHButton, Button flipVButton, Button rotateButton, Button rSelectorButton, Button analysisButton, Button resizeButton, Button pixelButton, Button textButton) {
        flipHButton.setOnAction(event -> {
            if (artboard.getImageView() != null) {
                FlipperFX.flipImage(artboard, true);
            }
        });

        flipVButton.setOnAction(event -> {
            if (artboard.getImageView() != null) {
                FlipperFX.flipImage(artboard, false);
            }
        });

        cropButton.setOnAction(event -> {
            if (artboard.getImageView() != null) {
                Crop.crop(artboard);
            }
        });

        rotateButton.setOnAction(event -> {
            if (artboard.getImageView() != null) {
                RotationFX.rotate(artboard);
            }
        });

        pixelButton.setOnAction(event -> {
            if (artboard.getImageView() != null) {
                PixelPosition.showPixelPosition(artboard);
            }
        });

        rSelectorButton.setOnAction(event -> {
            if (artboard.getImageView() != null) {
                Segment.showSegmentTool(artboard);
            }
        });

        analysisButton.setOnAction(event -> {
            if (artboard.getImageView() != null) {
                ImageAnalyzerGUI imageAnalyzer = new ImageAnalyzerGUI(artboard);
                imageAnalyzer.show();
            }
        });
    }
}