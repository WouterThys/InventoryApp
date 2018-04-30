package com.waldo.inventory.gui.dialogs.ordersearchitemdialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.inventory.gui.components.tablemodels.IOrderSearchItemsTableModel;
import com.waldo.inventory.gui.components.wrappers.SelectableTableItem;
import com.waldo.inventory.gui.dialogs.editordersdialog.EditOrdersDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

abstract class OrderSearchItemsDialogLayout extends iDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IOrderSearchItemsTableModel tableModel;
    private ITable<SelectableTableItem> orderItemTable;

    // Filters
    private IComboBox<Order> orderCb;
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
        orderCb = new IComboBox<>(SearchManager.sm().findPlannedOrders(), new ComparatorUtils.DbObjectNameComparator<>(), true);
        orderCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedOrder = (Order) orderCb.getSelectedItem();
                tableInitialize();
                updateEnabledComponents();
            }
        });
    }

    @Override
    public void initializeLayouts() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(orderItemTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JPanel filterPanel = new JPanel();
        filterPanel.add(GuiUtils.createComponentWithAddAction(orderCb, e -> {
            EditOrdersDialog dialog = new EditOrdersDialog(OrderSearchItemsDialogLayout.this, new Order(), Statics.DistributorType.Items, false);
            if (dialog.showDialog() == IDialog.OK) {
                selectedOrder = dialog.getOrder();
                orderCb.addItem(selectedOrder);
                orderCb.setSelectedItem(selectedOrder);

                tableInitialize();
                updateEnabledComponents();
            }
        }));

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