package com.viewer.viewerapp;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;

import static com.viewer.viewerapp.ImageHandler.artboard2;

public class Sidebar extends VBox {
    static ImageView newView;

    public Sidebar() {
        super();
        setSpacing(10);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));
        getStyleClass().add("sidebar");

        // Load the FontAwesome.tff font file
        Font.loadFont(getClass().getResourceAsStream("/fonts/FontAwesome.ttf"), 14);

        Button selectButton = createButton("Select", "MOUSE_POINTER");
        Button zoomButton = createButton("Zoom Tool", "SEARCH");
        Button cropButton = createButton("Crop Tool", "CROP");
        Button flipHButton = createButton("Horizontal Flip Tool", "ARROWS_H");
        Button flipVButton = createButton("Vertical Flip Tool", "ARROWS_V");
        Button rotateButton = createButton("Rotate Tool", "REFRESH");
        Button rSelectorButton = createButton("Region Selector Tool", "PENCIL");

        // Create buttons and add them to the sidebar
        getChildren().addAll(selectButton, zoomButton, cropButton, flipHButton, flipVButton, rotateButton, rSelectorButton);

        selectButton.setOnAction(event -> {
            if (artboard2.getImageView() != null) {
                if (artboard2.getChildren().size() > 0) {
                    artboard2.getChildren().remove(artboard2.getChildren().size() - 1);
                }
            }
        });

        flipHButton.setOnAction(event -> {
            if (artboard2.getImageView() != null) {
                try {
                    FlipperFX.Horfrip(newView);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        flipVButton.setOnAction(event -> {
            if (artboard2.getImageView() != null) {
                try {
                    FlipperFX.VertFlip(newView);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        cropButton.setOnAction(event -> {
            if (artboard2.getImageView() != null) {
                Crop.crop(artboard2);
            }
        });
    }

    private Button createButton(String tooltipText, String glyphName) {
        Button button = new Button();
        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setGlyphName(glyphName);
        icon.setSize("1.2em");
        button.setGraphic(icon);
        button.setTooltip(new Tooltip(tooltipText));
        return button;
    }
}
