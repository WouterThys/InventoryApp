package com.waldo.inventory.gui;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.IObjectSearchPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class TopToolBar extends JToolBar implements IObjectSearchPanel.IObjectSearchListener {

    private Application application;

    private IdBToolBar databaseToolBar;
    private IObjectSearchPanel searchPanel;

    public TopToolBar(Application application){
        this.application = application;

        // Layout
        setLayout(new BorderLayout(2,2));
        setFloatable(false);

        // Database stuff
        databaseToolBar = new IdBToolBar() {
            @Override
            protected void refresh() {

            }

            @Override
            protected void add() {
                Item newItem = null;
                try {
                    newItem = EditItemDialog.showDialog(application);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                if (newItem != null) {
                    newItem.save();
                }
            }

            @Override
            protected void delete() {
                Item selectedItem = application.getSelectedItem();
                if (selectedItem != null) {
                    if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(application, "Delete " + selectedItem + "?", "Delete", JOptionPane.YES_NO_OPTION)) {
                        selectedItem.delete();
                    }
                }
            }

            @Override
            protected void update() {
                Item selectedItem = application.getSelectedItem();
                try {
                    selectedItem = EditItemDialog.showDialog(application, selectedItem);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                if (selectedItem != null) {
                    selectedItem.save();
                }
            }
        };
        databaseToolBar.setFloatable(false);

        // Search stuff: search only for items
        searchPanel = new IObjectSearchPanel(true, this, DbObject.TYPE_ITEM);

        // Add
        add(databaseToolBar, BorderLayout.WEST);
        add(searchPanel, BorderLayout.EAST);
    }

    public void clearSearch() {
        searchPanel.clearSearch();
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
