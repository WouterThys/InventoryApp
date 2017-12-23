package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectpcbdialog.EditProjectPcbDialog;

import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.managers.CacheManager.cache;

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
        cache().addListener(ProjectPcb.class, this);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    protected boolean selectProjectObject(ProjectPcb projectPcb) {
        if (super.selectProjectObject(projectPcb)) {
            pcbItemPanel.updateComponents(selectedProjectObject);
        }
        return false;
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
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            Project project = (Project) object[0];
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
        if (selectedProject == null) {
            selectedProject = object.getProject();
        }

        selectedProject.updateProjectPcbs();
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