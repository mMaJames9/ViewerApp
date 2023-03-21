package com.viewer.viewerapp;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class Navbar extends MenuBar {

    public Navbar(ImageHandler imageHandler) {

        Menu fileMenu = createMenu("File", "Open", "Save", "Exit");
        Menu editMenu = createMenu("Edit", "Undo", "Redo", "Cut", "Copy", "Paste", "Delete");
        this.getMenus().addAll(fileMenu, editMenu);

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
}
