package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.GuiInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class OrderPanel extends JPanel implements GuiInterface {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentPanel.class);

    private Item newItem;

    public OrderPanel(Item newItem) {
        this.newItem = newItem;
    }

    public void setComponentValues() {

    }

    @Override
    public void initializeComponents() {

    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object object) {
        LOG.debug("Component panel: update components.");
    }
}
