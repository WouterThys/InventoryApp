package com.waldo.inventory.gui.components;

import javax.swing.*;
import java.awt.*;

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
