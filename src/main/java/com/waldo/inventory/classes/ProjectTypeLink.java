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
    private ProjectIDE projectIDE;

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
                // TODO ?db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onProjectIDEChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                java.util.List<ProjectIDE> list = db().getProjectIDES();
                if (list.contains(this)) {
                    list.remove(this);
                }
                // TODO ?db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onProjectIDEChangedListenerList);
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
        if (projectTypeId < UNKNOWN_ID && projectIDE != null) {
            projectTypeId = projectIDE.getId();
        }
        return projectTypeId;
    }

    public void setProjectTypeId(long projectTypeId) {
        projectIDE = null;
        this.projectTypeId = projectTypeId;
    }

    public ProjectIDE getProjectIDE() {
        if (projectIDE == null) {
            projectIDE = SearchManager.sm().findProjectIDEById(projectTypeId);
        }
        return projectIDE;
    }

    public void setProjectIDE(ProjectIDE projectIDE) {
        this.projectIDE = projectIDE;
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
