package com.waldo.inventory.gui;

import javax.swing.*;

public class TopToolBar extends JToolBar {

    private static final TopToolBar INSTANCE = new TopToolBar();

    public static TopToolBar getToolbar() {
        return INSTANCE;
    }

    private TopToolBar(){}

    public void init(Action refreshAction, Action newAction, Action deleteAction, Action editAction) {
        add(refreshAction);
        addSeparator();
        add(newAction);
        add(editAction);
        addSeparator();
        add(deleteAction);
    }
}
