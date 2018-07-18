package com.waldo.inventory.gui.components;

import com.waldo.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;

public class ILocationLabelPreview extends JComponent implements GuiUtils.GuiInterface {

    public ILocationLabelPreview() {
        setPreferredSize(new Dimension(200, 50));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        Graphics2D g2d = (Graphics2D) g;

        g2d.drawRect(10,10, 50,10);
    }

    @Override
    public void initializeComponents() {

    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object... objects) {

    }
}
