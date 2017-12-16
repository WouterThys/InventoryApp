package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.classes.dbclasses.ProjectIDE;
import com.waldo.inventory.classes.dbclasses.ProjectObject;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ProjectObjectPanel <T extends ProjectObject> extends JPanel implements
        GuiInterface,
        ActionListener,
        IdBToolBar.IdbToolBarListener ,
        CacheChangedListener<T> {

    public interface ProjectObjectListener {
        void onSelected(ProjectObject selectedObject);
    }
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectGridPanel<T> gridPanel;
    private IdBToolBar objectToolBar;
    private JButton runIdeBtn;
    private ILabel projectObjectNameLbl;

    final JPanel eastPanel = new JPanel(new BorderLayout());
    private final JPanel centerPanel = new JPanel(new BorderLayout());
    private final JPanel remarksPanel = new JPanel(new BorderLayout());
    final JPanel menuPanel = new JPanel(new BorderLayout());


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
    public ProjectObjectPanel(Application application, ProjectObjectListener objectListener) {
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
            runIdeBtn.setEnabled(objectSelected);
        } else {
            objectToolBar.setEnabled(false);
            runIdeBtn.setEnabled(false);
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
        return newObjSelected;
    }

    public T getSelectedProjectObject() {
        return selectedProjectObject;
    }

    public void setSelectedProjectObject(T selectedProjectObject) {
        this.selectedProjectObject = selectedProjectObject;
    }

//    void hideRemarks(boolean hide) {
//        if (!hide) {
//            remarksTe.setVisible(true);
//            hideRemarksLbl.setText("Remarks");
//        } else {
//            remarksTe.setVisible(false);
//            hideRemarksLbl.setText("Remarks ...");
//        }
//        hidingRemarks = !hide;
//    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Center
        gridPanel = new ProjectGridPanel<>(this::selectProjectObject);
        objectToolBar = new IdBToolBar(this);
        projectObjectNameLbl = new ILabel("", ILabel.CENTER);
        projectObjectNameLbl.setFont(18, Font.BOLD);
        runIdeBtn = new JButton(imageResource.readImage("Common.Execute", 24));
        runIdeBtn.addActionListener(e -> {
            if (selectedProjectObject != null && selectedProjectObject.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                ProjectIDE ide = selectedProjectObject.getProjectIDE();
                try {
                    ide.launch(new File(selectedProjectObject.getDirectory()));
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

        // Eas
//        remarksTe = new ITextEditor();
//        remarksTe.setEnabled(false);
//        hideRemarksLbl = new ILabel("Remarks", ILabel.CENTER);
//        hideRemarksLbl.setFont(Font.BOLD);
//        hideRemarksLbl.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (e.getClickCount() == 1) {
//                    hideRemarks(hidingRemarks);
//                }
//            }
//        });
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Panels
        menuPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

        // Add stuff to panels
        menuPanel.add(projectObjectNameLbl, BorderLayout.CENTER);
        menuPanel.add(runIdeBtn, BorderLayout.EAST);

        centerPanel.add(gridPanel, BorderLayout.CENTER);
        centerPanel.add(objectToolBar, BorderLayout.PAGE_START);

        eastPanel.add(menuPanel, BorderLayout.PAGE_START);
        eastPanel.add(remarksPanel, BorderLayout.SOUTH);
        eastPanel.setMinimumSize(new Dimension(300, 0));

        // Add
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPanel, eastPanel);
        splitPane.setResizeWeight(.5d);
        splitPane.setOneTouchExpandable(true);
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
        gridPanel.addTile(object);
        selectedProjectObject = object;
        updateEnabledComponents();
    }

    @Override
    public void onDeleted(T object) {
        if (selectedProject == null) {
            selectedProject = object.getProject();
        }
        selectedProject.updateProjectCodes();
        gridPanel.removeTile(object);
        selectedProjectObject = null;
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