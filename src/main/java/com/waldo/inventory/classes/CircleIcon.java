package com.waldo.inventory.classes;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class CircleIcon extends ImageIcon {

    private Color color;
    private int w;
    private int h;

    public CircleIcon(Color color) {
        this.color = color;
        this.h = 16;
        this.w = 16;
    }

    @Override
    public int getIconWidth() {
        return w;
    }

    @Override
    public int getIconHeight() {
        return h;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        //super.paintIcon(c, g, x, y);
        Graphics2D g2 = (Graphics2D) g;
        Ellipse2D.Double circle = new Ellipse2D.Double(0, y, w, h);
        g2.setColor(color);
        g2.fill(circle);
    }
}
