package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Resistor;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.IPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static com.waldo.inventory.gui.Application.imageResource;

public class IResistorImage extends IPanel {

    private final static double X_SCALE = 0.375;
    private final static double Y_SCALE = 0.236;
    private final static double W_SCALE = 0.0375;
    private final static double H_SCALE = 0.520;

    private final static double X_SPACE_SCALE = 0.0625;
    private final static double XT_SCALE = 0.8;
    private final static double YT_SCALE = 0.134;
    private final static double HT_SCALE = 0.730;

    private final BufferedImage resistorTemplate = (BufferedImage) imageResource.readImage("Template.Resistor").getImage();

    private final ResistorBand band1 = new ResistorBand();
    private final ResistorBand band2 = new ResistorBand();
    private final ResistorBand band3 = new ResistorBand();

    private final ResistorBand multiplierBand = new ResistorBand();
    private final ResistorBand toleranceBand = new ResistorBand();
    private final ResistorBand ppmBand = new ResistorBand();

    private final int originalWidth = resistorTemplate.getWidth();
    private final int originalHeight = resistorTemplate.getHeight();

    private Resistor resistor;

    public IResistorImage(Resistor resistor) {
        this.resistor = resistor;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public int getOriginalHeight() {
        return originalHeight;
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public JPanel createPrintablePanel() {
        BufferedImage resistorImage = GuiUtils.resizeImage(GuiUtils.imageFromPanel(this), 80, 36);

        ILabel valueLbl = new ILabel(resistor.getValue().toString(), SwingConstants.CENTER);
        ILabel imageLbl = new ILabel(new ImageIcon(resistorImage), SwingConstants.CENTER);
        ILabel smdLbl = new ILabel("2R2", SwingConstants.CENTER);

        valueLbl.setFont(26, Font.BOLD);
        smdLbl.setFont(18);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(valueLbl, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(imageLbl, BorderLayout.CENTER);
        centerPanel.add(smdLbl, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    @Override
    public void initializeComponents() {

    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object... objects) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        if (resistorTemplate != null) {
            g2d.drawImage(resistorTemplate, 0, 0, getWidth(), getHeight(),this);

            int x = (int)(getWidth() * X_SCALE);
            int y = (int)(getHeight() * Y_SCALE);
            int w = (int)(getWidth() * W_SCALE);
            int h = (int)(getHeight() * H_SCALE);
            int s = (int)(getWidth() * X_SPACE_SCALE);
            int xt = (int)(getWidth() * XT_SCALE);
            int yt = (int)(getHeight() * YT_SCALE);
            int ht = (int)(getHeight() * HT_SCALE);

            band1.init(x, y, w, h);
            band2.init(x + s, y, w, h);
            band3.init(x + 2*s, y, w, h);
            multiplierBand.init(x + (int)(3.5*s), y, w, h);
            toleranceBand.init(xt, yt, w, ht);

            band1.addToGraphics(g2d, resistor.getFirstBand().getColor());
            band2.addToGraphics(g2d, resistor.getSecondBand().getColor());
            if (!resistor.getBandType().equals(Statics.ResistorBandType.FourBand)) {
                band3.addToGraphics(g2d, resistor.getThirdBand().getColor());
            }
            multiplierBand.addToGraphics(g2d, resistor.getMultiplierBand().getColor());
            toleranceBand.addToGraphics(g2d, resistor.getToleranceBand().getColor());
        }
    }

    private class ResistorBand {
        private int x;
        private int y;
        private int w;
        private int h;

        void init(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        void addToGraphics(Graphics2D g2d, Color color) {
            GradientPaint background = new GradientPaint(x, y, color.darker(),
                    x+ w, y+ h, color.brighter());

            g2d.setPaint(background);
            g2d.fillRect(x,y, w, h);
        }
    }
}
