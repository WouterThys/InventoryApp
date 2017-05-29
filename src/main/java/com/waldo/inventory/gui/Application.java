package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.gui.panels.mainpanel.MainPanel;
import com.waldo.inventory.gui.panels.orderpanel.OrderPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.net.URL;
import java.util.*;

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

    private boolean updating = false;

    public Application() {
        // Status
        Status().init();

        // Initialize dB
        db().init();
        db().registerShutDownHook();

        // Resource manager
        URL url = Application.class.getResource("/settings/IconSettings.properties");
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

    public void setTableItems(java.util.List<DbObject> foundObject) {

        switch (tabbedPane.getSelectedIndex()) {
            case TAB_ITEMS:
                if (foundObject == null) {
                    mainPanel.updateTable(mainPanel.getLastSelectedDivision());
                } else {
                    java.util.List<Item> foundItems = new ArrayList<>(foundObject.size());
                    for (DbObject object : foundObject) {
                        foundItems.add((Item)object);
                    }
                    mainPanel.getTableModel().setItemList(foundItems);
                }
                break;

            case TAB_ORDERS:
                if (foundObject == null) {
                    orderPanel.updateTable(orderPanel.getLastSelectedOrder());
                } else {
                    java.util.List<OrderItem> foundItems = new ArrayList<>(foundObject.size());
                    for (DbObject object : foundObject) {
                        foundItems.add((OrderItem)object);
                    }
                    orderPanel.getTableModel().setItemList(foundItems);
                }
                break;

            default:
                break;
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

            // Update item
            item.setOrderState(Statics.ItemOrderState.PLANNED);
            item.save();

            // Add
            orderPanel.addItemToOrder(item, order);
        } finally {
            endWait();
        }
    }

    public boolean isUpdating() {
        return updating;
    }

    public void beginWait() {
        updating = true;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void endWait() {
        this.setCursor(Cursor.getDefaultCursor());
        updating = false;
    }

    public void setSelectedTab(int tab) {
        tabbedPane.setSelectedIndex(tab);
    }

    public OrderPanel getOrderPanel() {
        return orderPanel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        ((GuiInterface)tabbedPane.getSelectedComponent()).updateComponents(null);
    }
}
