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

//    protected abstract void refresh();
//    protected abstract void add();
//    protected abstract void delete();
//    protected abstract void update();


    private void init(IdbToolBarListener listener) {
        this.toolBarListener = listener;

        // Resource manager
        URL url = IdBToolBar.class.getResource("/settings/Settings.properties");
        ResourceManager resourceManager = new ResourceManager(url.getPath());

        // Actions
        Action refreshAction = new AbstractAction("Refresh", resourceManager.readImage("Toolbar.RefreshIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolBarListener.onToolBarRefresh();
            }
        };

        Action addAction = new AbstractAction("Add", resourceManager.readImage("Toolbar.AddIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolBarListener.onToolBarAdd();
            }
        };

        Action deleteAction = new AbstractAction("Delete", resourceManager.readImage("Toolbar.DeleteIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolBarListener.onToolBarDelete();
            }
        };

        Action editAction = new AbstractAction("Update", resourceManager.readImage("Toolbar.EditIcon")) {
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

    public void setBorderTitle(String title) {
        if (titledBorder != null) {
            titledBorder.setTitle(title);
        }
    }
}
