package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.panels.mainpanel.MainPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;

public class Application extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private ResourceManager resourceManager;

    private MainPanel mainPanel;
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
        mainPanel = new MainPanel(this);
        add(mainPanel, BorderLayout.CENTER);

        // Status bar
        add(new JLabel("Status"), BorderLayout.PAGE_END);

        //add(createTablePane(), BorderLayout.CENTER); // TODO: to MainPanel
        //add(new QueryPanel(this), BorderLayout.SOUTH);
    }

    public Item getSelectedItem() {
        return mainPanel.getSelectedItem();
    }

    public void setTableItems(java.util.List<Item> tableItems) {
        if (tableItems == null) {
            mainPanel.updateTable(mainPanel.getLastSelectedDivision());
        } else {
            mainPanel.getTableModel().setItemList(tableItems);
        }
    }

    public void clearSearch() {
        toolBar.clearSearch();
    }

}
