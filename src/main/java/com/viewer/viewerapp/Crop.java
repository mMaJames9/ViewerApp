package com.viewer.viewerapp;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import javafx.util.converter.NumberStringConverter;

import java.util.ArrayList;
import java.util.List;

public class Crop {

    private static final double HANDLE_SIZE = 10;
    private static IntegerField xEndField;
    private static IntegerField yEndField;

    public static void crop(Artboard artboard) {
        ImageView imageView = artboard.getImageView();
        Stage cropStage = createCropStage(imageView, artboard);
        cropStage.initModality(Modality.APPLICATION_MODAL);
        cropStage.showAndWait();
    }

    private static Stage createCropStage(ImageView imageView, Artboard artboard) {
        Stage cropStage = new Stage(StageStyle.UTILITY);
        cropStage.setResizable(false);

        VBox container = new VBox();
        container.setPadding(new Insets(10));
        container.setAlignment(Pos.CENTER);
        container.getStyleClass().add("crop-modal");
        Pair<Pane, Rectangle> cropPaneAndRectangle = createCropPane(imageView, artboard, cropStage);
        Pane cropPane = cropPaneAndRectangle.getKey();
        Rectangle cropRectangle = cropPaneAndRectangle.getValue();
        List<Rectangle> handles = createHandleRectangles(cropRectangle);
        Pane inputPane = createInputPane(artboard, cropStage, imageView, cropRectangle, handles);
        container.getChildren().addAll(cropPane, inputPane);

        Scene scene = new Scene(container);
        cropStage.setScene(scene);

        return cropStage;
    }

    private static Pair<Pane, Rectangle> createCropPane(ImageView imageView, Artboard artboard, Stage cropStage) {
        Pane pane = new Pane();
        pane.setPrefSize(imageView.getFitWidth(), imageView.getFitHeight());

        ImageView originalImageView = createOriginalImageView(imageView);
        pane.getChildren().add(originalImageView);

        Rectangle cropRectangle = createCropRectangle(pane);
        pane.getChildren().add(cropRectangle);

        List<Rectangle> handleRectangles = createHandleRectangles(cropRectangle);
        pane.getChildren().addAll(handleRectangles);

        Rectangle topRectangle = createSemiTransparentRectangle();
        Rectangle bottomRectangle = createSemiTransparentRectangle();
        Rectangle leftRectangle = createSemiTransparentRectangle();
        Rectangle rightRectangle = createSemiTransparentRectangle();

        pane.getChildren().addAll(topRectangle, bottomRectangle, leftRectangle, rightRectangle);

        bindSemiTransparentRectangles(pane, cropRectangle, topRectangle, bottomRectangle, leftRectangle, rightRectangle);

        setCropRectangleEventHandlers(cropRectangle, handleRectangles, originalImageView);
        setHandleRectangleEventHandlers(handleRectangles, cropRectangle, originalImageView);
        setEnterKeyEventHandler(artboard, cropStage, originalImageView, cropRectangle);

        return new Pair<>(pane, cropRectangle);
    }

    private static Rectangle createSemiTransparentRectangle() {
        Rectangle semiTransparentRectangle = new Rectangle();
        semiTransparentRectangle.setFill(Color.BLACK);
        semiTransparentRectangle.setOpacity(0.5);
        return semiTransparentRectangle;
    }

    private static void bindSemiTransparentRectangles(Pane pane, Rectangle cropRectangle, Rectangle topRectangle, Rectangle bottomRectangle, Rectangle leftRectangle, Rectangle rightRectangle) {
        topRectangle.widthProperty().bind(pane.widthProperty());
        topRectangle.heightProperty().bind(cropRectangle.yProperty());
        topRectangle.xProperty().bind(pane.layoutXProperty());
        topRectangle.yProperty().bind(pane.layoutYProperty());

        bottomRectangle.widthProperty().bind(pane.widthProperty());
        bottomRectangle.heightProperty().bind(Bindings.subtract(pane.heightProperty(), Bindings.add(cropRectangle.yProperty(), cropRectangle.heightProperty())));
        bottomRectangle.xProperty().bind(pane.layoutXProperty());
        bottomRectangle.yProperty().bind(cropRectangle.yProperty().add(cropRectangle.heightProperty()));

        leftRectangle.widthProperty().bind(cropRectangle.xProperty());
        leftRectangle.heightProperty().bind(cropRectangle.heightProperty());
        leftRectangle.xProperty().bind(pane.layoutXProperty());
        leftRectangle.yProperty().bind(cropRectangle.yProperty());

        rightRectangle.widthProperty().bind(Bindings.subtract(pane.widthProperty(), Bindings.add(cropRectangle.xProperty(), cropRectangle.widthProperty())));
        rightRectangle.heightProperty().bind(cropRectangle.heightProperty());
        rightRectangle.xProperty().bind(cropRectangle.xProperty().add(cropRectangle.widthProperty()));
        rightRectangle.yProperty().bind(cropRectangle.yProperty());
    }

