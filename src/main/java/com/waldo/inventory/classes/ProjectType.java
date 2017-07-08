package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.parser.ProjectParser;
import com.waldo.inventory.database.LogManager;
import com.waldo.inventory.gui.Application;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProjectType extends DbObject {

    private static final LogManager LOG = LogManager.LOG(ProjectType.class);
    public static final String TABLE_NAME = "projecttypes";

    // Launcher
    private boolean useDefaultLauncher;
    private String launcherPath;

    // Detection
    private String extension;
    private boolean openAsFolder; // Open File as directory or as file
    private boolean matchExtension; // Match extension or contain extension
    private boolean useParentFolder; // Use project directory or use matching folder

    // Parser
    private String parserName; // For db

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setString(3, extension);
        statement.setBoolean(4, openAsFolder);
        statement.setBoolean(5, useDefaultLauncher);
        statement.setString(6, launcherPath);
        statement.setBoolean(7, matchExtension);
        statement.setBoolean(8, useParentFolder);
        statement.setString(9, parserName);
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
        statement.setBoolean(7, matchExtension);
        statement.setBoolean(8, useParentFolder);
        statement.setString(9, parserName);
        statement.setLong(10, id); // WHERE id
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
            } else {
                ProjectType ref = (ProjectType) obj;

                if (!(ref.getExtension().equals(getExtension()))) {
                    return false;
                }
                if (!(ref.isOpenAsFolder() == isOpenAsFolder())) {
                    return false;
                }
                if (!(ref.isUseDefaultLauncher() == isUseDefaultLauncher())) {
                    return false;
                }
                if (!(ref.getLauncherPath().equals(getLauncherPath()))) {
                    return false;
                }
                if (!(ref.isMatchExtension() == isMatchExtension())) {
                    return false;
                }
                if (!(ref.isUseParentFolder() == isUseParentFolder())) {
                    return false;
                }
                if (!(ref.getParserName().equals(getParserName()))) {
                    return false;
                }
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
    public ProjectType createCopy(DbObject copyInto) {
        ProjectType projectType = (ProjectType) copyInto;
        copyBaseFields(projectType);
        projectType.setExtension(getExtension());
        projectType.setOpenAsFolder(isOpenAsFolder());
        projectType.setUseDefaultLauncher(isUseDefaultLauncher());
        projectType.setLauncherPath(getLauncherPath());
        projectType.setMatchExtension(isMatchExtension());
        projectType.setUseParentFolder(isUseParentFolder());
        projectType.setParserName(getParserName());

        return projectType;
    }

    @Override
    public ProjectType createCopy() {
        return createCopy(new ProjectType());
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

    public boolean isMatchExtension() {
        return matchExtension;
    }

    public void setMatchExtension(boolean matchExtension) {
        this.matchExtension = matchExtension;
    }

    public boolean isUseParentFolder() {
        return useParentFolder;
    }

    public void setUseParentFolder(boolean useParentFolder) {
        this.useParentFolder = useParentFolder;
    }

    public String getParserName() {
        if (parserName == null) {
            parserName = "";
        }
        return parserName;
    }

    public void setParserName(String parserName) {
        this.parserName = parserName;
    }

    public ProjectParser getProjectParser() {
        if (getParserName().isEmpty()) {
             return null;
        } else {
            return Application.getProjectParser(parserName);
        }
    }

    public boolean hasParser() {
        return (parserName != null && !parserName.isEmpty());
    }
}
