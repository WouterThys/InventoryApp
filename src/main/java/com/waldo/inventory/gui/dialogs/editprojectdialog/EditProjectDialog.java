package com.waldo.inventory.gui.dialogs.editprojectdialog;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.managers.CacheManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class EditProjectDialog extends EditProjectDialogLayout implements CacheChangedListener<Project> {

    public EditProjectDialog(Application application, String title, Project project) {
        super(application, title);

        CacheManager.cache().addOnProjectChangedListener(this);

        initializeComponents();
        initializeLayouts();
        updateComponents(project);
    }


    public Project getProject() {
        return selectedProject;
    }

    private boolean verify() {
        boolean ok = true;
        if (selectedProject.getName().isEmpty()) {
            nameTf.setError("Name can't be empty..");
            ok = false;
        } else {
            if (selectedProject.getId() < DbObject.UNKNOWN_ID) { // New
                for (Project project : cache().getProjects()) {
                    if (project.getName().toUpperCase().equals(this.selectedProject.getName().toUpperCase())) {
                        nameTf.setError("Name already exists..");
                        ok = false;
                        break;
                    }
                }
            }
        }

        if (selectedProject.getMainDirectory().isEmpty()) {
            directoryPnl.setError("Directory can't be empty..");
            ok = false;
        }

        return ok;
    }

    private void updateProjectDirectories() {
        SwingUtilities.invokeLater(() -> {
            if (selectedProject != null && selectedProject.isValidDirectory()) {
                List<ProjectIDE> ideList = ideTypeCcb.getSelectedElements();
                application.beginWait();
                try {
                    List<ProjectObject> projectObjectList = selectedProject.findProjectsInDirectory(
                            selectedProject.getMainDirectory(),
                            ideList);

                    newProjectCodes.clear();
                    newProjectPcbs.clear();
                    newProjectOthers.clear();
                    for (ProjectObject object : projectObjectList) {
                        if (object instanceof ProjectCode) {
                            ProjectCode code = (ProjectCode) object;
                            if (!newProjectCodes.contains(code)) {
                                newProjectCodes.add(code);
                            }
                        } else if (object instanceof ProjectPcb) {
                            ProjectPcb pcb = (ProjectPcb) object;
                            if (!newProjectPcbs.contains(pcb)) {
                                newProjectPcbs.add(pcb);
                            }
                        } else if (object instanceof ProjectOther) {
                            ProjectOther other = (ProjectOther) object;
                            if (!newProjectOthers.contains(other)) {
                                newProjectOthers.add(other);
                            }
                        }
                    }

                    tableInit(newProjectCodes, newProjectPcbs, newProjectOthers);
                } finally {
                    application.endWait();
                }
            }
        });
    }

    private void updateProjectObjects(Project project) {
            // CODE
            List<ProjectCode> currentCodes = new ArrayList<>(sm().findProjectCodesByProjectId(project.getId()));

            for (ProjectCode code : getSelectedProjectCodes()) {
                if (currentCodes.contains(code)) {
                    code = currentCodes.get(currentCodes.indexOf(code));
                    currentCodes.remove(code);
                }
                code.setProjectId(project.getId());
                code.save();
            }


            // - can be deleted
            for (ProjectCode code : currentCodes) {
                code.delete();
            }
            project.updateProjectCodes();

            // PCB
            List<ProjectPcb> currentPcbs = new ArrayList<>(sm().findProjectPcbsByProjectId(project.getId()));

            for (ProjectPcb pcb : getSelectedProjectPcbs()) {
                if (currentPcbs.contains(pcb)) {
                    pcb = currentPcbs.get(currentPcbs.indexOf(pcb));
                    currentPcbs.remove(pcb);
                }
                pcb.setProjectId(project.getId());
                pcb.save();
            }


            // - can be deleted
            for (ProjectPcb pcb : currentPcbs) {
                pcb.delete();
            }
            project.updateProjectPcbs();

            // OTHER
            List<ProjectOther> projectOthers = new ArrayList<>(sm().findProjectOthersByProjectId(project.getId()));

            for (ProjectOther other : getSelectedProjectOthers()) {
                if (projectOthers.contains(other)) {
                    other = projectOthers.get(projectOthers.indexOf(other));
                    projectOthers.remove(other);
                }
                other.setProjectId(project.getId());
                other.save();
            }


            // - can be deleted
            for (ProjectOther other : projectOthers) {
                other.delete();
            }
            project.updateProjectOthers();
    }

    //
    // Dialog ok
    //
    @Override
    protected void onOK() {
        if (verify()) {
            selectedProject.save();
        }
    }

    @Override
    protected void onCancel() {
        selectedProject = copyProject(originalProject);
        selectedProject.setCanBeSaved(true);
        super.onCancel();
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
        if (isShown) {
            return selectedProject;
        }
        return null;
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

    //
    // Project cache
    //
    @Override
    public void onInserted(Project project) {
        updateProjectObjects(project);
        super.onOK(); // Close dialog
    }

    @Override
    public void onUpdated(Project project) {
        updateProjectObjects(selectedProject);
        super.onOK(); // Close dialog
    }

    @Override
    public void onDeleted(Project object) {
        // Should not happen
    }

    @Override
    public void onCacheCleared() {
        // Don't care
    }
}