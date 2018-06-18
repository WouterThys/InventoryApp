package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.AbstractOrder;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.managers.OrderManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class OrderPopup extends JPopupMenu {

    protected OrderPopup(final AbstractOrder order) {
        super();

        init(order);
    }

    public abstract void onEditOrder(AbstractOrder order);
    public abstract void onDeleteOrder(AbstractOrder order);
    public abstract void onLocked(AbstractOrder order, boolean locked);


    private void init(final AbstractOrder order) {
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

        IActions.MoveToOrderedAction moveToOrderedAction = new IActions.MoveToOrderedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> OrderManager.moveToOrdered(order));
            }
        };

        IActions.MoveToReceivedAction moveToReceivedAction = new IActions.MoveToReceivedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> OrderManager.moveToReceived(order));
            }
        };

        IActions.BackToOrderedAction backToOrderedAction = new IActions.BackToOrderedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> OrderManager.backToOrdered(order));
            }
        };

        IActions.BackToPlannedAction backToPlannedAction = new IActions.BackToPlannedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> OrderManager.backToPlanned(order));
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
