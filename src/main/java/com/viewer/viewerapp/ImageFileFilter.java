package com.viewer.viewerapp;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImageFileFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String extension = getExtension(file);
        if (extension != null) {
            return switch (extension.toLowerCase()) {
                case "jpeg", "jpg", "tif", "tiff", "png", "xlsx" -> true;
                default -> false;
            };
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Allowed Files (JPEG, JPG, TIF, TIFF, PNG, XLSX)";
    }

    public String getExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
            return name.substring(lastDotIndex + 1).toLowerCase();
        }
        return null;
    }
}
