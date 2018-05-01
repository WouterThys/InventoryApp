package com.waldo.inventory.gui.dialogs.ordersearchitemdialog.orderitemwizarddialog;

import com.waldo.inventory.Utils.Statics.OrderImportType;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemWizardDialog extends OrderItemWizardDialogLayout {

    private List<Item> itemsToOrder = new ArrayList<>();

    public OrderItemWizardDialog(Window window, Order order, OrderImportType orderImportType, ProjectPcb projectPcb) {
        super(window, order, orderImportType, projectPcb);

        initializeComponents();
        initializeLayouts();
        updateComponents();

        orderCb.setEnabled(order == null);
        importTypeCb.setEnabled(orderImportType == null);
    }

    public List<Item> getItemsToOrder() {
        return itemsToOrder;
    }

    public Order getSelectedOrder() {
        return selectedOrder;
    }

    private void findItemsWithQuantities() {
        boolean allowEmptyRef = isAllowEmptyReference();
        List<Item> quantityItems = SearchManager.sm().findItemsToOrder();

        if (allowEmptyRef) {
            itemsToOrder.addAll(quantityItems);
        } else {
            for (Item item : quantityItems) {
                DistributorPartLink link = SearchManager.sm().findDistributorPartLink(selectedOrder.getDistributorId(), item);
                if (link != null) {
                    itemsToOrder.add(item);
                }
            }
        }
    }

    private void findItemsForPcbs(List<ProjectPcb> pcbList) {
        if (pcbList != null && pcbList.size() > 0) {

            for (ProjectPcb pcb : pcbList) {
                for (PcbItemProjectLink projectLink : pcb.getPcbItemList()) {
                    if (projectLink.getPcbItemItemLinkId() > DbObject.UNKNOWN_ID) {
                        PcbItemItemLink itemLink = projectLink.getPcbItemItemLink();
                        Item item = itemLink.getItem();
                        if (item != null) {
                            if (item.getAmount() < projectLink.getNumberOfReferences()) {
                                itemsToOrder.add(item);
                            }
                        }
                    }
                }
            }

        }
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

        itemsToOrder.clear();
        if (getSelectedType().equals(OrderImportType.FromQuantities)) {

            findItemsWithQuantities();

        } else {

            findItemsForPcbs(getPcbs());

        }

        super.onOK();
    }
}