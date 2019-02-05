package com.waldo.inventory.gui.components.actions;

import javax.swing.*;

public abstract class IAbstractAction extends AbstractAction {

    public IAbstractAction() {
        super();
    }

    public IAbstractAction(String name) {
        super(name);
    }

    public IAbstractAction(String name, ImageIcon imageIcon) {
        super(name, imageIcon);
    }

    public IAbstractAction(String name, ImageIcon imageIcon, String toolTip) {
        super(name, imageIcon);
        setTooltip(toolTip);
    }

    public void setName(String name) {
        putValue(Action.NAME, name);
    }

    public void setTooltip(String tooltip) {
        putValue(Action.SHORT_DESCRIPTION, tooltip);
    }

    public void setIcon(ImageIcon icon) {
        putValue(Action.SMALL_ICON, icon);
    }
}
