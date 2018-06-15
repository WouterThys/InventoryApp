package com.waldo.inventory.gui.dialogs.editimagedialog;

import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static com.waldo.inventory.gui.Application.imageResource;

public class ImageLabel extends ILabel implements MouseListener {

    private DbImage dbImage;

    private boolean isSelected = false;
    private Color background;

    ImageLabel(DbImage image) {
        super();
        this.dbImage = image;
        setText(image.toString());
        if (image.getImageIcon() != null) {
            ImageIcon icon = image.getImageIcon();
            if (icon.getIconWidth() > 128 || icon.getIconHeight() > 128) {
                icon = ImageResource.scaleImage(icon, new Dimension(128, 128));
            }
            setIcon(icon);
        } else {
            setIcon(imageResource.getDefaultImage(image.getImageType()));
        }

        setPreferredSize(new Dimension(140, 160));
        setMaximumSize(new Dimension(160, 180));

        addMouseListener(this);
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        background = getBackground();

        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
        setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    }

    public DbImage getDbImage() {
        return dbImage;
    }


    public void setSelected(boolean selected) {
        isSelected = selected;
        if (selected) {
            this.setBackground(Color.gray);
        } else {
            this.setBackground(background);
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        setBackground(Color.gray.darker());
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setBackground(Color.gray.brighter());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setSelected(isSelected);
    }
}
