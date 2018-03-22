package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.FileUtils;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

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
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert: {
                cache().add(this);
                break;
            }
            case Delete: {
                cache().remove(this);
                break;
            }
        }
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
            case Code:
                projectObject = new ProjectCode(getId());
                break;
            case Pcb:
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
            if (canBeSaved) {
                projectCodes = SearchManager.sm().findProjectCodesByProjectId(getId());
            } else {
                projectCodes = new ArrayList<>();
            }
        }
        return projectCodes;
    }

    public List<ProjectPcb> getProjectPcbs() {
        if (projectPcbs == null) {
            if (canBeSaved) {
                projectPcbs = SearchManager.sm().findProjectPcbsByProjectId(getId());
            } else {
                projectPcbs = new ArrayList<>();
            }
        }
        return projectPcbs;
    }

    public List<ProjectOther> getProjectOthers() {
        if (projectOthers == null) {
            if (canBeSaved) {
                projectOthers = SearchManager.sm().findProjectOthersByProjectId(getId());
            } else {
                projectOthers = new ArrayList<>();
            }
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

    public List<ProjectObject> getAllProjectObjects() {
        List<ProjectObject> projectObjects = new ArrayList<>();

        if (hasCodes()) projectObjects.addAll(getProjectCodes());
        if (hasPcbs()) projectObjects.addAll(getProjectPcbs());
        if (hasOthers()) projectObjects.addAll(getProjectOthers());

        return projectObjects;
    }
}
