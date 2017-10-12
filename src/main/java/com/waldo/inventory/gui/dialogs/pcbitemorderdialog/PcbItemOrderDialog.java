package com.waldo.inventory.gui.dialogs.pcbitemorderdialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;

import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class PcbItemOrderDialog extends PcbItemOrderDialogLayout {


    public PcbItemOrderDialog(Application application, String title, List<PcbItem> componentList) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(componentList);

        addTableListeners(createPcbItemListListener(), createOrderItemListListener());
    }

    private ListSelectionListener createPcbItemListListener() {
        return e -> {
            if (!e.getValueIsAdjusting()) {
                selectedComponent = pcbPanel.getSelectedComponent();
                pcbPanel.updateSelectedValueData(selectedComponent);

                if (selectedComponent.isOrdered()) {
                    orderPanel.setSelectedOrderItem(selectedComponent.getOrderItem());
                } else {
                    orderPanel.setSelectedOrderItem(null);
                }

                updateEnabledComponents();
            }
        };
    }

    private ListSelectionListener createOrderItemListListener() {
        return e -> {
            if (!e.getValueIsAdjusting()) {
                selectedOrderItem = orderPanel.getSelectedOrderItem();
                orderPanel.updateSelectedValueData(selectedOrderItem);
                updateEnabledComponents();
            }
        };
    }

    private OrderItem createOrderItem(PcbItem component) {
        OrderItem orderItem = new OrderItem();

        orderItem.setOrderId(selectedOrder.getId());
        orderItem.setItemId(component.getMatchedItemLink().getItemId());
        orderItem.setName(component.getMatchedItemLink().getItem().getName() + " - " + selectedOrder.toString());
        orderItem.setAmount(1);

        for (PcbItem c : sameSetComponents(component)) {
            c.setOrderItem(orderItem);
        }

        return orderItem;
    }

    private PcbItem findComponentForOrderItem(OrderItem orderItem) {
        for (PcbItem component : pcbPanel.getKcComponentList()) {
            if (component.isOrdered()) {
                if (component.getOrderItem().equals(orderItem)) {
                    return component;
                }
            }
        }
        return null;
    }

    private List<PcbItem> sameSetComponents(PcbItem component) {
        List<PcbItem> sameSet = new ArrayList<>();
        sameSet.add(component);
        if (component.getMatchedItemLink().isSetItem()) {
            Item parentItem = component.getMatchedItemLink().getItem();
            if (parentItem != null) {
                for (PcbItem c : pcbPanel.getKcComponentList()) {
                    if (c.getMatchedItemLink().isSetItem()) {
                        if (c.getMatchedItemLink().getItem().getId() == parentItem.getId()) {
                            sameSet.add(c);
                        }
                    }
                }
            }
        }
        return sameSet;
    }

    private void alreadyInOrder(OrderItem orderItem) {
        for (PcbItem component : pcbPanel.getKcComponentList()) {
            if (component.getMatchedItemLink().getItemId() == orderItem.getItemId()) {
                component.setOrderItem(orderItem);
                orderPanel.addOrderItem(orderItem,  false);
            }
        }
    }

    //
    // Dialog stuff
    //
    @Override
    public void windowOpened(WindowEvent e) {
        OrderItemDialog orderItemDialog = new OrderItemDialog(application, "Order", new ArrayList<>(), false);
        if (orderItemDialog.showDialog() == IDialog.OK) {
            selectedOrder = orderItemDialog.getSelectedOrder();
            if (selectedOrder == null) {
                super.onCancel();
            } else {
                for (OrderItem oi : selectedOrder.getOrderItems()) {
                    alreadyInOrder(oi);
                }
                pcbPanel.updateTable();
            }
        } else {
            super.onCancel();
        }
    }

    @Override
    protected void onOK() {
        List<OrderItem> orderItems = orderPanel.getOrderItems();
        for (int i = orderItems.size()-1; i >= 0; i--) {
            OrderItem orderItem = orderItems.get(i);
            if (selectedOrder.containsItemId(orderItem.getItemId())) {
                OrderItem containedOi = selectedOrder.findOrderItemInOrder(orderItem.getItemId());
                containedOi.setAmount(containedOi.getAmount() + orderItem.getAmount());
                orderItems.remove(i);
            }
        }
        application.addOrderItemsToOrder(orderItems, selectedOrder);
        super.onOK();
    }

    @Override
    protected void onCancel() {
        super.onCancel();
    }

    //
    // Button press
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(addToOrder)) {
            addToOrder();
        } else if (source.equals(removeFromOrder)) {
            removeFromOrder();
        } else if (source.equals(addAll)) {
            addAll();
        } else if (source.equals(addAllCheckStock)) {
            addAllCheckStock();
        }
    }

    private void addToOrder() {
        if (selectedComponent != null) {
            OrderItem toOrder = createOrderItem(selectedComponent);
            orderPanel.addOrderItem(toOrder, true);
            orderPanel.setSelectedOrderItem(toOrder);
        }
    }

    private void removeFromOrder() {
        if (selectedOrderItem != null) {
            if (selectedOrderItem.getAmount() > 1) {
                selectedOrderItem.setAmount(selectedOrderItem.getAmount() - 1);
                orderPanel.updateTable();
            } else {
                PcbItem component = findComponentForOrderItem(selectedOrderItem);
                if (component != null) {
                    for (PcbItem c : sameSetComponents(component)) {
                        c.setOrderItem(null);
                    }
                }
                orderPanel.removeOrderItem(selectedOrderItem);
                selectedOrderItem = null;
                orderPanel.setSelectedOrderItem(null);
            }
            pcbPanel.updateTable();
            updateEnabledComponents();
        }
    }

    private void addAll() {
        for (PcbItem pcbItem : pcbItemList) {
            OrderItem toOrder = createOrderItem(pcbItem);
            orderPanel.addOrderItem(toOrder, true);
            orderPanel.setSelectedOrderItem(toOrder);
        }
    }

    private void addAllCheckStock() {
        for (PcbItem pcbItem : pcbItemList) {
            int pcbAmount = pcbItem.getReferences().size();
            int itmAmount = pcbItem.getMatchedItemLink().getAmount();

            if (itmAmount < pcbAmount) {
                OrderItem toOrder = createOrderItem(pcbItem);
                toOrder.setAmount(pcbAmount - itmAmount);
                orderPanel.addOrderItem(toOrder, true);
                orderPanel.setSelectedOrderItem(toOrder);
            }
        }
    }

    //
    // Amount for order item changed
    //
    @Override
    public void onAmountChanged(int amount) {
        OrderItem selected = orderPanel.getSelectedOrderItem();
        if (selected != null) {
            selected.setAmount(amount);
        }
    }
}