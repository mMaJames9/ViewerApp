package com.viewer.viewerapp;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class RotationFX {

    public static ImageView imageView ;
    private static int rotationAngle;
static Button rotationButton = new Button("Rotate");

    public static void  rotateImage( ImageView imgv) throws IOException {

                    RotationFX.imrotate(imgv);

    }

    public static Image imrotate(ImageView imageView) throws IOException {
        rotationAngle += 90;
        Image image = imageView.getImage();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
;
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();

        double theta = Math.toRadians(rotationAngle);
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);

        int x0 = (int) Math.round((w - h * Math.abs(sinTheta)
                / cosTheta) / 2.0);
        int y0 = (int) Math.round((h - w * Math.abs(sinTheta)
                / cosTheta) / 2.0);
        int w2 = (int) Math.round(w * Math.abs(cosTheta) + h
                * Math.abs(sinTheta));
        int h2 = (int) Math.round(h * Math.abs(cosTheta) + w
                * Math.abs(sinTheta));

        BufferedImage rotatedImage = new BufferedImage(w2, h2, bufferedImage.getType());
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.translate(w2 / 2, h2 / 2);
        affineTransform.rotate(theta);
        affineTransform.translate(-w / 2, -h / 2);

        AffineTransformOp op = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);
        op.filter(bufferedImage, rotatedImage);

        Image resultImage = SwingFXUtils.toFXImage(rotatedImage, null);
        imageView.setImage(resultImage);

        return SwingFXUtils.toFXImage(rotatedImage, null);
    }

}
