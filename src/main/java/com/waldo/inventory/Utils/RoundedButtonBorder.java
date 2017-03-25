package com.waldo.inventory.Utils;

import javax.swing.border.Border;
import java.awt.*;

public class RoundedButtonBorder implements Border {

    private int radius;

    public RoundedButtonBorder(int radius) {
        this.radius = radius;
    }


    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
    }


    public boolean isBorderOpaque() {
        return true;
    }


    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.LIGHT_GRAY);
        g.drawRoundRect(x, y, width-1, height-1, radius, radius);
    }
}
