package com.waldo.inventory.gui.dialogs.kccomponentorderdialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.classes.kicad.KcComponent;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;

import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class KcComponentOrderDialog extends KcComponentOrderDialogLayout {


    public KcComponentOrderDialog(Application application, String title, List<KcComponent> componentList) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(componentList);

        addTableListeners(createKcListListener(), createOrderItemListListener());
    }

    private ListSelectionListener createKcListListener() {
        return e -> {
            if (!e.getValueIsAdjusting()) {
                selectedComponent = kcPanel.getSelectedComponent();
                kcPanel.updateSelectedValueData(selectedComponent);

                if (selectedComponent.isOrdered()) {
                    oiPanel.setSelectedOrderItem(selectedComponent.getOrderItem());
                } else {
                    oiPanel.setSelectedOrderItem(null);
                }

                updateEnabledComponents();
            }
        };
    }

    private ListSelectionListener createOrderItemListListener() {
        return e -> {
            if (!e.getValueIsAdjusting()) {
                selectedOrderItem = oiPanel.getSelectedOrderItem();
                oiPanel.updateSelectedValueData(selectedOrderItem);
                updateEnabledComponents();
            }
        };
    }

    private OrderItem createOrderItem(KcComponent component) {
        OrderItem orderItem = new OrderItem();

        orderItem.setOrderId(selectedOrder.getId());
        orderItem.setItemId(component.getMatchedItem().getItemId());
        orderItem.setName(component.getMatchedItem().getItem().getName() + " - " + selectedOrder.toString());
        orderItem.setAmount(1);

        for (KcComponent c : sameSetComponents(component)) {
            c.setOrderItem(orderItem);
        }

        return orderItem;
    }

    private KcComponent findComponentForOrderItem(OrderItem orderItem) {
        for (KcComponent component : kcPanel.getKcComponentList()) {
            if (component.isOrdered()) {
                if (component.getOrderItem().equals(orderItem)) {
                    return component;
                }
            }
        }
        return null;
    }

    private List<KcComponent> sameSetComponents(KcComponent component) {
        List<KcComponent> sameSet = new ArrayList<>();
        sameSet.add(component);
        if (component.getMatchedItem().isSetItem()) {
            Item parentItem = component.getMatchedItem().getItem();
            if (parentItem != null) {
                for (KcComponent c : kcPanel.getKcComponentList()) {
                    if (c.getMatchedItem().isSetItem()) {
                        if (c.getMatchedItem().getItem().getId() == parentItem.getId()) {
                            sameSet.add(c);
                        }
                    }
                }
            }
        }
        return sameSet;
    }

    private void alreadyInOrder(OrderItem orderItem) {
        for (KcComponent component : kcPanel.getKcComponentList()) {
            if (component.getMatchedItem().getItemId() == orderItem.getItemId()) {
                component.setOrderItem(orderItem);
                oiPanel.addOrderItem(orderItem,  false);
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
                kcPanel.updateTable();
            }
        } else {
            super.onCancel();
        }
    }

    @Override
    protected void onOK() {
        List<OrderItem> orderItems = oiPanel.getOrderItems();
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
        if (e.getSource().equals(addToOrderBtn)) {
            if (selectedComponent != null) {
                OrderItem toOrder = createOrderItem(selectedComponent);
                oiPanel.addOrderItem(toOrder, true);
                oiPanel.setSelectedOrderItem(toOrder);
            }
        } else if (e.getSource().equals(removeFromOrderBtn)) {
            if (selectedOrderItem != null) {
                if (selectedOrderItem.getAmount() > 1) {
                    selectedOrderItem.setAmount(selectedOrderItem.getAmount() - 1);
                    oiPanel.updateTable();
                } else {
                    KcComponent component = findComponentForOrderItem(selectedOrderItem);
                    if (component != null) {
                        for (KcComponent c : sameSetComponents(component)) {
                            c.setOrderItem(null);
                        }
                    }
                    oiPanel.removeOrderItem(selectedOrderItem);
                    selectedOrderItem = null;
                    oiPanel.setSelectedOrderItem(null);
                }
            }
        }
        kcPanel.updateTable();
        updateEnabledComponents();
    }

    //
    // Amount for order item changed
    //
    @Override
    public void onAmountChanged(int amount) {
        OrderItem selected = oiPanel.getSelectedOrderItem();
        if (selected != null) {
            selected.setAmount(amount);
        }
    }
}