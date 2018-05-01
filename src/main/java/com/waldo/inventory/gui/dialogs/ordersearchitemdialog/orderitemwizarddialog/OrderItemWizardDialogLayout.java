package com.waldo.inventory.gui.dialogs.ordersearchitemdialog.orderitemwizarddialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.OrderImportType;
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
import java.awt.event.ItemEvent;
import java.util.List;

abstract class OrderItemWizardDialogLayout extends iDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // Order
    IComboBox<Order> orderCb;
    private ILabel distributorLbl;

    // Select
    IComboBox<OrderImportType> importTypeCb;

    // Filter
    private ICheckBox allowEmptyReferenceCb;

    // Pcbs
    private IProjectPcbTableModel tableModel;
    private ITable<ProjectPcb> pcbTable;

    private IActions.AddAction addPcbAction;
    private IActions.DeleteAction removePcbAction;

    // View
    private JPanel cardPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Order selectedOrder;
    OrderImportType orderImportType;
    ProjectPcb projectPcb;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderItemWizardDialogLayout(Window window, Order selectedOrder, OrderImportType orderImportType, ProjectPcb projectPcb) {
        super(window, "");
        this.selectedOrder = selectedOrder;
        this.orderImportType = orderImportType;
        this.projectPcb = projectPcb;

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void updateEnabledComponents() {
        boolean pcbsSelected = pcbTable.getSelectedItems().size()  > 0;
        removePcbAction.setEnabled(pcbsSelected);
    }

    boolean isAllowEmptyReference() {
        return allowEmptyReferenceCb.isSelected();
    }

    OrderImportType getSelectedType() {
        return (OrderImportType) importTypeCb.getSelectedItem();
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
        if (selectedOrder == null) {
            gbc.addLine("Order: ", GuiUtils.createComponentWithAddAction(orderCb, e -> {
                EditOrdersDialog dialog = new EditOrdersDialog(OrderItemWizardDialogLayout.this, new Order(), Statics.DistributorType.Items, false);
                if (dialog.showDialog() == IDialog.OK) {
                    addOrder(dialog.getOrder());
                }
            }));
        } else {
            gbc.addLine("Order: ", orderCb);
        }
        gbc.addLine("Distributor: ", distributorLbl);
        gbc.addLine("Type: ", importTypeCb);

        orderPanel.setBorder(GuiUtils.createInlineTitleBorder("Order"));

        return orderPanel;
    }

    private JPanel createFromQuantityPanel() {
        JPanel quantityPnl = new JPanel(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(allowEmptyReferenceCb, BorderLayout.NORTH);
        //GridBagHelper gbc = new GridBagHelper(panel);
        //gbc.addLine("Allow empty reference: ", allowEmptyReferenceCb);

        quantityPnl.add(panel, BorderLayout.CENTER);
        quantityPnl.setBorder(GuiUtils.createInlineTitleBorder("From quantity"));

        return quantityPnl;
    }

    private JPanel createFromPcbPanel() {
        JPanel pcbPnl = new JPanel(new BorderLayout());

        JPanel tablePnl = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(pcbTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        tablePnl.add(scrollPane, BorderLayout.CENTER);
        tablePnl.add(GuiUtils.createNewToolbar(addPcbAction, removePcbAction), BorderLayout.SOUTH);

        pcbPnl.add(tablePnl, BorderLayout.CENTER);
        pcbPnl.setBorder(GuiUtils.createInlineTitleBorder("From PCBs"));

        return pcbPnl;
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
        orderCb.addItemListener(e -> editOrder((Order) orderCb.getSelectedItem()));
        distributorLbl = new ILabel();
        distributorLbl.setEnabled(false);

        // Select
        cardPanel = new JPanel(new CardLayout());
        importTypeCb = new IComboBox<>(OrderImportType.values());
        importTypeCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                CardLayout cl = (CardLayout) cardPanel.getLayout();
                OrderImportType type = (OrderImportType) e.getItem();
                cl.show(cardPanel, type.toString());
            }
        });

        // Filter
        allowEmptyReferenceCb = new ICheckBox("Allow empty reference", true);

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
        JPanel quantityPanel = createFromQuantityPanel();
        JPanel pcbPanel = createFromPcbPanel();

        cardPanel.add(OrderImportType.FromQuantities.toString(), quantityPanel);
        cardPanel.add(OrderImportType.FromPcb.toString(), pcbPanel);

        mainPanel.add(orderPanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);

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

        if (orderImportType != null) {
            importTypeCb.setSelectedItem(orderImportType);
        }

        if (projectPcb != null) {
            tableModel.addItem(projectPcb);
        }

        updateEnabledComponents();
    }
}