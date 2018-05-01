package com.waldo.inventory.gui.dialogs.ordersearchitemdialog;

import com.waldo.inventory.Utils.Statics.OrderImportType;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.inventory.gui.components.tablemodels.IOrderSearchItemsTableModel;
import com.waldo.inventory.gui.components.wrappers.SelectableTableItem;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    Order selectedOrder;
    OrderImportType orderImportType;
    ProjectPcb projectPcb;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderSearchItemsDialogLayout(Application application, Order order, OrderImportType orderImportType, ProjectPcb projectPcb) {
        super(application, "Search item to order");
        this.selectedOrder = order;
        this.orderImportType = orderImportType;
        this.projectPcb = projectPcb;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    abstract void onImportItems(Order order);
    abstract void onRowDoubleClicked(int row);

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

    public Item getSelectedItem() {
        SelectableTableItem selectableTableItem = orderItemTable.getSelectedItem();
        if (selectableTableItem != null) {
            return selectableTableItem.getItem();
        }
        return null;
    }

    void tableInitialize(List<Item> itemsToOrder) {
       tableModel.clearItemList();
       List<SelectableTableItem> tableItems = new ArrayList<>();

       for (Item item : itemsToOrder) {
           tableItems.add(new SelectableTableItem(item));
       }

       tableModel.setItemList(tableItems);
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
        orderItemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    Point point = e.getPoint();
                    int row = orderItemTable.rowAtPoint(point);
                    onRowDoubleClicked(row);
                }
            }
        });

        // Filters
        wizardAction = new IActions.WizardAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onImportItems(selectedOrder);
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

    }
}