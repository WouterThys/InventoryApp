package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.ResourceManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

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
    private TitledBorder titledBorder;
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

        // Resource manager
        URL url = IdBToolBar.class.getResource("/settings/Settings.properties");
        ResourceManager resourceManager = new ResourceManager(url.getPath());

        // Actions
        refreshAction = new AbstractAction("Refresh", resourceManager.readImage("Toolbar.RefreshIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolBarListener.onToolBarRefresh();
            }
        };

        addAction = new AbstractAction("Add", resourceManager.readImage("Toolbar.AddIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolBarListener.onToolBarAdd();
            }
        };

        deleteAction = new AbstractAction("Delete", resourceManager.readImage("Toolbar.DeleteIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolBarListener.onToolBarDelete();
            }
        };

        editAction = new AbstractAction("Update", resourceManager.readImage("Toolbar.EditIcon")) {
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
        titledBorder = BorderFactory.createTitledBorder(title);
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
}
