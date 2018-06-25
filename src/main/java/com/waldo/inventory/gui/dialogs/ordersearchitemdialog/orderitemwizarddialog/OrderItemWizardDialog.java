package com.waldo.inventory.gui.dialogs.ordersearchitemdialog.orderitemwizarddialog;

import com.waldo.inventory.Utils.Statics.OrderImportType;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.wrappers.SelectableTableItem;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemWizardDialog extends OrderItemWizardDialogLayout {

    private List<SelectableTableItem> itemsToOrder = new ArrayList<>();

    public OrderItemWizardDialog(Window window, ItemOrder itemOrder, OrderImportType orderImportType, ProjectPcb projectPcb) {
        super(window, itemOrder, orderImportType, projectPcb);

        initializeComponents();
        initializeLayouts();
        updateComponents();

        orderCb.setEnabled(itemOrder == null);
        importTypeCb.setEnabled(orderImportType == null);
    }

    public List<SelectableTableItem> getItemsToOrder() {
        return itemsToOrder;
    }

    public ItemOrder getSelectedOrder() {
        return selectedItemOrder;
    }

    private void findItemsWithQuantities() {
        boolean allowEmptyRef = isAllowEmptyReference();
        List<Item> quantityItems = SearchManager.sm().findItemsToOrder();

        for (Item item : quantityItems) {
            addItemToOrder(item, allowEmptyRef);
        }
    }

    private void addItemToOrder(Item item, boolean allowEmptyRef) {
        if (allowEmptyRef) {
            itemsToOrder.add(new SelectableTableItem(item));
        } else {
            DistributorPartLink link = SearchManager.sm().findDistributorPartLink(selectedItemOrder.getDistributorId(), item);
            if (link != null) {
                itemsToOrder.add(new SelectableTableItem(item));
            }
        }
    }

    private void findItemsForPcbs(List<ProjectPcb> pcbList) {
        if (pcbList != null && pcbList.size() > 0) {
            boolean allowEmptyRef = isAllowEmptyReference();
            for (ProjectPcb pcb : pcbList) {
                for (PcbItemProjectLink projectLink : pcb.getPcbItemList()) {
                    if (projectLink.getPcbItemItemLinkId() > DbObject.UNKNOWN_ID) {
                        PcbItemItemLink itemLink = projectLink.getPcbItemItemLink();
                        Item item = itemLink.getItem();
                        if (item != null) {
                            SelectableTableItem selectableTableItem = new SelectableTableItem(item);
                            selectableTableItem.setSelected(item.getAmount() < projectLink.getNumberOfReferences());
                            if (allowEmptyRef) {
                                itemsToOrder.add(selectableTableItem);
                            } else {
                                DistributorPartLink link = SearchManager.sm().findDistributorPartLink(selectedItemOrder.getDistributorId(), item);
                                if (link != null) {
                                    itemsToOrder.add(selectableTableItem);
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    @Override
    protected void onOK() {
        selectedItemOrder = (ItemOrder) orderCb.getSelectedItem();
        if (selectedItemOrder == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select an order..",
                    "No order",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        itemsToOrder.clear();
        if (getSelectedType().equals(OrderImportType.FromQuantities)) {

            findItemsWithQuantities();

        } else {

            findItemsForPcbs(getPcbs());

        }

        super.onOK();
    }
}