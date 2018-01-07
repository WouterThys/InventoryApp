package com.waldo.inventory.gui.dialogs.alllinkeditemsdialog;

import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.ILinkedItemsTableModel;

import javax.swing.*;
import java.awt.*;

abstract class AllLinkedItemsDialogLayout extends IDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILinkedItemsTableModel tableModel;
    private ITable<PcbItemItemLink> itemTable;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private PcbItem pcbItem;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    AllLinkedItemsDialogLayout(Application application, PcbItem pcbItem) {
        super(application, pcbItem.getPartName());

        this.pcbItem = pcbItem;

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setResizable(true);
        showTitlePanel(false);

        // This
        tableModel = new ILinkedItemsTableModel();
        itemTable = new ITable<>(tableModel);

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel mainPnl = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        mainPnl.add(scrollPane, BorderLayout.CENTER);

        getContentPanel().add(mainPnl, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (pcbItem != null) {
            tableModel.setItemList(pcbItem.getKnownItemLinks());
        }
    }
}