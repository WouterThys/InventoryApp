package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.classes.dbclasses.ProjectObject;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.panels.projectspanel.projectpreviewpanel.ProjectPreviewPanel;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ProjectObjectPanel<T extends ProjectObject> extends JPanel implements
        GuiUtils.GuiInterface,
        ActionListener,
        IdBToolBar.IdbToolBarListener,
        CacheChangedListener<T> {

    public interface ProjectObjectListener {
        void onSelected(ProjectObject selectedObject);
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectGridPanel<T> gridPanel;
    private IdBToolBar objectToolBar;
    private ILabel projectObjectNameLbl;

    final JPanel eastPanel = new JPanel(new BorderLayout());
    private final JPanel centerPanel = new JPanel(new BorderLayout());
    private final JPanel remarksPanel = new JPanel(new BorderLayout());
    final JPanel menuPanel = new JPanel(new BorderLayout());

    ProjectPreviewPanel<T> previewPanel;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    protected final Application application;
    private final ProjectObjectListener objectListener;
    Project selectedProject;
    T selectedProjectObject;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectObjectPanel(Application application, ProjectObjectListener objectListener) {
        this.application = application;
        this.objectListener = objectListener;

        initializeComponents();
        initializeLayouts();

        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    protected void updateEnabledComponents() {
        boolean projectSelected = (selectedProject != null && !selectedProject.isUnknown() && selectedProject.canBeSaved());
        boolean objectSelected = selectedProjectObject != null;
        if (projectSelected) {
            objectToolBar.setEnabled(true);
            objectToolBar.setDeleteActionEnabled(objectSelected);
            objectToolBar.setEditActionEnabled(objectSelected);
        } else {
            objectToolBar.setEnabled(false);
        }
    }

    protected boolean selectProjectObject(T projectObject) {
        boolean newObjSelected = false;
        if (projectObject != null) {
            if (!projectObject.equals(selectedProjectObject)) {
                selectedProjectObject = projectObject;
                projectObjectNameLbl.setText(projectObject.getName());

                if (objectListener != null) {
                    objectListener.onSelected(selectedProjectObject);
                }
                newObjSelected = true;
            }
        } else {
            newObjSelected = true;
            selectedProjectObject = null;
            projectObjectNameLbl.setText("");
        }

        updateEnabledComponents();
        previewPanel.updateComponents(selectedProjectObject);
        return newObjSelected;
    }

    public T getSelectedProjectObject() {
        return selectedProjectObject;
    }

    public void setSelectedProjectObject(T selectedProjectObject) {
        this.selectedProjectObject = selectedProjectObject;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Center
        gridPanel = new ProjectGridPanel<>(this::selectProjectObject);
        objectToolBar = new IdBToolBar(this, true, true, false, false);
        projectObjectNameLbl = new ILabel("", ILabel.CENTER);
        projectObjectNameLbl.setFont(18, Font.BOLD);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Panels
        menuPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JScrollPane scrollPane = new JScrollPane(gridPanel);

        // Add stuff to panels
        menuPanel.add(projectObjectNameLbl, BorderLayout.CENTER);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(objectToolBar, BorderLayout.PAGE_START);

        //eastPanel.add(menuPanel, BorderLayout.PAGE_START);
        eastPanel.add(remarksPanel, BorderLayout.SOUTH);
        eastPanel.setMinimumSize(new Dimension(300, 0));

        // Add
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centerPanel, eastPanel);
        splitPane.setResizeWeight(.3d);
        splitPane.setOneTouchExpandable(true);

        previewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, -1, -1),
                BorderFactory.createLineBorder(Color.lightGray, 1)
        ));

        add(previewPanel, BorderLayout.EAST);
        add(splitPane, BorderLayout.CENTER);
    }

    //
    // Remarks text edit save action listener
    //
    @Override
    public void actionPerformed(ActionEvent e) {
//        DefaultStyledDocument doc = remarksTe.getStyledDocument();
//        if (selectedProjectObject.getRemarksFileName().isEmpty()) {
//            try {
//                selectedProjectObject.setRemarksFile(FileUtils.createTempFile(selectedProjectObject.createRemarksFileName()));
//            } catch (Exception e1) {
//                e1.printStackTrace();
//                return;
//            }
//        }
//        try (OutputStream fos = new FileOutputStream(selectedProjectObject.getRemarksFile());
//             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
//
//            oos.writeObject(doc);
//            selectedProjectObject.save();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    //
    // Project object changed
    //
    @Override
    public void onInserted(T object) {
        if (selectedProject == null) {
            selectedProject = object.getProject();
        }
        selectedProject.updateProjectCodes();
        selectedProject.updateProjectPcbs();
        selectedProject.updateProjectOthers();
        gridPanel.addTile(object);
        selectProjectObject(object);
        updateEnabledComponents();
    }

    @Override
    public void onDeleted(T object) {
        if (selectedProject == null) {
            selectedProject = object.getProject();
        }
        selectedProject.updateProjectCodes();
        selectedProject.updateProjectPcbs();
        selectedProject.updateProjectOthers();
        gridPanel.removeTile(object);
        selectProjectObject(null);
        updateEnabledComponents();
    }

    @Override
    public void onCacheCleared() {

    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        updateComponents(selectedProject);
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedProjectObject != null) {
            int result = JOptionPane.showConfirmDialog(
                    ProjectObjectPanel.this,
                    "Are you sure you want to delete " + selectedProjectObject.getName() + "?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                selectedProjectObject.delete();
            }
        }
    }
}