package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.gui.components.actions.*;

import javax.swing.*;

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
        DeleteOrderItemAction deleteOrderItemAction = new DeleteOrderItemAction() {
            @Override
            public void onDeleteOrderItem() {
                OrderItemPopup.this.onDeleteOrderItem(orderItem);
            }
        };

        EditReferenceAction editReferenceAction = new EditReferenceAction() {
            @Override
            public void onEditReference() {
                OrderItemPopup.this.onEditReference(orderItem);
            }
        };

        // Item
        EditAction editAction = new EditAction() {
            @Override
            public void onEdit() {
                OrderItemPopup.this.onEditItem(orderItem);
            }
        };

        OpenItemDataSheetLocalAction openItemDataSheetLocalAction = new OpenItemDataSheetLocalAction() {
            @Override
            public void onOpenLocalDataSheet() {
                OrderItemPopup.this.onOpenLocalDataSheet(orderItem.getItem());
            }
        };

        OpenItemDataSheetOnlineAction openItemDataSheetOnlineAction = new OpenItemDataSheetOnlineAction() {
            @Override
            public void onOpenOnlineDataSheet() {
                OrderItemPopup.this.onOpenOnlineDataSheet(orderItem.getItem());
            }
        };

        OrderItemAction orderItemAction = new OrderItemAction() {
            @Override
            public void onOrderItem() {
                OrderItemPopup.this.onOrderItem(orderItem.getItem());
            }
        };

        ShowItemHistoryAction showItemHistoryAction = new ShowItemHistoryAction() {
            @Override
            public void onShowHistory() {
                OrderItemPopup.this.onShowHistory(orderItem.getItem());
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
