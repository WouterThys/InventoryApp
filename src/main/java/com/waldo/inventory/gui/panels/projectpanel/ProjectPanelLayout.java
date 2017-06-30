package com.waldo.inventory.gui.panels.projectpanel;

import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.classes.ProjectType;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.IDbObjectTreeModel;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.addprojectdialog.AddProjectDialog;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialog;
import com.waldo.inventory.gui.panels.orderpanel.OrderPanelLayout;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.*;

import static com.waldo.inventory.database.DbManager.db;

public abstract class ProjectPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        IdBToolBar.IdbToolBarListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITree projectTree;
    IDbObjectTreeModel treeModel;

    TopToolBar topToolBar;
    private IdBToolBar projectToolBar;
    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;
    Project selectedProject;

    /*
    *                  CONSTRUCTOR
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectPanelLayout(Application application) {
        this.application = application;
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        if (selectedProject == null || selectedProject.isUnknown() || !selectedProject.canBeSaved()) {
            projectToolBar.setEditActionEnabled(false);
            projectToolBar.setDeleteActionEnabled(false);
            topToolBar.setDeleteActionEnabled(false);
            topToolBar.setEditActionEnabled(false);
        } else {
            projectToolBar.setEditActionEnabled(true);
            projectToolBar.setDeleteActionEnabled(true);
            topToolBar.setDeleteActionEnabled(true);
            topToolBar.setEditActionEnabled(true);
        }
    }

    Project getProjectAt(int row) {
        // TODO
        return null;
    }

    public void selectProject(Project selectedProject) {
        if (selectedProject != null) {
//            List<selectedProject> itemList = tableModel.getItemList();
//            if (itemList != null) {
//                int ndx = itemList.indexOf(selectedItem);
//                if (ndx >= 0 && ndx < itemList.size()) {
//                    itemTable.setRowSelectionInterval(ndx, ndx);
//                    itemTable.scrollRectToVisible(new Rectangle(itemTable.getCellRect(ndx, 0, true)));
//                }
//            }
        }
    }

    private void createNodes(DefaultMutableTreeNode rootNode) {
        for (Project project : db().getProjects()) {
            DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project, true);
            rootNode.add(projectNode);

            for (ProjectDirectory projectDirectory : project.getProjectDirectories()) {
                for (ProjectType projectType : projectDirectory.getProjectTypes().keySet()) {
                    DefaultMutableTreeNode typeNode = new DefaultMutableTreeNode(projectType, false);
                    projectNode.add(typeNode);
                }
            }
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Project tree
        Project virtualRoot = new Project("All");
        virtualRoot.setCanBeSaved(false);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(virtualRoot, true);
        createNodes(rootNode);
        treeModel = new IDbObjectTreeModel(rootNode, IDbObjectTreeModel.TYPE_PROJECTS);

        projectTree = new ITree(treeModel);
        projectTree.addTreeSelectionListener(this);
        projectTree.setExpandsSelectedPaths(true);
        projectTree.setScrollsOnExpand(true);
        treeModel.setTree(projectTree);

        // Top tool bar
        topToolBar = new TopToolBar(application, this);

        // Project Tool bar
        projectToolBar = new IdBToolBar(new IdBToolBar.IdbToolBarListener() {
            @Override
            public void onToolBarRefresh() {
                updateComponents(selectedProject);
            }

            @Override
            public void onToolBarAdd() {
                AddProjectDialog dialog = new AddProjectDialog(application, "New Project");
                if (dialog.showDialog() == IDialog.OK) {
                    // Add order
                    Project p = dialog.getProject();
                    p.save();
                }
            }

            @Override
            public void onToolBarDelete() {
                if (selectedProject != null) {
                    int res = JOptionPane.showConfirmDialog(ProjectPanelLayout.this, "Are you sure you want to delete \"" + selectedProject.getName() + "\"?");
                    if (res == JOptionPane.OK_OPTION) {
                        selectedProject.delete();
                        selectedProject = null;
                    }
                }
            }

            @Override
            public void onToolBarEdit() {
                if (selectedProject != null) {
                    AddProjectDialog dialog = new AddProjectDialog(application, "New Project", selectedProject);
                    if (dialog.showDialog() == IDialog.OK) {
                        // Add order
                        Project p = dialog.getProject();
                        for(ProjectDirectory dir : p.getProjectDirectories()) {
                            dir.save();
                        }
                        p.save();
                    }
                }
            }
        });
        projectToolBar.setFloatable(false);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // West panel
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.add(new JScrollPane(projectTree), BorderLayout.CENTER);
        westPanel.add(projectToolBar, BorderLayout.PAGE_END);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topToolBar, BorderLayout.PAGE_START);

        // Add
        add(westPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {
        application.beginWait();
        try {
            // Update if needed
            if (object != null) {
                if (selectedProject == null || !selectedProject.equals(object)) {
                    selectedProject = (Project) object;
                }
            }

            // Enabled components
            updateEnabledComponents();
        } finally {
            application.endWait();
        }
    }
}
