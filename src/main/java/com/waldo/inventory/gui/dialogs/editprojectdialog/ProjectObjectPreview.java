package com.waldo.inventory.gui.dialogs.editprojectdialog;

import com.waldo.inventory.classes.dbclasses.ProjectObject;

public class ProjectObjectPreview {

    private final ProjectObject projectObject;
    private boolean addToProject;

    public ProjectObjectPreview(ProjectObject projectObject) {
        this.projectObject = projectObject;
        this.addToProject = true;
    }

    public ProjectObject getProjectObject() {
        return projectObject;
    }

    public boolean isAddToProject() {
        return addToProject;
    }

    public void setAddToProject(boolean addToProject) {
        this.addToProject = addToProject;
    }
}
