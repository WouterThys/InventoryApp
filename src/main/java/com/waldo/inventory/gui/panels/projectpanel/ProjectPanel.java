package com.waldo.inventory.gui.panels.projectpanel;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.TopToolBar;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

public class ProjectPanel extends ProjectPanelLayout {

    private DbObjectChangedListener<Project> projectsListener;
    private DbObjectChangedListener<ProjectDirectory> projectDirectoryListener;

    public ProjectPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();

        initActions();
        initListeners();

        DbManager.db().addOnProjectChangedListener(projectsListener);
        DbManager.db().addOnProjectDirectoryChangedListener(projectDirectoryListener);

        updateComponents(null);
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public TopToolBar getToolBar() {
        return topToolBar;
    }

    private void initActions() {

    }

    private void initListeners() {
        setProjectListener();
        setProjectDirectoryListener();
    }

    private void setProjectListener() {
        projectsListener = new DbObjectChangedListener<Project>() {
            @Override
            public void onAdded(Project project) {
                treeModel.addObject(project);
                updateComponents(project);
            }

            @Override
            public void onUpdated(Project newProject, Project oldProject) {
                treeModel.updateObject(newProject, oldProject);
                updateComponents(newProject);
            }

            @Override
            public void onDeleted(Project project) {
                treeModel.removeObject(project);
                selectedProject = null;
                selectedDirectory = null;
                updateComponents(null);
            }
        };
    }

    private void setProjectDirectoryListener() {
        projectDirectoryListener = new DbObjectChangedListener<ProjectDirectory>() {
            @Override
            public void onAdded(ProjectDirectory directory) {

            }

            @Override
            public void onUpdated(ProjectDirectory newDirectory, ProjectDirectory oldDirectory) {

            }

            @Override
            public void onDeleted(ProjectDirectory directory) {

            }
        };
    }

    //
    // Top toolbar listener
    //
    @Override
    public void onToolBarRefresh() {

    }

    @Override
    public void onToolBarAdd() {

    }

    @Override
    public void onToolBarDelete() {

    }

    @Override
    public void onToolBarEdit() {

    }

    //
    // Tree value changed
    //
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (!application.isUpdating()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) projectTree.getLastSelectedPathComponent();

            if (node == null) {
                selectedProject = null;
                selectedDirectory = null;
                return; // Nothing selected
            } else {
                DbObject obj = (DbObject)node.getUserObject();
                switch (DbObject.getType(obj)) {
                    case DbObject.TYPE_PROJECT:
                        selectedProject = (Project) obj;
                        selectedDirectory = null;
                        break;
                    case DbObject.TYPE_PROJECT_DIRECTORY:
                        selectedDirectory = (ProjectDirectory) obj;
                        selectedProject = selectedDirectory.getProject();
                        break;
                }
            }

            application.clearSearch();

            updateComponents(selectedProject);
        }
    }
}
