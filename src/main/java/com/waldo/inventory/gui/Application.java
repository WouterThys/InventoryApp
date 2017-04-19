package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.gui.panels.mainpanel.MainPanel;
import com.waldo.inventory.gui.panels.orderpanel.OrderPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.net.URL;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class Application extends JFrame implements ChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    public static final int TAB_ITEMS = 0;
    public static final int TAB_ORDERS = 1;

    private ResourceManager resourceManager;

    public JTabbedPane tabbedPane;

    private MainPanel mainPanel;
    private OrderPanel orderPanel;

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

        // Toolbars
//        toolBar = new TopToolBar(this);
//        add(toolBar, BorderLayout.PAGE_START);

        // Menu
        setJMenuBar(new MenuBar(this));

        // Main view
        // - Create components
        mainPanel = new MainPanel(this);
        orderPanel = new OrderPanel(this);
        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(this);
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
        mainPanel.getToolBar().clearSearch();
    }

    public void addItemToOrder(Item item, Order order) {
        try {
            beginWait();
            // Set tab
            tabbedPane.setSelectedIndex(TAB_ORDERS);

            // Add
            orderPanel.addItemToOrder(item, order);
        } finally {
            endWait();
        }
    }

    public void beginWait() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void endWait() {
        this.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        ((GuiInterface)tabbedPane.getSelectedComponent()).updateComponents(null);
    }
}
