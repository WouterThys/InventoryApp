package com.waldo.inventory.gui.dialogs.projectusedpcbitemsdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class UsedPcbItemsDialogLayout extends IDialog implements
        UsedPcbItemPanel.PcbItemListener,
        UsedConfirmedPanel.UsedListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    UsedPcbItemPanel pcbItemPnl;
    UsedConfirmedPanel usedPnl;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectPcb selectedPcb;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    UsedPcbItemsDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {

    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setResizable(true);
        setTitleIcon(imageResource.readImage("Projects.Used.Title"));
        setTitleName(getTitle());

        // Panels
        pcbItemPnl = new UsedPcbItemPanel(this);
        usedPnl = new UsedConfirmedPanel(this);


    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        // Center
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));

        pcbItemPnl.setBorder(PanelUtils.createTitleBorder("Pcb items"));
        usedPnl.setBorder(PanelUtils.createTitleBorder("To selectedOrder"));

        centerPanel.add(pcbItemPnl);
        centerPanel.add(usedPnl);

        // Add
        getContentPanel().add(centerPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        pcbItemPnl.updateComponents(selectedPcb);
        usedPnl.usedTableInit(pcbItemPnl.getAllLinks());
    }


}