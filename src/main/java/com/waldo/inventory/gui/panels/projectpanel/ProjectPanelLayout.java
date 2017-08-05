package com.waldo.inventory.gui.panels.projectpanel;

import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.classes.ProjectType;
import com.waldo.inventory.database.LogManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.treemodels.IDbObjectTreeModel;
import com.waldo.inventory.gui.dialogs.addprojectdialog.AddProjectDialog;
import com.waldo.inventory.gui.panels.projectpanel.extras.KiCadItemPanel;
import com.waldo.inventory.gui.panels.projectpanel.extras.ProjectGirdPanel;
import com.waldo.inventory.gui.panels.projectpanel.projectdetails.ProjectDetailsPanel;
import com.waldo.inventory.gui.panels.projectpanel.projecttypedetails.ProjectTypeDetails;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.waldo.inventory.database.DbManager.db;

public abstract class ProjectPanelLayout extends JPanel implements
        GuiInterface,
        ActionListener,
        TreeSelectionListener,
        IdBToolBar.IdbToolBarListener,
        ProjectGirdPanel.GridComponentClicked {

    private static final LogManager LOG = LogManager.LOG(ProjectPanelLayout.class);

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITree projectTree;
    IDbObjectTreeModel treeModel;

    TopToolBar topToolBar;
    private IdBToolBar projectToolBar;
    private ProjectGirdPanel projectGirdPanel;
    ProjectDetailsPanel detailsPanel;
    ProjectTypeDetails projectTypeDetails;

    KiCadItemPanel kiCadItemPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;
    Project selectedProject;
    ProjectDirectory selectedDirectory;
    ProjectType selectedProjectType;
    File lastProjectFile;


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

    public void recreateNodes() {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        rootNode.removeAllChildren();
        createNodes(rootNode);
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
            public void onToolBarRefresh(IdBToolBar source) {
                updateComponents(selectedProject);
            }

            @Override
            public void onToolBarAdd(IdBToolBar source) {
                AddProjectDialog dialog = new AddProjectDialog(application, "New Project");
                if (dialog.showDialog() == IDialog.OK) {
                    // Add Project
                    Project p = dialog.getProject();
                    saveProject(p);
                }
            }

            @Override
            public void onToolBarDelete(IdBToolBar source) {
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
                        selectedDirectory = null;
                    }
                }
            }

            @Override
            public void onToolBarEdit(IdBToolBar source) {
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
        projectGirdPanel = new ProjectGirdPanel(application, selectedProject, this);

        // Detail panel
        detailsPanel = new ProjectDetailsPanel(application);
        projectTypeDetails = new ProjectTypeDetails(application, this);

        // KiCad items panel
        kiCadItemPanel = new KiCadItemPanel(application);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        projectTree.setPreferredSize(new Dimension(300,200));
        JScrollPane pane = new JScrollPane(projectTree);

        kiCadItemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2,3,2,3),
                BorderFactory.createLineBorder(Color.gray, 1)
        ));

        // West panel
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.add(pane, BorderLayout.CENTER);
        westPanel.add(projectToolBar, BorderLayout.PAGE_END);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        //JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, projectGirdPanel, kiCadItemPanel);
        centerPanel.add(topToolBar, BorderLayout.PAGE_START);
        //centerPanel.add(split, BorderLayout.CENTER);
        centerPanel.add(projectGirdPanel, BorderLayout.CENTER);
        centerPanel.add(kiCadItemPanel, BorderLayout.EAST);

        // Details panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        //infoPanel.setPreferredSize(new Dimension(getPreferredSize().width, 200));
        infoPanel.add(detailsPanel, BorderLayout.CENTER);
        infoPanel.add(projectTypeDetails, BorderLayout.EAST);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2,3,2,3),
                BorderFactory.createLineBorder(Color.GRAY, 1)
        ));

        centerPanel.add(infoPanel, BorderLayout.SOUTH);

        // Add
        //add(westPanel, BorderLayout.WEST);
        //add(centerPanel, BorderLayout.CENTER);

        // Add
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westPanel, centerPanel);
        //add(pane, BorderLayout.WEST);
        //add(panel, BorderLayout.CENTER);
        add(splitPane, BorderLayout.CENTER);
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
            projectGirdPanel.updateComponents(selectedProject);

            // Enabled components
            updateEnabledComponents();

            // Details
            detailsPanel.updateComponents(selectedProject);
            projectTypeDetails.updateComponents(selectedProjectType);
        } finally {
            application.endWait();
        }
    }

}
