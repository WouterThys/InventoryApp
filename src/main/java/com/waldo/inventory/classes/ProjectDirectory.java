package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class ProjectDirectory extends DbObject {

    public static final String TABLE_NAME = "projectdirectories";

    private String directory;

    private long projectId;
    private Project project;

    private HashMap<ProjectType, List<File>> projectTypes;


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
    public int addParameters(PreparedStatement statement) throws SQLException {
//        if (projectId < UNKNOWN_ID) {
//            projectId = UNKNOWN_ID;
//        }

        int ndx = 1;
        statement.setString(ndx++, getName());
        statement.setLong(ndx++, getProjectId());
        statement.setString(ndx++, getDirectory());
        return ndx;
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
            if (!(ref.getProjectTypeMap() == getProjectTypeMap())) return false;
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

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<ProjectDirectory> list = db().getProjectDirectories();
                if (!list.contains(this)) {
                    list.add(this);
                }
                db().notifyListeners(DbManager.OBJECT_INSERT, this, db().onProjectDirectoryChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onProjectDirectoryChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<ProjectDirectory> list = db().getProjectDirectories();
                if (list.contains(this)) {
                    list.remove(this);
                }
                db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onProjectDirectoryChangedListenerList);
                break;
            }
        }
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void addProjectType(ProjectType projectType, File file) {
        if (projectType != null) {
            if (getProjectTypeMap().containsKey(projectType)) {
                getProjectTypeMap().computeIfAbsent(projectType, k -> new ArrayList<>());
            } else {
                getProjectTypeMap().put(projectType, new ArrayList<>());
            }
            getProjectTypeMap().get(projectType).add(file);
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
        if (projectId < UNKNOWN_ID && project != null) {
            projectId = project.getId();
        }
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public void setProject(Project project) {
        this.project = project;
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

    public HashMap<ProjectType, List<File>> getProjectTypeMap() {
        if (projectTypes == null) {
            projectTypes = DbManager.db().getProjectTypesForProjectDirectory(id);
        }
        return projectTypes;
    }

    public List<ProjectType> getProjectTypes() {
        return new ArrayList<>(getProjectTypeMap().keySet());
    }

    public List<File> getProjectFilesForType(ProjectType type) {
        return getProjectTypeMap().get(type);
    }
}
