package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.gui.components.actions.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class OrderPopup extends JPopupMenu {

    protected OrderPopup(final Order order) {
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
    public abstract void onLocked(Order order, boolean locked);


    private void init(final Order order) {
        IActions.EditAction editOrderAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditOrder(order);
            }
        };
        editOrderAction.setName("Edit order");

        IActions.DeleteAction deleteOrderAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteOrder(order);
            }
        };
        deleteOrderAction.setName("Delete order");

        IActions.OrderDetailsAction orderDetailsAction = new IActions.OrderDetailsAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOrderDetails(order);
            }
        };

        IActions.MoveToOrderedAction moveToOrderedAction = new IActions.MoveToOrderedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onMoveToOrdered(order);
            }
        };

        IActions.MoveToReceivedAction moveToReceivedAction = new IActions.MoveToReceivedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onMoveToReceived(order);
            }
        };

        IActions.BackToOrderedAction backToOrderedAction = new IActions.BackToOrderedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBackToOrdered(order);
            }
        };

        IActions.BackToPlannedAction backToPlannedAction = new IActions.BackToPlannedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBackToPlanned(order);
            }
        };

        IActions.LockAction lockAction = new IActions.LockAction(order.isLocked()) {
            @Override
            public void actionPerformed(ActionEvent e, boolean locked) {
                onLocked(order, locked);
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

        if (!order.isPlanned()) {
            addSeparator();
            add(lockAction);
        }
    }
}
