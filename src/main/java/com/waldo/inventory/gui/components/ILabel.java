package com.waldo.inventory.gui.components;


import com.waldo.inventory.database.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class ILabel extends JLabel {

    private static final LogManager LOG = LogManager.LOG(ILabel.class);

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
        URL url;
        try {
            url = new File(path).toURI().toURL();
            ImageIcon icon = imageResource.readImage(url);
            if (icon.getIconWidth() > 0) {
                setIcon(icon);
            } else {
                setIcon(imageResource.readImage("Common.UnknownIcon32"));
            }
        } catch (MalformedURLException e) {
            LOG.warning("Error reading icon.", e);
        }
    }

    public void setIcon(String path, int width, int height) {
        URL url;
        try {
            url = new File(path).toURI().toURL();
            setIcon(imageResource.readImage(url, width, height));
        } catch (Exception e) {
            LOG.error("Error settning icon.", e);
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

    public static VerticalLabelUI createVerticalLabel(boolean clockwise) {
        return new VerticalLabelUI(clockwise);
    }


    private static class VerticalLabelUI extends BasicLabelUI {
        static {
            labelUI = new VerticalLabelUI(false);
        }

        protected boolean clockwise;


        public VerticalLabelUI(boolean clockwise) {
            super();
            this.clockwise = clockwise;
        }


        public Dimension getPreferredSize(JComponent c) {
            Dimension dim = super.getPreferredSize(c);
            return new Dimension( dim.height, dim.width );
        }

        private static Rectangle paintIconR = new Rectangle();
        private static Rectangle paintTextR = new Rectangle();
        private static Rectangle paintViewR = new Rectangle();
        private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

        public void paint(Graphics g, JComponent c) {

            JLabel label = (JLabel)c;
            String text = label.getText();
            Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

            if ((icon == null) && (text == null)) {
                return;
            }

            FontMetrics fm = g.getFontMetrics();
            paintViewInsets = c.getInsets(paintViewInsets);

            paintViewR.x = paintViewInsets.left;
            paintViewR.y = paintViewInsets.top;

            // Use inverted height & width
            paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
            paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);

            paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
            paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

            String clippedText =
                    layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

            Graphics2D g2 = (Graphics2D) g;
            AffineTransform tr = g2.getTransform();
            if (clockwise) {
                g2.rotate( Math.PI / 2 );
                g2.translate( 0, - c.getWidth() );
            } else {
                g2.rotate( - Math.PI / 2 );
                g2.translate( - c.getHeight(), 0 );
            }

            if (icon != null) {
                icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
            }

            if (text != null) {
                int textX = paintTextR.x;
                int textY = paintTextR.y + fm.getAscent();

                if (label.isEnabled()) {
                    paintEnabledText(label, g, clippedText, textX, textY);
                } else {
                    paintDisabledText(label, g, clippedText, textX, textY);
                }
            }

            g2.setTransform( tr );
        }
    }
}
