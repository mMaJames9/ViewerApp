package com.viewer.viewerapp;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.util.Arrays;

public class Navbar extends MenuBar {

    private final Artboard artboard1;
    private final Artboard artboard2;

    public Navbar(Artboard artboard1, Artboard artboard2) {
        this.artboard1 = artboard1;
        this.artboard2 = artboard2;

        Menu fileMenu = createMenu("File", "Open", "Save", "Exit");
        Menu editMenu = createMenu("Edit", "Undo", "Redo", "Cut", "Copy", "Paste", "Delete");
        Menu viewMenu = createMenu("View", "Ruler", "Grid");
        this.getMenus().addAll(fileMenu, editMenu, viewMenu);

        setupFileMenuActions(fileMenu);
        setupViewMenuActions(viewMenu);
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

    private void setupFileMenuActions(Menu fileMenu) {
        fileMenu.getItems().get(0).setOnAction(event -> ImageHandler.choosePicture(Arrays.asList(artboard1, artboard2)));

        fileMenu.getItems().get(1).setOnAction(event -> {
            if (artboard2.getImageView() != null) {
                ImageHandler.saveFile(artboard2);
            }
        });

        fileMenu.getItems().get(2).setOnAction(event -> System.exit(0));
    }

    private void setupViewMenuActions(Menu viewMenu) {
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
}