package com.viewer.viewerapp;

import javafx.scene.image.ImageView;

public interface ImageCommand {
    void execute(ImageView imageView);
    void undo(ImageView imageView);
}

