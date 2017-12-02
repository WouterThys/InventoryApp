package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.managers.LogManager;
import com.waldo.inventory.database.interfaces.DbSettingsListener;
import com.waldo.inventory.database.settings.settingsclasses.LogSettings;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ICheckBox;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.ExecutionException;

import static com.waldo.inventory.database.settings.SettingsManager.settings;

public class LogsPanel extends JPanel implements
        GuiInterface,
        ItemListener,
        IEditedListener,
        IdBToolBar.IdbToolBarListener,
        DbSettingsListener<LogSettings>,
        ActionListener {

    private static final LogManager LOG = LogManager.LOG(LogsPanel.class);

    private IdBToolBar toolBar;
    private ILabel currentSettingLbl;

    private DefaultComboBoxModel<LogSettings> logSettingsCbModel;
    private JComboBox<LogSettings> logSettingsComboBox;

    private ICheckBox logInfoCb;
    private ICheckBox logDebugCb;
    private ICheckBox logWarnCb;
    private ICheckBox logErrorCb;

    private JButton saveBtn;
    private JButton useBtn;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;

    private LogSettings selectedLogSettings;
    private LogSettings originalLogSettings;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public LogsPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();

        settings().addLogSettingsListener(this);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        if (selectedLogSettings == null || selectedLogSettings.isDefault()) {
            toolBar.setDeleteActionEnabled(false);
            toolBar.setEditActionEnabled(false);
            logInfoCb.setEnabled(false);
            logDebugCb.setEnabled(false);
            logWarnCb.setEnabled(false);
            logErrorCb.setEnabled(false);

            saveBtn.setEnabled(false);
        } else {
            toolBar.setDeleteActionEnabled(true);
            logInfoCb.setEnabled(true);
            logDebugCb.setEnabled(true);
            logWarnCb.setEnabled(true);
            logErrorCb.setEnabled(true);

            saveBtn.setEnabled(
                    !selectedLogSettings.isSaved() ||
                    !selectedLogSettings.equals(originalLogSettings));
        }

        if (selectedLogSettings != null) {
            boolean isCurrent = selectedLogSettings.getName().equals(settings().getSelectedLogSettingsName());
            boolean isSaved = selectedLogSettings.isSaved();
            useBtn.setEnabled(!isCurrent && isSaved);
        }
    }

    private void updateFieldValues() {
        if (selectedLogSettings != null) {
            logInfoCb.setSelected(selectedLogSettings.isLogInfo());
            logDebugCb.setSelected(selectedLogSettings.isLogDebug());
            logWarnCb.setSelected(selectedLogSettings.isLogWarn());
            logErrorCb.setSelected(selectedLogSettings.isLogError());

            currentSettingLbl.setText(settings().getSelectedLogSettingsName());
        }
    }

    private void addNewLogSettings() {
        String newName = JOptionPane.showInputDialog(
                LogsPanel.this,
                "New settings name?");

        if (newName != null && !newName.isEmpty()) {
            addNewLogSettings(newName);
        }
    }

    private void addNewLogSettings(String newName) {
        if (settings().getLogSettingsByName(newName) == null) {
            LogSettings logSettings = new LogSettings(newName);
            settings().getLogSettingsList().add(logSettings);
            updateComponents(logSettings);
        } else {
            JOptionPane.showMessageDialog(
                    LogsPanel.this,
                    "Name " + newName + " already exists..",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void saveSettings(LogSettings toSave) {
        new SwingWorker<LogSettings, Object>() {
            @Override
            protected LogSettings doInBackground() throws Exception {
                application.beginWait();
                try {
                    settings().saveSettings(toSave);
                } finally {
                    application.endWait();
                }
                return toSave;
            }

            @Override
            protected void done() {
                try {
                    updateComponents(get());
                } catch (InterruptedException | ExecutionException e) {
                    LOG.error("Error updating components", e);
                }
            }
        }.execute();
    }

    private void useSettings(LogSettings toUse) {
        new SwingWorker<LogSettings, Object>() {
            @Override
            protected LogSettings doInBackground() throws Exception {
                application.beginWait();
                try {
                    settings().selectNewSettings(toUse);
                } finally {
                    application.endWait();
                }
                return settings().getLogSettings();
            }

            @Override
            protected void done() {
                try {
                    updateComponents(get());
                } catch (InterruptedException | ExecutionException e) {
                    LOG.error("Error updating components", e);
                }
            }
        }.execute();
    }

    private void deleteLogSetting(LogSettings toDelete) {
        int res = JOptionPane.showConfirmDialog(LogsPanel.this,
                "Are you sure you want to delete " + toDelete.getName() + "?",
                "Delete log setting",
                JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            new SwingWorker<LogSettings, Object>() {
                @Override
                protected LogSettings doInBackground() throws Exception {
                    application.beginWait();
                    try {
                        settings().deleteSetting(toDelete);
                    } finally {
                        application.endWait();
                    }
                    return settings().getLogSettings();
                }

                @Override
                protected void done() {
                    try {
                        updateComponents(get());
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.error("Error updating components", e);
                    }
                }
            }.execute();
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Label
        currentSettingLbl = new ILabel();
        currentSettingLbl.setAlignmentX(CENTER_ALIGNMENT);
        currentSettingLbl.setForeground(Color.gray);
        Font f = currentSettingLbl.getFont();
        Font newFont = new Font(f.getName(), Font.BOLD, f.getSize() + 3);
        currentSettingLbl.setFont(newFont);

        // Combo box
        logSettingsCbModel = new DefaultComboBoxModel<>();
        logSettingsComboBox = new JComboBox<>(logSettingsCbModel);
        logSettingsComboBox.setAlignmentX(RIGHT_ALIGNMENT);
        logSettingsComboBox.addItemListener(this);
        logSettingsComboBox.setPreferredSize(new Dimension(120, 30));

        // Check boxes
        logInfoCb = new ICheckBox("Log info");
        logInfoCb.addEditedListener(this, "logInfo");
        logDebugCb = new ICheckBox("Log debug");
        logDebugCb.addEditedListener(this, "logDebug");
        logWarnCb = new ICheckBox("Log warnings");
        logWarnCb.addEditedListener(this, "logWarn");
        logErrorCb = new ICheckBox("Log errors");
        logErrorCb.addEditedListener(this, "logError");

        // Buttons
        saveBtn = new JButton("Save");
        saveBtn.setEnabled(false);
        saveBtn.setAlignmentX(RIGHT_ALIGNMENT);
        saveBtn.addActionListener(this);

        useBtn = new JButton("Use this");
        useBtn.setEnabled(false);
        useBtn.setAlignmentX(LEFT_ALIGNMENT);
        useBtn.addActionListener(this);

        // Toolbar
        toolBar = new IdBToolBar(this);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(saveBtn);
        buttonsPanel.add(useBtn);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        JPanel currentPanel = new JPanel(new BorderLayout());
        currentPanel.add(new ILabel("Current log setting: "), BorderLayout.NORTH);
        currentPanel.add(currentSettingLbl, BorderLayout.CENTER);
        currentPanel.setBorder(BorderFactory.createEmptyBorder(2,15,2,15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(logSettingsComboBox, BorderLayout.WEST);
        headerPanel.add(currentPanel, BorderLayout.CENTER);
        headerPanel.add(toolBar, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        // - Add to panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        settingsPanel.add(logInfoCb, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        settingsPanel.add(logDebugCb, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        settingsPanel.add(logWarnCb, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        settingsPanel.add(logErrorCb, gbc);

        TitledBorder titledBorder = BorderFactory.createTitledBorder("Log options");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));

        // Add to panel
        add(headerPanel, BorderLayout.NORTH);
        add(settingsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    @Override
    public void updateComponents(Object... object) {
        application.beginWait();
        try {
            logSettingsCbModel.removeAllElements();
            for (LogSettings settings : settings().getLogSettingsList()) {
                logSettingsCbModel.addElement(settings);
            }

            selectedLogSettings = (LogSettings) object[0];

            if (selectedLogSettings != null) {
                logSettingsComboBox.setSelectedItem(selectedLogSettings);
                originalLogSettings = selectedLogSettings.createCopy();
                updateFieldValues();
            } else {
                originalLogSettings = null;
            }
            updateEnabledComponents();
        } finally {
            application.endWait();
        }

    }

    //
    // Settings combo box value changed
    //
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (!application.isUpdating()) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object o = e.getItem();
                if (o instanceof LogSettings) {
                    updateComponents(o);
                }
            }
        }
    }

    //
    // Edited listeners
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        if (!application.isUpdating()) {
            updateEnabledComponents();
        }
    }

    @Override
    public DbObject getGuiObject() {
        if (!application.isUpdating()) {
            return selectedLogSettings;
        }
        return null;
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        settings().updateLogSettings();
        settings().updateSelectedSettings();
        updateComponents(settings().getLogSettings());
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        addNewLogSettings();
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedLogSettings != null) {
            if (selectedLogSettings.isDefault()) {
                JOptionPane.showMessageDialog(LogsPanel.this,
                        "Can't remove default settings..",
                        "Can not delete",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                deleteLogSetting(selectedLogSettings);
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
//        if (selectedLogSettings != null) {
//            if (selectedLogSettings.isDefault()) {
//                JOptionPane.showMessageDialog(LogsPanel.this,
//                        "Can't edit default settings..",
//                        "Can not edit",
//                        JOptionPane.INFORMATION_MESSAGE);
//            } else {
//
//            }
//        }
    }

    //
    // Save or Use button clicked
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(saveBtn)) {
            // Save
            saveSettings(selectedLogSettings);
        } else {
            // Use
            if (selectedLogSettings.isSaved()) {
                useSettings(selectedLogSettings);
            } else {
                JOptionPane.showMessageDialog(
                        LogsPanel.this,
                        "Save settings first!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    //
    // Log settings changed*
    //

    @Override
    public void onSettingsChanged(LogSettings newSettings) {
        if (!application.isUpdating()) {
            updateComponents(newSettings);
        }
    }
}