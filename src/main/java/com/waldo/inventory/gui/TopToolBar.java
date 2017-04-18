package com.waldo.inventory.gui;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.components.IObjectSearchPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TopToolBar extends JToolBar implements IObjectSearchPanel.IObjectSearchListener {

    private Application application;

    private IdBToolBar mainViewToolBar;
    private IObjectSearchPanel searchPanel;
    private JPanel contentPane;

    public TopToolBar(Application application, IdBToolBar.IdbToolBarListener toolBarListener){
        this.application = application;

        // Layout
        setFloatable(false);
        setRollover(true);

        // Tool bars
        mainViewToolBar = new IdBToolBar(toolBarListener);
        mainViewToolBar.setFloatable(false);
        contentPane = new JPanel();
        contentPane.setOpaque(false);

        // Search stuff: search only for items
        searchPanel = new IObjectSearchPanel(true, this, DbObject.TYPE_ITEM);

        // Add
        add(mainViewToolBar);
        addSeparator();
        add(contentPane);
        add(Box.createHorizontalGlue());
        addSeparator();
        add(searchPanel);
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
}
