package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectpcbdialog.EditProjectPcbDialog;

import java.awt.*;
import java.awt.event.ActionEvent;

public class ProjectPcbPanel extends ProjectObjectPanel<ProjectPcb> {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private PcbItemPanel pcbItemPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectPcbPanel(Application application, ProjectObjectListener listener) {
        super(application, listener);
        DbManager.db().addOnProjectPcbChangedListener(this);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    protected void selectProjectObject(ProjectPcb projectPcb) {
        super.selectProjectObject(projectPcb);
        if (projectPcb != null) {
            pcbItemPanel.updateComponents(selectedProjectObject);
        } else {
            pcbItemPanel.updateComponents(null);
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        super.initializeComponents();
        pcbItemPanel = new PcbItemPanel(application);
    }

    @Override
    public void initializeLayouts() {
        super.initializeLayouts();
        eastPanel.add(pcbItemPanel, BorderLayout.CENTER);
        menuPanel.add(pcbItemPanel.getToolbarPanel(), BorderLayout.WEST);
        //hideRemarks(true);
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            Project project = (Project) object;
            if (!project.equals(selectedProject)) {
                selectedProject = project;
                gridPanel.drawTiles(selectedProject.getProjectPcbs());
            }
        } else {
            selectedProject = null;
        }
        selectProjectObject(selectedProjectObject);
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (selectedProject != null) {
            ProjectPcb newProjectPcb = new ProjectPcb(selectedProject.getId());
            EditProjectPcbDialog dialog = new EditProjectPcbDialog(application, "Add pcb", newProjectPcb);
            dialog.showDialog();
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedProjectObject != null) {
            EditProjectPcbDialog dialog = new EditProjectPcbDialog(application, "Edit " + selectedProjectObject.getName(), selectedProjectObject);
            if (dialog.showDialog() == IDialog.OK) {
                selectedProjectObject.save();
            }
        }
    }

    //
    // Project code changed
    //
    @Override
    public void onUpdated(ProjectPcb object) {
        gridPanel.drawTiles(selectedProject.getProjectPcbs());
        updateEnabledComponents();
    }

    //
    // Text edit save action listener
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
}