package com.waldo.inventory.gui.dialogs.pendingordersdialog;

import com.waldo.inventory.classes.dbclasses.PendingOrder;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.tablemodels.IPendingOrdersTableModel;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

abstract class PendingOrdersDialogLayout extends IDialog
    implements ListSelectionListener, IdBToolBar.IdbToolBarListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    IPendingOrdersTableModel tableModel;
    ITable<PendingOrder> pendingOrderTable;

    private IdBToolBar toolBar;

    private IActions.OrderItemAction orderItemAction;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final Application application;
     PendingOrder selectedPendingOrder;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PendingOrdersDialogLayout(Application application, String title) {
        super(application, title);
        this.application = application;

    }

    abstract void orderItems(List<PendingOrder> pendingOrders);

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean enabled = selectedPendingOrder != null;

        toolBar.setEditActionEnabled(enabled);
        toolBar.setDeleteActionEnabled(enabled);
    }

    List<PendingOrder> getSelectedPendingOrders() {
        return pendingOrderTable.getSelectedItems();
    }

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

        toolBar = new IdBToolBar(this);

        orderItemAction = new IActions.OrderItemAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orderItems(getSelectedPendingOrders());
            }
        };
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel topTbPanel = new JPanel(new BorderLayout());
        topTbPanel.add(toolBar, BorderLayout.EAST);

        JPanel btmTbPanel = new JPanel(new BorderLayout());
        btmTbPanel.add(GuiUtils.createNewToolbar(orderItemAction), BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(pendingOrderTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        getContentPanel().add(topTbPanel, BorderLayout.PAGE_START);
        getContentPanel().add(scrollPane, BorderLayout.CENTER);
        getContentPanel().add(btmTbPanel, BorderLayout.PAGE_END);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        tableModel.setItemList(new ArrayList<>(cache().getPendingOrders()));

        updateEnabledComponents();
    }
}