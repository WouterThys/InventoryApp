package com.waldo.inventory.gui;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.components.IObjectSearchPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TopToolBar extends JPanel implements IObjectSearchPanel.IObjectSearchListener {

    private Application application;

    private IdBToolBar mainViewToolBar;
    private IObjectSearchPanel searchPanel;
    private JPanel contentPane;

    public TopToolBar(Application application, IdBToolBar.IdbToolBarListener toolBarListener){
        this.application = application;

        // Layout
        setLayout(new BorderLayout());
        //setFloatable(false);
        //setRollover(true);

        // Tool bars
        mainViewToolBar = new IdBToolBar(toolBarListener);
        mainViewToolBar.setFloatable(false);
        contentPane = new JPanel();
        contentPane.setOpaque(false);

        // Search stuff: search only for items
        searchPanel = new IObjectSearchPanel(true, this, DbObject.TYPE_ITEM);

        // Add
        add(mainViewToolBar, BorderLayout.WEST);
        //addSeparator();
        add(contentPane, BorderLayout.CENTER);
        //addSeparator();
        add(searchPanel, BorderLayout.EAST);
    }

    public void clearSearch() {
        searchPanel.clearSearch();
    }

    public JPanel getContentPane() {
        return contentPane;
    }


    @Override
    public void onDbObjectFound(java.util.List<DbObject> foundObject) {
        List<Item> foundItems = new ArrayList<>(foundObject.size());
        for (DbObject object : foundObject) {
            foundItems.add((Item)object);
        }
        application.setTableItems(foundItems);
    }

    @Override
    public void onSearchCleared() {
        application.setTableItems(null); // Should set the table to the selected sub category
    }

    public void setRefreshEnabled(boolean enabled) {
        mainViewToolBar.setRefreshEnabled(enabled);
    }

    public void setAddActionEnabled(boolean enabled) {
        mainViewToolBar.setAddActionEnabled(enabled);
    }

    public void setDeleteActionEnabled(boolean enabled) {
        mainViewToolBar.setDeleteActionEnabled(enabled);
    }

    public void setEditActionEnabled(boolean enabled) {
        mainViewToolBar.setEditActionEnabled(enabled);
    }

    public void setSearchEnabled(boolean enabled) {
        searchPanel.setEnabled(enabled);
    }
}
