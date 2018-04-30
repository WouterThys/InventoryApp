package com.waldo.inventory.gui.dialogs.ordersearchitemdialog.orderitemwizarddialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.inventory.gui.components.tablemodels.IProjectPcbTableModel;
import com.waldo.inventory.gui.dialogs.editordersdialog.EditOrdersDialog;
import com.waldo.inventory.gui.dialogs.selectpcbdialog.SelectPcbDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils.GridBagHelper;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

abstract class OrderItemWizardDialogLayout extends iDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // Order
    IComboBox<Order> orderCb;
    private ILabel distributorLbl;

    // Filter
    private ICheckBox allowEmptyDistributorCb;
    private ICheckBox availableLessThanMinimumCb;
    private ICheckBox forPcbCb;

    private IProjectPcbTableModel tableModel;
    private ITable<ProjectPcb> pcbTable;

    private IActions.AddAction addPcbAction;
    private IActions.DeleteAction removePcbAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Order selectedOrder;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderItemWizardDialogLayout(Window window, Order selectedOrder) {
        super(window, "");
        this.selectedOrder = selectedOrder;

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void updateEnabledComponents() {
        boolean withPcbs = forPcbCb.isSelected();
        boolean pcbsSelected = pcbTable.getSelectedItems().size()  > 0;

        pcbTable.setEnabled(withPcbs);
        addPcbAction.setEnabled(withPcbs);
        removePcbAction.setEnabled(withPcbs && pcbsSelected);
    }

    boolean isAllowEmptyReference() {
        return allowEmptyDistributorCb.isSelected();
    }

    boolean isAvailableLessThanMinimum() {
        return availableLessThanMinimumCb.isSelected();
    }

    boolean isFromPcb() {
        return forPcbCb.isSelected();
    }

    List<ProjectPcb> getPcbs() {
        return tableModel.getItemList();
    }

    private void addOrder(Order order) {
        selectedOrder = order;
        if (order != null) {
            orderCb.addItem(selectedOrder);
            orderCb.setSelectedItem(selectedOrder);
            if (order.getDistributorId() > DbObject.UNKNOWN_ID) {
                distributorLbl.setText(selectedOrder.getDistributor().toString());
            }
        }
        updateEnabledComponents();
    }

    private void editOrder(Order order) {
        selectedOrder = order;
        if (order != null) {
            if (order.getDistributorId() > DbObject.UNKNOWN_ID) {
                distributorLbl.setText(selectedOrder.getDistributor().toString());
            }
        }
        updateEnabledComponents();
    }

    private JPanel createOrderPanel() {
        JPanel orderPanel = new JPanel();

        GridBagHelper gbc = new GridBagHelper(orderPanel);
        gbc.addLine("Order: ", GuiUtils.createComponentWithAddAction(orderCb, e -> {
            EditOrdersDialog dialog = new EditOrdersDialog(OrderItemWizardDialogLayout.this, new Order(), Statics.DistributorType.Items, false);
            if (dialog.showDialog() == IDialog.OK) {
                addOrder(dialog.getOrder());
            }
        }));
        gbc.addLine("Distributor: ", distributorLbl);

        orderPanel.setBorder(GuiUtils.createInlineTitleBorder("Order"));

        return orderPanel;
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new BorderLayout());

        JPanel checkBoxPnl = new JPanel();
        GridBagHelper gbc = new GridBagHelper(checkBoxPnl, 250);
        gbc.addLine("Allow empty reference: ", allowEmptyDistributorCb);
        gbc.addLine("Item available < minimum: ", availableLessThanMinimumCb);
        gbc.addLine("Search for creating PCB: ", forPcbCb);

        JPanel pcbPnl = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(pcbTable);
        pcbPnl.add(scrollPane, BorderLayout.CENTER);
        pcbPnl.add(GuiUtils.createNewToolbar(addPcbAction, removePcbAction), BorderLayout.SOUTH);

        filterPanel.add(checkBoxPnl, BorderLayout.NORTH);
        filterPanel.add(pcbPnl, BorderLayout.CENTER);
        filterPanel.setBorder(GuiUtils.createInlineTitleBorder("Filters"));

        return filterPanel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        showTitlePanel(false);
        setResizable(true);

        // Order
        orderCb = new IComboBox<>(SearchManager.sm().findPlannedOrders(), new ComparatorUtils.DbObjectNameComparator<>(), true);
        orderCb.addItemListener(e -> {
            editOrder((Order) orderCb.getSelectedItem());
        });
        distributorLbl = new ILabel();
        distributorLbl.setEnabled(false);

        // Filter
        allowEmptyDistributorCb = new ICheckBox("", true);
        availableLessThanMinimumCb = new ICheckBox("", true);
        forPcbCb = new ICheckBox("", false);
        forPcbCb.addActionListener(e -> updateEnabledComponents());

        tableModel = new IProjectPcbTableModel();
        pcbTable = new ITable<>(tableModel);
        pcbTable.getSelectionModel().addListSelectionListener(e -> updateEnabledComponents());

        // Actions
        addPcbAction = new IActions.AddAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectPcbDialog dialog = new SelectPcbDialog(OrderItemWizardDialogLayout.this);
                if (dialog.showDialog() == IDialog.OK) {
                    List<ProjectPcb> selectedPcbs = dialog.getSelectedPcbs();
                    tableModel.addItems(selectedPcbs);
                    updateEnabledComponents();
                }
            }
        };
        removePcbAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ProjectPcb> pcbList = pcbTable.getSelectedItems();
                if (pcbList.size() > 0) {
                    int res;
                    if (pcbList.size() == 1) {
                        res = JOptionPane.showConfirmDialog(
                                OrderItemWizardDialogLayout.this,
                                "Do you want to delete " + pcbList.get(0) + " from the list?",
                                "Delete",
                                JOptionPane.YES_NO_OPTION
                        );
                    } else {
                        res = JOptionPane.showConfirmDialog(
                                OrderItemWizardDialogLayout.this,
                                "Do you want to delete " + pcbList.size() + " PCB's from the list?",
                                "Delete",
                                JOptionPane.YES_NO_OPTION
                        );
                    }

                    if (res == JOptionPane.YES_OPTION) {
                        tableModel.removeItems(pcbList);
                    }
                    updateEnabledComponents();
                }
            }
        };

    }

    @Override
    public void initializeLayouts() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel orderPanel = createOrderPanel();
        JPanel filterPanel = createFilterPanel();

        mainPanel.add(orderPanel, BorderLayout.NORTH);
        mainPanel.add(filterPanel, BorderLayout.CENTER);

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(mainPanel, BorderLayout.CENTER);
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (selectedOrder != null) {
            orderCb.setSelectedItem(selectedOrder);
            editOrder(selectedOrder);
        }

        updateEnabledComponents();
    }
}