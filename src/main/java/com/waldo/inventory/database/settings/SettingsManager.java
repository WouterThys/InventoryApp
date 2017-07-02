package com.waldo.inventory.database.settings;

import com.waldo.inventory.database.settings.settingsclasses.DbSettings;
import com.waldo.inventory.database.settings.settingsclasses.FileSettings;
import com.waldo.inventory.database.settings.settingsclasses.LogSettings;
import com.waldo.inventory.gui.Application;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.scriptResource;

public class SettingsManager {

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

    // Gui settings

    // Log settings
    private String selectedLogSettings = DEFAULT;
    private List<LogSettings> logSettingsList = null;

    private SettingsManager() {}

    public boolean init() {
        String dbFile = Application.startUpPath + "src/main/java/com/waldo/inventory/database/settings/settings.db";
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
            updateSettings();
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

    /**
     * GETTERS
     */

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

    /**
     * PRIVATE GETTERS
     */
    public String getSelectedDbSettingsName() {
        if (selectedDbSettings == null || selectedDbSettings.isEmpty()) {
            try {
                updateSettings();
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
                updateSettings();
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
                updateSettings();
            } catch (SQLException e) {
                e.printStackTrace();
                selectedLogSettings = DEFAULT;
            }
        }
        return selectedLogSettings;
    }




    public List<DbSettings> getDbSettingsList() {
        if (dbSettingsList == null) {
            updateDbSettings();
        }
        return dbSettingsList;
    }

    public List<LogSettings> getLogSettingsList() {
        if (logSettingsList == null) {
            updateLogSettings();
        }
        return logSettingsList;
    }

    public List<FileSettings> getFileSettingsList() {
        if (fileSettingsList == null) {
            updateFileSettings();
        }
        return fileSettingsList;
    }


    /**
     * SETTINGS DATABASE QUERIES
     */


    private void updateSettings() throws SQLException {
        String sql = scriptResource.readString("settings.sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    selectedDbSettings = rs.getString("dbsettings");
                }
            }
        }
    }

    private void updateDbSettings() {
        dbSettingsList = new ArrayList<>();

        String sql = scriptResource.readString("dbsettings.sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    DbSettings dbSettings = new DbSettings();
                    dbSettings.setName(rs.getString("name"));
                    dbSettings.setDbFile(rs.getString("dbfile"));
                    dbSettings.setDbUserName(rs.getString("dbusername"));
                    dbSettings.setDbUserPw(rs.getString("dbuserpw"));
                    dbSettings.setDbMaxIdleConnections(rs.getInt("dbmaxidleconnections"));
                    dbSettings.setDbMaxActiveConnections(rs.getInt("dbmaxactiveconnections"));
                    dbSettings.setDbInitialSize(rs.getInt("dbinitialsize"));
                    dbSettings.setDbRemoveAbandonedTimeout(rs.getInt("dbremoveabandonedtimeout"));
                    dbSettings.setDbPoolPreparedStatements(rs.getBoolean("dbpoolpreparedstatemens"));
                    dbSettings.setDbLogAbandoned(rs.getBoolean("dblogabandoned"));
                    dbSettings.setDbRemoveAbandoned(rs.getBoolean("dbremoveabandoned"));

                    dbSettingsList.add(dbSettings);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateFileSettings() {
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

                    fileSettingsList.add(fileSettings);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLogSettings() {
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

                    logSettingsList.add(logSettings);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
