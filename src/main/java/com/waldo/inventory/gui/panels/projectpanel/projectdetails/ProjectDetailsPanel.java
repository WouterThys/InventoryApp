package com.waldo.inventory.gui.panels.projectpanel.projectdetails;

import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;

import java.nio.file.Path;
import java.nio.file.Paths;

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
        if (!project.getIconPath().isEmpty()) {
            Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgProjectsPath(), project.getIconPath());
            iconLabel.setIcon(path.toString(), 128, 128);
        }
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
