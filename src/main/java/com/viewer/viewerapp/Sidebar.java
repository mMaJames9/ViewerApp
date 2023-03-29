package com.viewer.viewerapp;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;

import static com.viewer.viewerapp.ImageHandler.artboard2;
import static com.viewer.viewerapp.TextTool.gridPane;
import static com.viewer.viewerapp.TextTool.textGroup;

public class Sidebar extends VBox {


    static ImageView newview;

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
        Button AnalysisButton = createButton("analysis Tool", "SPOON");

        Button ColorizeButton = createButton("Color Tool", "TINT");
        Button Resizebutton = createButton("Resize Tool", "RECYCLE");
        Button pixelbutton = createButton("Pixel position Tool", "QUIDDITCH");
        Button textButton = createButton("Text  Tool", "TEXT");

        // Creating buttons actions
// Create select button
        selectButton.setOnAction(event -> {
            if (artboard2 != null) {
                if (artboard2.getChildren().size() > 4) {
                    artboard2.getChildren().remove(artboard2.getChildren().size() - 1);
                }
            }
        });
        // Create Zoom button

        // Create Crop button
        cropButton.setOnAction(event -> {
            if (artboard2 != null) {
                try {
                    Crop.crop(newview);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                artboard2.getChildren().add(Crop.cropButton);

            }
        });

        // Create flipH button
        flipHButton.setOnAction(e -> {
            if (artboard2 != null) {
                try {
                    FlipperFX.Horfrip(newview);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        // Create flipv button
        flipVButton.setOnAction(e -> {
            if (artboard2 != null) {
                try {
                    FlipperFX.VertFlip(newview);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        // Create rotate button
        rotateButton.setOnAction(event -> {
            if (artboard2 != null) {
                try {
                    RotationFX.rotateImage(newview);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Create text button
        textButton.setOnAction(event -> {
            if (artboard2 != null) {
                try {
                    TextTool.addTextTool(newview);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        // Create analysis button
        AnalysisButton.setOnAction(event -> {
            if (artboard2 != null) {
                try {
                    ImageAnalyzerGUI.Measure();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Create regionSelector button
        rSelectorButton.setOnAction(event -> {
            if (artboard2 != null) {
                try {
                    Segment.segments(newview);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                artboard2.getChildren().add(Segment.segmentButton);
                artboard2.setAlignment(Pos.TOP_LEFT);
            }
        });

        // Create color button
        ColorizeButton.setOnAction(event -> {
            if (artboard2 != null) {
                try {
                    ColorizeSegment.Colorize(newview);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                artboard2.getChildren().addAll(ColorizeSegment.segmentButton);

            }
        });

        // Create resize button
        Resizebutton.setOnAction(event -> {
            if (artboard2 != null) {
                ImageResizeGUI.resize(newview);
                FlowPane inputPane = new FlowPane();
                inputPane.getChildren().addAll(ImageResizeGUI.widthLabel, ImageResizeGUI.widthTextField, ImageResizeGUI.heightLabel, ImageResizeGUI.heightTextField,ImageResizeGUI.resizeButton);
                artboard2.getChildren().add(inputPane);
            }
        });

        // Create pixel point button
        pixelbutton.setOnAction(event -> {
            if (artboard2 != null) {
                try {
                    PixelPosition.pixelPosition(newview);
                    artboard2.getChildren().addAll(PixelPosition.line1, PixelPosition.line2);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Create buttons and add them to the sidebar
        getChildren().addAll(selectButton, zoomButton, cropButton,textButton, flipHButton, flipVButton, rotateButton, rSelectorButton,AnalysisButton,ColorizeButton,Resizebutton,pixelbutton);
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
