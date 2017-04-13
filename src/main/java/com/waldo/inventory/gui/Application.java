package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.panels.mainpanel.ItemListPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class Application extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private ResourceManager resourceManager;

    private ItemListPanel itemListPanel;
    private TopToolBar toolBar;

    public Application() {
        URL url = Application.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());
        initComponents();
    }

    private void initComponents() {
        // Toolbar
        toolBar = new TopToolBar(this);
        add(toolBar, BorderLayout.PAGE_START);

        // Menu
        setJMenuBar(new MenuBar(this));

        // Main view
        itemListPanel = new ItemListPanel(this);
        add(itemListPanel, BorderLayout.CENTER);

        //add(createTablePane(), BorderLayout.CENTER); // TODO: to ItemListPanel
        //add(new QueryPanel(this), BorderLayout.SOUTH);
    }

    public Item getSelectedItem() {
        return itemListPanel.getSelectedItem();
    }

    public void setTableItems(java.util.List<Item> tableItems) {
        if (tableItems == null) {
            try {
                itemListPanel.updateTable(itemListPanel.getLastSelectedDivision());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            itemListPanel.getTableModel().setItemList(tableItems);
        }
    }

    public void clearSearch() {
        toolBar.clearSearch();
    }

}
