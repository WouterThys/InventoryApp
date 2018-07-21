package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.LabelAnnotation;
import com.waldo.inventory.classes.dbclasses.LocationLabel;
import com.waldo.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class ILocationLabelPreview extends JPanel implements GuiUtils.GuiInterface {

    private LocationLabel locationLabel;
    private Image image;
    private int imageX;
    private int imageY;


    private boolean paintImage = true;
    private double zoom = -1; // -1 == auto zoom

    public ILocationLabelPreview(LocationLabel locationLabel) {
        super();
        this.locationLabel = locationLabel;
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (locationLabel != null) {
            paintImage((Graphics2D) g);
            paintAnnotations((Graphics2D) g, locationLabel.getAnnotationList());
        }

    }


    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
//        this.zoom = zoom;
//
//        if (zoom > 0) {
//
//            int imW = image.getWidth(this);
//            int imH = image.getHeight(this);
//
//            image = image.getScaledInstance((int) (imW * zoom), (int) (imH * zoom), Image.SCALE_SMOOTH);
//        } else {
//            this.zoom = -1;
//        }
//        updateAll();
    }

    public void autoZoom() {
        zoom = -1;
        updateAll();
    }




    public void repaintAll() {
        repaintImage();
    }

    public void updateAll() {
        updateImage();
    }



    public void repaintImage() {
        image = null;
        repaint();
    }

    public void updateImage() {
        repaint();
    }


    int drawCnt = 0;
    private void paintImage(Graphics2D g) {
        if (paintImage) {
            if (image == null && locationLabel.getImageId() > DbObject.UNKNOWN_ID) {
                DbImage dbImage = imageResource.getImage(Statics.ImageType.Other, locationLabel.getImageId());
                image = dbImage.getImageIcon().getImage();
            }

            int imW = image.getWidth(this);
            int imH = image.getHeight(this);

            int myW = getWidth();
            int myH = getHeight();

            // Resize ?
            if (zoom < 0) {
                double autoZoom = 0;

                Dimension newDimension = new Dimension(imW, imH);
                while ((newDimension.getWidth() < (myW - (0.1 * myW))) && (newDimension.getHeight() < (myH - (0.1 * myH)))) {
                    autoZoom += 0.1; // Add 10%
                    newDimension.setSize((int) (imW + (imW * autoZoom)), (int) (imH + (imH * autoZoom)));
                }

                image = image.getScaledInstance(newDimension.width, newDimension.height, Image.SCALE_SMOOTH);
                imW = image.getWidth(this);
                imH = image.getHeight(this);

                zoom = autoZoom;
            }

            imageX = ((myW / 2) - (imW / 2));
            imageY = ((myH / 2) - (imH / 2));

        System.out.println(drawCnt + " Drawing image");
        drawCnt++;

        }

        if (image != null) {
            g.drawImage(image, imageX, imageY, this);
        }
    }


    public void updateAnnotations() {
        paintImage = false;
        paintAnnotations((Graphics2D) getGraphics(), locationLabel.getAnnotationList());
        repaint();
        SwingUtilities.invokeLater(() -> paintImage = true);
    }


    private void paintAnnotations(Graphics2D g, List<LabelAnnotation> annotationList) {
        for (LabelAnnotation a : annotationList) {

            switch (a.getType()) {
                case Text:
                    paintAnnotationText(g, a);
                    break;

                case Image:
                    paintAnnotationImage(g, a);
                    break;
            }

        }
    }

    private void paintAnnotationText(Graphics2D g, LabelAnnotation a) {
        Font font;
        String fontName = a.getTextFontName();
        if (fontName.isEmpty()) {
            font = g.getFont();
        } else {
            font = new Font(fontName, Font.PLAIN, 16);
        }

        if (a.getTextFontSize() > 0) {
            font = new Font(font.getName(), Font.PLAIN, a.getTextFontSize());
        }

        g.setFont(font);
        g.drawString(a.getText(), (int)a.getStartX(), (int)a.getStartY());
    }

    private void paintAnnotationImage(Graphics2D g, LabelAnnotation a) {

    }



    @Override
    public void initializeComponents() {

    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object... objects) {
        if (objects != null && objects.length > 0) {
            locationLabel = (LocationLabel) objects[0];
        }
        repaintAll();
    }
}
