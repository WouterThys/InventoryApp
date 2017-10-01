package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.managers.SearchManager;

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
    private boolean validated = false;

    private HashMap<ProjectIDE, List<File>> projectTypes;


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

        if (result.length() > 25) {
            while (result.length() > 25) {
                int ndx = result.indexOf("/");
                result = result.substring(ndx+1, result.length());
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
    public void addProjectType(ProjectIDE projectIDE, File file) {
        if (projectIDE != null) {
            if (getProjectTypeMap().containsKey(projectIDE)) {
                getProjectTypeMap().computeIfAbsent(projectIDE, k -> new ArrayList<>());
            } else {
                getProjectTypeMap().put(projectIDE, new ArrayList<>());
            }
            getProjectTypeMap().get(projectIDE).add(file);
        }
    }

    public void removeProjectType(ProjectIDE projectIDE, File file) {
        if (projectIDE != null) {
            List<File> filesForType = getProjectFilesForType(projectIDE);
            if (file != null) {
                if (filesForType.contains(file)) {
                    ProjectTypeLink linkToDelete = SearchManager.sm().findProjectTypeLink(getId(), projectIDE.getId(), file.getPath());
                    if (linkToDelete != null) {
                        linkToDelete.delete();
                    }
                    filesForType.remove(file);
                    if (filesForType.size() == 0) {
                        getProjectTypes().remove(projectIDE);
                    }
                }
            } else {
                for (File file1 : filesForType) {
                    ProjectTypeLink linkToDelete = SearchManager.sm().findProjectTypeLink(getId(), projectIDE.getId(), file1.getPath());
                    if (linkToDelete != null) {
                        linkToDelete.delete();
                    }
                    filesForType.remove(file1);
                    if (filesForType.size() == 0) {
                        getProjectTypes().remove(projectIDE);
                    }
                }
            }
        }
    }

    public List<ProjectValidationError> validate() {
        List<ProjectValidationError> errors = new ArrayList<>();

        File directoryFile = new File(getDirectory());
        if (directoryFile.exists()) {
            for (ProjectIDE type : getProjectTypes()) {
                for (File file : getProjectFilesForType(type)) {
                    if (!file.exists()) {
                        ProjectValidationError error = new ProjectValidationError(this, type, file, "Project type " + file.toString() + " does not exist..");
                        errors.add(error);
                    }
                }
            }
        } else {
            ProjectValidationError error = new ProjectValidationError(this, "Directory " + directoryFile.toString() + " does not exist..");
            errors.add(error);
        }

        validated = true;
        return errors;
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

    public HashMap<ProjectIDE, List<File>> getProjectTypeMap() {
        if (projectTypes == null) {
            projectTypes = DbManager.db().getProjectTypesForProjectDirectory(id);
        }
        return projectTypes;
    }

    public List<ProjectIDE> getProjectTypes() {
        return new ArrayList<>(getProjectTypeMap().keySet());
    }

    public List<File> getProjectFilesForType(ProjectIDE type) {
        return getProjectTypeMap().get(type);
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }
}
