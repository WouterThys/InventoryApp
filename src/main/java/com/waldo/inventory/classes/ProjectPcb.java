package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class ProjectPcb extends DbObject {

    public static final String TABLE_NAME = "projectpcbs";

    // Variables
    private String directory;

    private long projectId;
    private Project project;

    private long projectIDEId;
    private ProjectIDE projectIDE;

    private String remarks;

    // Items??


    public ProjectPcb() {
        super(TABLE_NAME);
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        // Add parameters
        statement.setString(ndx++, getDirectory());
        statement.setLong(ndx++, getProjectId());
        statement.setLong(ndx++, getProjectIDEId());
        statement.setString(ndx++, getRemarks());

        return ndx;
    }

    @Override
    public ProjectPcb createCopy(DbObject copyInto) {
        ProjectPcb cpy = (ProjectPcb) copyInto;
        copyBaseFields(cpy);

        // Add variables
        cpy.setDirectory(getDirectory());
        cpy.setProjectId(getProjectId());
        cpy.setProjectIDEId(getProjectIDEId());
        cpy.setRemarks(getRemarks());

        return cpy;
    }

    @Override
    public ProjectPcb createCopy() {
        return createCopy(new ProjectPcb());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<ProjectPcb> list = db().getProjectPcbs();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<ProjectPcb> list = db().getProjectPcbs();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onProjectPcbChangedListenerList);
    }

    public static ProjectPcb getUnknownProjectPcbs() {
        ProjectPcb u = new ProjectPcb();
        u.setName(UNKNOWN_NAME);
        u.setId(UNKNOWN_ID);
        u.setCanBeSaved(false);
        return u;
    }

    // Getters and setters
    public String getDirectory() {
        if (directory == null) {
            directory = "";
        }
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        project = null;
        this.projectId = projectId;
    }

    public Project getProject() {
        if (project == null) {
            SearchManager.sm().findProjectById(projectId);
        }
        return project;
    }

    public long getProjectIDEId() {
        return projectIDEId;
    }

    public void setProjectIDEId(long projectIDEId) {
        projectIDE = null;
        this.projectIDEId = projectIDEId;
    }

    public ProjectIDE getProjectIDE() {
        if (projectIDE == null) {
            projectIDE = SearchManager.sm().findProjectIDEById(projectIDEId);
        }
        return projectIDE;
    }

    public String getRemarks() {
        if (remarks == null) {
            remarks = "";
        }
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}