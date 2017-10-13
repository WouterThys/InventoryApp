package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.managers.SearchManager;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class Project extends DbObject {

    public static final String TABLE_NAME = "projects";

    private String mainDirectory;
    private List<ProjectCode> projectCodes;
    private List<ProjectPcb> projectPcbs;
    private List<ProjectOther> projectOthers;

    public Project() {
        super(TABLE_NAME);
    }

    public Project(String name) {
        super(TABLE_NAME);
        setName(name);
    }

    public static Project getUnknownProject() {
        Project p = new Project();
        p.setName(UNKNOWN_NAME);
        p.setId(UNKNOWN_ID);
        p.setCanBeSaved(false);
        return p;
    }

    public boolean hasCodes() {
        return getProjectCodes().size() > 0;
    }

    public boolean hasPcbs() {
        return getProjectPcbs().size() > 0;
    }

    public boolean hasOthers() {
        return getProjectOthers().size() > 0;
    }

    public boolean isValidDirectory() {
        if (!getMainDirectory().isEmpty()) {
            File file = new File(getMainDirectory());
            return file.exists();
        }
        return false;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setString(ndx++, getMainDirectory());
        return ndx;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result) {
            if (!(obj instanceof Project)) {
                return false;
            }
            if (!(((Project) obj).getMainDirectory().equals(getMainDirectory()))) {
                return false;
            }
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
    public Project createCopy(DbObject copyInto) {
        Project product = (Project) copyInto;
        copyBaseFields(product);

        product.setMainDirectory(getMainDirectory());

        return product;
    }

    @Override
    public Project createCopy() {
        return createCopy(new Project());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<Project> list = db().getProjects();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<Project> list = db().getProjects();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onProjectChangedListenerList);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
//    private boolean hasDirectory(String directory) {
//        for (ProjectDirectory dir : getProjectDirectories()) {
//            if (dir.getDirectory().equals(directory)) {
//                return true;
//            }
//        }
//        return false;
//    }

    public void addProjectObject(ProjectObject object) {
        if (object instanceof ProjectCode) {
            ProjectCode code = (ProjectCode) object;
            if (!getProjectCodes().contains(code)) {
                projectCodes.add(code);
            }
        } else if (object instanceof ProjectPcb) {
            ProjectPcb pcb = (ProjectPcb) object;
            if (!getProjectPcbs().contains(pcb)) {
                projectPcbs.add(pcb);
            }
        } else if (object instanceof ProjectOther) {
            ProjectOther other = (ProjectOther) object;
            if (!getProjectOthers().contains(other)) {
                projectOthers.add(other);
            }
        }
    }

//    private void addDirectory(ProjectDirectory projectDirectory, List<ProjectIDE> projectIDES) {
//        if (projectDirectory != null && !hasDirectory(projectDirectory.getDirectory())) {
//            updateProjectTypesToDirectory(projectDirectory, projectIDES);
//            if (id > UNKNOWN_ID) {
//                projectDirectory.setProjectId(id);
//                projectDirectory.save();
//                save();
//            }
//            getProjectDirectories().add(projectDirectory);
//        }
//    }

//    public void addDirectory(String projectDirectory, List<ProjectIDE> projectIDES) {
//        ProjectDirectory directory = new ProjectDirectory();
//        directory.setDirectory(projectDirectory);
//        addDirectory(directory, projectIDES);
//    }

//    public void updateDirectory(ProjectDirectory projectDirectory, List<ProjectIDE> projectIDES) {
//        if (projectDirectory != null && getProjectDirectories().contains(projectDirectory)) {
//            updateProjectTypesToDirectory(projectDirectory, projectIDES);
//            if (id > UNKNOWN_ID) {
//                projectDirectory.setProjectId(id);
//                projectDirectory.save();
//                save();
//            }
//        }
//    }
//
//    public void removeDirectory(ProjectDirectory projectDirectory) {
//        if (getProjectDirectories().contains(projectDirectory)) {
//            getProjectDirectories().remove(projectDirectory);
//            projectDirectory.delete(); // Should also delete entry in link table
//        }
//    }

    public List<ProjectObject> findProjectsInDirectory(String projectDirectory, List<ProjectIDE> projectIDES) {
        List<ProjectObject> projects = new ArrayList<>();
        for (ProjectIDE ide : projectIDES) {
            if (ide.isOpenAsFolder()) {
                if (ide.isMatchExtension()) {
                    if(FileUtils.is(new File(projectDirectory), ide.getExtension())) {
                        projects.add(createProjectObject(projectDirectory, ide));
                    } else {
                        List<File> foundFiles = FileUtils.findFileInFolder(new File(projectDirectory), ide.getExtension(), false);
                        for (File f : foundFiles) {
                            projects.add(createProjectObject(f.getAbsolutePath(), ide));
                        }
                    }
                } else {
                    if (ide.isUseParentFolder()) {
                        List<File> foundFiles = FileUtils.containsGetParents(new File(projectDirectory), ide.getExtension());
                        for(File f :foundFiles) {
                            projects.add(createProjectObject(f.getAbsolutePath(), ide));
                        }
                    } else {
                        List<File> foundFiles = FileUtils.findFileInFolder(new File(projectDirectory), ide.getExtension(), false);
                        for (File f : foundFiles) {
                            projects.add(createProjectObject(f.getAbsolutePath(), ide));
                        }
                    }
                }
            } else {
                List<File> foundFiles = FileUtils.findFileInFolder(new File(projectDirectory), ide.getExtension(), true);
                for (File f : foundFiles) {
                    projects.add(createProjectObject(f.getAbsolutePath(), ide));
                }
            }
        }
        return projects;
    }

    private ProjectObject createProjectObject(String projectDirectory, ProjectIDE projectIDE) {
        ProjectObject projectObject;
        switch (projectIDE.getProjectType()) {
            case Statics.ProjectTypes.Code:
                projectObject = new ProjectCode(getId());
                break;
            case Statics.ProjectTypes.Pcb:
                projectObject = new ProjectPcb(getId());
                break;
            default:
                projectObject = new ProjectOther(getId());
                break;
        }
        projectObject.setDirectory(projectDirectory);
        projectObject.setName(FileUtils.getLastPathPart(projectDirectory));
        projectObject.setProjectIDEId(projectIDE.getId());
        return projectObject;
    }

//    public void saveAll() throws SQLException {
//        // Save project
//        save();
//        int cnt = 0;
//        for (ProjectDirectory directory : getProjectDirectories()) {
//            if (directory.getId() <= UNKNOWN_ID) {
//                directory.setProject(this);
//                directory.setName(getName() + "-dir" + String.valueOf(cnt));
//                cnt++;
//            }
//            // Save directory
//            directory.save();
//            for (ProjectIDE type : directory.getProjectTypeMap().keySet())  {
//                for (File file : directory.getProjectTypeMap().get(type)) {
//                    // Save link between type and directory
//                    ProjectTypeLink ptl = sm().findProjectTypeLink(directory.getId(), type.getId(), file.getAbsolutePath());
//                    if (ptl == null) {
//                        ptl = new ProjectTypeLink();
//                    }
//                    ptl.setProjectIDE(type);
//                    ptl.setProjectDirectory(directory);
//                    ptl.setFilePath(file.getAbsolutePath());
//                    ptl.save();
//                }
//            }
//        }
//    }
//
//    public List<ProjectValidationError> validate() {
//        List<ProjectValidationError> errors = new ArrayList<>();
//        for (ProjectDirectory directory : getProjectDirectories()) {
//            errors.addAll(directory.validate());
//        }
//        validated = true;
//        return errors;
//    }
//
//    public void recreateProjectStructure() {
//        List<ProjectDirectory> newDirs = new ArrayList<>();
//
//        for (int i = getProjectDirectories().size() - 1; i >= 0; i--) {
//            ProjectDirectory directory = getProjectDirectories().get(i);
//            File dirFile = new File(directory.getDirectory());
//            if (dirFile.exists()) {
//                newDirs.add(directory);
//            }
//            removeDirectory(directory);
//        }
//
//        for (ProjectDirectory directory : newDirs) {
//            addDirectory(directory, DbManager.db().getProjectIDES());
//        }
//    }

    /*
     *                  GETTERS - SETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//    public List<ProjectDirectory> getProjectDirectories() {
//        if (projectDirectories == null) {
//            projectDirectories = DbManager.db().getProjectDirectoryListForProject(id);
//        }
//        return projectDirectories;
//    }
//
//    public void setProjectDirectories(List<ProjectDirectory> projectDirectories) {
//        this.projectDirectories = projectDirectories;
//    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getIconPath() {
        return super.getIconPath();
    }

    @Override
    public void setIconPath(String iconPath) {
        super.setIconPath(iconPath);
    }

    public List<ProjectCode> getProjectCodes() {
        if (projectCodes == null) {
            projectCodes = SearchManager.sm().findProjectCodesByProjectId(getId());
        }
        return projectCodes;
    }

    public List<ProjectPcb> getProjectPcbs() {
        if (projectPcbs == null) {
            projectPcbs = SearchManager.sm().findProjectPcbsByProjectId(getId());
        }
        return projectPcbs;
    }

    public List<ProjectOther> getProjectOthers() {
        if (projectOthers == null) {
            projectOthers = SearchManager.sm().findProjectOthersByProjectId(getId());
        }
        return projectOthers;
    }

    public void updateProjectCodes() {
        projectCodes = null;
    }

    public void updateProjectPcbs() {
        projectPcbs = null;
    }

    public void updateProjectOthers() {
        projectOthers = null;
    }

    public String getMainDirectory() {
        if (mainDirectory == null) {
            mainDirectory = "";
        }
        return mainDirectory;
    }

    public void setMainDirectory(String mainDirectory) {
        this.mainDirectory = mainDirectory;
    }
}
