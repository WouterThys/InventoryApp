package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectDirectory extends DbObject {

    public static final String TABLE_NAME = "projectdirectories";

    private String directory;

    private long projectId;
    private Project project;

    private List<ProjectType> projectTypes;

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setString(3, directory);
        statement.setLong(4, projectId);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setString(3, directory);
        statement.setLong(4, projectId);
        statement.setLong(5, id); // WHERE id
        statement.execute();
    }

    public ProjectDirectory() {
        super(TABLE_NAME);
    }

    public static ProjectDirectory getUnknownProjectDirectory() {
        ProjectDirectory pt = new ProjectDirectory();
        pt.setName(UNKNOWN_NAME);
        pt.setId(UNKNOWN_ID);
        pt.setCanBeSaved(false);
        return pt;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result) {
            if (!(obj instanceof ProjectDirectory)) {
                return false;
            }
            ProjectDirectory ref = (ProjectDirectory) obj;
            if (!(ref.getDirectory().equals(getDirectory()))) { return false; }
            if (!(ref.getProjectId() == getProjectId())) return false;
            if (!(ref.getProjectTypes() == getProjectTypes())) return false;
        }
        return result;
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {
            return false; // TODO
        }
    }

    @Override
    public ProjectDirectory createCopy() {
        ProjectDirectory projectDirectory = new ProjectDirectory();
        copyBaseFields(projectDirectory);
        projectDirectory.setDirectory(getDirectory());
        projectDirectory.setProjectId(getProjectId());
        projectDirectory.setProjectTypes(getProjectTypes());
        return projectDirectory;
    }

    public String getDirectory() {
        if (directory == null) {
            return "";
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
        this.projectId = projectId;
    }

    public Project getProject() {
        if (project == null) {
            if (projectId > UNKNOWN_ID) {
                project = SearchManager.sm().findProjectById(projectId);
                if (project == null) {
                    project = Project.getUnknownProject();
                }
            } else {
                project = Project.getUnknownProject();
            }
        }
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<ProjectType> getProjectTypes() {
        if (projectTypes == null) {
            projectTypes = DbManager.db().getProjectTypesForProjectDirectory(id);
        }
        return projectTypes;
    }

    public void setProjectTypes(List<ProjectType> projectTypes) {
        this.projectTypes = projectTypes;
    }
}
