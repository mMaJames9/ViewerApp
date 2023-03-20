package com.viewer.viewerapp;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

public class ImageHandler {

    private static final ImageFileFilter imageFileFilter = new ImageFileFilter();
    private static final int INTENSITY_SCALAR = 3;
    static Artboard artboard2;
    private static Image image;


    public static Image getImage() {
        return image;
    }

    public static void setArtboard(Artboard artboard) {
        ImageHandler.artboard2 = artboard;
    }

    private Image createImageFromXLSX(File file) throws IOException, InvalidFormatException {
        try (Workbook workbook = new XSSFWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum() + 1;
            int columnCount = sheet.getRow(0).getLastCellNum();
            BufferedImage bufferedImage = new BufferedImage(columnCount, rowCount, BufferedImage.TYPE_INT_RGB);
            for (int row = 0; row < rowCount; row++) {
                for (int column = 0; column < columnCount; column++) {
                    Cell cell = sheet.getRow(row).getCell(column);
                    if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                        int value = (int) cell.getNumericCellValue();
                        Color color = Color.rgb(value, value, value);
                        int red = (int) (color.getRed() * 255);
                        int green = (int) (color.getGreen() * 255);
                        int blue = (int) (color.getBlue() * 255);
                        int rgb = (red << 16) | (green << 8) | blue;
                        bufferedImage.setRGB(column, row, rgb);
                    }
                }
            }
            return SwingFXUtils.toFXImage(bufferedImage, null);
        }
    }

    public void choosePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(imageFileFilter.getDescription(), "*.jpg", "*.jpeg", "*.tif", "*.tiff", "*.png", "*.xlsx"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                String extension = imageFileFilter.getExtension(file);
                assert extension != null;
                if (extension.equalsIgnoreCase("xlsx")) {
                    image = createImageFromXLSX(file);
                } else {
                    image = new Image(file.toURI().toString());
                }

                Artboard.updateAllArtboards();

                Sidebar.newView = artboard2.getImageView();

            } catch (IOException | InvalidFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error opening file");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    public Workbook toWorkbook() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Image Data");

        PixelReader pixelReader = image.getPixelReader();
        for (int i = 0; i < image.getHeight(); i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < image.getWidth(); j++) {
                Cell cell = row.createCell(j);
                Color color = pixelReader.getColor(j, i);
                int red = (int) (color.getRed() * 255);
                int green = (int) (color.getGreen() * 255);
                int blue = (int) (color.getBlue() * 255);
                int intensity = (red + green + blue) / INTENSITY_SCALAR;
                cell.setCellValue(intensity);
            }
        }
        return workbook;
    }

    public void saveFile() {
        RadioButton pngButton = new RadioButton("PNG");
        RadioButton xlsxButton = new RadioButton("XLSX");
        ToggleGroup fileTypeGroup = new ToggleGroup();
        pngButton.setToggleGroup(fileTypeGroup);
        xlsxButton.setToggleGroup(fileTypeGroup);
        pngButton.setSelected(true);
        VBox fileTypePanel = new VBox(pngButton, xlsxButton);
        Alert fileTypeDialog = new Alert(Alert.AlertType.CONFIRMATION);
        fileTypeDialog.setTitle("Select file type");
        fileTypeDialog.setHeaderText(null);
        fileTypeDialog.setContentText("Select the file type to save the image:");
        fileTypeDialog.getDialogPane().setContent(fileTypePanel);
        Optional<ButtonType> option = fileTypeDialog.showAndWait();
        if (option.isEmpty() || option.get() == ButtonType.CANCEL) {
            return;
        }
        FileChooser.ExtensionFilter fileFilter;
        String extension;
        if (pngButton.isSelected()) {
            fileFilter = new FileChooser.ExtensionFilter("PNG image (*.png)", "png");
            extension = "png";
        } else {
            fileFilter = new FileChooser.ExtensionFilter("Excel workbook (*.xlsx)", "xlsx");
            extension = "xlsx";
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image");
        fileChooser.getExtensionFilters().add(fileFilter);
        File fileToSave = fileChooser.showSaveDialog(null);
        if (fileToSave == null) {
            return;
        }
        String fileName = fileToSave.getName();
        if (!fileName.endsWith("." + extension)) {
            fileToSave = new File(fileToSave.getParentFile(), fileName + "." + extension);
        }
        try {
            if (pngButton.isSelected()) {
                ImageView imageView = artboard2.getImageView();
                WritableImage imageToSave = imageView.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageToSave, null);
                ImageIO.write(bufferedImage, extension, fileToSave);
            } else {
                Workbook workbook = toWorkbook();
                FileOutputStream fos = new FileOutputStream(fileToSave);
                workbook.write(fos);
                fos.close();
            }

            showInfoAlert();
        } catch (Exception ex) {
            showErrorAlert(ex);
        }
    }


    private void showInfoAlert() {
        Alert successDialog = new Alert(Alert.AlertType.INFORMATION);
        successDialog.setTitle("Success");
        successDialog.setHeaderText(null);
        successDialog.setContentText("Image saved successfully!");
        successDialog.showAndWait();
    }

    private void showErrorAlert(Exception ex) {
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
        errorDialog.setTitle("Error");
        errorDialog.setHeaderText(null);
        errorDialog.setContentText("Failed to save image: " + ex.getMessage());
        errorDialog.showAndWait();
    }
}