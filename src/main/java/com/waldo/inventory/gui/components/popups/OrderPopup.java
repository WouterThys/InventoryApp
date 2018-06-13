package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.ItemOrder;
import com.waldo.inventory.gui.components.actions.IActions;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class OrderPopup extends JPopupMenu {

    protected OrderPopup(final ItemOrder itemOrder) {
        super();

        init(itemOrder);
    }

    public abstract void onEditOrder(ItemOrder itemOrder);
    public abstract void onDeleteOrder(ItemOrder itemOrder);
    public abstract void onOrderDetails(ItemOrder itemOrder);
    public abstract void onMoveToOrdered(ItemOrder itemOrder);
    public abstract void onMoveToReceived(ItemOrder itemOrder);
    public abstract void onBackToOrdered(ItemOrder itemOrder);
    public abstract void onBackToPlanned(ItemOrder itemOrder);
    public abstract void onLocked(ItemOrder itemOrder, boolean locked);


    private void init(final ItemOrder itemOrder) {
        IActions.EditAction editOrderAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditOrder(itemOrder);
            }
        };
        editOrderAction.setName("Edit itemOrder");

        IActions.DeleteAction deleteOrderAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteOrder(itemOrder);
            }
        };
        deleteOrderAction.setName("Delete itemOrder");

        IActions.OrderDetailsAction orderDetailsAction = new IActions.OrderDetailsAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOrderDetails(itemOrder);
            }
        };

        IActions.MoveToOrderedAction moveToOrderedAction = new IActions.MoveToOrderedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onMoveToOrdered(itemOrder);
            }
        };

        IActions.MoveToReceivedAction moveToReceivedAction = new IActions.MoveToReceivedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onMoveToReceived(itemOrder);
            }
        };

        IActions.BackToOrderedAction backToOrderedAction = new IActions.BackToOrderedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBackToOrdered(itemOrder);
            }
        };

        IActions.BackToPlannedAction backToPlannedAction = new IActions.BackToPlannedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBackToPlanned(itemOrder);
            }
        };

        IActions.LockAction lockAction = new IActions.LockAction(itemOrder.isLocked()) {
            @Override
            public void actionPerformed(ActionEvent e, boolean locked) {
                onLocked(itemOrder, locked);
            }
        };

        add(editOrderAction);
        add(deleteOrderAction);
        addSeparator();
        add(orderDetailsAction);
        addSeparator();
        JMenu stateMenu = new JMenu("ItemOrder state");

        if (itemOrder.isPlanned()) {
            stateMenu.add(moveToOrderedAction);
        } else if (itemOrder.isReceived()) {
            stateMenu.add(backToOrderedAction);
        } else if (itemOrder.isOrdered()) {
            stateMenu.add(moveToReceivedAction);
            stateMenu.add(backToPlannedAction);
        }
        add(stateMenu);

        if (!itemOrder.isPlanned()) {
            addSeparator();
            add(lockAction);
        }
    }
}
