package com.waldo.inventory.gui.dialogs.ordersearchitemdialog;

import com.waldo.inventory.Utils.Statics.OrderImportType;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.wrappers.SelectableTableItem;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.ordersearchitemdialog.orderitemwizarddialog.OrderItemWizardDialog;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class OrderSearchItemsDialog extends OrderSearchItemsDialogLayout {

    private boolean firstTime = true;

    public OrderSearchItemsDialog(Application application) {
        this(application, (Order)null);
    }

    public OrderSearchItemsDialog(Application application, Order order) {
        this(application, order, null, null);
    }

    public OrderSearchItemsDialog(Application application, ProjectPcb projectPcb) {
        this(application, null, OrderImportType.FromPcb, projectPcb);
    }

    public OrderSearchItemsDialog(Application application, Order order, OrderImportType orderImportType, ProjectPcb projectPcb) {
        super(application, order, orderImportType, projectPcb);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

    @Override
    protected void onOK() {

        if (selectedOrder == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select an order..",
                    "No order",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }


        ((Application) parent).addItemsToOrder(getSelectedItems(), selectedOrder);
        super.onOK();
    }

    @Override
    void onImportItems(Order order) {

            OrderItemWizardDialog dialog = new OrderItemWizardDialog(OrderSearchItemsDialog.this, order, orderImportType, projectPcb);
            if (dialog.showDialog() == IDialog.OK) {

                int res = JOptionPane.YES_OPTION;
                if (hasItems()) {
                    res = JOptionPane.showConfirmDialog(
                            OrderSearchItemsDialog.this,
                            "There are already some items in the list, remove them first? \n (No will keep them) ",
                            "Remove",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                }

                if (res == JOptionPane.YES_OPTION) {
                    tableInitialize(dialog.getItemsToOrder());
                } else {
                    tableAddItems(dialog.getItemsToOrder());
                }

                selectedOrder = dialog.getSelectedOrder();
            }
            updateEnabledComponents();

    }

    @Override
    void onSearchItems() {
        AdvancedSearchDialog dialog = new AdvancedSearchDialog(parent, true);
        if (dialog.showDialog() == IDialog.OK) {
            List<Item> items = dialog.getAllSelectedItems();
            List<SelectableTableItem> selectableTableItems = new ArrayList<>();
            for (Item item : items) {
                selectableTableItems.add(new SelectableTableItem(item));
            }
            tableAddItems(selectableTableItems);
        }
    }

    @Override
    void onRowDoubleClicked(int row) {
        if (row >= 0) {
            Item item = getSelectedItem();
            if (item != null) {
                EditItemDialog dialog = new EditItemDialog<>(OrderSearchItemsDialog.this, "Item", item);
                dialog.showDialog();
            }
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
        super.windowActivated(e);

        if (firstTime) {
            onImportItems(selectedOrder);
            firstTime = false;
        }
    }
}
