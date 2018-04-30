package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.database.settings.settingsclasses.LogSettings;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.utils.icomponents.ICheckBox;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;

public class LogsPanel extends SettingsPnl<LogSettings> {

    private ICheckBox logInfoCb;
    private ICheckBox logDebugCb;
    private ICheckBox logWarnCb;
    private ICheckBox logErrorCb;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public LogsPanel(iDialog parent) {
        super(parent, settings().getLogSettings());
        settings().addLogSettingsListener(this);
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    protected List<LogSettings> getAllSettingsList() {
        return settings().getLogSettingsList();
    }

    @Override
    protected LogSettings getSettingsByName(String name) {
        return settings().getLogSettingsByName(name);
    }

    @Override
    protected LogSettings createNew(String name) {
        return new LogSettings(name);
    }

    @Override
    protected LogSettings refreshSettings() {
        settings().updateSelectedSettings();
        selectedSettings = settings().getLogSettings();
        return selectedSettings;
    }

    @Override
    protected boolean updateEnabledComponents() {
        boolean enabled = super.updateEnabledComponents();
        logInfoCb.setEnabled(enabled);
        logDebugCb.setEnabled(enabled);
        logWarnCb.setEnabled(enabled);
        logErrorCb.setEnabled(enabled);
        return enabled;
    }

    @Override
    protected void updateFieldValues(LogSettings selectedLogSettings) {
        super.updateFieldValues(selectedLogSettings);
        if (selectedLogSettings != null) {
            logInfoCb.setSelected(selectedLogSettings.isLogInfo());
            logDebugCb.setSelected(selectedLogSettings.isLogDebug());
            logWarnCb.setSelected(selectedLogSettings.isLogWarn());
            logErrorCb.setSelected(selectedLogSettings.isLogError());
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        super.initializeComponents();
        // Check boxes
        logInfoCb = new ICheckBox("Log info");
        logInfoCb.addEditedListener(this, "logInfo");
        logDebugCb = new ICheckBox("Log debug");
        logDebugCb.addEditedListener(this, "logDebug");
        logWarnCb = new ICheckBox("Log warnings");
        logWarnCb.addEditedListener(this, "logWarn");
        logErrorCb = new ICheckBox("Log errors");
        logErrorCb.addEditedListener(this, "logError");

    }

    @Override
    public void initializeLayouts() {
        contentPanel.setLayout(new BorderLayout());

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

        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                GuiUtils.createInlineTitleBorder("Log options"),
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));

        // Add to panel
        contentPanel.add(settingsPanel, BorderLayout.NORTH);
        super.initializeLayouts();
    }

    @Override
    public void updateComponents(Object... args) {
        Application.beginWait(LogsPanel.this);
        try {
            if (args.length > 0) {
                if (args[0] instanceof SettingsManager) {
                    selectedSettings = ((SettingsManager) args[0]).getLogSettings();
                } else {
                    selectedSettings = (LogSettings) args[0];
                }
            }

            if (selectedSettings != null) {
                cbSelectSettings(selectedSettings);
                originalSettings = selectedSettings.createCopy();
                updateFieldValues(selectedSettings);
            } else {
                originalSettings = null;
            }
            updateEnabledComponents();
        } finally {
            Application.endWait(LogsPanel.this);
        }

    }
}