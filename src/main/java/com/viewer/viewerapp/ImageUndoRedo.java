package com.viewer.viewerapp;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Stack;

public class ImageUndoRedo {
    private ImageView imageView;
    private Stack<ImageCommand> undoStack = new Stack<>();
    private Stack<ImageCommand> redoStack = new Stack<>();

    public ImageUndoRedo(ImageView imageView) {
        this.imageView = imageView;
    }

    public void executeCommand(ImageCommand command) {
        undoStack.push(command);
        command.execute(imageView);
        redoStack.clear();
    }

    public static class State {
        private final Image image;
        private final double viewportWidth;
        private final double viewportHeight;
        private final double viewportMinX;
        private final double viewportMinY;

        public State(Image image, double viewportWidth, double viewportHeight, double viewportMinX, double viewportMinY) {
            this.image = image;
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
            this.viewportMinX = viewportMinX;
            this.viewportMinY = viewportMinY;
        }

        public Image getImage() {
            return image;
        }

        public double getViewportWidth() {
            return viewportWidth;
        }

        public double getViewportHeight() {
            return viewportHeight;
        }

        public double getViewportMinX() {
            return viewportMinX;
        }

        public double getViewportMinY() {
            return viewportMinY;
        }
    }

    public static class StateStack {
        private static ImageView imageView;
        private static final Stack<State> undoStack = new Stack<>();
        private static final Stack<State> redoStack = new Stack<>();

        public StateStack(ImageView imageView) {
            this.imageView = imageView;
            pushState();
        }
        public void pushState() {
            State state = new State(
                    imageView.getImage(),
                    imageView.getViewport().getWidth(),
                    imageView.getViewport().getHeight(),
                    imageView.getViewport().getMinX(),
                    imageView.getViewport().getMinY()
            );
            undoStack.push(state);
            redoStack.clear();
        }

        public static boolean canUndo() {
            return undoStack.size() > 1;
        }

        public static void undo() {
            if (canUndo()) {
                State state = undoStack.pop();
                redoStack.push(state);
                State prevState = undoStack.peek();
                restoreState(prevState);
            }
        }

        public static boolean canRedo() {
            return !redoStack.isEmpty();
        }

        public static void redo() {
            if (canRedo()) {
                State state = redoStack.pop();
                undoStack.push(state);
                restoreState(state);
            }
        }

        private static void restoreState(State state) {
            imageView.setImage(state.getImage());
            imageView.setViewport(new Rectangle2D(
                    state.getViewportMinX(),
                    state.getViewportMinY(),
                    state.getViewportWidth(),
                    state.getViewportHeight()
            ));
        }
    }
}