    private static ImageView createOriginalImageView(ImageView imageView) {
        ImageView originalImageView = new ImageView(imageView.getImage());
        originalImageView.setPreserveRatio(true);
        originalImageView.setFitWidth(imageView.getFitWidth());
        originalImageView.setFitHeight(imageView.getFitHeight());

        return originalImageView;
    }

    private static Rectangle createCropRectangle(Pane pane) {
        Rectangle cropRectangle = new Rectangle();
        cropRectangle.setFill(Color.TRANSPARENT);
        cropRectangle.setStroke(Color.WHITE);
        cropRectangle.setStrokeWidth(2);
        cropRectangle.setOpacity(1);
        cropRectangle.setWidth(pane.getPrefWidth());
        cropRectangle.setHeight(pane.getPrefHeight());

        return cropRectangle;
    }

    private static List<Rectangle> createHandleRectangles(Rectangle cropRectangle) {
        List<Rectangle> handleRectangles = new ArrayList<>();

        for (Corner corner : Corner.values()) {
            Rectangle handleRectangle = new Rectangle(HANDLE_SIZE, HANDLE_SIZE, Color.WHITE);
            handleRectangle.setStroke(Color.BLACK);
            handleRectangle.setStrokeWidth(1);

            if (corner.isCorner()) {
                CornerHandleEventHandler eventHandler = new CornerHandleEventHandler(handleRectangle, cropRectangle, corner, handleRectangles);
                handleRectangle.setOnMousePressed(eventHandler::onMousePressed);
                handleRectangle.setOnMouseDragged(eventHandler::onMouseDragged);
                handleRectangle.setOnMouseReleased(eventHandler::onMouseReleased);
            } else {
                SideHandleEventHandler eventHandler = new SideHandleEventHandler(handleRectangle, cropRectangle, corner, handleRectangles);
                handleRectangle.setOnMousePressed(eventHandler::onMousePressed);
                handleRectangle.setOnMouseDragged(eventHandler::onMouseDragged);
                handleRectangle.setOnMouseReleased(eventHandler::onMouseReleased);
            }

            handleRectangle.setOnMouseEntered(event -> {
                switch (corner) {
                    case TOP_LEFT, BOTTOM_RIGHT -> handleRectangle.setCursor(Cursor.NW_RESIZE);
                    case TOP_RIGHT, BOTTOM_LEFT -> handleRectangle.setCursor(Cursor.NE_RESIZE);
                    case TOP, BOTTOM -> handleRectangle.setCursor(Cursor.N_RESIZE);
                    case LEFT, RIGHT -> handleRectangle.setCursor(Cursor.W_RESIZE);
                }
            });

            handleRectangle.setOnMouseExited(event -> handleRectangle.setCursor(Cursor.DEFAULT));

            handleRectangles.add(handleRectangle);
        }

        return handleRectangles;
    }

    private static void setCropRectangleEventHandlers(Rectangle cropRectangle, List<Rectangle> handleRectangles, ImageView imageView) {
        CropRectangleEventHandler eventHandler = new CropRectangleEventHandler(cropRectangle, handleRectangles, imageView);
        cropRectangle.setOnMousePressed(eventHandler::onMousePressed);
        cropRectangle.setOnMouseDragged(eventHandler::onMouseDragged);
        cropRectangle.setOnMouseReleased(eventHandler::onMouseReleased);
    }

    private static void setHandleRectangleEventHandlers(List<Rectangle> handleRectangles, Rectangle cropRectangle, ImageView originalImageView) {
        for (int i = 0; i < handleRectangles.size(); i++) {
            HandleRectangleEventHandler eventHandler = new HandleRectangleEventHandler(handleRectangles.get(i), cropRectangle, handleRectangles, originalImageView);
            handleRectangles.get(i).setOnMousePressed(eventHandler::onMousePressed);
            handleRectangles.get(i).setOnMouseDragged(eventHandler::onMouseDragged);
            handleRectangles.get(i).setOnMouseReleased(eventHandler::onMouseReleased);
        }
    }

