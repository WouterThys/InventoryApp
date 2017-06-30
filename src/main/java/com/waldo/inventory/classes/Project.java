package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.database.DbManager;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Project extends DbObject {

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
    public void addDirectory(ProjectDirectory projectDirectory) {
        if (projectDirectory != null && !getProjectDirectories().contains(projectDirectory)) {
            updateProjectTypesToDirectory(projectDirectory);
            if (id > UNKNOWN_ID) {
                projectDirectory.setProjectId(id);
                projectDirectory.save();
                save();
            }
            getProjectDirectories().add(projectDirectory);
        } else {
            addDirectory("");
        }
    }

    public void addDirectory(String projectDirectory) {
        ProjectDirectory directory = new ProjectDirectory();
        directory.setDirectory(projectDirectory);
        addDirectory(directory);
    }

    public void updateDirectory(ProjectDirectory projectDirectory) {
        if (projectDirectory != null && getProjectDirectories().contains(projectDirectory)) {
            updateProjectTypesToDirectory(projectDirectory);
            if (id > UNKNOWN_ID) {
                projectDirectory.setProjectId(id);
                projectDirectory.save();
                save();
            }
        }
    }

    public void removeDirectory(ProjectDirectory projectDirectory) {
        if (getProjectDirectories().contains(projectDirectory)) {
            projectDirectory.delete(); // Should also delete entry in link table
            getProjectDirectories().remove(projectDirectory);
        }
    }

    public void updateProjectTypesToDirectory(ProjectDirectory projectDirectory) {
        for (ProjectType type : DbManager.db().getProjectTypes()) {
            List<File> foundFiles;
            if (type.isOpenAsFolder()) {
                if(FileUtils.isOrContains(new File(projectDirectory.getDirectory()), type.getExtension())) {
                    projectDirectory.addProjectType(type, new File(projectDirectory.getDirectory()));
                }
            } else {
                foundFiles = FileUtils.findFileInFolder(new File(projectDirectory.getDirectory()), type.getExtension());
                for (File f : foundFiles) {
                    projectDirectory.addProjectType(type, f);
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
}
