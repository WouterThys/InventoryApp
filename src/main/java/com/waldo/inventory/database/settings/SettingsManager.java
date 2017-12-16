package com.waldo.inventory.database.settings;

import com.waldo.inventory.managers.LogManager;
import com.waldo.inventory.database.interfaces.DbSettingsListener;
import com.waldo.inventory.database.settings.settingsclasses.DbSettings;
import com.waldo.inventory.database.settings.settingsclasses.DbSettingsObject;
import com.waldo.inventory.database.settings.settingsclasses.FileSettings;
import com.waldo.inventory.database.settings.settingsclasses.LogSettings;
import com.waldo.inventory.gui.Application;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.settings.settingsclasses.DbSettingsObject.*;
import static com.waldo.inventory.gui.Application.scriptResource;

public class SettingsManager {

    private LogManager LOG;
    public static final String DEFAULT = "default";

    // Variables
    private static final SettingsManager INSTANCE = new SettingsManager();

    public static SettingsManager settings() {
        return INSTANCE;
    }
    private static Connection getConnection() throws SQLException {
        return settings().getDataSource().getConnection();
    }

    private BasicDataSource dataSource;

    // Db settings
    private String selectedDbSettings = DEFAULT;
    private List<DbSettings> dbSettingsList = null;

    // File settings
    private String selectedFileSettings = DEFAULT;
    private List<FileSettings> fileSettingsList = null;

    // Log settings
    private String selectedLogSettings = DEFAULT;
    private List<LogSettings> logSettingsList = null;

    // Listeners
    private final List<DbSettingsListener<LogSettings>> onLogSettingsChangedList = new ArrayList<>();
    private final List<DbSettingsListener<DbSettings>> onDbSettingsChangedList = new ArrayList<>();
    private final List<DbSettingsListener<FileSettings>> onFileSettingsChangedList = new ArrayList<>();

