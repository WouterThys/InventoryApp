package com.waldo.inventory.gui.panels.projectpanel.projectdetails;

import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.gui.Application;

public class ProjectDetailsPanel extends ProjectDetailsPanelLayout {

    public ProjectDetailsPanel(Application application) {
        super(application);
        initializeComponents();
        initializeLayouts();
    }

    @Override
    public void updateComponents(Object object) {
        if (object == null) {
            setVisible(false);
            selectedProject = null;
        } else {
            if (object instanceof Project) {
                setVisible(true);
                selectedProject = (Project) object;

                updateIcon(selectedProject);
                updateTextFields(selectedProject);
                updateDirectoryList(selectedProject);
            }
        }
    }

    private void updateIcon(Project project) {
        iconLabel.setIcon(project.getIconPath(), 128, 128);
    }

    private void updateTextFields(Project project) {
        nameTextField.setText(project.getName());
    }

    private void updateDirectoryList(Project project) {
        listModel.removeAllElements();
        for(ProjectDirectory directory : project.getProjectDirectories()) {
            listModel.addElement(directory);
        }
    }

}
