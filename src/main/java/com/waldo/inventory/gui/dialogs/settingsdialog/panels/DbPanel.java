package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.database.LogManager;
import com.waldo.inventory.database.interfaces.SettingsListener;
import com.waldo.inventory.database.settings.settingsclasses.DbSettings;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.ExecutionException;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;

public class DbPanel extends JPanel implements
        GuiInterface,
        ItemListener,
        IEditedListener,
        IdBToolBar.IdbToolBarListener,
        SettingsListener<DbSettings>,
        ActionListener {


    private static final LogManager LOG = LogManager.LOG(DbPanel.class);

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private IdBToolBar toolBar;
    private ILabel currentSettingLbl;

    private DefaultComboBoxModel<DbSettings> dbSettingsCbModel;
    private JComboBox<DbSettings> dbSettingsComboBox;
    
    private ITextField dbNameTf;
    private ITextField dbIpTf;
    private ITextField userNameTf;
    private ITextField userPwTf;

    private JButton saveBtn;
    private JButton useBtn;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;

    private DbSettings selectedDbSettings;
    private DbSettings originalDbSettings;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public DbPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();

    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        if (selectedDbSettings == null || selectedDbSettings.isDefault()) {
            toolBar.setDeleteActionEnabled(false);
            toolBar.setEditActionEnabled(false);
            dbNameTf.setEnabled(false);
            dbIpTf.setEnabled(false);
            userNameTf.setEnabled(false);
            userPwTf.setEnabled(false);

            saveBtn.setEnabled(false);
        } else {
            toolBar.setDeleteActionEnabled(true);
            dbNameTf.setEnabled(true);
            dbIpTf.setEnabled(true);
            userNameTf.setEnabled(true);
            userPwTf.setEnabled(true);

            saveBtn.setEnabled(
                    !selectedDbSettings.isSaved() ||
                            !selectedDbSettings.equals(originalDbSettings));
        }

        if (selectedDbSettings != null) {
            boolean isCurrent = selectedDbSettings.getName().equals(settings().getSelectedDbSettingsName());
            boolean isSaved = selectedDbSettings.isSaved();
            useBtn.setEnabled(!isCurrent && isSaved);
        }
    }

    private void updateFieldValues() {
        if (selectedDbSettings != null) {
            dbNameTf.setText(selectedDbSettings.getDbName());
            dbIpTf.setText(selectedDbSettings.getDbIp());
            userNameTf.setText(selectedDbSettings.getDbUserName());
            userPwTf.setText(selectedDbSettings.getDbUserPw());

            currentSettingLbl.setText(settings().getSelectedDbSettingsName());
        }
    }

    private void addNewDbSettings() {
        String newName = JOptionPane.showInputDialog(
                DbPanel.this,
                "New settings name?");

        if (newName != null && !newName.isEmpty()) {
            addNewDbSettings(newName);
        }
    }

    private void addNewDbSettings(String newName) {
        if (settings().getDbSettingsByName(newName) == null) {
            DbSettings dbSettings = new DbSettings(newName);
            settings().getDbSettingsList().add(dbSettings);
            updateComponents(dbSettings);
        } else {
            JOptionPane.showMessageDialog(
                    DbPanel.this,
                    "Name " + newName + " already exists..",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void saveSettings(DbSettings toSave) {
        new SwingWorker<DbSettings, Object>() {
            @Override
            protected DbSettings doInBackground() throws Exception {
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

    private void useSettings(DbSettings toUse) {
        new SwingWorker<DbSettings, Object>() {
            @Override
            protected DbSettings doInBackground() throws Exception {
                application.beginWait();
                try {
                    settings().selectNewSettings(toUse);
                } finally {
                    application.endWait();
                }
                return settings().getDbSettings();
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

    private void deleteDbSetting(DbSettings toDelete) {
        int res = JOptionPane.showConfirmDialog(DbPanel.this,
                "Are you sure you want to delete " + toDelete.getName() + "?",
                "Delete log setting",
                JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            new SwingWorker<DbSettings, Object>() {
                @Override
                protected DbSettings doInBackground() throws Exception {
                    application.beginWait();
                    try {
                        settings().deleteSetting(toDelete);
                    } finally {
                        application.endWait();
                    }
                    return settings().getDbSettings();
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
        dbSettingsCbModel = new DefaultComboBoxModel<>();
        dbSettingsComboBox = new JComboBox<>(dbSettingsCbModel);
        dbSettingsComboBox.setAlignmentX(RIGHT_ALIGNMENT);
        dbSettingsComboBox.addItemListener(this);
        dbSettingsComboBox.setPreferredSize(new Dimension(120, 30));

        //  fields
        dbNameTf = new ITextField();
        dbIpTf = new ITextField();
        userNameTf = new ITextField();
        userPwTf = new ITextField();
        dbNameTf.addEditedListener(this, "dbName");
        dbIpTf.addEditedListener(this, "dbIp");
        userNameTf.addEditedListener(this, "dbUserName");
        userPwTf.addEditedListener(this, "dbUserPw");

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
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel currentPanel = new JPanel(new BorderLayout());
        currentPanel.add(new ILabel("Current file setting: "), BorderLayout.NORTH);
        currentPanel.add(currentSettingLbl, BorderLayout.CENTER);
        currentPanel.setBorder(BorderFactory.createEmptyBorder(2, 15, 2, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(dbSettingsComboBox, BorderLayout.WEST);
        headerPanel.add(currentPanel, BorderLayout.CENTER);
        headerPanel.add(toolBar, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        // - Add to panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);

        JComponent[] jComponents = new JComponent[]{ dbNameTf, dbIpTf, userNameTf, userPwTf};
        ILabel[] iLabels = new ILabel[]{
                new ILabel("Db file name: ", ILabel.RIGHT),
                new ILabel("Db ip address: ", ILabel.RIGHT),
                new ILabel("Db user name: ", ILabel.RIGHT),
                new ILabel("Db user password: ", ILabel.RIGHT)
        };

        for (int i = 0; i < jComponents.length; i++) {
            // Label
            gbc.gridx = 0;
            gbc.weightx = 0;
            gbc.gridy = i;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.EAST;
            settingsPanel.add(iLabels[i], gbc);
            // Component
            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.gridy = i;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            settingsPanel.add(jComponents[i], gbc);
        }

        TitledBorder titledBorder = BorderFactory.createTitledBorder("File options");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Add to panel
        add(headerPanel, BorderLayout.NORTH);
        add(settingsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    @Override
    public void updateComponents(Object object) {
        application.beginWait();
        try {
            dbSettingsCbModel.removeAllElements();
            for (DbSettings settings : settings().getDbSettingsList()) {
                dbSettingsCbModel.addElement(settings);
            }

            selectedDbSettings = (DbSettings) object;

            if (selectedDbSettings != null) {
                dbSettingsComboBox.setSelectedItem(selectedDbSettings);
                originalDbSettings = selectedDbSettings.createCopy();
                updateFieldValues();
            } else {
                originalDbSettings = null;
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
                if (o instanceof DbSettings) {
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
            return selectedDbSettings;
        }
        return null;
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh() {
        settings().updateDbSettings();
        settings().updateSelectedSettings();
        updateComponents(settings().getDbSettings());
    }

    @Override
    public void onToolBarAdd() {
        addNewDbSettings();
    }

    @Override
    public void onToolBarDelete() {
        if (selectedDbSettings != null) {
            if (selectedDbSettings.isDefault()) {
                JOptionPane.showMessageDialog(DbPanel.this,
                        "Can't remove default settings..",
                        "Can not delete",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                deleteDbSetting(selectedDbSettings);
            }
        }
    }

    @Override
    public void onToolBarEdit() {
//        if (selectedDbSettings != null) {
//            if (selectedDbSettings.isDefault()) {
//                JOptionPane.showMessageDialog(DbPanel.this,
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
            saveSettings(selectedDbSettings);
        } else {
            // Use
            if (selectedDbSettings.isSaved()) {
                useSettings(selectedDbSettings);
            } else {
                JOptionPane.showMessageDialog(
                        DbPanel.this,
                        "Save settings first!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    //
    // Log settings changed
    //

    @Override
    public void onSettingsChanged(DbSettings newSettings) {
        if (!application.isUpdating()) {
            updateComponents(newSettings);
        }
    }
}