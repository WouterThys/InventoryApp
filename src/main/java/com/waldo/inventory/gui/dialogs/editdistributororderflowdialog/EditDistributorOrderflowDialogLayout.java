package com.waldo.inventory.gui.dialogs.editdistributororderflowdialog;

import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.DistributorOrderFlow;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.tablemodels.IOrderFlowTableModel;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

abstract class EditDistributorOrderflowDialogLayout extends IDialog implements ListSelectionListener, IdBToolBar.IdbToolBarListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IOrderFlowTableModel tableModel;
    ITable<DistributorOrderFlow> orderFlowTable;

    private IdBToolBar toolBar;
    private IActions.NavigateUpAction moveUpAction;
    private IActions.NavigateDownAction moveDownAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Distributor distributor;
    DistributorOrderFlow selectedOrderFlow;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditDistributorOrderflowDialogLayout(Window window) {
        super(window, "ItemOrder flow");

    }

    public abstract void moveUp(DistributorOrderFlow flow);
    public abstract void moveDown(DistributorOrderFlow flow);

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean enabled = selectedOrderFlow != null;

        toolBar.setDeleteActionEnabled(enabled);
        toolBar.setEditActionEnabled(enabled);
        moveDownAction.setEnabled(enabled);
        moveUpAction.setEnabled(enabled);
    }

    void tableInitialize(List<DistributorOrderFlow> orderFlowList) {
        if (orderFlowList != null) {
            tableModel.setItemList(orderFlowList);
        }
    }

    void tableAddFlow(DistributorOrderFlow flow) {
        tableModel.addItem(flow);
    }

    void tableDeleteFlow(DistributorOrderFlow flow) {
        tableModel.removeItem(flow);
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        showTitlePanel(false);
        setResizable(true);
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setEnabled(false);

        tableModel = new IOrderFlowTableModel();
        orderFlowTable = new ITable<>(tableModel);
        orderFlowTable.getSelectionModel().addListSelectionListener(this);

        moveUpAction = new IActions.NavigateUpAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveUp(selectedOrderFlow);
            }
        };

        moveDownAction = new IActions.NavigateDownAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveDown(selectedOrderFlow);
            }
        };

        toolBar = new IdBToolBar(this);
        toolBar.addSeparateAction(moveDownAction);
        toolBar.addAction(moveUpAction);
    }

    @Override
    public void initializeLayouts() {

        JScrollPane scrollPane = new JScrollPane(orderFlowTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(toolBar, BorderLayout.PAGE_END);
        panel.add(scrollPane);

        getContentPanel().add(panel);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        tableInitialize(distributor.getOrderFlowTemplate());
        updateEnabledComponents();
    }
}