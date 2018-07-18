package com.waldo.inventory.gui.components;

import javax.swing.*;
import java.awt.*;

public class ILocationPrintable extends JComponent {

    public ILocationPrintable() {
        setPreferredSize(new Dimension(200, 50));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        Graphics2D g2d = (Graphics2D) g;

        g2d.drawRect(10,10, 50,10);
    }
}
