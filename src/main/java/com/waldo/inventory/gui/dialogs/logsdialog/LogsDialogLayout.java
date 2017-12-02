package com.waldo.inventory.gui.dialogs.logsdialog;


import com.waldo.inventory.classes.dbclasses.Log;
import com.waldo.inventory.database.settings.settingsclasses.LogSettings;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.ILogTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public abstract class LogsDialogLayout extends IDialog implements
        ItemListener,
        ListSelectionListener,
        ActionListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ICheckBox showInfoCb;
    ICheckBox showDebugCb;
    ICheckBox showWarnCb;
    ICheckBox showErrorCb;

    private JButton clearLogsBtn;

    private ILogTableModel logTableModel;
    ITable logTable;

    private LogsDetailPanel detailPanel;

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
    void updateLogTable(boolean showInfo, boolean showDebug, boolean showWarn, boolean showError) {
        List<Log> logList = cache().getLogsByType(showInfo, showDebug, showWarn, showError);
        logList.sort(new Log.LogComparator());

        logTableModel.setItemList(logList);
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

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title
        setTitleIcon(imageResource.readImage("Log.Title"));
        setTitleName("Logs");

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
        logTableModel = new ILogTableModel();
        logTable = new ITable(logTableModel);
        logTable.getSelectionModel().addListSelectionListener(this);
        logTable.setRowHeight(32);
        logTable.getColumnModel().getColumn(0).setMinWidth(32); // Icon
        logTable.getColumnModel().getColumn(0).setMaxWidth(32); // Icon
        logTable.getColumnModel().getColumn(1).setMinWidth(100); // Time
        logTable.getColumnModel().getColumn(1).setMaxWidth(100); // Time
        logTable.getColumnModel().getColumn(2).setMinWidth(100); // Class
        logTable.getColumnModel().getColumn(2).setMaxWidth(100); // Class
        logTable.getColumnModel().getColumn(3).setMinWidth(300); // Message
        logTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        logTable.setDefaultRenderer(ILabel.class, new ITableEditors.LogTypeEditor());
        logTable.setOpaque(true);

        // Details
        detailPanel = new LogsDetailPanel(application);

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());
        JScrollPane pane = new JScrollPane(logTable);
        pane.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(detailPanel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));


        getContentPanel().add(createCheckboxPanel(), BorderLayout.BEFORE_FIRST_LINE);
        getContentPanel().add(pane, BorderLayout.CENTER);
        getContentPanel().add(panel, BorderLayout.EAST);

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        application.beginWait();
        try {

            Log selectedLog;
            if (object.length != 0 && object[0] != null) {
                selectedLog = (Log) object[0];
            } else {
                selectedLog = null;
            }

            // Details
            detailPanel.updateComponents(selectedLog);
        } finally {
            application.endWait();
        }
    }
}
