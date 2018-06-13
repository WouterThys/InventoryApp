package com.waldo.inventory.gui.dialogs.ordersearchitemdialog;

import com.waldo.inventory.Utils.Statics.OrderImportType;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.ItemOrder;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.inventory.gui.components.tablemodels.IOrderSearchItemsTableModel;
import com.waldo.inventory.gui.components.wrappers.SelectableTableItem;
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
    private IActions.SearchAction searchAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ItemOrder selectedItemOrder;
    OrderImportType orderImportType;
    ProjectPcb projectPcb;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderSearchItemsDialogLayout(Application application, ItemOrder itemOrder, OrderImportType orderImportType, ProjectPcb projectPcb) {
        super(application, "Search item to itemOrder");
        this.selectedItemOrder = itemOrder;
        this.orderImportType = orderImportType;
        this.projectPcb = projectPcb;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    abstract void onImportItems(ItemOrder itemOrder);
    abstract void onRowDoubleClicked(int row);
    abstract void onSearchItems();

    void updateEnabledComponents() {
        boolean hasOrder = selectedItemOrder != null;
        boolean hasSelected = getSelectedItems().size() > 0;

        getButtonOK().setEnabled(hasOrder && hasSelected);
    }

    List<Item> getSelectedItems() {
        List<Item> selectedItems = new ArrayList<>();
        for (SelectableTableItem selectableTableItem : tableModel.getItemList()) {
            if (selectableTableItem.isSelected()) {
                selectedItems.add(selectableTableItem.getItem());
            }
        }
        return selectedItems;
    }

    boolean hasItems() {
        return tableModel.getItemList().size() > 0;
    }

    public Item getSelectedItem() {
        SelectableTableItem selectableTableItem = orderItemTable.getSelectedItem();
        if (selectableTableItem != null) {
            return selectableTableItem.getItem();
        }
        return null;
    }

    void tableInitialize(List<SelectableTableItem> itemsToOrder) {
       tableModel.setItemList(itemsToOrder);
    }

    void tableAddItems(List<SelectableTableItem> itemsToOrder) {
        tableModel.addItems(itemsToOrder);
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
                SwingUtilities.invokeLater(() -> onImportItems(selectedItemOrder));
            }
        };

        searchAction = new IActions.SearchAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onSearchItems());
            }
        };
    }

    @Override
    public void initializeLayouts() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(orderItemTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JPanel filterPanel = new JPanel(new BorderLayout());
        JButton wizardBtn = new JButton(wizardAction);
        JButton searchBtn = new JButton(searchAction);
        Box box = Box.createHorizontalBox();
        box.add(wizardBtn);
        box.add(searchBtn);
        filterPanel.add(box, BorderLayout.EAST);

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