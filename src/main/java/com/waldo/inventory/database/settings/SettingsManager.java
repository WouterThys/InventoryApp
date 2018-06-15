package com.waldo.inventory.database.settings;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.interfaces.DbSettingsListener;
import com.waldo.inventory.database.settings.settingsclasses.*;
import com.waldo.inventory.gui.Application;
import org.apache.commons.dbcp2.BasicDataSource;

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

    // General settings
    private String selectedGeneralSettings = DEFAULT;
    private List<GeneralSettings> generalSettingsList = null;

    // Db settings
    private String selectedDbSettings = DEFAULT;
    private List<DbSettings> dbSettingsList = null;

    // Image settings
    private String selectedImageServerSettings = DEFAULT;
    private List<ImageServerSettings> imageServerSettingsList = null;

    // Log settings
    private String selectedLogSettings = DEFAULT;
    private List<LogSettings> logSettingsList = null;

    // Listeners
    private final List<DbSettingsListener<LogSettings>> onLogSettingsChangedList = new ArrayList<>();
    private final List<DbSettingsListener<DbSettings>> onDbSettingsChangedList = new ArrayList<>();
    private final List<DbSettingsListener<ImageServerSettings>> onImageServerSettingsChangedList = new ArrayList<>();
    private final List<DbSettingsListener<GeneralSettings>> onGeneralSettingsChangedList = new ArrayList<>();

    /*
     *                  MAIN STUFF
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private SettingsManager() {}

    public boolean init() {
        String dbFile = Application.startUpPath + "settings.db";

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:" + dbFile);
        dataSource.setUsername("waldo");
        dataSource.setPassword("");
        dataSource.setMaxIdle(10);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setLogAbandoned(false);
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
    public void addGeneralSettingsListener(DbSettingsListener<GeneralSettings> listener) {
        if (!onGeneralSettingsChangedList.contains(listener)) {
            onGeneralSettingsChangedList.add(listener);
        }
    }

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

    public void addImageServerSettingsListener(DbSettingsListener<ImageServerSettings> listener) {
        if (!onImageServerSettingsChangedList.contains(listener)) {
            onImageServerSettingsChangedList.add(listener);
        }
    }

    private <T extends DbSettingsObject> void notifyListeners(T newSettings, List<DbSettingsListener<T>> listeners) {
        for (DbSettingsListener<T> l : listeners) {
            l.onSettingsChanged(newSettings);
        }
    }

    private void notifyAllListeners() {
        SwingUtilities.invokeLater(() -> {
            notifyListeners(getGeneralSettings(), onGeneralSettingsChangedList);
            notifyListeners(getLogSettings(), onLogSettingsChangedList);
            notifyListeners(getDbSettings(), onDbSettingsChangedList);
            notifyListeners(getImageServerSettings(), onImageServerSettingsChangedList);
        });
    }

    /*
     *                  GETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public GeneralSettings getGeneralSettings() {
        for (GeneralSettings settings : getGeneralSettingsList()) {
            if (settings.getName().equals(getSelectedGeneralSettingsName())) {
                return settings;
            }
        }
        return null;
    }

    public DbSettings getDbSettings() {
        for (DbSettings settings : getDbSettingsList()) {
            if (settings.getName().equals(getSelectedDbSettingsName())) {
                return settings;
            }
        }
        return null;
    }

    public ImageServerSettings getImageServerSettings() {
        for (ImageServerSettings settings : getImageServerSettingsList()) {
            if (settings.getName().equals(getSelectedImageServerSettingsName())) {
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


    public String getSelectedGeneralSettingsName() {
        if (selectedGeneralSettings == null || selectedGeneralSettings.isEmpty()) {
            try {
                readSelectedSettingsFromDb();
            } catch (SQLException e) {
                e.printStackTrace();
                selectedGeneralSettings = DEFAULT;
            }
        }
        return selectedGeneralSettings;
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

    public String getSelectedImageServerSettingsName() {
        if (selectedImageServerSettings == null || selectedImageServerSettings.isEmpty()) {
            try {
                readSelectedSettingsFromDb();
            } catch (SQLException e) {
                e.printStackTrace();
                selectedImageServerSettings = DEFAULT;
            }
        }
        return selectedImageServerSettings;
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


    public List<GeneralSettings> getGeneralSettingsList() {
        if (generalSettingsList == null) {
            readGeneralSettingsFromDb();
        }
        return generalSettingsList;
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

    public List<ImageServerSettings> getImageServerSettingsList() {
        if (imageServerSettingsList == null) {
            readImageServerSettingsFromDb();
        }
        return imageServerSettingsList;
    }


    public GeneralSettings getGeneralSettingsByName(String name) {
        for (GeneralSettings settings : getGeneralSettingsList()) {
            if (settings.getName().equals(name)) {
                return settings;
            }
        }
        return null;
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

    public ImageServerSettings getImageServerSettingsByName(String name) {
        for (ImageServerSettings settings : getImageServerSettingsList()) {
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

    public void updateGeneralSettings() {
        generalSettingsList = null;
    }

    public void updateLogSettings() {
        logSettingsList = null; // This will update the list when fetched next time
    }

    public void updateDbSettings() {
        dbSettingsList = null;
    }

    public void updateImageServerSettings() {
        imageServerSettingsList = null;
    }


    /*
     *                  DELETES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public boolean deleteSetting(DbSettingsObject toDelete) {
        boolean result = false;
        if (!toDelete.isDefault()) {
            // Set back default value
            switch (DbSettingsObject.getType(toDelete)) {
                case SETTINGS_TYPE_GENERAL:
                    selectNewSettings(getGeneralSettingsByName(DEFAULT));
                    break;
                case SETTINGS_TYPE_LOG:
                    selectNewSettings(getLogSettingsByName(DEFAULT));
                    break;
                case SETTINGS_TYPE_DB:
                    selectNewSettings(getDbSettingsByName(DEFAULT));
                    break;
                case SETTINGS_TYPE_IMAGE:
                    selectNewSettings(getImageServerSettingsByName(DEFAULT));
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
            case SETTINGS_TYPE_GENERAL:
                saveGeneralSettings((GeneralSettings) settings);
                break;
            case SETTINGS_TYPE_LOG:
                saveLogSettings((LogSettings) settings);
                break;
            case SETTINGS_TYPE_DB:
                saveDbSettings((DbSettings) settings);
                break;
            case SETTINGS_TYPE_IMAGE:
                saveImageServerSettings((ImageServerSettings) settings);
                break;
        }
        settings.setSaved(true);
    }

    private void saveGeneralSettings(GeneralSettings generalSettings) {
        if (generalSettings.isSaved()) {
            // Update
            updateGeneralSetting(generalSettings);
            readGeneralSettingsFromDb();
            notifyListeners(getGeneralSettings(), onGeneralSettingsChangedList);
        } else {
            // Insert
            insertGeneralSetting(generalSettings);
            readGeneralSettingsFromDb();

        }
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

    private void saveImageServerSettings(ImageServerSettings imageServerSettings) {
        if (imageServerSettings.isSaved()) {
            // Update
            updateImageServerSetting(imageServerSettings);
            readImageServerSettingsFromDb();
            notifyListeners(getLogSettings(), onLogSettingsChangedList);
        } else {
            // Insert
            insertImageServerSetting(imageServerSettings);
            readImageServerSettingsFromDb();
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
            case SETTINGS_TYPE_GENERAL:
                sql = scriptResource.readString("settings.sqlUpdateGeneral");
                break;
            case SETTINGS_TYPE_LOG:
                sql = scriptResource.readString("settings.sqlUpdateLog");
                break;
            case SETTINGS_TYPE_DB:
                sql = scriptResource.readString("settings.sqlUpdateDb");
                break;
            case SETTINGS_TYPE_IMAGE:
                sql = scriptResource.readString("settings.sqlUpdateImageServer");
                break;
        }

        try {
            selectSettings(settings, sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        switch (getType(settings)) {
            case SETTINGS_TYPE_GENERAL:
                selectedGeneralSettings = settings.getName();
                notifyListeners((GeneralSettings) settings, onGeneralSettingsChangedList);
                break;
            case SETTINGS_TYPE_LOG:
                selectedLogSettings = settings.getName();
                notifyListeners((LogSettings) settings, onLogSettingsChangedList);
                break;
            case SETTINGS_TYPE_DB:
                selectedDbSettings = settings.getName();
                notifyListeners((DbSettings) settings, onDbSettingsChangedList);
                break;
            case SETTINGS_TYPE_IMAGE:
                selectedImageServerSettings = settings.getName();
                notifyListeners((ImageServerSettings) settings, onImageServerSettingsChangedList);
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
                    selectedImageServerSettings = rs.getString("imageserversettings");
                    selectedLogSettings = rs.getString("logsettings");
                    selectedGeneralSettings = rs.getString("generalsettings");
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


    private void readGeneralSettingsFromDb() {
        generalSettingsList = new ArrayList<>();

        String sql = scriptResource.readString("generalsettings.sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    GeneralSettings gs = new GeneralSettings();
                    gs.setName(rs.getString("name"));
                    gs.setGuiDetailsView(rs.getString("guiDetailsView"));
                    gs.setGuiLookAndFeel(rs.getString("guiLookAndFeel"));
                    gs.setGuiStartUpFullScreen(rs.getBoolean("guiStartUpFullScreen"));
                    gs.setAutoOrderEnabled(rs.getBoolean("autoOrderEnabled"));

                    gs.setSaved(true);

                    generalSettingsList.add(gs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void readImageServerSettingsFromDb() {
        imageServerSettingsList = new ArrayList<>();

        String sql = scriptResource.readString("imageserversettings.sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    ImageServerSettings imageServerSettings = new ImageServerSettings();

                    imageServerSettings.setName(rs.getString("name"));
                    //imageServerSettings.setType(rs.getString("type"));

                    imageServerSettings.setImageServerName(rs.getString("imageServerName"));
                    imageServerSettings.setConnectAsName(rs.getString("connectAsName"));

                    DbSettings imageDbSettings = new DbSettings();
                    imageDbSettings.setDbName(rs.getString("dbname"));
                    imageDbSettings.setDbIp(rs.getString("dbip"));
                    imageDbSettings.setDbUserName(rs.getString("dbusername"));
                    imageDbSettings.setDbUserPw(rs.getString("dbuserpw"));

                    imageServerSettings.setImageDbSettings(imageDbSettings);

                    imageServerSettings.setSaved(true);

                    imageServerSettingsList.add(imageServerSettings);
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


    private void insertGeneralSetting(GeneralSettings general) {
        String sql = scriptResource.readString("generalsettings.sqlInsert");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, general.getName());
                stmt.setString(2, general.getGuiDetailsView().toString());
                stmt.setString(3, general.getGuiLookAndFeel());
                stmt.setBoolean(4, general.isGuiStartUpFullScreen());
                stmt.setBoolean(5, general.isAutoOrderEnabled());
                stmt.execute();
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

    private void insertImageServerSetting(ImageServerSettings set) {
        String sql = scriptResource.readString("imageserversettings.sqlInsert");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int ndx = 1;
                stmt.setString(ndx++, set.getName());
                stmt.setString(ndx++, set.getType().toString());
                stmt.setString(ndx++, set.getImageServerName());
                stmt.setString(ndx++, set.getConnectAsName());
                stmt.setString(ndx++, set.getImageDbSettings().getDbName());
                stmt.setString(ndx++, set.getImageDbSettings().getDbIp());
                stmt.setString(ndx++, set.getImageDbSettings().getDbUserName());
                stmt.setString(ndx++, set.getImageDbSettings().getDbUserPw());
                stmt.setString(ndx, Statics.DbTypes.Online.toString());
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
                stmt.setString(6, set.getDbType().toString());
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateGeneralSetting(GeneralSettings general) {
        String sql = scriptResource.readString("generalsettings.sqlUpdate");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, general.getGuiDetailsView().toString());
                stmt.setString(2, general.getGuiLookAndFeel());
                stmt.setBoolean(3, general.isGuiStartUpFullScreen());
                stmt.setBoolean(4, general.isAutoOrderEnabled());

                stmt.setString(5, general.getName()); // Where name
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

    private void updateImageServerSetting(ImageServerSettings set) {
        String sql = scriptResource.readString("imageserversettings.sqlUpdate");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int ndx = 1;
                stmt.setString(ndx++, set.getType().toString());
                stmt.setString(ndx++, set.getImageServerName());
                stmt.setString(ndx++, set.getConnectAsName());
                stmt.setString(ndx++, set.getImageDbSettings().getDbName());
                stmt.setString(ndx++, set.getImageDbSettings().getDbIp());
                stmt.setString(ndx++, set.getImageDbSettings().getDbUserName());
                stmt.setString(ndx++, set.getImageDbSettings().getDbUserPw());
                stmt.setString(ndx++, Statics.DbTypes.Online.toString());
                stmt.setString(ndx,  set.getName()); // Where name

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
                stmt.setString(5, set.getDbType().toString());

                stmt.setString(6, set.getName()); // Where name

                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
