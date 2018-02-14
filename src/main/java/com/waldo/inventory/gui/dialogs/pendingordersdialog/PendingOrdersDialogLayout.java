package com.waldo.inventory.gui.dialogs.pendingordersdialog;

import com.waldo.inventory.classes.dbclasses.PendingOrder;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.tablemodels.IPendingOrdersTableModel;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

abstract class PendingOrdersDialogLayout extends IDialog
    implements ListSelectionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    IPendingOrdersTableModel tableModel;
    ITable<PendingOrder> pendingOrderTable;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PendingOrder selectedPendingOrder;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PendingOrdersDialogLayout(Window window, String title) {
        super(window, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        setResizable(true);
        setTitleName(getTitle());
        setTitleIcon(imageResource.readImage("Orders.Pending.Title"));

        tableModel = new IPendingOrdersTableModel();
        pendingOrderTable = new ITable<>(tableModel);
        pendingOrderTable.getSelectionModel().addListSelectionListener(this);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(pendingOrderTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        getContentPanel().add(scrollPane, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        tableModel.setItemList(cache().getPendingOrders());
    }
}