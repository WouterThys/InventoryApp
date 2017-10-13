package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectOther;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.treemodels.IFileTreeModel;
import com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectobjectdialog.EditProjectObjectDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ProjectOtherPanel extends ProjectObjectPanel<ProjectOther> {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JTree otherFilesTree;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectOtherPanel(Application application, ProjectObjectListener listener) {
        super(application, listener);
        DbManager.db().addOnProjectOtherChangedListener(this);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    protected void selectProjectObject(ProjectOther projectCode) {
        super.selectProjectObject(projectCode);
        IFileTreeModel fileTreeModel;
        if (projectCode != null) {
            fileTreeModel = new IFileTreeModel(new File(selectedProjectObject.getDirectory()));
        } else {
            fileTreeModel = new IFileTreeModel();
        }
        otherFilesTree.setModel(fileTreeModel);
    }


    /*
         *                  LISTENERS
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        super.initializeComponents();

        otherFilesTree = new JTree();
        otherFilesTree.setCellRenderer(ITree.getFilesRenderer());
    }

    @Override
    public void initializeLayouts() {
        super.initializeLayouts();

        JScrollPane pane = new JScrollPane(otherFilesTree);
        eastPanel.add(pane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            Project project = (Project) object;
            if (!project.equals(selectedProject)) {
                selectedProject = project;
                gridPanel.drawTiles(selectedProject.getProjectOthers());
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
            ProjectOther newProjectOther = new ProjectOther(selectedProject.getId());
            EditProjectObjectDialog dialog = new EditProjectObjectDialog<>(application, "Add", newProjectOther);
            dialog.showDialog();
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedProjectObject != null) {
            EditProjectObjectDialog dialog = new EditProjectObjectDialog<>(application, "Edit " + selectedProjectObject.getName(), selectedProjectObject);
            dialog.showDialog();
        }
    }

    //
    // Project Other changed
    //
    @Override
    public void onUpdated(ProjectOther object) {
        gridPanel.drawTiles(selectedProject.getProjectOthers());
        updateEnabledComponents();
    }
}