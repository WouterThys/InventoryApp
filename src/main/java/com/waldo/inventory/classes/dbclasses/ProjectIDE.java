package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.parser.PcbItemParser;
import com.waldo.inventory.Utils.parser.PcbParser;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.LogManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

public class ProjectIDE extends DbObject {

    private static final LogManager LOG = LogManager.LOG(ProjectIDE.class);
    public static final String TABLE_NAME = "projectides";

    private String projectType; // Statics.ProjectTypes

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

    public ProjectIDE() {
        super(TABLE_NAME);
    }

    public void launch(File file) throws IOException {
        if (useDefaultLauncher) {
            Desktop.getDesktop().open(file);
        } else {
            Process p = Runtime.getRuntime().exec(launcherPath + " " + file.getAbsolutePath());
        }
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setString(ndx++, projectType);
        statement.setString(ndx++, extension);
        statement.setBoolean(ndx++, openAsFolder);
        statement.setBoolean(ndx++, useDefaultLauncher);
        statement.setString(ndx++, launcherPath);
        statement.setBoolean(ndx++, matchExtension);
        statement.setBoolean(ndx++, useParentFolder);
        statement.setString(ndx++, parserName);
        return ndx;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result) {
            if (!(obj instanceof ProjectIDE)) {
                return false;
            } else {
                ProjectIDE ref = (ProjectIDE) obj;

                if (!(ref.getProjectType().equals(getProjectType()))) {
                    return false;
                }
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
    public ProjectIDE createCopy(DbObject copyInto) {
        ProjectIDE projectIDE = (ProjectIDE) copyInto;
        copyBaseFields(projectIDE);
        projectIDE.setProjectType(getProjectType());
        projectIDE.setExtension(getExtension());
        projectIDE.setOpenAsFolder(isOpenAsFolder());
        projectIDE.setUseDefaultLauncher(isUseDefaultLauncher());
        projectIDE.setLauncherPath(getLauncherPath());
        projectIDE.setMatchExtension(isMatchExtension());
        projectIDE.setUseParentFolder(isUseParentFolder());
        projectIDE.setParserName(getParserName());

        return projectIDE;
    }

    @Override
    public ProjectIDE createCopy() {
        return createCopy(new ProjectIDE());
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                java.util.List<ProjectIDE> list = cache().getProjectIDES();
                if (!list.contains(this)) {
                    list.add(this);
                }
                cache().notifyListeners(DatabaseAccess.OBJECT_INSERT, this, cache().onProjectIDEChangedListenerList);
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                cache().notifyListeners(DatabaseAccess.OBJECT_UPDATE, this, cache().onProjectIDEChangedListenerList);
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                java.util.List<ProjectIDE> list = cache().getProjectIDES();
                if (list.contains(this)) {
                    list.remove(this);
                }
                cache().notifyListeners(DatabaseAccess.OBJECT_DELETE, this, cache().onProjectIDEChangedListenerList);
                break;
            }
        }
    }

    public String getProjectType() {
        if (projectType == null) {
            projectType = "";
        }
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
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

    public PcbParser getPcbItemParser() {
        if (getParserName().isEmpty()) {
             return null;
        } else {
            return PcbItemParser.getInstance().getParser(parserName);
        }
    }

    public boolean hasParser() {
        return (parserName != null && !parserName.isEmpty());
    }
}
