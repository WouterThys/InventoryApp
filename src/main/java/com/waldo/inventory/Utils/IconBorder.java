package com.waldo.inventory.Utils;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.net.URL;

public class IconBorder extends AbstractBorder {

    private final ImageIcon icon;
    private final Border originalBorder;

    public IconBorder(ImageIcon icon, Border originalBorder) {
        this.icon = icon;
        this.originalBorder = originalBorder;
    }

    public IconBorder(URL imageURL, Border originalBorder) {
        this(new ImageIcon(java.awt.Toolkit.getDefaultToolkit().createImage(imageURL)), originalBorder);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        originalBorder.paintBorder(c, g, x, y, width, height);
        Insets insets = getBorderInsets(c);
        int by = (c.getHeight() / 2) - (icon.getIconHeight() / 2);
        int w = Math.max(2, insets.left);
        int bx = x + width - (icon.getIconHeight() + (w * 2)) + 2;
        g.translate(bx, by);
        g.drawImage(icon.getImage(), x, y,icon.getImageObserver());
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return originalBorder.getBorderInsets(c);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
