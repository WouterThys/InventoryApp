package com.waldo.inventory.gui.components;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IImageButton extends JButton { //} implements ChangeListener {


    private ImageIcon activeIcon;
    private ImageIcon rollOverIcon;

    public IImageButton(ImageIcon activeIcon, ImageIcon rollOverIcon, ImageIcon pressedIcon, ImageIcon disabledIcon) {
        super();

        setContentAreaFilled(false);
        setBorder(BorderFactory.createEmptyBorder());

        this.activeIcon = activeIcon;
        this.rollOverIcon = rollOverIcon;

        setIcon(activeIcon);
        setRolloverIcon(rollOverIcon);
        setPressedIcon(pressedIcon);
        setDisabledIcon(disabledIcon);
    }

//    @Override
//    public void stateChanged(ChangeEvent e) {
//        ButtonModel model = (ButtonModel) e.getSource();
//        if (model.isRollover()) {
//            setIcon();
//        }
//    }
}
