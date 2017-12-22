package com.waldo.inventory.gui;

import com.mysql.jdbc.MysqlErrorNumbers;
import com.waldo.inventory.Main;
import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.database.interfaces.DbErrorListener;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.SelectDataSheetDialog;
import com.waldo.inventory.gui.dialogs.historydialog.HistoryDialog;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;
import com.waldo.inventory.gui.dialogs.settingsdialog.SettingsDialog;
import com.waldo.inventory.gui.panels.mainpanel.MainPanel;
import com.waldo.inventory.gui.panels.orderpanel.OrderPanel;
import com.waldo.inventory.gui.panels.projectspanel.ProjectsPanel;
import com.waldo.inventory.managers.ErrorManager;
import com.waldo.inventory.managers.LogManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class Application extends JFrame implements ChangeListener, DbErrorListener {

    private static final LogManager LOG = LogManager.LOG(Application.class);
    static final int TAB_ITEMS = 0;
    static final int TAB_ORDERS = 1;
    private static final int TAB_PROJECTS = 2;

    public static String startUpPath;
    public static ResourceManager imageResource;
    public static ResourceManager stringResource;
    public static ResourceManager scriptResource;
    public static ResourceManager colorResource;

    private JTabbedPane tabbedPane;

    private MainPanel mainPanel;
    private OrderPanel orderPanel;
    private ProjectsPanel projectPanel;

    private boolean updating = false;

    public Application(String startUpPath) {
        Application.startUpPath = startUpPath;
        // Status
        Status().init();
        boolean result;

        // Resource manager
        try {
            imageResource = new ResourceManager("settings/", "IconSettings.properties");
            stringResource = new ResourceManager("settings/", "Strings.properties");
            scriptResource = new ResourceManager("db/scripts/", "scripts.properties");
            colorResource = new ResourceManager("settings/", "Colors.properties");
        } catch (Exception e) {
            LOG.error("Error initializing resources.", e);
        }

        // Cache only
        if (Main.CACHE_ONLY) {
            JOptionPane.showMessageDialog(this,
                    "Running on cache only..",
                    "Cache only",
                    JOptionPane.WARNING_MESSAGE);
        }

        // Initialize dB
        result = initDatabases();

        if (!result) {
            SettingsDialog dialog = new SettingsDialog(this, "Settings");
            if (dialog.showDialog() == IDialog.OK) {
                // Try again
                if (initDatabases()) {
                    initComponents();
                } else {
                    System.exit(-1);
                }
            } else {
                System.exit(-1);
            }
        } else {
            initComponents();
        }
    }

    private boolean initDatabases() {
        boolean result = false;
        try {
            if (settings().init()) {
                LOG.info("Reading settings successful!!");
                DatabaseAccess.db().init();

                settings().registerShutDownHook();
                DatabaseAccess.db().startBackgroundWorkers();
                DatabaseAccess.db().registerShutDownHook();
                DatabaseAccess.db().addErrorListener(this);

                result = true;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error initializing database",
                        "Initialize error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Initialize error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        // Status bar
        add(Status(), BorderLayout.PAGE_END);
        Status().setMessage("Initializing");

        // Icon
        try {
            Image image = imageResource.readImage("Main.Icon").getImage();
            setIconImage(image);
        } catch (Exception e) {
            Status().setError("Error setting application image", e);
        }

        // Menu
        setJMenuBar(new MenuBar(this));

        // Main view
        // - Create components
        mainPanel = new MainPanel(this);
        orderPanel = new OrderPanel(this);
        projectPanel = new ProjectsPanel(this);

        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(this);
        //  - Add tabs
        tabbedPane.addTab("Components ", imageResource.readImage("MainTab.Components"), mainPanel, "Components");
        tabbedPane.addTab("Orders ", imageResource.readImage("MainTab.Orders"), orderPanel, "Orders");
        tabbedPane.addTab("Projects ", imageResource.readImage("MainTab.Projects"), projectPanel, "Projects");
        // - Add to main view
        add(tabbedPane, BorderLayout.CENTER);

        Status().setMessage("Ready");
    }

    public Item getSelectedItem() {
        return mainPanel.getSelectedItem();
    }

    OrderItem getSelectedOrderItem() {
        return orderPanel.getSelectedOrderItem();
    }

    public void setSelectedItem(Item selectedItem) {
        mainPanel.tableSelectItem(selectedItem);
    }

    void setSelectedOrderItem(OrderItem selectedOrderItem) {
        orderPanel.tableSelectOrderItem(selectedOrderItem);
    }

    void setTableItems(java.util.List<DbObject> foundObject) {
        switch (tabbedPane.getSelectedIndex()) {
            case TAB_ITEMS:
                if (foundObject == null) {
                    mainPanel.tableInitialize(mainPanel.getLastSelectedDivision());
                } else {
                    java.util.List<Item> foundItems = new ArrayList<>(foundObject.size());
                    for (DbObject object : foundObject) {
                        foundItems.add((Item) object);
                    }
                    mainPanel.getTableModel().setItemList(foundItems);
                }
                break;

            case TAB_ORDERS:
                if (foundObject == null) {
                    orderPanel.tableInitialize(orderPanel.getSelectedOrder());
                } else {
                    java.util.List<OrderItem> foundItems = new ArrayList<>(foundObject.size());
                    for (DbObject object : foundObject) {
                        foundItems.add((OrderItem) object);
                    }
                    orderPanel.getTableModel().setItemList(foundItems);
                }
                break;

            case TAB_PROJECTS:
                if (foundObject == null) {
                    // TODO: update project panel
                } else {
                    List<Project> foundProjects = new ArrayList<>(foundObject.size());
                    for (DbObject object : foundObject) {
                        foundProjects.add((Project) object);
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
                //mainPanel.getToolBar().clearSearch();
                break;
            case TAB_ORDERS:
                //orderPanel.getToolBar().clearSearch();
                break;
            case TAB_PROJECTS:
                projectPanel.getToolBar().clearSearch();
                break;
        }

    }

    public void addItemsToOrder(List<Item> itemsToOrder, Order order) {
        beginWait();
        try {
            // Switch tab
            setSelectedTab(TAB_ORDERS);
            // Update items
            for (Item item : itemsToOrder) {
                item.setOrderState(Statics.ItemOrderStates.PLANNED);
                item.save();
            }
        } finally {
            endWait();
        }
        // Add
        Map<String, Item> failedItems = orderPanel.addItemsToOrder(itemsToOrder, order);
        if (failedItems != null && failedItems.size() > 0) {
            // TODO Show error message
        }
    }

    public void addOrderItemsToOrder(List<OrderItem> itemsToOrder, Order order) {
        beginWait();
        try {
            // Switch tab
            setSelectedTab(TAB_ORDERS);
            // Update items
            for (OrderItem orderItem : itemsToOrder) {
                orderItem.getItem().setOrderState(Statics.ItemOrderStates.PLANNED);
                orderItem.getItem().save();
            }
        } finally {
            endWait();
        }
        // Add
        Map<String, Item> failedItems = orderPanel.addOrderItemsToOrder(itemsToOrder, order);
        if (failedItems != null && failedItems.size() > 0) {
            // TODO Show error message
        }
    }

    public boolean isUpdating() {
        return updating;
    }

    public void beginWait() {
        beginWait(this);
    }

    public void beginWait(Component component) {
        updating = true;
        component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void endWait() {
        endWait(this);
    }

    public void endWait(Component component) {
        component.setCursor(Cursor.getDefaultCursor());
        updating = false;
    }

    private void setSelectedTab(int tab) {
        tabbedPane.setSelectedIndex(tab);
    }

    int getSelectedTab() {
        return tabbedPane.getSelectedIndex();
    }


    @Override
    public void stateChanged(ChangeEvent e) {
        ((GuiInterface) tabbedPane.getSelectedComponent()).updateComponents();
    }

    //
    // Db errors
    //
    @Override
    public void onSelectError(DbObject object, Throwable throwable, String sql) {
        showErrorMessage(object, throwable, "Select");
    }

    @Override
    public void onInsertError(DbObject object, Throwable throwable, String sql) {
        if (!ErrorManager.em().handle(object, throwable, sql)) {
            showErrorMessage(object, throwable, "Insert");
        }
    }

    @Override
    public void onUpdateError(DbObject object, Throwable throwable, String sql) {
        if (!ErrorManager.em().handle(object, throwable, sql)) {
            showErrorMessage(object, throwable, "Update");
        }
    }

    @Override
    public void onDeleteError(DbObject object, Throwable throwable, String sql) {
        if (!ErrorManager.em().handle(object, throwable, sql)) {
            showErrorMessage(object, throwable, "Delete");
        }
    }

    private void showErrorMessage(DbObject object, Throwable throwable, String error) {
        final String message;
        final String title;

            if (throwable != null) {
                message = throwable.getMessage();
            } else {
                message = error + " error";
            }
            if (object != null) {
                title = error + " error on " + object.getName();
            } else {
                title = error + " error";
            }

        if (throwable instanceof SQLException) {
            SQLException exception = (SQLException) throwable;
            if (exception.getErrorCode() != MysqlErrorNumbers.ER_TABLEACCESS_DENIED_ERROR) {
                try {
                    LOG.error(title, throwable);
                } catch (Exception e) {
                    System.err.println("Error while logging");
                }
            }
        } else {
            try {
                LOG.error(title, throwable);
            } catch (Exception e) {
                System.err.println("Error while logging");
            }
        }

        SwingUtilities.invokeLater(() -> {
                try {
                    JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    System.err.println("Error while logging");
                }

        });
    }

    //
    // Shared functions
    //
    public void openDataSheet(Item item) {
        if (item != null) {
            String local = item.getLocalDataSheet();
            String online = item.getOnlineDataSheet();
            if (local != null && !local.isEmpty() && online != null && !online.isEmpty()) {
                SelectDataSheetDialog.showDialog(this, online, local);
            } else if (local != null && !local.isEmpty()) {
                try {
                    OpenUtils.openPdf(local);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error opening the file: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else if (online != null && !online.isEmpty()) {
                try {
                    OpenUtils.browseLink(online);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(this,
                            "Error opening the file: " + e1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        }
    }

    public void openDataSheet(Item item, boolean online) {
        if (item != null) {
            if (online) {
                String onlineDs = item.getOnlineDataSheet();
                if (!onlineDs.isEmpty()) {
                    try {
                        OpenUtils.browseLink(onlineDs);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(this,
                                "Error opening the file: " + e1.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        e1.printStackTrace();
                    }
                }
            } else {
                String local = item.getLocalDataSheet();
                if (local != null && !local.isEmpty()) {
                    try {
                        OpenUtils.openPdf(local);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Error opening the file: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void orderItem(Item item) {
        int result = JOptionPane.YES_OPTION;
        if (item.isDiscourageOrder()) {
            result = JOptionPane.showConfirmDialog(
                    this,
                    "This item is marked to discourage new orders, \n do you really want to order it?",
                    "Discouraged to order",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
        }
        if (result == JOptionPane.YES_OPTION) {
            OrderItemDialog dialog = new OrderItemDialog(this, "Order " + item.getName(), item, true);
            dialog.showDialog();
        }
    }

    public void showHistory(Item item) {
        HistoryDialog dialog = new HistoryDialog(this, item);
        dialog.showDialog();
    }
}
