package com.viewer.viewerapp;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import static com.viewer.viewerapp.ImageHandler.artboard2;

public class Navbar extends MenuBar {

    public Navbar(ImageHandler imageHandler) {

        Menu fileMenu = createMenu("File", "Open", "Save", "Exit");
        Menu editMenu = createMenu("Edit", "Undo", "Redo", "Cut", "Copy", "Paste", "Delete");
        Menu windowMenu = createMenu("Window", "Show Grid", "Remove Grid", "Show Rulers", "Remove Rulers");
        this.getMenus().addAll(fileMenu, editMenu,windowMenu);

        // fileMenu
        fileMenu.getItems().get(0).setOnAction(event -> imageHandler.choosePicture());

        fileMenu.getItems().get(1).setOnAction(event -> {
            if (artboard2.getImageView() != null) {
                imageHandler.saveFile();
            }
        });

        fileMenu.getItems().get(2).setOnAction(event -> System.exit(0));

        // windowMenu
        windowMenu.getItems().get(0).setOnAction(event -> {
            if (artboard2.getImageView() != null) {
                artboard2.addGrid();
            }
        });

        windowMenu.getItems().get(1).setOnAction(event -> {
            if (artboard2.getImageView() != null) {
                artboard2.removeGrid();
            }
        });
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
