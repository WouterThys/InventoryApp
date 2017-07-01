package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.database.DbManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.SearchManager.sm;

public class Project extends DbObject {

    private static final Logger LOG = LoggerFactory.getLogger(Project.class);
    public static final String TABLE_NAME = "projects";

    private List<ProjectDirectory> projectDirectories;

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, id); // WHERE id
        statement.execute();
    }

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

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result) {
            if (!(obj instanceof Project)) {
                return false;
            }
            if (!(((Project) obj).getProjectDirectories().equals(getProjectDirectories()))) {
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
    public Product createCopy() {
        Product product = new Product();
        copyBaseFields(product);
        // Directories will be fetched from db when the getter is called
        return product;
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public boolean hasDirectory(String directory) {
        for (ProjectDirectory dir : getProjectDirectories()) {
            if (dir.getDirectory().equals(directory)) {
                return true;
            }
        }
        return false;
    }


    public void addDirectory(ProjectDirectory projectDirectory, List<ProjectType> projectTypes) {
        if (projectDirectory != null && !hasDirectory(projectDirectory.getDirectory())) {
            updateProjectTypesToDirectory(projectDirectory, projectTypes);
            if (id > UNKNOWN_ID) {
                projectDirectory.setProjectId(id);
                projectDirectory.save();
                save();
            }
            getProjectDirectories().add(projectDirectory);
        }
    }

    public void addDirectory(String projectDirectory, List<ProjectType> projectTypes) {
        ProjectDirectory directory = new ProjectDirectory();
        directory.setDirectory(projectDirectory);
        addDirectory(directory, projectTypes);
    }

    public void updateDirectory(ProjectDirectory projectDirectory, List<ProjectType> projectTypes) {
        if (projectDirectory != null && getProjectDirectories().contains(projectDirectory)) {
            updateProjectTypesToDirectory(projectDirectory, projectTypes);
            if (id > UNKNOWN_ID) {
                projectDirectory.setProjectId(id);
                projectDirectory.save();
                save();
            }
        }
    }

    public void removeDirectory(ProjectDirectory projectDirectory) {
        if (getProjectDirectories().contains(projectDirectory)) {
            getProjectDirectories().remove(projectDirectory);
            projectDirectory.delete(); // Should also delete entry in link table
        }
    }

    public void updateProjectTypesToDirectory(ProjectDirectory projectDirectory, List<ProjectType> projectTypes) {
        for (ProjectType type : projectTypes) {

            if (type.isOpenAsFolder()) {
                if (type.isMatchExtension()) {
                    if(FileUtils.is(new File(projectDirectory.getDirectory()), type.getExtension())) {
                        projectDirectory.addProjectType(type, new File(projectDirectory.getDirectory()));
                    } else {
                        List<File> foundFiles = FileUtils.findFileInFolder(new File(projectDirectory.getDirectory()), type.getExtension(), false);
                        for (File f : foundFiles) {
                            projectDirectory.addProjectType(type, f);
                        }
                    }
                } else {
                    if (type.isUseParentFolder()) {
                        List<File> foundFiles = FileUtils.containsGetParents(new File(projectDirectory.getDirectory()), type.getExtension());
                        for(File f :foundFiles) {
                            projectDirectory.addProjectType(type, f);
                        }
                    } else {
                        List<File> foundFiles = FileUtils.findFileInFolder(new File(projectDirectory.getDirectory()), type.getExtension(), false);
                        for (File f : foundFiles) {
                            projectDirectory.addProjectType(type, f);
                        }
                    }
                }
            } else {
                List<File> foundFiles = FileUtils.findFileInFolder(new File(projectDirectory.getDirectory()), type.getExtension(), true);
                for (File f : foundFiles) {
                    projectDirectory.addProjectType(type, f);
                }
            }
        }
    }

    public void saveAll() throws SQLException {
        // Save project
        saveSynchronously();
        int cnt = 0;
        for (ProjectDirectory directory : getProjectDirectories()) {
            if (directory.getId() <= UNKNOWN_ID) {
                directory.setProjectId(id);
                directory.setName(getName() + "-dir" + String.valueOf(cnt));
                cnt++;
            }
            // Save directory
            directory.saveSynchronously();
            for (ProjectType type : directory.getProjectTypes().keySet())  {
                for (File file : directory.getProjectTypes().get(type)) {
                    // Save link between type and directory
                    ProjectTypeLink ptl = sm().findProjectTypeLink(directory.getId(), type.getId(), file.getAbsolutePath());
                    if (ptl == null) {
                        ptl = new ProjectTypeLink();
                    }
                    ptl.setProjectTypeId(type.getId());
                    ptl.setProjectDirectoryId(directory.getId());
                    ptl.setFilePath(file.getAbsolutePath());
                    ptl.saveSynchronously();
                }
            }
        }
    }


    /*
     *                  GETTERS - SETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public List<ProjectDirectory> getProjectDirectories() {
        if (projectDirectories == null) {
            projectDirectories = DbManager.db().getProjectDirectoryListForProject(id);
        }
        return projectDirectories;
    }

    public void setProjectDirectories(List<ProjectDirectory> projectDirectories) {
        this.projectDirectories = projectDirectories;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public String getName() {
        return super.getName();
    }
}
