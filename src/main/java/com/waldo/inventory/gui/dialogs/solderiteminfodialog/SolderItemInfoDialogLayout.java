package com.waldo.inventory.gui.dialogs.solderiteminfodialog;

import com.waldo.inventory.classes.dbclasses.CreatedPcbLink;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.classes.dbclasses.SolderItem;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.utils.DateUtils;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;

abstract class SolderItemInfoDialogLayout extends iDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField pcbItemTf;
    private ITextField referenceTf;
    private ITextField usedItemTf;
    private ITextField stateTf;

    private ITextField numTimesSolderedTf;
    private ITextField dateSolderedTf;

    private ITextField numTimesDesolderedTf;
    private ITextField dateDesolderedTf;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private SolderItem solderItem;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SolderItemInfoDialogLayout(Window window, SolderItem solderItem) {
        super(window, "Solder item");
        this.solderItem = solderItem;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        showTitlePanel(false);

        pcbItemTf = new ITextField(false);
        referenceTf = new ITextField(false);
        usedItemTf = new ITextField(false);
        stateTf = new ITextField(false);

        numTimesSolderedTf = new ITextField(false);
        dateSolderedTf = new ITextField(false);
        numTimesDesolderedTf = new ITextField(false);
        dateDesolderedTf = new ITextField(false);
    }

    @Override
    public void initializeLayouts() {

        GuiUtils.GridBagHelper gbc;

        JPanel itemPnl = new JPanel();
        itemPnl.setBorder(GuiUtils.createInlineTitleBorder("Item"));
        gbc = new GuiUtils.GridBagHelper(itemPnl, 140);
        gbc.addLine("Pcb item: ", pcbItemTf);
        gbc.addLine("Reference: ", referenceTf);
        gbc.addLine("Used item: ", usedItemTf);
        gbc.addLine("State: ", stateTf);

        JPanel solderPnl = new JPanel();
        solderPnl.setBorder(GuiUtils.createInlineTitleBorder("Soldering"));
        gbc = new GuiUtils.GridBagHelper(solderPnl, 140);
        gbc.addLine("Times soldered: ", numTimesSolderedTf);
        gbc.addLine("Last soldered: ", dateSolderedTf);
        gbc.addLine("Times desoldered: ", numTimesDesolderedTf);
        gbc.addLine("Last de-soldered: ", dateDesolderedTf);

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        getContentPanel().add(itemPnl, BorderLayout.NORTH);
        getContentPanel().add(solderPnl, BorderLayout.SOUTH);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (solderItem != null) {
            CreatedPcbLink createdPcbLink = solderItem.getCreatedPcbLink();
            if (createdPcbLink != null) {
                PcbItemProjectLink projectLink = createdPcbLink.getPcbItemProjectLink();
                if (projectLink != null) {
                    pcbItemTf.setText(projectLink.getPrettyName());
                }
            }
            referenceTf.setText(solderItem.getName());
            if (solderItem.getUsedItemId() > DbObject.UNKNOWN_ID) {
                usedItemTf.setText(solderItem.getUsedItem().toString());
            }
            stateTf.setText(solderItem.getState().toString());
            numTimesSolderedTf.setText(String.valueOf(solderItem.getNumTimesSoldered()));
            dateSolderedTf.setText(DateUtils.formatDateTime(solderItem.getSolderDate()));
            numTimesDesolderedTf.setText(String.valueOf(solderItem.getNumTimesDesoldered()));
            dateDesolderedTf.setText(DateUtils.formatDateTime(solderItem.getDesolderDate()));
        }
    }
}