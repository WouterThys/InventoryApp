package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class ILabel extends JLabel {

    private String statusInfo = "";

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

    private void setListener() {
        if (getMouseListeners() != null && getMouseListeners().length > 0) {
            return;
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!statusInfo.isEmpty()) {
                    Status().holdMessage(statusInfo);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Status().clear();
            }
        });
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
        setListener();
    }

    public void setFontSize(int size) {
        Font labelFont = this.getFont();
        this.setFont(new Font(labelFont.getName(), labelFont.getStyle(), size));
    }

    public void setIcon(String path) {
        URL url = ILabel.class.getResource("/settings/IconSettings.properties");
        ResourceManager resourceManager = new ResourceManager(url.getPath());

        try {
            url = new File(path).toURI().toURL();
            ImageIcon icon = resourceManager.readImage(url);
            if (icon.getIconWidth() > 0) {
                setIcon(icon);
            } else {
                setIcon(resourceManager.readImage("Common.UnknownIcon32"));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setIcon(String path, int width, int height) {
        URL url = ILabel.class.getResource("/settings/IconSettings.properties");
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
