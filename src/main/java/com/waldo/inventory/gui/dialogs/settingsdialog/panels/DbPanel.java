package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.database.interfaces.DbSettingsListener;
import com.waldo.inventory.database.settings.settingsclasses.DbSettings;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.managers.LogManager;
import org.apache.commons.dbcp.BasicDataSource;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import static com.waldo.inventory.database.settings.SettingsManager.settings;

public class DbPanel extends JPanel implements
        GuiInterface,
        ItemListener,
        IEditedListener,
        IdBToolBar.IdbToolBarListener,
        DbSettingsListener<DbSettings>,
        ActionListener {


    private static final LogManager LOG = LogManager.LOG(DbPanel.class);

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // Db settings
    private IdBToolBar toolBar;
    private ILabel currentSettingLbl;

    private DefaultComboBoxModel<DbSettings> dbSettingsCbModel;
    private JComboBox<DbSettings> dbSettingsComboBox;

    private IComboBox<String> dbTypeCb;
    private ITextField dbNameTf;
    private ITextField dbIpTf;
    private ITextField userNameTf;
    private IPasswordField userPwTf;

    private JButton saveBtn;
    private JButton useBtn;
    private JButton testBtn;

    // Backups and cache settings
    private ITextFieldButtonPanel backupPathPnl;
    private JButton createBackupBtn;


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
        boolean settingSelected = !(selectedDbSettings == null || selectedDbSettings.isDefault());
        boolean onlineDb = (dbTypeCb.getSelectedItem() != null && dbTypeCb.getSelectedItem().equals(Statics.DbTypes.Online));

        toolBar.setEditActionEnabled(false);
        toolBar.setDeleteActionEnabled(settingSelected);

        dbNameTf.setEnabled(settingSelected);
        dbIpTf.setEnabled(settingSelected);
        userNameTf.setEnabled(settingSelected && onlineDb);
        userPwTf.setEnabled(settingSelected && onlineDb);
        dbTypeCb.setEnabled(settingSelected);

        if (settingSelected) {
            saveBtn.setEnabled(false);
        } else {
            saveBtn.setEnabled(!selectedDbSettings.isSaved() || !selectedDbSettings.equals(originalDbSettings));
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
            dbTypeCb.setSelectedItem(selectedDbSettings.getDbType());

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
                    DatabaseAccess.db().init();
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
                } catch (Exception e){
                    LOG.error("Error initializing db");
                    JOptionPane.showMessageDialog(DbPanel.this,
                            "Error re-initializing daabase..",
                            "Initialisation error",
                            JOptionPane.ERROR_MESSAGE);
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

    private void testDatabaseValues() {
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
            //dataSource.setDatabaseName(dbName);
            //dataSource.setUser(dbUserName);
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
        JPanel dbSettingsPanel = new JPanel();
        dbSettingsPanel.setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(testBtn);
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
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(settingsPanel);

        gbc.addLine("Db type: ", dbTypeCb);
        gbc.addLine("Db file name: ", dbNameTf);
        gbc.addLine("Db ip address: ", dbIpTf);
        gbc.addLine("Db user name: ", userNameTf);
        gbc.addLine("Db user password: ", userPwTf);

        TitledBorder titledBorder = BorderFactory.createTitledBorder("Db options");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Add to panel
        dbSettingsPanel.add(headerPanel, BorderLayout.NORTH);
        dbSettingsPanel.add(settingsPanel, BorderLayout.CENTER);
        dbSettingsPanel.add(buttonsPanel, BorderLayout.SOUTH);

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
        // SETTINGS
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

        // Type
        DefaultComboBoxModel<String> dbTypeCbModel = new DefaultComboBoxModel<>(Statics.DbTypes.All);
        dbTypeCb = new IComboBox<>(dbTypeCbModel);
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

        // Buttons
        saveBtn = new JButton("Save");
        saveBtn.setEnabled(false);
        saveBtn.setAlignmentX(RIGHT_ALIGNMENT);
        saveBtn.addActionListener(this);

        useBtn = new JButton("Use this");
        useBtn.setEnabled(false);
        useBtn.setAlignmentX(LEFT_ALIGNMENT);
        useBtn.addActionListener(this);

        testBtn = new JButton("Test");
        testBtn.addActionListener(this);

        // Toolbar
        toolBar = new IdBToolBar(this);

        // BACKUP
        backupPathPnl = new GuiUtils.IBrowseFilePanel("", "/home/");
        createBackupBtn = new JButton();
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel settings = createDbSettingsPanel();
        JPanel backup = createDbBackupPanel();

        settings.setBorder(GuiUtils.createTitleBorder("Settings"));
        backup.setBorder(GuiUtils.createTitleBorder("Backups and Cache"));

        add(settings);
        add(backup);
    }

    @Override
    public void updateComponents(Object... object) {
        application.beginWait();
        try {
            dbSettingsCbModel.removeAllElements();
            for (DbSettings settings : settings().getDbSettingsList()) {
                dbSettingsCbModel.addElement(settings);
            }

            selectedDbSettings = (DbSettings) object[0];

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
            if (e.getSource().equals(dbTypeCb)) {

            } else {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Object o = e.getItem();
                    if (o instanceof DbSettings) {
                        updateComponents(o);
                    }
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
    public void onToolBarRefresh(IdBToolBar source) {
        settings().updateDbSettings();
        settings().updateSelectedSettings();
        updateComponents(settings().getDbSettings());
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        addNewDbSettings();
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
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
    public void onToolBarEdit(IdBToolBar source) {
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
        } else if(e.getSource().equals(useBtn)) {
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
        } else if (e.getSource().equals(testBtn)) {
            testDatabaseValues();
        }
    }

    //
    // Log settings changed
    //
    @Override
    public void onSettingsChanged(DbSettings newSettings) {

    }
}