package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.panels.mainpanel.MainPanel;
import com.waldo.inventory.gui.panels.orderpanel.OrderPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class Application extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private ResourceManager resourceManager;

    private JTabbedPane tabbedPane;

    private MainPanel mainPanel;
    private OrderPanel orderPanel;
    private TopToolBar toolBar;

    public Application() {
        // Status
        Status().init();

        // Initialize dB
        db().init();
        db().registerShutDownHook();

        // Resource manager
        URL url = Application.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());

        // Components
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        // Status bar
        add(Status(), BorderLayout.PAGE_END);
        Status().setMessage("Initializing");

        // Toolbar
        toolBar = new TopToolBar(this);
        add(toolBar, BorderLayout.PAGE_START);
        // Menu
        setJMenuBar(new MenuBar(this));
        // Main view
        // - Create components
        mainPanel = new MainPanel(this);
        orderPanel = new OrderPanel();
        tabbedPane = new JTabbedPane();
        //  - Add tabs
        tabbedPane.addTab("Components", resourceManager.readImage("EditItem.InfoIcon"), mainPanel, "Components");
        tabbedPane.addTab("Orders", resourceManager.readImage("EditItem.OrderIcon"), orderPanel, "Orders");
        // - Add to main view
        add(tabbedPane, BorderLayout.CENTER);

        Status().setMessage("Ready");
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
