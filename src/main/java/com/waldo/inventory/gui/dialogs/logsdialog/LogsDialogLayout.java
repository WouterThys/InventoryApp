package com.waldo.inventory.gui.dialogs.logsdialog;


import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.ObjectLog;
import com.waldo.inventory.classes.dbclasses.Log;
import com.waldo.inventory.database.settings.settingsclasses.LogSettings;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.tablemodels.ICacheLogTableModel;
import com.waldo.inventory.gui.components.tablemodels.ISystemLogTableModel;
import com.waldo.inventory.managers.CacheManager;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.ICheckBox;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.SearchManager.sm;

abstract class LogsDialogLayout extends IDialog implements
        ItemListener,
        ListSelectionListener,
        ActionListener {

    private static final long KILOBYTE = 1024L;

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    //
    private JTabbedPane logTabsPnl;

    // System logs
    ICheckBox showInfoCb;
    ICheckBox showDebugCb;
    ICheckBox showWarnCb;
    ICheckBox showErrorCb;

    private JButton clearLogsBtn;
    private SystemLogsDetailPanel systemDetailPanel;
    private ISystemLogTableModel systemLogTableModel;
    ITable<Log> systemLogTable;

    // Cache logs
    private ILabel initTimeLbl;
    private ILabel freeMemoryLbl;
    private ILabel maxMemoryLbl;
    private ILabel totalMemoryLbl;
    private ICacheLogTableModel cacheLogTableModel;
    private ITable<ObjectLog> cacheLogTable;
    private ObjectLogsDetailPanel objectLogsDetailPanel;
    private AbstractAction clearCacheListAa;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LogsDialogLayout(Application application, String title) {
        super(application, title);
        setResizable(true);
    }


    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateSystemLogTable(boolean showInfo, boolean showDebug, boolean showWarn, boolean showError) {
        List<Log> logList = sm().getLogsByType(showInfo, showDebug, showWarn, showError);
        logList.sort(new Log.LogComparator());

        systemLogTableModel.setItemList(logList);
    }

    void updateCacheLogTable() {
        List<ObjectLog> logList = CacheManager.cache().getObjectLogList();
        cacheLogTableModel.setItemList(logList);
    }

    private JPanel createCheckboxPanel() {
        JPanel cbPanel = new JPanel(new GridBagLayout());

        showInfoCb.setAlignmentX(CENTER_ALIGNMENT);
        showDebugCb.setAlignmentX(CENTER_ALIGNMENT);
        showWarnCb.setAlignmentX(CENTER_ALIGNMENT);
        showErrorCb.setAlignmentX(CENTER_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 1;
        cbPanel.add(showInfoCb, gbc);

        gbc.gridx = 1;
        cbPanel.add(showDebugCb, gbc);

        gbc.gridx = 2;
        cbPanel.add(showWarnCb, gbc);

        gbc.gridx = 3;
        cbPanel.add(showErrorCb, gbc);

        gbc.gridx = 4;
        cbPanel.add(clearLogsBtn, gbc);

        cbPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray, 1),
                BorderFactory.createEmptyBorder(5,10,5,10)
        ));

        return cbPanel;
    }

    private void initializeSystemLogComponents() {
        // Checkboxes
        showDebugCb = new ICheckBox("Show debug", true);
        showDebugCb.addItemListener(this);
        showInfoCb = new ICheckBox("Show info", true);
        showInfoCb.addItemListener(this);
        showWarnCb = new ICheckBox("Show warnings", true);
        showWarnCb.addItemListener(this);
        showErrorCb = new ICheckBox("Show errors", true);
        showErrorCb.addItemListener(this);

        LogSettings s = settings().getLogSettings();
        if (!s.isLogDebug()) {
            showDebugCb.setForeground(Color.RED);
            showDebugCb.setToolTipText("Debug log is disabled in settings..");
        }
        if (!s.isLogInfo()) {
            showInfoCb.setForeground(Color.RED);
            showInfoCb.setToolTipText("Info log is disabled in settings..");
        }
        if (!s.isLogWarn()) {
            showWarnCb.setForeground(Color.RED);
            showWarnCb.setToolTipText("Warnings are disabled in settings..");
        }
        if (!s.isLogError()) {
            showErrorCb.setForeground(Color.RED);
            showErrorCb.setToolTipText("Errors are disabled in settings..");
        }

        clearLogsBtn = new JButton("Clear All");
        clearLogsBtn.addActionListener(this);

        // Table
        systemLogTableModel = new ISystemLogTableModel();
        systemLogTable = new ITable<>(systemLogTableModel);
        systemLogTable.getSelectionModel().addListSelectionListener(this);
        systemLogTable.setRowHeight(32);
        systemLogTable.setExactColumnWidth(0, 32); // Icon
        systemLogTable.setExactColumnWidth(1, 100); // Time
        systemLogTable.setExactColumnWidth(2, 100); // Class
        systemLogTable.setExactColumnWidth(3, 300); // Message
        systemLogTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        systemLogTable.setOpaque(true);

        // Details
        systemDetailPanel = new SystemLogsDetailPanel();
    }

    private void initializeCacheLogComponents() {
        // Table
        cacheLogTableModel = new ICacheLogTableModel(new ComparatorUtils.CacheLogComparator());
        cacheLogTable = new ITable<>(cacheLogTableModel);
        cacheLogTable.setExactColumnWidth(1, 60); // Size column
        cacheLogTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                objectLogsDetailPanel.updateComponents(cacheLogTable.getSelectedItem());
            }
        });

        initTimeLbl = new ILabel(DateUtils.formatDateTime(CacheManager.cache().getInitTime()));
        Runtime r = Runtime.getRuntime();

        maxMemoryLbl = new ILabel(String.format("%,8d%n", (r.maxMemory() / KILOBYTE)));
        freeMemoryLbl = new ILabel(String.format("%,8d%n", (r.freeMemory() / KILOBYTE)));
        totalMemoryLbl = new ILabel(String.format("%,8d%n", (r.totalMemory() / KILOBYTE)));

        clearCacheListAa = new AbstractAction("Clear cache", imageResource.readIcon("Cache.Clear")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                ObjectLog log = cacheLogTable.getSelectedItem();
                if (log != null) {
                    log.getCacheList().clear();
                    cacheLogTableModel.updateTable();
                }
            }
        };

        objectLogsDetailPanel = new ObjectLogsDetailPanel();

    }

    private JPanel createCacheHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));

        JPanel timePnl = new JPanel();
        timePnl.add(new ILabel("Initialisation: "));
        timePnl.add(initTimeLbl);

        JPanel memoryPnl = new JPanel();
        memoryPnl.setBorder(BorderFactory.createEmptyBorder(1,20,1,20));
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(memoryPnl);
        gbc.addLine("Total memory (kb): ", totalMemoryLbl);
        gbc.addLine("Free memory (kb): ", freeMemoryLbl);
        gbc.addLine("Max memory (kb): ", maxMemoryLbl);

        JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
        toolbar.setFloatable(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        toolbar.add(clearCacheListAa);

        panel.add(timePnl, BorderLayout.WEST);
        panel.add(memoryPnl, BorderLayout.CENTER);
        panel.add(toolbar, BorderLayout.EAST);

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title
        setTitleIcon(imageResource.readIcon("Log.Title"));
        setTitleName(getTitle());

        // This
        logTabsPnl = new JTabbedPane();

        // System
        initializeSystemLogComponents();

        // Cache
        initializeCacheLogComponents();
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel systemLogPnl = new JPanel(new BorderLayout());
        JPanel cacheLogPnl = new JPanel(new BorderLayout());

        // System
        JScrollPane systemLogPane = new JScrollPane(systemLogTable);
        systemLogPane.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        JPanel systemCbPanel = new JPanel(new BorderLayout());
        systemCbPanel.add(systemDetailPanel, BorderLayout.CENTER);
        systemCbPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        systemLogPnl.add(createCheckboxPanel(), BorderLayout.BEFORE_FIRST_LINE);
        systemLogPnl.add(systemLogPane, BorderLayout.CENTER);
        systemLogPnl.add(systemCbPanel, BorderLayout.EAST);

        // Cache
        JScrollPane cacheLogPane = new JScrollPane(cacheLogTable);
        cacheLogPane.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        cacheLogPnl.add(createCacheHeaderPanel(), BorderLayout.PAGE_START);
        cacheLogPnl.add(cacheLogPane, BorderLayout.CENTER);
        cacheLogPnl.add(objectLogsDetailPanel, BorderLayout.EAST);


        // Bring it all together
        logTabsPnl.addTab("System", systemLogPnl);
        logTabsPnl.addTab("Cache", cacheLogPnl);

        getContentPanel().add(logTabsPnl);

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        beginWait();
        try {

            Log selectedLog;
            if (object.length != 0 && object[0] != null) {
                selectedLog = (Log) object[0];
            } else {
                selectedLog = null;
            }

            // Details
            systemDetailPanel.updateComponents(selectedLog);
        } finally {
            endWait();
        }
    }
}
