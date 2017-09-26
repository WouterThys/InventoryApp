package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectCode;
import com.waldo.inventory.classes.ProjectIDE;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.treemodels.IFileTreeModel;
import com.waldo.inventory.gui.panels.projectpanel.extras.ProjectGirdPanel;
import com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectcodedialog.EditProjectCodeDialog;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.io.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class ProjectCodePanel extends JPanel implements
        GuiInterface,
        IdBToolBar.IdbToolBarListener,
        DbObjectChangedListener<ProjectCode> {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ProjectGirdPanel<ProjectCode> gridPanel;
    private IdBToolBar codeToolBar;
    private JButton runIdeBtn;
    private ILabel projectCodeNameLbl;
    private JTree codeFilesTree;
    private IFileTreeModel fileTreeModel;
    private ITextEditor remarksTe;
    private JButton saveRemarksBtn;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;
    private Project selectedProject;
    private ProjectCode selectedProjectCode;


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectCodePanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();

        DbManager.db().addOnProjectCodeChangedListener(this);

        updateComponents(null);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        boolean enabled = selectedProjectCode != null;
        codeToolBar.setDeleteActionEnabled(enabled);
        codeToolBar.setEditActionEnabled(enabled);
        remarksTe.setEnabled(enabled);
        runIdeBtn.setEnabled(enabled);
    }

    private void selectProjectCode(ProjectCode projectCode) {
        if (projectCode != null) {
            selectedProjectCode = projectCode;
            fileTreeModel = new IFileTreeModel(new File(selectedProjectCode.getDirectory()));
            codeFilesTree.setModel(fileTreeModel);
            remarksTe.setDocument(selectedProjectCode.getRemarksFile());
            projectCodeNameLbl.setText(projectCode.getName());
        } else {
            selectedProjectCode = null;
            fileTreeModel = new IFileTreeModel();
            codeFilesTree.setModel(fileTreeModel);
            remarksTe.setDocument(null);
            projectCodeNameLbl.setText("");
        }
        updateEnabledComponents();
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Center
        gridPanel = new ProjectGirdPanel<>(this::selectProjectCode);
        codeToolBar = new IdBToolBar(this);
        projectCodeNameLbl = new ILabel("", ILabel.CENTER);
        projectCodeNameLbl.setFont(18, Font.BOLD);
        runIdeBtn = new JButton(imageResource.readImage("Common.Execute", 24));
        runIdeBtn.addActionListener(e -> {
            if (selectedProjectCode != null && selectedProjectCode.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                ProjectIDE ide = selectedProjectCode.getProjectIDE();
                try {
                    ide.launch(new File(selectedProjectCode.getDirectory()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to open IDE",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        // East
        codeFilesTree = new JTree();
        codeFilesTree.setCellRenderer(ITree.getFilesRenderer());
        remarksTe = new ITextEditor();
        remarksTe.setEnabled(false);
        saveRemarksBtn = new JButton("Save");
        saveRemarksBtn.addActionListener(e -> {
            DefaultStyledDocument doc = remarksTe.getStyledDocument();
            if (selectedProjectCode.getRemarksFileName().isEmpty()) {
                try {
                    selectedProjectCode.setRemarksFile(FileUtils.createTempFile(selectedProjectCode.createRemarksFileName()));
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return;
                }
            }
            try (OutputStream fos = new FileOutputStream(selectedProjectCode.getRemarksFile());
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {

                oos.writeObject(doc);
                selectedProjectCode.save();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Panels
        JPanel eastPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel remarksPanel = new JPanel(new BorderLayout());
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

        JScrollPane pane = new JScrollPane(codeFilesTree);

        // Add stuff to panels
        menuPanel.add(projectCodeNameLbl, BorderLayout.CENTER);
        menuPanel.add(runIdeBtn, BorderLayout.EAST);

        centerPanel.add(gridPanel, BorderLayout.CENTER);
        centerPanel.add(codeToolBar, BorderLayout.PAGE_START);

        remarksPanel.add(remarksTe, BorderLayout.CENTER);
        remarksPanel.add(saveRemarksBtn, BorderLayout.SOUTH);

        eastPanel.add(menuPanel, BorderLayout.PAGE_START);
        eastPanel.add(pane, BorderLayout.CENTER);
        eastPanel.add(remarksPanel, BorderLayout.SOUTH);
        eastPanel.setMinimumSize(new Dimension(300, 0));

        // Add
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPanel, eastPanel);
        splitPane.setResizeWeight(.5d);
        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            selectedProject = (Project) object;
            gridPanel.drawTiles(selectedProject.getProjectCodes());
        } else {
            selectedProject = null;
        }
        selectedProjectCode = null;
        selectProjectCode(null);
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        updateComponents(selectedProject);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (selectedProject != null) {
            ProjectCode newProjectCode = new ProjectCode(selectedProject.getId());
            EditProjectCodeDialog dialog = new EditProjectCodeDialog(application, "Add code", newProjectCode);
            dialog.showDialog();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedProjectCode != null) {
            int result = JOptionPane.showConfirmDialog(
                    ProjectCodePanel.this,
                    "Are you sure you want to delete " + selectedProjectCode.getName() + "?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                selectedProjectCode.delete();
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedProjectCode != null) {
            EditProjectCodeDialog dialog = new EditProjectCodeDialog(application, "Edit " + selectedProjectCode.getName(), selectedProjectCode);
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
    public void onInserted(ProjectCode object) {
        selectedProject.updateProjectCodes();
        gridPanel.addTile(object);
        selectedProjectCode = object;
        updateEnabledComponents();
    }

    @Override
    public void onUpdated(ProjectCode object) {
        gridPanel.drawTiles(selectedProject.getProjectCodes());
        updateEnabledComponents();
    }

    @Override
    public void onDeleted(ProjectCode object) {
        selectedProject.updateProjectCodes();
        gridPanel.removeTile(object);
        selectedProjectCode = null;
        updateEnabledComponents();
    }

    @Override
    public void onCacheCleared() {

    }
}