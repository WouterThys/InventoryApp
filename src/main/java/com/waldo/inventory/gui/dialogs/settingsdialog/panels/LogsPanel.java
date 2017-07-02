package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.database.settings.settingsclasses.LogSettings;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ICheckBox;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static com.waldo.inventory.database.settings.SettingsManager.settings;

public class LogsPanel extends JPanel implements
        GuiInterface,
        ItemListener, IEditedListener {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JPanel settingsPanel;
    IdBToolBar toolBar;

    DefaultComboBoxModel<LogSettings> logSettingsCbModel;
    JComboBox<LogSettings> logSettingsComboBox;

    ICheckBox logInfoCb;
    ICheckBox logDebugCb;
    ICheckBox logWarnCb;
    ICheckBox logErrorCb;

    JButton saveBtn;

    TitledBorder titledBorder;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;

    LogSettings selectedLogSettings;
    LogSettings originalLogSettings;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public LogsPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();

    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        if (selectedLogSettings == null || selectedLogSettings.isDefault()) {
            toolBar.setDeleteActionEnabled(false);
            toolBar.setEditActionEnabled(false);
            logInfoCb.setEnabled(false);
            logDebugCb.setEnabled(false);
            logWarnCb.setEnabled(false);
            logErrorCb.setEnabled(false);
        } else {
            toolBar.setDeleteActionEnabled(true);
            logInfoCb.setEnabled(true);
            logDebugCb.setEnabled(true);
            logWarnCb.setEnabled(true);
            logErrorCb.setEnabled(true);

        }
    }

    void updateFieldValues() {
        if (selectedLogSettings != null) {
            logInfoCb.setSelected(selectedLogSettings.isLogInfo());
            logDebugCb.setSelected(selectedLogSettings.isLogError());
            logWarnCb.setSelected(selectedLogSettings.isLogWarn());
            logErrorCb.setSelected(selectedLogSettings.isLogError());
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
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

        // Save button
        saveBtn = new JButton("Save");
        saveBtn.setEnabled(false);
        saveBtn.setAlignmentX(RIGHT_ALIGNMENT);

        // Toolbar
        toolBar = new IdBToolBar(new IdBToolBar.IdbToolBarListener() {
            @Override
            public void onToolBarRefresh() {

            }

            @Override
            public void onToolBarAdd() {

            }

            @Override
            public void onToolBarDelete() {

            }

            @Override
            public void onToolBarEdit() {

            }
        });
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.add(logSettingsComboBox, BorderLayout.CENTER);
        header.add(toolBar, BorderLayout.EAST);

        settingsPanel = new JPanel(new GridBagLayout());
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

        titledBorder = BorderFactory.createTitledBorder("Db settings");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));

        // Add to panel
        add(header, BorderLayout.NORTH);
        add(settingsPanel, BorderLayout.CENTER);
        add(saveBtn, BorderLayout.SOUTH);
    }

    @Override
    public void updateComponents(Object object) {
        application.beginWait();
        try {
            logSettingsCbModel.removeAllElements();
            for (LogSettings settings : settings().getLogSettingsList()) {
                logSettingsCbModel.addElement(settings);
            }

            selectedLogSettings = (LogSettings) object;
            updateEnabledComponents();

            if (selectedLogSettings != null) {
//            originalLogSettings = selectedLogSettings.createCopy(); // TODO
                updateFieldValues();

            } else {
                originalLogSettings = null;
            }
        } finally {
            application.endWait();
        }

    }

    //
    // Settings combo box checked
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

    }

    @Override
    public DbObject getGuiObject() {
        return null;
    }
}