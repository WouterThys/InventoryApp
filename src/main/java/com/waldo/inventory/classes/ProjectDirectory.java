package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ProjectDirectory extends DbObject {

    public static final String TABLE_NAME = "projectdirectories";

    private String directory;

    private long projectId;
    private Project project;

    private HashMap<ProjectType, ArrayList<File>> projectTypes;

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setLong(2, projectId);
        statement.setString(3, directory);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setLong(2, projectId);
        statement.setString(3, directory);
        statement.setLong(4, id); // WHERE id
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
    public String toString() {
        String result = getDirectory();

        if (result.length() > 30) {
            while (result.length() > 30) {
                int ndx = result.indexOf("/");
                result = result.substring(ndx+1, result.length() - 1);
            }
            result = ".../" + result;
        }

        return result;
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
    public ProjectDirectory createCopy(DbObject copyInto) {
        ProjectDirectory projectDirectory = (ProjectDirectory) copyInto;
        copyBaseFields(projectDirectory);
        projectDirectory.setDirectory(getDirectory());
        projectDirectory.setProjectId(getProjectId());
        return projectDirectory;
    }

    @Override
    public ProjectDirectory createCopy() {
        return createCopy(new ProjectDirectory());
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void addProjectType(ProjectType projectType, File file) {
        if (projectType != null) {
            if (getProjectTypes().containsKey(projectType)) {
                getProjectTypes().computeIfAbsent(projectType, k -> new ArrayList<>());
            } else {
                getProjectTypes().put(projectType, new ArrayList<>());
            }
            getProjectTypes().get(projectType).add(file);
        }
    }


    /*
     *                  GETTERS - SETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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

    public HashMap<ProjectType, ArrayList<File>> getProjectTypes() {
        if (projectTypes == null) {
            projectTypes = DbManager.db().getProjectTypesForProjectDirectory(id);
        }
        return projectTypes;
    }

}
