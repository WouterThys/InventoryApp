package com.waldo.inventory.gui.dialogs.ordersearchitemdialog;

import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.inventory.gui.components.tablemodels.IOrderSearchItemsTableModel;
import com.waldo.inventory.gui.components.wrappers.SelectableTableItem;
import com.waldo.inventory.gui.dialogs.ordersearchitemdialog.orderitemwizarddialog.OrderItemWizardDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

abstract class OrderSearchItemsDialogLayout extends iDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IOrderSearchItemsTableModel tableModel;
    private ITable<SelectableTableItem> orderItemTable;

    // Filters
    private IActions.WizardAction wizardAction;
    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Order selectedOrder;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderSearchItemsDialogLayout(Application application, Order order) {
        super(application, "Search item to order");
        selectedOrder = order;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void updateEnabledComponents() {
        boolean hasOrder = selectedOrder != null;
        boolean hasSelected = getSelectedItems().size() > 0;

        getButtonOK().setEnabled(hasOrder && hasSelected);
    }

    public List<Item> getSelectedItems() {
        List<Item> selectedItems = new ArrayList<>();
        for (SelectableTableItem selectableTableItem : tableModel.getItemList()) {
            if (selectableTableItem.isSelected()) {
                selectedItems.add(selectableTableItem.getItem());
            }
        }
        return selectedItems;
    }

    void tableInitialize() {
        // If distributor selected..
        // If order selected..
        // If other filter..

        List<Item> itemList = SearchManager.sm().findItemsToOrder();
        List<SelectableTableItem> selectableTableItems = new ArrayList<>();
        for (Item item : itemList) {
            if (selectedOrder == null) {
                selectableTableItems.add(new SelectableTableItem(item));
            } else {
                Distributor d = selectedOrder.getDistributor();
                if (d != null) {
                    if (SearchManager.sm().findDistributorPartLink(d.getId(), item) != null) {
                        selectableTableItems.add(new SelectableTableItem(item));
                    }
                }
            }
        }

        tableModel.setItemList(selectableTableItems);
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        setResizable(true);
        getButtonOK().setEnabled(false);
        getButtonOK().setText("Add to order");

        // Table
        tableModel = new IOrderSearchItemsTableModel();
        orderItemTable = new ITable<>(tableModel);

        // Filters
        wizardAction = new IActions.WizardAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrderItemWizardDialog dialog = new OrderItemWizardDialog(OrderSearchItemsDialogLayout.this, selectedOrder);
                if (dialog.showDialog() == IDialog.OK) {
                    // Get order items


                    updateEnabledComponents();
                }
            }
        };
    }

    @Override
    public void initializeLayouts() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(orderItemTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(GuiUtils.createNewToolbar(wizardAction), BorderLayout.WEST);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(panel);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        tableInitialize();
    }
}