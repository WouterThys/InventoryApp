package com.waldo.inventory.gui.dialogs.createpcbdialog;

import com.waldo.inventory.classes.dbclasses.CreatedPcb;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

abstract class CreatePcbDialogLayout extends IDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    // New or existing order
    private ILabel selectPcbLbl;
    IComboBox<CreatedPcb> createdPcbCb;
    private IActions.AddAction addNewPcbAa;
    private IActions.GoAction addNowAa;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectPcb projectPcb;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    CreatePcbDialogLayout(Window window, String title, ProjectPcb projectPcb) {
        super(window, title);
        this.projectPcb = projectPcb;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    abstract void onAddNewPcb(ProjectPcb projectPcb);
    abstract void onGoAction(CreatedPcb createdPcb);


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        getButtonOK().setVisible(false);
        showTitlePanel(false);

        selectPcbLbl = new ILabel("Select a PCB, or add a new one.");
        addNewPcbAa = new IActions.AddAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddNewPcb(projectPcb);
            }
        };
        addNowAa = new IActions.GoAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onGoAction((CreatedPcb) createdPcbCb.getSelectedItem());
            }
        };
        createdPcbCb = new IComboBox<>();
    }

    @Override
    public void initializeLayouts() {
        GuiUtils.GridBagHelper gbc;

        JPanel addNowPanel = new JPanel();
        gbc = new GuiUtils.GridBagHelper(addNowPanel, 0);
        gbc.addLine("", selectPcbLbl);
        gbc.addLine("", GuiUtils.createComponentWithActions(createdPcbCb, addNewPcbAa, addNowAa));

        getContentPanel().add(addNowPanel);
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        List<CreatedPcb> createdPcbs = SearchManager.sm().findCreatedPcbsByForProjectPcb(projectPcb.getId());
        Vector<CreatedPcb> pcbs = new Vector<>(createdPcbs);

        DefaultComboBoxModel<CreatedPcb> pcbCbModel = new DefaultComboBoxModel<>(pcbs);
        createdPcbCb.setModel(pcbCbModel);
        if (args.length != 0 && args[0] != null) {
            createdPcbCb.setSelectedItem(args[0]);
        }
    }
}