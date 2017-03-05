package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TopToolBar extends JToolBar {

    private static final TopToolBar INSTANCE = new TopToolBar();

    private Action refreshAction;
    private Action newAction;
    private Action deleteAction;
    private Action saveAction;
    private static Application app;

    public static TopToolBar getToolbar(Application app) {
        TopToolBar.app = app;
        return INSTANCE;
    }

    private TopToolBar(){}

    public void init() {
        initActions();

        add(refreshAction);
        addSeparator();
        add(newAction);
        add(saveAction);
        addSeparator();
        add(deleteAction);
    }

    private void initActions() {
        refreshAction = new AbstractAction("Refresh", ImageUtils.loadImageIcon("refresh", ImageUtils.ICON_SIZE_24)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.refreshItemList();
            }
        };

        newAction = new AbstractAction("Create", ImageUtils.loadImageIcon("create", ImageUtils.ICON_SIZE_24)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.createNewItem();
            }
        };

        deleteAction = new AbstractAction("Delete", ImageUtils.loadImageIcon("delete", ImageUtils.ICON_SIZE_24)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.deleteItem();
            }
        };

        saveAction = new AbstractAction("Save", ImageUtils.loadImageIcon("save", ImageUtils.ICON_SIZE_24)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.saveItem();
            }
        };
    }

}
