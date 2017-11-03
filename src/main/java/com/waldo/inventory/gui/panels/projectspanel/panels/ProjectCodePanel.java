package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectCode;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.treemodels.IFileTreeModel;
import com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectcodedialog.EditProjectCodeDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ProjectCodePanel extends ProjectObjectPanel<ProjectCode> {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JTree codeFilesTree;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectCodePanel(Application application, ProjectObjectListener listener) {
        super(application, listener);
        DbManager.db().addOnProjectCodeChangedListener(this);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    protected boolean selectProjectObject(ProjectCode projectCode) {
        IFileTreeModel fileTreeModel;
        if (super.selectProjectObject(projectCode)) {
           if (selectedProjectObject != null) {
               fileTreeModel = new IFileTreeModel(new File(selectedProjectObject.getDirectory()));
           } else {
               fileTreeModel = new IFileTreeModel();
           }
            codeFilesTree.setModel(fileTreeModel);
        }

        return false;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        super.initializeComponents();

        codeFilesTree = new JTree();
        codeFilesTree.setCellRenderer(ITree.getFilesRenderer());
    }

    @Override
    public void initializeLayouts() {
        super.initializeLayouts();

        JScrollPane pane = new JScrollPane(codeFilesTree);
        eastPanel.add(pane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            Project project = (Project) object[0];
            if (!project.equals(selectedProject)) {
                selectedProject = project;
                gridPanel.drawTiles(selectedProject.getProjectCodes());
            }
        }
        selectProjectObject(selectedProjectObject);
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (selectedProject != null) {
            ProjectCode newProjectCode = new ProjectCode(selectedProject.getId());
            EditProjectCodeDialog dialog = new EditProjectCodeDialog(application, "Add code", newProjectCode);
            dialog.showDialog();
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedProjectObject != null) {
            EditProjectCodeDialog dialog = new EditProjectCodeDialog(application, "Edit " + selectedProjectObject.getName(), selectedProjectObject);
            if (dialog.showDialog() == IDialog.OK) {
                ProjectCode newProjectCode = dialog.getProjectCode();
                newProjectCode.save();
            }
        }
    }

    //
    // Project code changed
    //
    @Override
    public void onUpdated(ProjectCode object) {
        gridPanel.drawTiles(selectedProject.getProjectCodes());
        updateEnabledComponents();
    }
}