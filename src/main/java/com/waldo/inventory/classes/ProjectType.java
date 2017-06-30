package com.waldo.inventory.classes;

import com.waldo.inventory.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProjectType extends DbObject {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectType.class);
    public static final String TABLE_NAME = "projecttypes";

    private String extension;
    private boolean openAsFolder;
    private boolean useDefaultLauncher;
    private String launcherPath;

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setString(3, extension);
        statement.setBoolean(4, openAsFolder);
        statement.setBoolean(5, useDefaultLauncher);
        statement.setString(6, launcherPath);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setString(3, extension);
        statement.setBoolean(4, openAsFolder);
        statement.setBoolean(5, useDefaultLauncher);
        statement.setString(6, launcherPath);
        statement.setLong(7, id); // WHERE id
        statement.execute();
    }

    public ProjectType() {
        super(TABLE_NAME);
    }

    public static ProjectType getUnknownProjectType() {
        ProjectType pt = new ProjectType();
        pt.setName(UNKNOWN_NAME);
        pt.setId(UNKNOWN_ID);
        pt.setCanBeSaved(false);
        return pt;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result) {
            if (!(obj instanceof ProjectType)) {
                return false;
            }
            if (!(((ProjectType)obj).getExtension().equals(getExtension()))) {
                return false;
            }
            if (!(((ProjectType)obj).isOpenAsFolder() == isOpenAsFolder())) {
                return false;
            }
            if (!(((ProjectType)obj).isUseDefaultLauncher() == isUseDefaultLauncher())) {
                return false;
            }
            if (!(((ProjectType)obj).getLauncherPath().equals(getLauncherPath()))) {
                return false;
            }

        }
        return result;
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {
            return (getExtension().toUpperCase().contains(searchTerm));
        }
    }

    @Override
    public ProjectType createCopy() {
        ProjectType projectType = new ProjectType();
        copyBaseFields(projectType);
        projectType.setExtension(getExtension());
        projectType.setOpenAsFolder(isOpenAsFolder());
        projectType.setUseDefaultLauncher(getUseDefaultLauncher());
        projectType.setLauncherPath(getLauncherPath());
        return projectType;
    }

    public String getExtension() {
        if (extension == null) {
            extension = "";
        }
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isOpenAsFolder() {
        return openAsFolder;
    }

    public void setOpenAsFolder(boolean openAsFolder) {
        this.openAsFolder = openAsFolder;
    }

    public void setOpenAsFolder(String openAsFolder) {
        try {
            this.openAsFolder = Boolean.valueOf(openAsFolder);
        } catch (Exception e) {
            LOG.error("Failed to parse boolean: " + openAsFolder + e);
        }
    }

    public String getOpenAsFolder() {
        return String.valueOf(openAsFolder);
    }

    public boolean isUseDefaultLauncher() {
        return useDefaultLauncher;
    }

    public void setUseDefaultLauncher(boolean useDefaultLauncher) {
        this.useDefaultLauncher = useDefaultLauncher;
    }

    public void setUseDefaultLauncher(String useDefaultLauncher) {
        try {
            this.useDefaultLauncher = Boolean.valueOf(useDefaultLauncher);
        } catch (Exception e) {
            LOG.error("Failed to parse boolean: " + useDefaultLauncher  + e);
        }
    }

    public String getUseDefaultLauncher() {
        return String.valueOf(useDefaultLauncher);
    }

    public String getLauncherPath() {
        if (launcherPath == null) {
            launcherPath = "";
        }
        return launcherPath;
    }

    public void setLauncherPath(String launcherPath) {
        this.launcherPath = launcherPath;
    }
}
