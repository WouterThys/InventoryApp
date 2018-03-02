package com.waldo.inventory.gui.components;

import com.waldo.utils.icomponents.IPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.waldo.inventory.gui.Application.imageResource;

public class IResistorImage extends IPanel {

    private final BufferedImage resistorTemplate = (BufferedImage) imageResource.readImage("Template.Resistor").getImage();

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(resistorTemplate.getWidth(), resistorTemplate.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        if (resistorTemplate != null) {

            GradientPaint background = new GradientPaint(570f, 173f, Color.BLUE.darker(),
                    653f, 540f, Color.BLUE.brighter());


            g2d.drawImage(resistorTemplate, 0, 0, this);
            g2d.setPaint(background);
            g2d.fillRect(570,173, 83, 367);
        }
    }

    @Override
    public void initializeComponents() {
        Rectangle rectangle = new Rectangle(10,10, 50,100);
    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object... objects) {

    }
}
