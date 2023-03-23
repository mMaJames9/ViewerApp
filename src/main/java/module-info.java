module com.viewer.viewerapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.poi.ooxml;

    opens com.viewer.viewerapp to javafx.fxml;
    exports com.viewer.viewerapp;
}