package com.waldo.inventory.gui;

import com.mysql.jdbc.MysqlErrorNumbers;
import com.waldo.inventory.Main;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.database.interfaces.DbErrorListener;
import com.waldo.inventory.gui.dialogs.SelectDataSheetDialog;
import com.waldo.inventory.gui.dialogs.addtoorderdialog.AddToOrderDialog;
import com.waldo.inventory.gui.dialogs.historydialog.HistoryDialog;
import com.waldo.inventory.gui.dialogs.settingsdialog.SettingsCacheDialog;
import com.waldo.inventory.gui.panels.mainpanel.MainPanel;
import com.waldo.inventory.gui.panels.orderspanel.OrdersPanel;
import com.waldo.inventory.gui.panels.projectspanel.ProjectsPanel;
import com.waldo.inventory.managers.ErrorManager;
import com.waldo.inventory.managers.LogManager;
import com.waldo.utils.OpenUtils;
import com.waldo.utils.ResourceManager;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class Application extends JFrame implements ChangeListener, DbErrorListener {

    private static final LogManager LOG = LogManager.LOG(Application.class);
    public static final int TAB_ITEMS = 0;
    public static final int TAB_ORDERS = 1;
    private static final int TAB_PROJECTS = 2;

    public static String startUpPath;
    public static ImageResource imageResource;
    public static ResourceManager scriptResource;
    public static ResourceManager colorResource;

    private JTabbedPane tabbedPane;

    public Application(String startUpPath) {
        Application.startUpPath = startUpPath;
        // Status
        Status().init();
        boolean result;

        // Resource manager
        try {
            imageResource = ImageResource.getInstance();
            imageResource.initIcons("settings/", "Icons.properties");
            scriptResource = new ResourceManager("settings/", "Scripts.properties");
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
            SettingsCacheDialog dialog = new SettingsCacheDialog(this, "Settings", true);
            if (dialog.showDialog() == IDialog.OK) {
                // Try again
                if (!initDatabases()) {
                    System.exit(-1);
                }
            } else {
                System.exit(-1);
            }
        } else {
            try {
                imageResource.initServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public void initComponents() {
        setLayout(new BorderLayout());
        // Status bar
        add(Status(), BorderLayout.PAGE_END);
        Status().setMessage("Initializing");

        // Icon
        try {
            Image image = imageResource.readIcon("Main.Icon").getImage();
            setIconImage(image);
        } catch (Exception e) {
            Status().setError("Error setting application image", e);
        }

        // Menu
        setJMenuBar(new MenuBar(this));

        // Main view
        // - Create components
        MainPanel mainPanel = new MainPanel(this);
        OrdersPanel ordersPanel = new OrdersPanel(this);
        ProjectsPanel projectPanel = new ProjectsPanel(this);

        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(this);
        //  - Add tabs
        tabbedPane.addTab("Components ", imageResource.readIcon("Tag.S"), mainPanel, "Components");
        tabbedPane.addTab("Orders ", imageResource.readIcon("Order.S"), ordersPanel, "Orders");
        tabbedPane.addTab("Projects ", imageResource.readIcon("BluePrint.S"), projectPanel, "Projects");
        // - Add to main view
        add(tabbedPane, BorderLayout.CENTER);

        Status().setMessage("Ready");
        Status().updateConnectionStatus();


        //////////

//        new Thread(() -> {
//
//            Map<String, List<ProjectIDE>> map = new TreeMap<>();
//
//            for (ProjectIDE item : cache().getProjectIDES()) {
//                String path = item.getIconPath();
//                if (path != null && !path.isEmpty()) {
//                    if (!map.containsKey(path)) {
//                        map.put(path, new ArrayList<>());
//                    }
//                    map.get(path).add(item);
//                }
//            }
//
//
//            for (String path : map.keySet()) {
//                if (path != null && !path.isEmpty()) {
//                    imageResource.requestImage(new ImageResource.ImageRequester() {
//                        @Override
//                        public ImageType getImageType() {
//                            return ImageType.IdeImage;
//                        }
//
//                        @Override
//                        public String getImageName() {
//                            return path;
//                        }
//
//                        @Override
//                        public void setImage(ImageIcon image) {
//                            DbImage dbImage = new DbImage(ImageType.IdeImage, image, path);
//                            dbImage.setDbObjects(map.get(path));
//                            dbImage.save();
//                        }
//                    });
//                }
//            }
//
//
//        }).start();

    }

    public static boolean isUpdating(Component component) {
        return component.getCursor().getType() == Cursor.WAIT_CURSOR;
    }

    public static void beginWait(Component component) {
        component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public static void endWait(Component component) {
        component.setCursor(Cursor.getDefaultCursor());
    }

    public void setSelectedTab(int tab) {
        tabbedPane.setSelectedIndex(tab);
    }


    @Override
    public void stateChanged(ChangeEvent e) {
        ((GuiUtils.GuiInterface) tabbedPane.getSelectedComponent()).updateComponents();
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
                title = error + " error on " + object.getClass().getSimpleName() + " " + object.getName();
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

        // Check if discouraged
        if (item.isDiscourageOrder()) {
            result = JOptionPane.showConfirmDialog(
                    this,
                    "This item is marked to discourage new orders, \n do you really want to order it?",
                    "Discouraged to order",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
        }

        // Check if item is replaced
        if (item.getReplacementItemId() > DbObject.UNKNOWN_ID) {
            int res = JOptionPane.showConfirmDialog(
                    this,
                    "This item is replaced with item '" + item.getReplacementItem() + "'. Do you want to order the replacement in stead?",
                    "Replaced with item",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (res == JOptionPane.YES_OPTION) {
                item = item.getReplacementItem();
            }
        }

        // ItemOrder
        if (result == JOptionPane.YES_OPTION) {
            AddToOrderDialog<Item> dialog = new AddToOrderDialog<>(this, item, true);
            dialog.showDialog();
        }
    }

    public void orderItems(List<Item> itemList) {

        // Check discourage
        for (Item item : new ArrayList<>(itemList)) {
            if (item.isDiscourageOrder()) {
                int result = JOptionPane.showConfirmDialog(
                        this,
                        item + " is marked to discourage new orders, \n do you really want to order it?",
                        "Discouraged to order",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (result == JOptionPane.NO_OPTION) {
                    itemList.remove(item);
                }
            }
        }

        // Check replacements
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            if (item.getReplacementItemId() > DbObject.UNKNOWN_ID) {
                int res = JOptionPane.showConfirmDialog(
                        this,
                        item + "is replaced with '" + item.getReplacementItem() + "'. Do you want to order the replacement in stead?",
                        "Replaced with item",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (res == JOptionPane.YES_OPTION) {
                    itemList.set(i, item.getReplacementItem());
                }
            }
        }


        // ItemOrder
        if (itemList.size() > 0) {
            AddToOrderDialog<Item> dialog = new AddToOrderDialog<>(this, itemList, true);
            dialog.showDialog();
        }
    }

    public void showHistory(Item item) {
        HistoryDialog dialog = new HistoryDialog(this, item);
        dialog.showDialog();
    }
}
