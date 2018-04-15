package com.waldo.inventory.gui.dialogs.createpcbdialog;

import com.waldo.inventory.classes.dbclasses.CreatedPcb;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Vector;

abstract class SelectPcbDialogLayout extends IDialog implements ItemListener{

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    // New or existing order
    private ILabel selectPcbLbl;
    IComboBox<CreatedPcb> createdPcbCb;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectPcb projectPcb;
    CreatedPcb selectedPcb;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SelectPcbDialogLayout(Window window, String title, ProjectPcb projectPcb) {
        super(window, title);
        this.projectPcb = projectPcb;
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        getButtonOK().setText("Select");
        showTitlePanel(false);

        selectPcbLbl = new ILabel("Select a PCB.");
        createdPcbCb = new IComboBox<>();
        createdPcbCb.addItemListener(this);
    }

    @Override
    public void initializeLayouts() {
        GuiUtils.GridBagHelper gbc;

        JPanel addNowPanel = new JPanel();
        gbc = new GuiUtils.GridBagHelper(addNowPanel, 0);
        gbc.addLine("", selectPcbLbl);
        gbc.addLine("", createdPcbCb);

        getContentPanel().add(addNowPanel);
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        List<CreatedPcb> createdPcbs = SearchManager.sm().findCreatedPcbsForProjectPcb(projectPcb.getId());
        Vector<CreatedPcb> pcbs = new Vector<>(createdPcbs);

        DefaultComboBoxModel<CreatedPcb> pcbCbModel = new DefaultComboBoxModel<>(pcbs);
        createdPcbCb.setModel(pcbCbModel);
        if (pcbs.size() > 0) {
            selectedPcb = pcbs.get(0);
        }
    }
}