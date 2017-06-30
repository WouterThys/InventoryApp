package com.waldo.inventory.gui.panels.projectpanel;

import com.waldo.inventory.classes.Project;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.TopToolBar;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

public class ProjectPanel extends ProjectPanelLayout {


    public ProjectPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();

        initActions();
        initListeners();

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
                return; // Nothing selected
            }

            application.clearSearch();

            updateComponents(node.getUserObject());
        }
    }
}
