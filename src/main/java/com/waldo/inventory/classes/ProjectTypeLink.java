package com.waldo.inventory.classes;

import com.waldo.inventory.database.SearchManager;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProjectTypeLink extends DbObject {

    public static final String TABLE_NAME = "projecttypelink";

    private long projectDirectoryId;
    private ProjectDirectory projectDirectory;

    private long projectTypeId;
    private ProjectType projectType;

    private String filePath;

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setLong(1, projectDirectoryId);
        statement.setLong(2, projectTypeId);
        statement.setString(3, filePath);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setLong(1, projectDirectoryId);
        statement.setLong(2, projectTypeId);
        statement.setString(3, filePath);
        statement.setLong(4, id); // WHERE id
        statement.execute();
    }

    public ProjectTypeLink() {
        super(TABLE_NAME);
    }


    @Override
    public ProjectTypeLink createCopy() {
        return null;
    }

    public long getProjectDirectoryId() {
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
        return projectTypeId;
    }

    public void setProjectTypeId(long projectTypeId) {
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
