package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectIDE;
import com.waldo.inventory.classes.ProjectObject;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextEditor;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.panels.projectpanel.extras.ProjectGirdPanel;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ProjectObjectPanel <T extends ProjectObject> extends JPanel implements
        GuiInterface,
        ActionListener,
        IdBToolBar.IdbToolBarListener ,
        DbObjectChangedListener<T> {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    protected ProjectGirdPanel<T> gridPanel;
    protected IdBToolBar objectToolBar;
    protected JButton runIdeBtn;
    protected ILabel projectObjectNameLbl;
    protected ITextEditor remarksTe;
    protected ILabel hideRemarksLbl;

    protected JPanel eastPanel = new JPanel(new BorderLayout());
    protected JPanel centerPanel = new JPanel(new BorderLayout());
    protected JPanel remarksPanel = new JPanel(new BorderLayout());
    protected JPanel menuPanel = new JPanel(new BorderLayout());


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    protected Application application;
    protected Project selectedProject;
    protected T selectedProjectObject;
    protected boolean hidingRemarks = false;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectObjectPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();

        updateComponents(null);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    protected void updateEnabledComponents() {
        boolean enabled = selectedProjectObject != null;
        objectToolBar.setDeleteActionEnabled(enabled);
        objectToolBar.setEditActionEnabled(enabled);
        remarksTe.setEnabled(enabled);
        runIdeBtn.setEnabled(enabled);
    }

    protected void selectProjectObject(T projectObject) {
        if (projectObject != null) {
            selectedProjectObject = projectObject;
            remarksTe.setDocument(projectObject.getRemarksFile());
            projectObjectNameLbl.setText(projectObject.getName());
        } else {
            selectedProjectObject = null;
            remarksTe.setDocument(null);
            projectObjectNameLbl.setText("");
        }
        updateEnabledComponents();
    }

    protected void hideRemarks(boolean hide) {
        if (!hide) {
            remarksTe.setVisible(true);
            hideRemarksLbl.setText("Remarks");
        } else {
            remarksTe.setVisible(false);
            hideRemarksLbl.setText("Remarks ...");
        }
        hidingRemarks = !hide;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Center
        gridPanel = new ProjectGirdPanel<>(this::selectProjectObject);
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
        remarksTe = new ITextEditor();
        remarksTe.setEnabled(false);
        hideRemarksLbl = new ILabel("Remarks", ILabel.CENTER);
        hideRemarksLbl.setFont(Font.BOLD);
        hideRemarksLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    hideRemarks(hidingRemarks);
                }
            }
        });
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

        remarksPanel.add(hideRemarksLbl, BorderLayout.PAGE_START);
        remarksPanel.add(remarksTe, BorderLayout.CENTER);

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
        DefaultStyledDocument doc = remarksTe.getStyledDocument();
        if (selectedProjectObject.getRemarksFileName().isEmpty()) {
            try {
                selectedProjectObject.setRemarksFile(FileUtils.createTempFile(selectedProjectObject.createRemarksFileName()));
            } catch (Exception e1) {
                e1.printStackTrace();
                return;
            }
        }
        try (OutputStream fos = new FileOutputStream(selectedProjectObject.getRemarksFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(doc);
            selectedProjectObject.save();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //
    // Project object changed
    //
    @Override
    public void onInserted(T object) {
        selectedProject.updateProjectCodes();
        gridPanel.addTile(object);
        selectedProjectObject = object;
        updateEnabledComponents();
    }

    @Override
    public void onDeleted(T object) {
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