package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ILabel extends JLabel {

    public ILabel() {
        super();
    }

    public ILabel(Icon image) {
        super(image);
    }

    public ILabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
    }

    public ILabel(String text) {
        super(text);
    }

    public ILabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    public ILabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public void setFontSize(int size) {
        Font labelFont = this.getFont();
        this.setFont(new Font(labelFont.getName(), labelFont.getStyle(), size));
    }

    public void setIcon(String path) {
        URL url = ILabel.class.getResource("/settings/Settings.properties");
        ResourceManager resourceManager = new ResourceManager(url.getPath());

        try {
            url = new File(path).toURI().toURL();
            setIcon(resourceManager.readImage(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setIcon(String path, int width, int height) {
        URL url = ILabel.class.getResource("/settings/Settings.properties");
        ResourceManager resourceManager = new ResourceManager(url.getPath());

        try {
            url = new File(path).toURI().toURL();
            setIcon(resourceManager.readImage(url, width, height));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void adjustTextSize() {
        Font labelFont = this.getFont();
        String labelText = this.getText();

        int stringWidth = this.getFontMetrics(labelFont).stringWidth(labelText);
        int componentWidth = this.getWidth();

        double widthRatio = (double) componentWidth / (double)stringWidth;

        int newFontSize = (int)(labelFont.getSize() * widthRatio);
        int componentHeight = this.getHeight();

        int fontSizeToUse = Math.min(newFontSize, componentHeight);

        this.setFont(new Font(labelFont.getName(), labelFont.getStyle(), fontSizeToUse));
    }
}