    /*
     *                  MAIN STUFF
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private SettingsManager() {}

    public boolean init() {
        String dbFile = Application.startUpPath + "settings.db";
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("net.sf.log4jdbc.DriverSpy");
        dataSource.setUrl("jdbc:log4jdbc:sqlite:" + dbFile);
        dataSource.setUsername("waldo");
        dataSource.setPassword("");
        dataSource.setMaxIdle(10);
        dataSource.setMaxActive(10);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setLogAbandoned(false);
        dataSource.setRemoveAbandoned(true);
        dataSource.setInitialSize(5);
        dataSource.setRemoveAbandonedTimeout(60);

        try {
            String sql = "PRAGMA foreign_keys=ON;";
            try (Connection connection = getConnection()) {
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            readSelectedSettingsFromDb();
            notifyAllListeners();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void close() {
        if(dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private DataSource getDataSource() {
        return dataSource;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void addLogSettingsListener(DbSettingsListener<LogSettings> listener) {
        if (!onLogSettingsChangedList.contains(listener)) {
            onLogSettingsChangedList.add(listener);
        }
    }

    public void addDbSettingsListener(DbSettingsListener<DbSettings> listener) {
        if (!onDbSettingsChangedList.contains(listener)) {
            onDbSettingsChangedList.add(listener);
        }
    }

    public void addFileSettingsListener(DbSettingsListener<FileSettings> listener) {
        if (!onFileSettingsChangedList.contains(listener)) {
            onFileSettingsChangedList.add(listener);
        }
    }

    private <T extends DbSettingsObject> void notifyListeners(T newSettings, List<DbSettingsListener<T>> listeners) {
        for (DbSettingsListener<T> l : listeners) {
            l.onSettingsChanged(newSettings);
        }
//        System.out.println(
//                "Log settings changed to: " + getLogSettings().getNameText() + "\r\n " +
//                        "\t log info: " + Boolean.toString(getLogSettings().isLogInfo())  + "\r\n " +
//                        "\t log debug: " + Boolean.toString(getLogSettings().isLogDebug())  + "\r\n " +
//                        "\t log warnings: " + Boolean.toString(getLogSettings().isLogWarn())  + "\r\n " +
//                        "\t log errors: " + Boolean.toString(getLogSettings().isLogError()));
    }

    private void notifyAllListeners() {
        SwingUtilities.invokeLater(() -> {
            notifyListeners(getLogSettings(), onLogSettingsChangedList);
            notifyListeners(getDbSettings(), onDbSettingsChangedList);
            notifyListeners(getFileSettings(), onFileSettingsChangedList);
        });
    }

    /*
     *                  GETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public DbSettings getDbSettings() {
        for (DbSettings settings : getDbSettingsList()) {
            if (settings.getName().equals(getSelectedDbSettingsName())) {
                return settings;
            }
        }
        return null;
    }

    public FileSettings getFileSettings() {
        for (FileSettings settings : getFileSettingsList()) {
            if (settings.getName().equals(getSelectedFileSettingsName())) {
                return settings;
            }
        }
        return null;
    }

    public LogSettings getLogSettings() {
        for (LogSettings settings : getLogSettingsList()) {
            if (settings.getName().equals(getSelectedLogSettingsName())) {
                return settings;
            }
        }
        return null;
    }


    public String getSelectedDbSettingsName() {
        if (selectedDbSettings == null || selectedDbSettings.isEmpty()) {
            try {
                readSelectedSettingsFromDb();
            } catch (SQLException e) {
                e.printStackTrace();
                selectedDbSettings = DEFAULT;
            }
        }
        return selectedDbSettings;
    }

    public String getSelectedFileSettingsName() {
        if (selectedFileSettings == null || selectedFileSettings.isEmpty()) {
            try {
                readSelectedSettingsFromDb();
            } catch (SQLException e) {
                e.printStackTrace();
                selectedFileSettings = DEFAULT;
            }
        }
        return selectedFileSettings;
    }

    public String getSelectedLogSettingsName() {
        if (selectedLogSettings == null || selectedLogSettings.isEmpty()) {
            try {
                readSelectedSettingsFromDb();
            } catch (SQLException e) {
                e.printStackTrace();
                selectedLogSettings = DEFAULT;
            }
        }
        return selectedLogSettings;
    }


    public List<DbSettings> getDbSettingsList() {
        if (dbSettingsList == null) {
            readDbSettingsFromDb();
        }
        return dbSettingsList;
    }

    public List<LogSettings> getLogSettingsList() {
        if (logSettingsList == null) {
            readLogSettingsFromDb();
        }
        return logSettingsList;
    }

    public List<FileSettings> getFileSettingsList() {
        if (fileSettingsList == null) {
            readFileSettingsFromDb();
        }
        return fileSettingsList;
    }


    public LogSettings getLogSettingsByName(String name) {
        for (LogSettings settings : getLogSettingsList()) {
            if (settings.getName().equals(name)) {
                return settings;
            }
        }
        return null;
    }

    public DbSettings getDbSettingsByName(String name) {
        for (DbSettings settings : getDbSettingsList()) {
            if (settings.getName().equals(name)) {
                return settings;
            }
        }
        return null;
    }

    public FileSettings getFileSettingsByName(String name) {
        for (FileSettings settings : getFileSettingsList()) {
            if (settings.getName().equals(name)) {
                return settings;
            }
        }
        return null;
    }



    /*
     *                  UPDATES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void updateSelectedSettings() {
        try {
            readSelectedSettingsFromDb();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLogSettings() {
        logSettingsList = null; // This will update the list when fetched next time
    }

    public void updateDbSettings() {
        dbSettingsList = null;
    }

    public void updateFileSettings() {
        fileSettingsList = null;
    }


    /*
     *                  DELETES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public boolean deleteSetting(DbSettingsObject toDelete) {
        boolean result = false;
        if (!toDelete.isDefault()) {
            // Set back default value
            switch (DbSettingsObject.getType(toDelete)) {
                case SETTINGS_TYPE_LOG:
                    selectNewSettings(getLogSettingsByName(DEFAULT));
                    break;
                case SETTINGS_TYPE_DB:
                    selectNewSettings(getLogSettingsByName(DEFAULT));
                    break;
                case SETTINGS_TYPE_FILE:
                    selectNewSettings(getLogSettingsByName(DEFAULT));
                    break;
            }

            // Delete
            String sql = "DELETE FROM " + toDelete.getTableName() + " WHERE name = ?";
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, toDelete.getName());
                    statement.execute();
                    result = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                result = false;
            }
        }
        if (result) {
            updateLogSettings();
        }
        return result;
    }

    /*
     *                  SAVE
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void saveSettings(DbSettingsObject settings) {
        switch (getType(settings)) {
            case SETTINGS_TYPE_LOG:
                saveLogSettings((LogSettings) settings);
                break;
            case SETTINGS_TYPE_DB:
                saveDbSettings((DbSettings) settings);
                break;
            case SETTINGS_TYPE_FILE:
                saveFileSettings((FileSettings) settings);
                break;
        }
        settings.setSaved(true);
    }

    private void saveLogSettings(LogSettings logSettings) {
        if (logSettings.isSaved()) {
            // Update
            updateLogSetting(logSettings);
            readLogSettingsFromDb();
            notifyListeners(getLogSettings(), onLogSettingsChangedList);
        } else {
            // Insert
            insertLogSetting(logSettings);
            readLogSettingsFromDb();
        }
    }

    private void saveFileSettings(FileSettings fileSettings) {
        if (fileSettings.isSaved()) {
            // Update
            updateFileSetting(fileSettings);
            readFileSettingsFromDb();
            notifyListeners(getLogSettings(), onLogSettingsChangedList);
        } else {
            // Insert
            insertFileSetting(fileSettings);
            readFileSettingsFromDb();
        }
    }

    private void saveDbSettings(DbSettings dbSettings) {
        if (dbSettings.isSaved()) {
            // Update
            updateDbSetting(dbSettings);
            readDbSettingsFromDb();
            notifyListeners(getLogSettings(), onLogSettingsChangedList);
        } else {
            // Insert
            insertDbSetting(dbSettings);
            readDbSettingsFromDb();
        }
    }


    /*
     *                  SELECT SETTINGS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void selectNewSettings(DbSettingsObject settings) {
        String sql = "";
        switch (getType(settings)) {
            case SETTINGS_TYPE_LOG:
                sql = scriptResource.readString("settings.sqlUpdateLog");
                break;
            case SETTINGS_TYPE_DB:
                sql = scriptResource.readString("settings.sqlUpdateDb");
                break;
            case SETTINGS_TYPE_FILE:
                sql = scriptResource.readString("settings.sqlUpdateFile");
                break;
        }

        try {
            selectSettings(settings, sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        switch (getType(settings)) {
            case SETTINGS_TYPE_LOG:
                selectedLogSettings = settings.getName();
                notifyListeners((LogSettings) settings, onLogSettingsChangedList);
                break;
            case SETTINGS_TYPE_DB:
                selectedDbSettings = settings.getName();
                notifyListeners((DbSettings) settings, onDbSettingsChangedList);
                break;
            case SETTINGS_TYPE_FILE:
                selectedFileSettings = settings.getName();
                notifyListeners((FileSettings) settings, onFileSettingsChangedList);
                break;
        }
    }


    /*
     *                  QUERIES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void readSelectedSettingsFromDb() throws SQLException {
        String sql = scriptResource.readString("settings.sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    selectedDbSettings = rs.getString("dbsettings");
                    selectedFileSettings = rs.getString("filesettings");
                    selectedLogSettings = rs.getString("logsettings");
                }
            }
        }
    }

    private void selectSettings(DbSettingsObject settings, String sql) throws SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1,settings.getName());
                stmt.execute();
            }
        }
    }


    private void readDbSettingsFromDb() {
        dbSettingsList = new ArrayList<>();

        String sql = scriptResource.readString("dbsettings.sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    DbSettings dbSettings = new DbSettings();
                    dbSettings.setName(rs.getString("name"));
                    dbSettings.setDbName(rs.getString("dbname"));
                    dbSettings.setDbIp(rs.getString("dbip"));
                    dbSettings.setDbUserName(rs.getString("dbusername"));
                    dbSettings.setDbUserPw(rs.getString("dbuserpw"));
                    dbSettings.setDbType(rs.getString("dbtype"));

                    dbSettings.setSaved(true);

                    dbSettingsList.add(dbSettings);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void readFileSettingsFromDb() {
        fileSettingsList = new ArrayList<>();

        String sql = scriptResource.readString("filesettings.sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    FileSettings fileSettings = new FileSettings();

                    fileSettings.setName(rs.getString("name"));
                    fileSettings.setImgDistributorsPath(rs.getString("distributors"));
                    fileSettings.setImgDivisionsPath(rs.getString("divisions"));
                    fileSettings.setImgIdesPath(rs.getString("ides"));
                    fileSettings.setImgItemsPath(rs.getString("items"));
                    fileSettings.setImgManufacturersPath(rs.getString("manufacturers"));
                    fileSettings.setImgProjectsPath(rs.getString("projects"));
                    fileSettings.setFileOrdersPath(rs.getString("orders"));

                    fileSettings.setSaved(true);

                    fileSettingsList.add(fileSettings);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void readLogSettingsFromDb() {
        logSettingsList = new ArrayList<>();

        String sql = scriptResource.readString("logsettings.sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    LogSettings logSettings = new LogSettings();

                    logSettings.setName(rs.getString("name"));
                    logSettings.setLogInfo(rs.getBoolean("loginfo"));
                    logSettings.setLogDebug(rs.getBoolean("logdebug"));
                    logSettings.setLogWarn(rs.getBoolean("logwarn"));
                    logSettings.setLogError(rs.getBoolean("logerror"));

                    logSettings.setSaved(true);

                    logSettingsList.add(logSettings);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void insertLogSetting(LogSettings set) {
        String sql = scriptResource.readString("logsettings.sqlInsert");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, set.getName());
                stmt.setBoolean(2, set.isLogInfo());
                stmt.setBoolean(3, set.isLogDebug());
                stmt.setBoolean(4, set.isLogWarn());
                stmt.setBoolean(5, set.isLogError());
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertFileSetting(FileSettings set) {
        String sql = scriptResource.readString("filesettings.sqlInsert");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1,  set.getName());
                stmt.setString(2, set.getImgDistributorsPath());
                stmt.setString(3, set.getImgDivisionsPath());
                stmt.setString(4, set.getImgIdesPath());
                stmt.setString(5, set.getImgItemsPath());
                stmt.setString(6, set.getImgManufacturersPath());
                stmt.setString(7, set.getImgProjectsPath());
                stmt.setString(8, set.getFileOrdersPath());
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertDbSetting(DbSettings set) {
        String sql = scriptResource.readString("dbsettings.sqlInsert");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, set.getName());
                stmt.setString(2, set.getDbName());
                stmt.setString(3, set.getDbIp());
                stmt.setString(4, set.getDbUserName());
                stmt.setString(5, set.getDbUserPw());
                stmt.setString(6, set.getDbType());
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateLogSetting(LogSettings set) {
        String sql = scriptResource.readString("logsettings.sqlUpdate");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setBoolean(1, set.isLogInfo());
                stmt.setBoolean(2, set.isLogDebug());
                stmt.setBoolean(3, set.isLogWarn());
                stmt.setBoolean(4, set.isLogError());

                stmt.setString(5, set.getName()); // Where name

                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateFileSetting(FileSettings set) {
        String sql = scriptResource.readString("filesettings.sqlUpdate");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, set.getImgDistributorsPath());
                stmt.setString(2, set.getImgDivisionsPath());
                stmt.setString(3, set.getImgIdesPath());
                stmt.setString(4, set.getImgItemsPath());
                stmt.setString(5, set.getImgManufacturersPath());
                stmt.setString(6, set.getImgProjectsPath());
                stmt.setString(7, set.getFileOrdersPath());

                stmt.setString(8,  set.getName()); // Where name

                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateDbSetting(DbSettings set) {
        String sql = scriptResource.readString("dbsettings.sqlUpdate");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, set.getDbName());
                stmt.setString(2, set.getDbIp());
                stmt.setString(3, set.getDbUserName());
                stmt.setString(4, set.getDbUserPw());
                stmt.setString(5, set.getDbType());

                stmt.setString(6, set.getName()); // Where name

                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
