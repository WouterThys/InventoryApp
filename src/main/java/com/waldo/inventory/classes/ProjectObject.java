package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ProjectObject extends DbObject {

    // Variables
    private String directory;

    private long projectId;
    private Project project;

    private long projectIDEId;
    private ProjectIDE projectIDE;

    private String remarksFile;


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
        try (InputStream inputStream = new FileInputStream(getRemarksFile())) {
            statement.setBlob(ndx++, inputStream);
            statement.setLong(ndx, getId());
            statement.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return DbManager.OBJECT_SCRIPT_EXECUTED;
    }

    @Override
    public ProjectObject createCopy(DbObject copyInto) {
        ProjectObject cpy = (ProjectObject) copyInto;
        copyBaseFields(cpy);

        // Add variables
        cpy.setDirectory(getDirectory());
        cpy.setProjectId(getProjectId());
        cpy.setProjectIDEId(getProjectIDEId());
        cpy.setRemarksFile(getRemarksFile());

        return cpy;
    }

    public void createName() {
        if (!getDirectory().isEmpty()) {
            setName(FileUtils.getLastPathPart(getDirectory()));
        }
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
            project = SearchManager.sm().findProjectById(projectId);
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

    public File getRemarksFile() {
        if (remarksFile != null && !remarksFile.isEmpty()) {
            return new File(remarksFile);
        }
        return null;
    }

    public String getRemarksFileName() {
        if (remarksFile == null) {
            remarksFile = "";
        }
        return remarksFile;
    }

    public void setRemarksFile(File remarksFile) {
        if (remarksFile != null && remarksFile.exists()) {
            this.remarksFile = remarksFile.getAbsolutePath();
        }
    }
}