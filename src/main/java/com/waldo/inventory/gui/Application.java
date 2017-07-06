package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.parser.KiCad.KiCadParser;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.LogManager;
import com.waldo.inventory.gui.dialogs.settingsdialog.SettingsDialog;
import com.waldo.inventory.gui.panels.mainpanel.MainPanel;
import com.waldo.inventory.gui.panels.orderpanel.OrderPanel;
import com.waldo.inventory.gui.panels.projectpanel.ProjectPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class Application extends JFrame implements ChangeListener {

    private static final LogManager LOG = LogManager.LOG(Application.class);
    public static final int TAB_ITEMS = 0;
    public static final int TAB_ORDERS = 1;
    public static final int TAB_PROJECTS = 2;

    public static String startUpPath;
    public static ResourceManager imageResource;
    public static ResourceManager stringResource;
    public static ResourceManager scriptResource;

    private JTabbedPane tabbedPane;

    private MainPanel mainPanel;
    private OrderPanel orderPanel;
    private ProjectPanel projectPanel;

    private boolean updating = false;
    private String dbFileName = "";

    public Application(String startUpPath) {
        Application.startUpPath = startUpPath;
        // Status
        Status().init();

        // Resource manager
        try {
            imageResource = new ResourceManager("settings/", "IconSettings.properties");
            stringResource = new ResourceManager("settings/", "Strings.properties");
            scriptResource = new ResourceManager("db/scripts/", "scripts.properties");
        } catch (Exception e) {
            LOG.error("Error initializing resources.", e);
            //System.exit(-1);
        }

        // Initialize dB
        try {
            if (settings().init()) {
                LOG.info("Reading settings successful!!");
                db().init();
            } else {
                // TODO: cry a little
            }

        } catch (Exception e){
            LOG.error("Error initialising db", e);
            SettingsDialog dialog = new SettingsDialog(this, "Settings");
            dialog.showDialog(); // TODO better dialog
        }
        settings().registerShutDownHook();
        db().registerShutDownHook();



        /******* TEST *******/
        KiCadParser parser = new KiCadParser();
        File file = new File("/home/waldo/Dropbox/elentrik/projects/pcbs/Supply/variable/digital/SupplyMainDigital.net");
        parser.parse(file);

        /******* TEST *******/

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
        projectPanel = new ProjectPanel(this);

        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(this);
        //  - Add tabs
        tabbedPane.addTab("Components", imageResource.readImage("EditItem.InfoIcon"), mainPanel, "Components");
        tabbedPane.addTab("Orders", imageResource.readImage("EditItem.OrderIcon"), orderPanel, "Orders");
        tabbedPane.addTab("Projects", imageResource.readImage("EditItem.ProjectIcon"), projectPanel, "Projects");
        // - Add to main view
        add(tabbedPane, BorderLayout.CENTER);

        Status().setMessage("Ready");
        //add(createTablePane(), BorderLayout.CENTER); // TODO: to MainPanel
        //add(new QueryPanel(this), BorderLayout.SOUTH);
    }

    public Item getSelectedItem() {
        return mainPanel.getSelectedItem();
    }

    public OrderItem getSelectedOrderItem() {
        return orderPanel.getSelectedOrderItem();
    }

    public Project getSelectedProject() {
        return projectPanel.getSelectedProject();
    }

    public void setSelectedItem(Item selectedItem) {
        mainPanel.selectItem(selectedItem);
    }

    public void setSelectedOrderItem(OrderItem selectedOrderItem) {
        orderPanel.selectOrderItem(selectedOrderItem);
    }

    public void setSelectedProject(Project selectedProject) {
        projectPanel.selectProject(selectedProject);
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
                    orderPanel.getTableModel().setOrderItemList(foundItems);
                }
                break;

            case TAB_PROJECTS:
                if (foundObject == null) {
                    // TODO: update project panel
                } else {
                    List<Project> foundProjects = new ArrayList<>(foundObject.size());
                    for (DbObject object : foundObject) {
                        foundProjects.add((Project)object);
                    }
                    // TODO: update project panel
                }

            default:
                break;
        }
    }

    public void clearSearch() {
        switch (tabbedPane.getSelectedIndex()) {
            case TAB_ITEMS:
                mainPanel.getToolBar().clearSearch();
                break;
            case TAB_ORDERS:
                orderPanel.getToolBar().clearSearch();
                break;
            case TAB_PROJECTS:
                projectPanel.getToolBar().clearSearch();
                break;
        }

    }

    public void addItemToOrder(Item item, Order order) {
        try {
            beginWait();
            // Set tab
            tabbedPane.setSelectedIndex(TAB_ORDERS);

            // Update item
            item.setOrderState(Statics.ItemOrderStates.PLANNED);
            item.save();

            // Add
            orderPanel.addItemToOrder(item, order);
        } finally {
            endWait();
        }
    }

    public void addItemsToOrder(List<Item> itemsToOrder, Order order) {
        try {
            beginWait();
            // Set tab
            tabbedPane.setSelectedIndex(TAB_ORDERS);

            // Update items
            for (Item item : itemsToOrder) {
                item.setOrderState(Statics.ItemOrderStates.PLANNED);
                item.save();
            }

            // Add
            orderPanel.addItemsToOrder(itemsToOrder, order);
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

    public int getSelectedTab() {
        return tabbedPane.getSelectedIndex();
    }

    public OrderPanel getOrderPanel() {
        return orderPanel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        ((GuiInterface)tabbedPane.getSelectedComponent()).updateComponents(null);
    }
}
