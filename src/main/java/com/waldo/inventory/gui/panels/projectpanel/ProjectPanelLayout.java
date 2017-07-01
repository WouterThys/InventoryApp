package com.waldo.inventory.gui.panels.projectpanel;

import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.classes.ProjectType;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.addprojectdialog.AddProjectDialog;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialog;
import com.waldo.inventory.gui.panels.orderpanel.OrderPanelLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;

import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.waldo.inventory.database.DbManager.db;

public abstract class ProjectPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        IdBToolBar.IdbToolBarListener {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectPanelLayout.class);

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITree projectTree;
    IDbObjectTreeModel treeModel;

    TopToolBar topToolBar;
    private IdBToolBar projectToolBar;

    IGridPanel<ProjectType, ArrayList<File>> typePanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;
    Project selectedProject;
    ProjectDirectory selectedDirectory;

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
            treeModel.setSelectedObject(selectedProject);
        }
    }

    private void createNodes(DefaultMutableTreeNode rootNode) {
        for (Project project : db().getProjects()) {
            DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project, true);
            rootNode.add(projectNode);

            for (ProjectDirectory projectDirectory : project.getProjectDirectories()) {
                DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(projectDirectory, false);
                projectNode.add(dirNode);
            }
        }
    }

    private void saveProject(Project project) {
        application.beginWait();
        try {
            project.saveAll();
        } catch (SQLException e) {
            LOG.error("Error saving project: " +e);
            JOptionPane.showMessageDialog(
                    ProjectPanelLayout.this,
                    "Error saving project: " + e,
                    "Db error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            application.endWait();
        }
    }

    private void updateTileView() {
        HashMap<ProjectType, ArrayList<File>> map = new HashMap<>();
        if (selectedDirectory == null) {
            if (selectedProject != null) {
                for (ProjectDirectory directory : selectedProject.getProjectDirectories()) {
                    map.putAll(directory.getProjectTypes());
                }
            }
        } else {
            map = selectedDirectory.getProjectTypes();
        }
        typePanel.setMap(map);
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
                    // Add Project
                    Project p = dialog.getProject();
                    saveProject(p);
                }
            }

            @Override
            public void onToolBarDelete() {
                if (selectedProject != null) {
                    int res = JOptionPane.showConfirmDialog(ProjectPanelLayout.this, "Are you sure you want to delete \"" + selectedProject.getName() + "\"?");
                    if (res == JOptionPane.OK_OPTION) {
                        application.beginWait();
                        try {
                            selectedProject.delete();
                        } finally {
                            application.endWait();
                        }
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
                        saveProject(p);
                    }
                }
            }
        });
        projectToolBar.setFloatable(false);

        // Type panel
        typePanel = new IGridPanel<>();
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        projectTree.setPreferredSize(new Dimension(300,200));
        JScrollPane pane = new JScrollPane(projectTree);

        // West panel
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.add(pane, BorderLayout.CENTER);
        westPanel.add(projectToolBar, BorderLayout.PAGE_END);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topToolBar, BorderLayout.PAGE_START);
        centerPanel.add(typePanel, BorderLayout.CENTER);

        // Add
        add(westPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {
        application.beginWait();
        try {
            // Update if needed
            if (object != null && object instanceof Project) {
                if (selectedProject == null || !selectedProject.equals(object)) {
                    selectedProject = (Project) object;
                    selectProject(selectedProject);
                }
            }

            // Update tile view
            updateTileView();

            // Enabled components
            updateEnabledComponents();
        } finally {
            application.endWait();
        }
    }

}
