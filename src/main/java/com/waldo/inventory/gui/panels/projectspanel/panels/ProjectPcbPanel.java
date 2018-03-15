package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.editprojectpcbdialog.EditProjectPcbDialog;
import com.waldo.inventory.gui.panels.projectspanel.projectpreviewpanel.ProjectPcbPreviewPanel;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
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

    @Override
    protected JPopupMenu showPopup(ProjectPcb projectObject) {
        JPopupMenu popupMenu = super.showPopup(projectObject);

        popupMenu.addSeparator();
        popupMenu.add(((ProjectPcbPreviewPanel)previewPanel).getLinkAa());
        popupMenu.add(((ProjectPcbPreviewPanel)previewPanel).getOrderAa());
        popupMenu.add(((ProjectPcbPreviewPanel)previewPanel).getParseAa());
        popupMenu.add(((ProjectPcbPreviewPanel)previewPanel).getUsedAa());

        return popupMenu;
    }

    /*
         *                  LISTENERS
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        super.initializeComponents();
        pcbItemPanel = new PcbItemPanel(application);
        previewPanel = new ProjectPcbPreviewPanel(application) {
            @Override
            public void onToolBarDelete(IdBToolBar source) {
                ProjectPcbPanel.this.onToolBarDelete(source);
            }

            @Override
            public void onToolBarEdit(IdBToolBar source) {
                ProjectPcbPanel.this.onToolBarEdit(source);
            }
        };
    }

    @Override
    public void initializeLayouts() {
        super.initializeLayouts();
        eastPanel.add(pcbItemPanel, BorderLayout.CENTER);
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
            if (dialog.showDialog() == IDialog.OK) {
                pcbItemPanel.updateComponents(newProjectPcb);
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedProjectObject != null) {
            EditProjectPcbDialog dialog = new EditProjectPcbDialog(application, "Edit " + selectedProjectObject.getName(), selectedProjectObject);
            if (dialog.showDialog() == IDialog.OK) {
                selectedProjectObject.save();
                updateComponents(selectedProjectObject);
            }
        }
    }

    @Override
    public void onDeleted(ProjectPcb object) {
        super.onDeleted(object);
        pcbItemPanel.updateComponents((ProjectPcb)null);
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