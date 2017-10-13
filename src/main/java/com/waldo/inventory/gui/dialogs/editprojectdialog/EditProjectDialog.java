package com.waldo.inventory.gui.dialogs.editprojectdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectIDE;
import com.waldo.inventory.classes.ProjectObject;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class EditProjectDialog extends EditProjectDialogLayout {

    private boolean isNew = false;


    public EditProjectDialog(Application application, String title, Project project) {
        super(application, title);

        isNew = false;

        initializeComponents();
        initializeLayouts();
        updateComponents(project);
    }

    public EditProjectDialog(Application application, String title) {
        super(application, title);

        isNew = true;

        initializeComponents();
        initializeLayouts();
        updateComponents(new Project());
    }


    public Project getProject() {
        return project;
    }

    private boolean verify() {
        boolean ok = true;
        if (project.getName().isEmpty()) {
            nameTf.setError("Name can't be empty..");
            ok = false;
        } else {
            if (isNew) {
                for (Project project : DbManager.db().getProjects()) {
                    if (project.getName().toUpperCase().equals(this.project.getName().toUpperCase())) {
                        nameTf.setError("Name already exists..");
                        ok = false;
                        break;
                    }
                }
            }
        }

        if (project.getMainDirectory().isEmpty()) {
            directoryPnl.setError("Directory can't be empty..");
            ok = false;
        }

        return ok;
    }

    private void updateProjectDirectories() {
        SwingUtilities.invokeLater(() -> {
            if (project != null && project.isValidDirectory()) {
                application.beginWait();
                List<ProjectIDE> ideList = ideTypeCcb.getSelectedElements();
                try {
                    List<ProjectObject> projectObjectList = project.findProjectsInDirectory(
                            project.getMainDirectory(),
                            ideList);

                    project.getProjectCodes().clear();
                    project.getProjectPcbs().clear();
                    project.getProjectOthers().clear();

                    for (ProjectObject obj : projectObjectList) {
                        project.addProjectObject(obj);
                    }

                    projectGridPanel.drawTiles(projectObjectList);
                } finally {
                    application.endWait();
                }
            }
        });
    }

    //
    // Dialog ok
    //
    @Override
    protected void onOK() {
        if (verify()) {
            super.onOK();
        }
    }

    //
    // Edited values
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        updateEnabledComponents();
    }

    @Override
    public DbObject getGuiObject() {
        return project;
    }

    //
    // Found projects in directory clicked
    //
    @Override
    public void onGridComponentClick(ProjectObject projectObject) {

    }

    //
    // Find projects button
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        updateProjectDirectories();
    }
}