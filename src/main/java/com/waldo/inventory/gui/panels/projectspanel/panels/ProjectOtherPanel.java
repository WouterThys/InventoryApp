package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.classes.dbclasses.ProjectOther;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.treemodels.IFileTreeModel;
import com.waldo.inventory.gui.dialogs.editprojectobjectdialog.EditProjectOtherDialog;
import com.waldo.inventory.gui.panels.projectspanel.preview.ProjectOtherPreviewPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.waldo.inventory.managers.CacheManager.cache;

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
        cache().addListener(ProjectOther.class,this);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    protected boolean selectProjectObject(ProjectOther projectCode) {
        super.selectProjectObject(projectCode);
        IFileTreeModel fileTreeModel;
        if (projectCode != null) {
            fileTreeModel = new IFileTreeModel(new File(selectedProjectObject.getDirectory()));
        } else {
            fileTreeModel = new IFileTreeModel();
        }
        otherFilesTree.setModel(fileTreeModel);
        return false;
    }


    /*
         *                  LISTENERS
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        super.initializeComponents();

        otherFilesTree = new JTree();
        otherFilesTree.setCellRenderer(ITree.getFilesRenderer());
        previewPanel = new ProjectOtherPreviewPanel(application) {
            @Override
            public void onToolBarDelete(IdBToolBar source) {
                ProjectOtherPanel.this.onToolBarDelete(source);
            }

            @Override
            public void onToolBarEdit(IdBToolBar source) {
                ProjectOtherPanel.this.onToolBarEdit(source);
            }
        };
    }

    @Override
    public void initializeLayouts() {
        super.initializeLayouts();

        JScrollPane pane = new JScrollPane(otherFilesTree);
        bottomPanel.add(pane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            Project project = (Project) object[0];
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
            EditProjectOtherDialog dialog = new EditProjectOtherDialog(application, "Add", newProjectOther);
            dialog.showDialog();
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedProjectObject != null) {
            EditProjectOtherDialog dialog = new EditProjectOtherDialog(application, "Edit " + selectedProjectObject.getName(), selectedProjectObject);
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