package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.database.DbManager.db;

public class ProjectTypeLink extends DbObject {

    public static final String TABLE_NAME = "projecttypelinks";

    private long projectDirectoryId;
    private ProjectDirectory projectDirectory;

    private long projectTypeId;
    private ProjectType projectType;

    private String filePath;

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
//        if (projectDirectoryId < UNKNOWN_ID) {
//            projectDirectoryId = UNKNOWN_ID;
//        }
//        if (projectTypeId < UNKNOWN_ID) {
//            projectTypeId = UNKNOWN_ID;
//        }

        int ndx = 1;
        statement.setLong(ndx++, getProjectDirectoryId());
        statement.setLong(ndx++, getProjectTypeId());
        statement.setString(ndx++, filePath);
        return ndx;
    }

    public ProjectTypeLink() {
        super(TABLE_NAME);
    }

    public ProjectTypeLink(long projectId, long typeId, String filePath) {
        super(TABLE_NAME);
        setProjectDirectoryId(projectId);
        setProjectTypeId(typeId);
        setFilePath(filePath);
    }

    @Override
    public DbObject createCopy(DbObject copyInto) {
        return null;
    }

    @Override
    public ProjectTypeLink createCopy() {
        return null;
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                java.util.List<ProjectTypeLink> list = db().getProjectTypeLinks();
                if (!list.contains(this)) {
                    list.add(this);
                }
                // TODO ?db().notifyListeners(DbManager.OBJECT_INSERT, this, db().onProjectTYpeL);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                // TODO ?db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onProjectTypeChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                java.util.List<ProjectType> list = db().getProjectTypes();
                if (list.contains(this)) {
                    list.remove(this);
                }
                // TODO ?db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onProjectTypeChangedListenerList);
                break;
            }
        }
    }

    public long getProjectDirectoryId() {
        if (projectDirectoryId < UNKNOWN_ID && projectDirectory != null) {
            projectDirectoryId = projectDirectory.getId();
        }
        return projectDirectoryId;
    }

    public void setProjectDirectoryId(long projectDirectoryId) {
        this.projectDirectoryId = projectDirectoryId;
    }

    public ProjectDirectory getProjectDirectory() {
        if (projectDirectory == null) {
            projectDirectory = SearchManager.sm().findProjectDirectoryById(projectDirectoryId);
        }
        return projectDirectory;
    }

    public void setProjectDirectory(ProjectDirectory projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

    public long getProjectTypeId() {
        if (projectTypeId < UNKNOWN_ID && projectType != null) {
            projectTypeId = projectType.getId();
        }
        return projectTypeId;
    }

    public void setProjectTypeId(long projectTypeId) {
        projectType = null;
        this.projectTypeId = projectTypeId;
    }

    public ProjectType getProjectType() {
        if (projectType == null) {
            projectType = SearchManager.sm().findProjectTypeById(projectTypeId);
        }
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public String getFilePath() {
        if (filePath == null) {
            filePath = "";
        }
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public File getFile() {
        if (!getFilePath().isEmpty()) {
            return new File(getFilePath());
        }
        return null;
    }
}
