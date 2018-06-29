package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.database.settings.settingsclasses.DbSettings;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.utils.OpenUtils;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.IPasswordField;
import com.waldo.utils.icomponents.ITextField;
import com.waldo.utils.icomponents.ITextFieldActionPanel;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;

public class DbPanel extends SettingsPnl<DbSettings> {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IComboBox<Statics.DbTypes> dbTypeCb;
    private ITextField dbNameTf;
    private ITextField dbIpTf;
    private ITextField userNameTf;
    private IPasswordField userPwTf;

    private IActions.TestAction testAction;

    // Backups and cache settings
    private ITextFieldActionPanel backupPathPnl;
    private JButton createBackupBtn;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public DbPanel(ICacheDialog parent) {
        super(parent, settings().getDbSettings());
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    protected List<DbSettings> getAllSettingsList() {
        return settings().getDbSettingsList();
    }

    @Override
    protected DbSettings getSettingsByName(String name) {
        return settings().getDbSettingsByName(name);
    }

    @Override
    protected DbSettings createNew(String name) {
        return new DbSettings(name);
    }

    @Override
    protected DbSettings refreshSettings() {
        settings().updateSelectedSettings();
        selectedSettings = settings().getDbSettings();
        return selectedSettings;
    }

    @Override
    protected boolean updateEnabledComponents() {
        super.updateEnabledComponents();
        boolean settingSelected = !(selectedSettings == null || selectedSettings.isDefault());
        boolean onlineDb = (dbTypeCb.getSelectedItem() != null && dbTypeCb.getSelectedItem().equals(Statics.DbTypes.Online));

        dbNameTf.setEnabled(settingSelected);
        dbIpTf.setEnabled(settingSelected);
        userNameTf.setEnabled(settingSelected && onlineDb);
        userPwTf.setEnabled(settingSelected && onlineDb);
        dbTypeCb.setEnabled(settingSelected);
        return true;
    }

    @Override
    protected void updateFieldValues(DbSettings selectedDbSettings) {
        super.updateFieldValues(selectedSettings);
        if (selectedDbSettings != null) {
            dbNameTf.setText(selectedDbSettings.getDbName());
            dbIpTf.setText(selectedDbSettings.getDbIp());
            userNameTf.setText(selectedDbSettings.getDbUserName());
            userPwTf.setText(selectedDbSettings.getDbUserPw());
            dbTypeCb.setSelectedItem(selectedDbSettings.getDbType());
        }
    }

    private void testDatabaseValues() {

//        new SwingWorker<DbSettings, Object>() {
//            @Override
//            protected DbSettings doInBackground() throws Exception {
//                Application.beginWait(DbPanel.this);
//                try {
//                    settings().deleteSetting(toDelete);
//                } finally {
//                    Application.endWait(DbPanel.this);
//                }
//                return settings().getDbSettings();
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    updateComponents(get());
//                } catch (InterruptedException | ExecutionException e) {
//                    LOG.error("Error updating components", e);
//                }
//            }
//        }.execute();


        boolean errors = false;

        String dbName = dbNameTf.getText();
        String dbIp = dbIpTf.getText();
        String dbUserName = userNameTf.getText();
        String dbUserPw = userPwTf.getText();

        if (dbName == null || dbName.isEmpty()) {
            dbNameTf.setError("Database name can not be empty..");
            errors = true;
        }
        if (dbIp == null || dbIp.isEmpty()) {
            dbIpTf.setError("Database ip can not be empty..");
            errors = true;
        } else {
            if(!(OpenUtils.isValidIpAddress(dbIp))) {
                dbIpTf.setError("Invalid ip address");
                errors = true;
            }
        }

        if (dbUserName == null) {
            dbUserName = "";
        }
        if (dbUserPw == null) {
            dbUserPw = "";
        }

        if (!errors) {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUrl(DbSettings.createMysqlUrl(dbIp, dbName));
            dataSource.setUsername(dbUserName);
            dataSource.setPassword(dbUserPw);

            // TODO: on new thread with timeout

            try {
                if (DatabaseAccess.testConnection(dataSource)) {
                    try {
                        java.util.List<String> missingTables = (DatabaseAccess.testDatabase(dataSource));
                        if (missingTables.size() == 0) {
                            JOptionPane.showMessageDialog(this,
                                    "Success!!",
                                    "Test result",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            StringBuilder missing = new StringBuilder();
                            for (String m : missingTables) {
                                missing.append(m).append(", ");
                            }
                            JOptionPane.showMessageDialog(this,
                                    "Connection success, but the database is missing following tables: " + missing,
                                    "Test result",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this,
                                "Connection success, but tables check encountered error: " + e.getMessage(),
                                "Test result",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Connection failed",
                            "Test result",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Connection failed with error: " + e.getMessage(),
                        "Test result",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createDbSettingsPanel() {
        JPanel dbSettingsPanel = new JPanel(new BorderLayout());

        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.add(GuiUtils.createNewToolbar(testAction), BorderLayout.EAST);

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        // - Add to panel
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(settingsPanel, 160);
        gbc.addLine("Db type: ", dbTypeCb);
        gbc.addLine("Db file name: ", dbNameTf);
        gbc.addLine("Db ip address: ", dbIpTf);
        gbc.addLine("Db user name: ", userNameTf);
        gbc.addLine("Db user password: ", userPwTf);

        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                GuiUtils.createInlineTitleBorder("Database options"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Add to panel
        dbSettingsPanel.add(settingsPanel, BorderLayout.CENTER);
        dbSettingsPanel.add(toolbarPanel, BorderLayout.SOUTH);

        return dbSettingsPanel;
    }

    private JPanel createDbBackupPanel() {
        JPanel dbBackupPanel = new JPanel(new BorderLayout());

        dbBackupPanel.add(backupPathPnl, BorderLayout.CENTER);
        dbBackupPanel.add(createBackupBtn, BorderLayout.SOUTH);

        return dbBackupPanel;
    }
    
    
    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        super.initializeComponents();

        // Type
        dbTypeCb = new IComboBox<>(Statics.DbTypes.values());
        dbTypeCb.addItemListener(this);
        dbTypeCb.addEditedListener(this, "dbType", String.class);

        //  fields
        dbNameTf = new ITextField();
        dbIpTf = new ITextField();
        userNameTf = new ITextField();
        userPwTf = new IPasswordField();
        dbNameTf.addEditedListener(this, "dbName");
        dbIpTf.addEditedListener(this, "dbIp");
        userNameTf.addEditedListener(this, "dbUserName");
        userPwTf.addEditedListener(this, "dbUserPw");

        testAction = new IActions.TestAction(imageResource.readIcon("Test.S")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                testDatabaseValues();
            }
        };

        // BACKUP
        backupPathPnl = new GuiUtils.IBrowseFilePanel("", "/home/");
        createBackupBtn = new JButton();
    }

    @Override
    public void initializeLayouts() {
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JPanel settings = createDbSettingsPanel();
        JPanel backup = createDbBackupPanel();

        //settings.setBorder(GuiUtils.createTitleBorder("Settings"));
        backup.setBorder(GuiUtils.createInlineTitleBorder("Backups and Cache"));

       contentPanel.add(settings);
       contentPanel.add(backup);
       super.initializeLayouts();
    }

    @Override
    public void updateComponents(Object... object) {
        Application.beginWait(DbPanel.this);
        try {
            if (object.length > 0) {
                if (object[0] instanceof SettingsManager) {
                    selectedSettings = ((SettingsManager) object[0]).getDbSettings();
                } else {
                    selectedSettings = (DbSettings) object[0];
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
            Application.endWait(DbPanel.this);
        }

    }
}