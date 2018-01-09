package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.gui.components.actions.*;

import javax.swing.*;

public abstract class OrderPopup extends JPopupMenu {

    protected OrderPopup(Order order) {
        super();

        init(order);
    }

    public abstract void onEditOrder(Order order);
    public abstract void onDeleteOrder(Order order);
    public abstract void onOrderDetails(Order order);
    public abstract void onMoveToOrdered(Order order);
    public abstract void onMoveToReceived(Order order);
    public abstract void onBackToOrdered(Order order);
    public abstract void onBackToPlanned(Order order);


    private void init(Order order) {
        EditAction editOrderAction = new EditAction("Edit order") {
            @Override
            public void onEdit() {
                onEditOrder(order);
            }
        };

        DeleteAction deleteOrderAction = new DeleteAction("Delete order") {
            @Override
            public void onDelete() {
                onDeleteOrder(order);
            }
        };

        OrderDetailsAction orderDetailsAction = new OrderDetailsAction() {
            @Override
            public void onOrderDetails() {
                OrderPopup.this.onOrderDetails(order);
            }
        };

        MoveToOrderedAction moveToOrderedAction = new MoveToOrderedAction() {
            @Override
            public void onMoveToOrdered() {
                OrderPopup.this.onMoveToOrdered(order);
            }
        };

        MoveToReceivedAction moveToReceivedAction = new MoveToReceivedAction() {
            @Override
            public void onMoveToReceived() {
                OrderPopup.this.onMoveToReceived(order);
            }
        };

        BackToOrderedAction backToOrderedAction = new BackToOrderedAction() {
            @Override
            public void onBackToOrdered() {
                OrderPopup.this.onBackToOrdered(order);
            }
        };

        BackToPlannedAction backToPlannedAction = new BackToPlannedAction() {
            @Override
            public void onBackToPlanned() {
                OrderPopup.this.onBackToPlanned(order);
            }
        };

        add(editOrderAction);
        add(deleteOrderAction);
        addSeparator();
        add(orderDetailsAction);
        addSeparator();
        JMenu stateMenu = new JMenu("Order state");
        if (order.isPlanned()) {
            stateMenu.add(moveToOrderedAction);
        } else if (order.isReceived()) {
            stateMenu.add(backToOrderedAction);
        } else if (order.isOrdered()) {
            stateMenu.add(moveToReceivedAction);
            stateMenu.add(backToPlannedAction);
        }
        add(stateMenu);

    }
}
