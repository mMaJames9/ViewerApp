package com.viewer.viewerapp;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

import javax.swing.undo.UndoManager;
import java.util.Stack;


public class Navbar extends MenuBar {

    private UndoManager undoManager = new UndoManager();

    public Navbar(ImageHandler imageHandler) {

        Menu fileMenu = createMenu("File", "Open", "Save", "Exit");
        Menu editMenu = createMenu("Edit",  "Redo", "Cut", "Copy", "Paste", "Delete");
        MenuItem undoItem = new MenuItem("Undo");
        editMenu.getItems().add(undoItem);
        this.getMenus().addAll(fileMenu, editMenu);


        undoItem.setOnAction(event -> {
            if (!ImageHandler.undoStack.isEmpty()) {
                Image previousImage =ImageHandler.undoStack.pop();
                ImageHandler.redoStack.push(Sidebar.newview.getImage());
                Sidebar.newview.setImage(previousImage);
            }
        });

        fileMenu.getItems().get(0).setOnAction(event -> imageHandler.choosePicture());
        fileMenu.getItems().get(1).setOnAction(event -> imageHandler.saveFile());
        fileMenu.getItems().get(2).setOnAction(event -> System.exit(0));
    }

    private Menu createMenu(String menuName, String... itemNames) {
        Menu menu = new Menu(menuName);
        for (String itemName : itemNames) {
            MenuItem item = new MenuItem(itemName);
            menu.getItems().add(item);
        }
        return menu;
    }

    private void undo() {
        if (ImageHandler.undoStack.size() > 1) {
            ImageHandler.redoStack.push(ImageHandler.undoStack.pop());
            Image previousImage = ImageHandler.undoStack.peek();
            Sidebar.newview.setImage(previousImage);
            undoManager.undo();
        }
    }

    private void redo() {
        if (!ImageHandler.redoStack.isEmpty()) {
            Image nextImage = ImageHandler.redoStack.pop();
            ImageHandler.undoStack.push(nextImage);
            Sidebar.newview.setImage(nextImage);
            undoManager.redo();
        }
    }
}
