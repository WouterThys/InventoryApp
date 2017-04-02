package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URL;

public abstract class IdBToolBar extends JToolBar {

    private Action refreshAction;
    private Action addAction;
    private Action deleteAction;
    private Action editAction;

    public IdBToolBar() {
        super();
        init();
    }

    public IdBToolBar(int orientation) {
        super(orientation);
        init();
    }

    public IdBToolBar(String name) {
        super(name);
        init();
    }

    public IdBToolBar(String name, int orientation) {
        super(name, orientation);
        init();
    }

    protected abstract void refresh();
    protected abstract void add();
    protected abstract void delete();
    protected abstract void update();


    private void init() {
        URL url = IDialogPanel.class.getResource("/settings/Settings.properties");
        ResourceManager resourceManager = new ResourceManager(url.getPath());
        refreshAction = new AbstractAction("Refresh", resourceManager.readImage("Toolbar.RefreshIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        };

        addAction = new AbstractAction("Add", resourceManager.readImage("Toolbar.AddIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                add();
            }
        };

        deleteAction = new AbstractAction("Delete", resourceManager.readImage("Toolbar.DeleteIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        };

        editAction = new AbstractAction("Update", resourceManager.readImage("Toolbar.EditIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        };

        add(refreshAction);
        addSeparator();
        add(addAction);
        add(deleteAction);
        addSeparator();
        add(editAction);
    }

//    private void initialize() {
//        if (refreshAction != null) {
//            add(refreshAction);
//            addSeparator();
//        }
//        if (addAction != null) {
//            add(addAction);
//        }
//        if (deleteAction != null) {
//            add(deleteAction);
//        }
//        if (editAction != null) {
//            addSeparator();
//            add(editAction);
//        }
//    }
//
//    public void setActions(Action refreshAction, Action addAction, Action deleteAction, Action editAction) {
//        this.refreshAction = refreshAction;
//        this.addAction = addAction;
//        this.deleteAction = deleteAction;
//        this.editAction = editAction;
//        initialize();
//    }
//
//    public void setRefreshAction(Action refreshAction) {
//        this.refreshAction = refreshAction;
//    }
//
//    public void setAddAction(Action addAction) {
//        this.addAction = addAction;
//    }
//
//    public void setDeleteAction(Action deleteAction) {
//        this.deleteAction = deleteAction;
//    }
//
//    public void setEditAction(Action editAction) {
//        this.editAction = editAction;
//    }
}
