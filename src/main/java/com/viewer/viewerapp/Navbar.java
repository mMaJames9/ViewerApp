package com.viewer.viewerapp;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import static com.viewer.viewerapp.ImageHandler.artboard2;

public class Navbar extends MenuBar {

    public Navbar(ImageHandler imageHandler) {

        Menu fileMenu = createMenu("File", "Open", "Save", "Exit");
        Menu editMenu = createMenu("Edit", "Undo", "Redo", "Cut", "Copy", "Paste", "Delete");
        Menu viewMenu = createMenu("View", "Ruler", "Grid");
        this.getMenus().addAll(fileMenu, editMenu, viewMenu);

        fileMenu.getItems().get(0).setOnAction(event -> imageHandler.choosePicture());

        fileMenu.getItems().get(1).setOnAction(event -> {
            if (artboard2.getImageView() != null) {
                imageHandler.saveFile();
            }
        });

        fileMenu.getItems().get(2).setOnAction(event -> System.exit(0));

        CheckMenuItem rulerItem = (CheckMenuItem) viewMenu.getItems().get(0);
        rulerItem.setSelected(true);
        rulerItem.setOnAction(event -> {
            if (rulerItem.isSelected()) {
                artboard2.addRulers();
            } else {
                artboard2.removeRulers();
            }
        });

        CheckMenuItem gridItem = (CheckMenuItem) viewMenu.getItems().get(1);
        gridItem.setSelected(true);
        gridItem.setOnAction(event -> {
            if (gridItem.isSelected()) {
                artboard2.addGrid();
            } else {
                artboard2.removeGrid();
            }
        });
    }

    private Menu createMenu(String menuName, String... itemNames) {
        Menu menu = new Menu(menuName);
        for (String itemName : itemNames) {
            MenuItem item;
            if (itemName.equals("Ruler") || itemName.equals("Grid")) {
                item = new CheckMenuItem(itemName);
            } else {
                item = new MenuItem(itemName);
            }
            menu.getItems().add(item);
        }
        return menu;
    }
}