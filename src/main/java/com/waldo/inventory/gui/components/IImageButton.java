package com.waldo.inventory.gui.components;

import javax.swing.*;

class IImageButton extends JButton { //} implements ChangeListener {

    IImageButton(ImageIcon activeIcon, ImageIcon rollOverIcon, ImageIcon pressedIcon, ImageIcon disabledIcon) {
        super();

        setContentAreaFilled(false);
        setBorder(BorderFactory.createEmptyBorder());

        setIcon(activeIcon);
        setRolloverIcon(rollOverIcon);
        setPressedIcon(pressedIcon);
        setDisabledIcon(disabledIcon);
    }
}
