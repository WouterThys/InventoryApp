package com.waldo.inventory.gui.components;

import javax.swing.*;

public class IdBToolBar extends JToolBar {

    private Action refreshAction;
    private Action addAction;
    private Action deleteAction;
    private Action editAction;

    public IdBToolBar() {
        super();
    }

    public IdBToolBar(int orientation) {
        super(orientation);
    }

    public IdBToolBar(String name) {
        super(name);
    }

    public IdBToolBar(String name, int orientation) {
        super(name, orientation);
    }

    private void initialize() {
        if (refreshAction != null) {
            add(refreshAction);
            addSeparator();
        }
        if (addAction != null) {
            add(addAction);
        }
        if (deleteAction != null) {
            add(deleteAction);
        }
        if (editAction != null) {
            addSeparator();
            add(editAction);
        }
    }

    public void setActions(Action refreshAction, Action addAction, Action deleteAction, Action editAction) {
        this.refreshAction = refreshAction;
        this.addAction = addAction;
        this.deleteAction = deleteAction;
        this.editAction = editAction;
    }

    public void setRefreshAction(Action refreshAction) {
        this.refreshAction = refreshAction;
    }

    public void setAddAction(Action addAction) {
        this.addAction = addAction;
    }

    public void setDeleteAction(Action deleteAction) {
        this.deleteAction = deleteAction;
    }

    public void setEditAction(Action editAction) {
        this.editAction = editAction;
    }
}