    private static void setEnterKeyEventHandler(Artboard artboard, Stage cropStage, ImageView originalImageView, Rectangle cropRectangle) {
        EventHandler<KeyEvent> enterKeyEventHandler = new EventHandler<>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    // Get the crop rectangle's position and dimensions
                    double x = cropRectangle.getX();
                    double y = cropRectangle.getY();
                    double width = cropRectangle.getWidth();
                    double height = cropRectangle.getHeight();

                    // Call the updated cropImage method with the new arguments
                    Image croppedImage = cropImage(originalImageView, x, y, width, height);

                    // Update the originalImageView and the artboard with the cropped image
                    originalImageView.setImage(croppedImage);
                    artboard.setImage(croppedImage);

                    // Close the crop stage and remove the event handler
                    cropStage.close();
                    cropStage.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                }
            }
        };

        cropStage.addEventHandler(KeyEvent.KEY_PRESSED, enterKeyEventHandler);
    }

    private static void updateHandleRectangles(List<Rectangle> handleRectangles, Rectangle cropRectangle) {
        for (int i = 0; i < handleRectangles.size(); i++) {
            Rectangle handleRectangle = handleRectangles.get(i);
            double x = 0;
            double y = 0;

            switch (i) {
                case 0 -> {
                    x = cropRectangle.getX() - HANDLE_SIZE / 2;
                    y = cropRectangle.getY() - HANDLE_SIZE / 2;
                }
                case 1 -> {
                    x = cropRectangle.getX() + cropRectangle.getWidth() / 2 - HANDLE_SIZE / 2;
                    y = cropRectangle.getY() - HANDLE_SIZE / 2;
                }
                case 2 -> {
                    x = cropRectangle.getX() + cropRectangle.getWidth() - HANDLE_SIZE / 2;
                    y = cropRectangle.getY() - HANDLE_SIZE / 2;
                }
                case 3 -> {
                    x = cropRectangle.getX() + cropRectangle.getWidth() - HANDLE_SIZE / 2;
                    y = cropRectangle.getY() + cropRectangle.getHeight() / 2 - HANDLE_SIZE / 2;
                }
                case 4 -> {
                    x = cropRectangle.getX() + cropRectangle.getWidth() - HANDLE_SIZE / 2;
                    y = cropRectangle.getY() + cropRectangle.getHeight() - HANDLE_SIZE / 2;
                }
                case 5 -> {
                    x = cropRectangle.getX() + cropRectangle.getWidth() / 2 - HANDLE_SIZE / 2;
                    y = cropRectangle.getY() + cropRectangle.getHeight() - HANDLE_SIZE / 2;
                }
                case 6 -> {
                    x = cropRectangle.getX() - HANDLE_SIZE / 2;
                    y = cropRectangle.getY() + cropRectangle.getHeight() - HANDLE_SIZE / 2;
                }
                case 7 -> {
                    x = cropRectangle.getX() - HANDLE_SIZE / 2;
                    y = cropRectangle.getY() + cropRectangle.getHeight() / 2 - HANDLE_SIZE / 2;
                }
            }

            handleRectangle.setX(x);
            handleRectangle.setY(y);
        }
    }

    public static Image cropImage(ImageView imageView, double x, double y, double width, double height) {
        Image image = imageView.getImage();

        // Get the scale factors for the ImageView
        double scaleX = image.getWidth() / imageView.getBoundsInLocal().getWidth();
        double scaleY = image.getHeight() / imageView.getBoundsInLocal().getHeight();

        // Convert the crop rectangle's coordinates to the original image's coordinates
        x = Math.max(0, x * scaleX);
        y = Math.max(0, y * scaleY);
        width = Math.min(image.getWidth() - x, width * scaleX);
        height = Math.min(image.getHeight() - y, height * scaleY);

        // Create a writable image with the original dimensions
        WritableImage croppedImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());

        // Copy the pixels from the original image to the cropped image
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = croppedImage.getPixelWriter();

        for (int readY = 0; readY < image.getHeight(); readY++) {
            for (int readX = 0; readX < image.getWidth(); readX++) {
                if (readX >= x && readX < x + width && readY >= y && readY < y + height) {
                    // Copy the pixel if it is within the crop rectangle
                    Color color = pixelReader.getColor(readX, readY);
                    pixelWriter.setColor(readX, readY, color);
                } else {
                    // Set the pixel to transparent if it is outside the crop rectangle
                    pixelWriter.setColor(readX, readY, Color.TRANSPARENT);
                }
            }
        }

        return croppedImage;
    }

    private static void updateCropRectangle(Rectangle cropRectangle, Rectangle handleRectangle, int handleIndex) {
        double newX = cropRectangle.getX();
        double newY = cropRectangle.getY();
        double newWidth = cropRectangle.getWidth();
        double newHeight = cropRectangle.getHeight();

        switch (handleIndex) {
            case 0 -> {
                newWidth += newX - handleRectangle.getX();
                newHeight += newY - handleRectangle.getY();
                newX = handleRectangle.getX();
                newY = handleRectangle.getY();
            }
            case 1 -> {
                newHeight += newY - handleRectangle.getY();
                newY = handleRectangle.getY();
            }
            case 2 -> {
                newWidth = handleRectangle.getX() - newX + HANDLE_SIZE / 2;
                newHeight += newY - handleRectangle.getY();
                newY = handleRectangle.getY();
            }
            case 3 -> newWidth = handleRectangle.getX() - newX + HANDLE_SIZE / 2;
            case 4 -> {
                newWidth = handleRectangle.getX() - newX + HANDLE_SIZE / 2;
                newHeight = handleRectangle.getY() - newY + HANDLE_SIZE / 2;
            }
            case 5 -> newHeight = handleRectangle.getY() - newY + HANDLE_SIZE / 2;
            case 6 -> {
                newWidth += newX - handleRectangle.getX();
                newHeight = handleRectangle.getY() - newY + HANDLE_SIZE / 2;
                newX = handleRectangle.getX();
            }
            case 7 -> {
                newWidth += newX - handleRectangle.getX();
                newX = handleRectangle.getX();
            }
        }

        cropRectangle.setX(newX);
        cropRectangle.setY(newY);
        cropRectangle.setWidth(newWidth);
        cropRectangle.setHeight(newHeight);
    }

    private static Pane createInputPane(Artboard artboard, Stage cropStage, ImageView originalImageView, Rectangle cropRectangle, List<Rectangle> handles) {
        VBox inputPane = new VBox();
        inputPane.setPadding(new Insets(10));
        inputPane.setSpacing(20);

        HBox axesPanes = new HBox(5);
        HBox.setHgrow(axesPanes, Priority.ALWAYS); // Make the axesPanes responsive

        HBox xAxisFields = new HBox(5);
        IntegerField xStartField = new IntegerField();
        xStartField.setPromptText("X-start");
        xStartField.setEditable(false);
        xEndField = new IntegerField();
        xEndField.setPromptText("X-end");
        xEndField.setEditable(false);
        xAxisFields.getChildren().addAll(xStartField, xEndField);
        TitledPane xAxisPane = new TitledPane("X axis", xAxisFields);

        HBox yAxisFields = new HBox(5);
        IntegerField yStartField = new IntegerField();
        yStartField.setPromptText("Y-start");
        yStartField.setEditable(false);
        yEndField = new IntegerField();
        yEndField.setPromptText("Y-end");
        yEndField.setEditable(false);
        yAxisFields.getChildren().addAll(yStartField, yEndField);
        TitledPane yAxisPane = new TitledPane("Y axis", yAxisFields);

        xStartField.textProperty().bindBidirectional(cropRectangle.xProperty(), new NumberStringConverter());
        yStartField.textProperty().bindBidirectional(cropRectangle.yProperty(), new NumberStringConverter());

        xEndField.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString((int) (cropRectangle.getX() + cropRectangle.getWidth())), cropRectangle.xProperty(), cropRectangle.widthProperty()));

        yEndField.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString((int) (cropRectangle.getY() + cropRectangle.getHeight())), cropRectangle.yProperty(), cropRectangle.heightProperty()));

        xStartField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                updateHandles(handles, cropRectangle);
            }
        });

        xEndField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                double xEnd = Double.parseDouble(newValue);
                double width = xEnd - cropRectangle.getX();
                cropRectangle.setWidth(width);
                updateHandles(handles, cropRectangle);
            }
        });

        yStartField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                updateHandles(handles, cropRectangle);
            }
        });

        yEndField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                double yEnd = Double.parseDouble(newValue);
                double height = yEnd - cropRectangle.getY();
                cropRectangle.setHeight(height);
                updateHandles(handles, cropRectangle);
            }
        });

        axesPanes.getChildren().addAll(xAxisPane, yAxisPane);
        inputPane.getChildren().add(axesPanes);

        Button validateButton = new Button("Validate");
        validateButton.setOnAction(e -> {
            // Get the crop rectangle's position and dimensions
            double x = cropRectangle.getX();
            double y = cropRectangle.getY();
            double width = Double.parseDouble(xEndField.getText()) - x;
            double height = Double.parseDouble(yEndField.getText()) - y;

            // Call the updated cropImage method with the new arguments
            Image croppedImage = cropImage(originalImageView, x, y, width, height);

            // Update the originalImageView and the artboard with the cropped image
            originalImageView.setImage(croppedImage);
            artboard.setImage(croppedImage);

            // Close the crop stage and remove the event handler
            cropStage.close();
        });

        inputPane.getChildren().add(validateButton);

        return inputPane;
    }

    private static void updateHandles(List<Rectangle> handles, Rectangle cropRectangle) {
        double x = cropRectangle.getX();
        double y = cropRectangle.getY();
        double width = cropRectangle.getWidth();
        double height = cropRectangle.getHeight();

        handles.get(0).setX(x - HANDLE_SIZE / 2.0);
        handles.get(0).setY(y - HANDLE_SIZE / 2.0);

        handles.get(1).setX(x + width - HANDLE_SIZE / 2.0);
        handles.get(1).setY(y - HANDLE_SIZE / 2.0);

        handles.get(2).setX(x - HANDLE_SIZE / 2.0);
        handles.get(2).setY(y + height - HANDLE_SIZE / 2.0);

        handles.get(3).setX(x + width - HANDLE_SIZE / 2.0);
        handles.get(3).setY(y + height - HANDLE_SIZE / 2.0);
    }

    public enum Corner {
        TOP_LEFT(), TOP(), TOP_RIGHT(), RIGHT(), BOTTOM_RIGHT(), BOTTOM(), BOTTOM_LEFT(), LEFT();

        Corner() {
        }

        public boolean isCorner() {
            return this == TOP_LEFT || this == TOP_RIGHT || this == BOTTOM_LEFT || this == BOTTOM_RIGHT;
        }
    }

    private static class CornerHandleEventHandler extends HandleEventHandler {
        public CornerHandleEventHandler(Rectangle handleRectangle, Rectangle cropRectangle, Corner corner, List<Rectangle> handleRectangles) {
            super(handleRectangle, cropRectangle, corner, handleRectangles);
        }

        @Override
        protected void updateCropRectangle(double newX, double newY) {
            switch (corner) {
                case TOP_LEFT -> {
                    cropRectangle.setWidth(cropRectangle.getWidth() + (cropRectangle.getX() - newX));
                    cropRectangle.setHeight(cropRectangle.getHeight() + (cropRectangle.getY() - newY));
                    cropRectangle.setX(newX);
                    cropRectangle.setY(newY);
                }
                case TOP_RIGHT -> {
                    cropRectangle.setWidth(newX - cropRectangle.getX());
                    cropRectangle.setHeight(cropRectangle.getHeight() + (cropRectangle.getY() - newY));
                    cropRectangle.setY(newY);
                }
                case BOTTOM_LEFT -> {
                    cropRectangle.setWidth(cropRectangle.getWidth() + (cropRectangle.getX() - newX));
                    cropRectangle.setHeight(newY - cropRectangle.getY());
                    cropRectangle.setX(newX);
                }
                case BOTTOM_RIGHT -> {
                    cropRectangle.setWidth(newX - cropRectangle.getX());
                    cropRectangle.setHeight(newY - cropRectangle.getY());
                }
            }
        }
    }

    private static class SideHandleEventHandler extends HandleEventHandler {
        public SideHandleEventHandler(Rectangle handleRectangle, Rectangle cropRectangle, Corner corner, List<Rectangle> handleRectangles) {
            super(handleRectangle, cropRectangle, corner, handleRectangles);
        }

        @Override
        protected void updateCropRectangle(double newX, double newY) {
            switch (corner) {
                case TOP -> {
                    cropRectangle.setHeight(cropRectangle.getHeight() + (cropRectangle.getY() - newY));
                    cropRectangle.setY(newY);
                }
                case BOTTOM -> cropRectangle.setHeight(newY - cropRectangle.getY());
                case LEFT -> {
                    cropRectangle.setWidth(cropRectangle.getWidth() + (cropRectangle.getX() - newX));
                    cropRectangle.setX(newX);
                }
                case RIGHT -> cropRectangle.setWidth(newX - cropRectangle.getX());
            }
        }
    }

    private static class CropRectangleEventHandler {
        private final Rectangle cropRectangle;
        private final List<Rectangle> handleRectangles;
        private final ImageView imageView;
        private double offsetX;
        private double offsetY;

        public CropRectangleEventHandler(Rectangle cropRectangle, List<Rectangle> handleRectangles, ImageView imageView) {
            this.cropRectangle = cropRectangle;
            this.handleRectangles = handleRectangles;
            this.imageView = imageView;
        }

        public void onMousePressed(MouseEvent event) {
            offsetX = event.getX() - cropRectangle.getX();
            offsetY = event.getY() - cropRectangle.getY();
        }

        public void onMouseDragged(MouseEvent event) {
            double newX = event.getX() - offsetX;
            double newY = event.getY() - offsetY;

            // Ensure the crop rectangle stays within the image bounds
            newX = Math.max(0, Math.min(newX, imageView.getFitWidth() - cropRectangle.getWidth()));
            newY = Math.max(0, Math.min(newY, imageView.getFitHeight() - cropRectangle.getHeight()));

            cropRectangle.setX(newX);
            cropRectangle.setY(newY);

            updateHandleRectangles(handleRectangles, cropRectangle);
        }

        public void onMouseReleased(MouseEvent event) {
            // Do nothing
        }
    }

    private record HandleRectangleEventHandler(Rectangle handleRectangle, Rectangle cropRectangle,
                                               List<Rectangle> handleRectangles, ImageView imageView) {

        public void onMousePressed(MouseEvent event) {
            // Do nothing
        }

        public void onMouseDragged(MouseEvent event) {
            double newX = event.getSceneX() - HANDLE_SIZE / 2;
            double newY = event.getSceneY() - HANDLE_SIZE / 2;

            // Check if the handle and crop rectangle are within the image bounds
            if (newX >= 0 && newX <= imageView.getFitWidth() - HANDLE_SIZE && newY >= 0 && newY <= imageView.getFitHeight() - HANDLE_SIZE) {
                handleRectangle.setX(newX);
                handleRectangle.setY(newY);

                updateCropRectangle(cropRectangle, handleRectangle, handleRectangles.indexOf(handleRectangle));
                updateHandleRectangles(handleRectangles, cropRectangle);
            }
        }

        public void onMouseReleased(MouseEvent event) {
            // Do nothing
        }
    }

    private static abstract class HandleEventHandler {
        protected final Rectangle handleRectangle;
        protected final Rectangle cropRectangle;
        protected final Corner corner;
        protected final List<Rectangle> handleRectangles;
        protected double offsetX;
        protected double offsetY;

        public HandleEventHandler(Rectangle handleRectangle, Rectangle cropRectangle, Corner corner, List<Rectangle> handleRectangles) {
            this.handleRectangle = handleRectangle;
            this.cropRectangle = cropRectangle;
            this.corner = corner;
            this.handleRectangles = handleRectangles;
        }

        public void onMousePressed(MouseEvent event) {
            offsetX = event.getX() - handleRectangle.getX();
            offsetY = event.getY() - handleRectangle.getY();
        }

        public void onMouseDragged(MouseEvent event) {
            double newX = event.getX() - offsetX;
            double newY = event.getY() - offsetY;
            updateCropRectangle(newX, newY);
            updateHandleRectangles(handleRectangles, cropRectangle);
        }

        public void onMouseReleased(MouseEvent event) {
            // Do nothing
        }

        protected abstract void updateCropRectangle(double newX, double newY);
    }

    public static class IntegerField extends TextField {

        @Override
        public void replaceText(int start, int end, String text) {
            if (text.matches("[0-9]*")) {
                super.replaceText(start, end, text);
            }
        }

        @Override
        public void replaceSelection(String text) {
            if (text.matches("[0-9]*")) {
                super.replaceSelection(text);
            }
        }
    }

}