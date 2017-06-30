package com.waldo.inventory.gui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public class IdBToolBar extends JToolBar {

    public interface IdbToolBarListener {
        void onToolBarRefresh();
        void onToolBarAdd();
        void onToolBarDelete();
        void onToolBarEdit();
    }

    private Action refreshAction;
    private Action addAction;
    private Action deleteAction;
    private Action editAction;
    private IdbToolBarListener toolBarListener;

    public IdBToolBar(IdbToolBarListener listener) {
        super();
        init(listener);
    }

    public IdBToolBar(IdbToolBarListener listener, int orientation) {
        super(orientation);
        init(listener);
    }

    public IdBToolBar(IdbToolBarListener listener, String name) {
        super(name);
        init(listener);
    }

    public IdBToolBar(IdbToolBarListener listener, String name, int orientation) {
        super(name, orientation);
        init(listener);
    }

    private void init(IdbToolBarListener listener) {
        this.toolBarListener = listener;

        // Actions
        refreshAction = new AbstractAction("Refresh", imageResource.readImage("Toolbar.RefreshIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolBarListener.onToolBarRefresh();
            }
        };

        addAction = new AbstractAction("Add", imageResource.readImage("Toolbar.AddIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolBarListener.onToolBarAdd();
            }
        };

        deleteAction = new AbstractAction("Delete", imageResource.readImage("Toolbar.DeleteIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolBarListener.onToolBarDelete();
            }
        };

        editAction = new AbstractAction("Update", imageResource.readImage("Toolbar.EditIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolBarListener.onToolBarEdit();
            }
        };

        add(refreshAction);
        addSeparator();
        add(addAction);
        add(deleteAction);
        addSeparator();
        add(editAction);

        setOpaque(false);
        super.setBorder(new EmptyBorder(5,5,5,5));
    }

    public void setBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);
        super.setBorder(titledBorder);
    }

    public void setRefreshActionEnabled(boolean enabled) {
        refreshAction.setEnabled(enabled);
    }

    public void setAddActionEnabled(boolean enabled) {
        addAction.setEnabled(enabled);
    }

    public void setDeleteActionEnabled(boolean enabled) {
        deleteAction.setEnabled(enabled);
    }

    public void setEditActionEnabled(boolean enabled) {
        editAction.setEnabled(enabled);
    }

    public void setRefreshVisible(boolean visible) {
        Component c = getComponentAtIndex(0);
        c.setVisible(visible);
    }

    public void setAddVisible(boolean visible) {
        getComponentAtIndex(1).setVisible(visible);
    }

    public void setDeleteVisible(boolean visible) {
        getComponentAtIndex(2).setVisible(visible);
    }

    public void setEditVisible(boolean visible) {
        getComponentAtIndex(3).setVisible(visible);
    }
}
