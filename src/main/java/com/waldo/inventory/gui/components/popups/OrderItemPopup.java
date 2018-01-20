package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.gui.components.actions.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class OrderItemPopup extends JPopupMenu {

    protected OrderItemPopup(OrderItem orderItem) {
        super();

        init(orderItem);
    }

    public abstract void onDeleteOrderItem(OrderItem orderItem);
    public abstract void onEditReference(OrderItem orderItem);

    public abstract void onEditItem(OrderItem orderItem);
    public abstract void onOpenLocalDataSheet(Item item);
    public abstract void onOpenOnlineDataSheet(Item item);
    public abstract void onOrderItem(Item item);
    public abstract void onShowHistory(Item item);

    private void init(OrderItem orderItem) {
        // Order item
        IActions.DeleteAction deleteOrderItemAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrderItemPopup.this.onDeleteOrderItem(orderItem);
            }
        };
        deleteOrderItemAction.setName("Delete order item");

        IActions.EditReferenceAction editReferenceAction = new IActions.EditReferenceAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrderItemPopup.this.onEditReference(orderItem);
            }
        };

        // Item
        IActions.EditAction editAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrderItemPopup.this.onEditItem(orderItem);
            }
        };
        editAction.setName("Edit order item");

        IActions.OpenItemDataSheetLocalAction openItemDataSheetLocalAction = new IActions.OpenItemDataSheetLocalAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOpenLocalDataSheet(orderItem.getItem());
            }
        };

        IActions.OpenItemDataSheetOnlineAction openItemDataSheetOnlineAction = new IActions.OpenItemDataSheetOnlineAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOpenOnlineDataSheet(orderItem.getItem());
            }
        };

        IActions.OrderItemAction orderItemAction = new IActions.OrderItemAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOrderItem(orderItem.getItem());
            }
        };

        IActions.ShowItemHistoryAction showItemHistoryAction = new IActions.ShowItemHistoryAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onShowHistory(orderItem.getItem());
            }
        };


        JMenu dsMenu = new JMenu("Open data sheet");
        dsMenu.add(new JMenuItem(openItemDataSheetOnlineAction));
        dsMenu.add(new JMenuItem(openItemDataSheetLocalAction));

        openItemDataSheetOnlineAction.setEnabled(!orderItem.getItem().getOnlineDataSheet().isEmpty());
        openItemDataSheetLocalAction.setEnabled(!orderItem.getItem().getLocalDataSheet().isEmpty());

        add(deleteOrderItemAction);
        add(editReferenceAction);
        addSeparator();
        add(editAction);
        add(showItemHistoryAction);
        add(dsMenu);
        addSeparator();
        add(orderItemAction);
    }


}
