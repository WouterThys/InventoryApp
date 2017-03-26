package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.panels.itemlist.ItemListPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.SQLException;

public class Application extends JFrame {

    private ResourceManager resourceManager;

    private Action refreshItemsAction;
    private Action addItemAction;
    public Action updateItemAction;
    private Action deleteItemAction;

    private ItemListPanel itemListPanel;

    public Application() {
        URL url = ItemListPanel.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());
        initActions();
        initComponents();
    }

    private void initComponents() {
        // Toolbar
        TopToolBar ttb = TopToolBar.getToolbar();
        ttb.init(refreshItemsAction, addItemAction, updateItemAction, deleteItemAction);
        add(ttb, BorderLayout.PAGE_START);

        // Menu
        setJMenuBar(new MenuBar(this));

        // Main view
        itemListPanel = new ItemListPanel(this);
        add(itemListPanel, BorderLayout.CENTER);

        //add(createTablePane(), BorderLayout.CENTER); // TODO: to ItemListPanel
        //add(new QueryPanel(this), BorderLayout.SOUTH);
    }

    private void initActions() {
        initRefreshItemsAction();
        initAddItemAction();
        initUpdateItemAction();
        initDeleteItemAction();
    }

    private void initDeleteItemAction() {
        deleteItemAction = new AbstractAction("Delete", resourceManager.readImage("Toolbar.DeleteIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Item selectedItem = itemListPanel.getSelectedItem();
                if (selectedItem != null) {
                    System.out.println(selectedItem.getName() + " will be deleted.");
                    if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Application.this, "Delete " + selectedItem + "?", "Delete", JOptionPane.YES_NO_OPTION)) {
                        try {
                            selectedItem.delete();
                        } catch (SQLException e1) {
                            JOptionPane.showMessageDialog(Application.this, "Failed to delete the selected contact", "Delete", JOptionPane.ERROR_MESSAGE);
                            e1.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    private void initUpdateItemAction() {
        updateItemAction = new AbstractAction("Edit", resourceManager.readImage("Toolbar.EditIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Item selectedItem = itemListPanel.getSelectedItem();
                try {
                    selectedItem = EditItemDialog.showDialog(Application.this, selectedItem);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                if (selectedItem != null) {
                    System.out.println(selectedItem.getName() + " will be updated.");
                    try {
                        selectedItem.save();
                    } catch (SQLException e2) {
                        JOptionPane.showMessageDialog(Application.this, "Error saving Item: "+ e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
    }

    private void initAddItemAction() {
        addItemAction = new AbstractAction("Add", resourceManager.readImage("Toolbar.AddIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Item newItem = null;
                try {
                    newItem = EditItemDialog.showDialog(Application.this);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                if (newItem != null) {
                    System.out.println(newItem.getName() + " will be added.");
                    try {
                        newItem.save();
                    } catch (SQLException e2) {
                        JOptionPane.showMessageDialog(Application.this, "Error saving Item: "+ e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
    }

    private void initRefreshItemsAction() {
        refreshItemsAction = new AbstractAction("Refresh", resourceManager.readImage("Toolbar.RefreshIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
    }

}
