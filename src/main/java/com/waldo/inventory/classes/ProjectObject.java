package com.waldo.inventory.classes;

import com.waldo.inventory.database.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ProjectObject extends DbObject {

    // Variables
    private String directory;

    private long projectId;
    private Project project;

    private long projectIDEId;
    private ProjectIDE projectIDE;

    private String remarks;


    public ProjectObject(String tableName) {
        super(tableName);
    }

    public ProjectObject(String tableName, String name) {
        super(tableName);
        setName(name);
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
    public ProjectObject createCopy(DbObject copyInto) {
        ProjectObject cpy = (ProjectObject) copyInto;
        copyBaseFields(cpy);

        // Add variables
        cpy.setDirectory(getDirectory());
        cpy.setProjectId(getProjectId());
        cpy.setProjectIDEId(getProjectIDEId());
        cpy.setRemarks(getRemarks());

        return cpy;
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