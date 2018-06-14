package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.AbstractOrderLine;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.ItemOrderLine;

import javax.swing.*;

public abstract class OrderLinePopup extends JPopupMenu {

    protected OrderLinePopup(ItemOrderLine orderItem) {
        super();

        init(orderItem);
    }

    public abstract void onDeleteOrderItem(AbstractOrderLine orderItem);
    public abstract void onEditReference(ItemOrderLine orderItem);

    public abstract void onEditItem(Item item);
    public abstract void onOpenLocalDataSheet(Item item);
    public abstract void onOpenOnlineDataSheet(Item item);
    public abstract void onOrderItem(Item item);
    public abstract void onShowHistory(Item item);

    private void init(ItemOrderLine itemOrderLine) {
//        // ItemOrder item
//        IActions.DeleteAction deleteOrderItemAction = new IActions.DeleteAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                OrderLinePopup.this.onDeleteOrderItem(itemOrderLine);
//            }
//        };
//        deleteOrderItemAction.setName("Delete order item");
//
//        IActions.EditReferenceAction editReferenceAction = new IActions.EditReferenceAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                OrderLinePopup.this.onEditReference(itemOrderLine);
//            }
//        };
//
//        deleteOrderItemAction.setEnabled(!itemOrderLine.isLocked());
//        editReferenceAction.setEnabled(!itemOrderLine.isLocked());
//        add(deleteOrderItemAction);
//        add(editReferenceAction);
//
//        Item item = itemOrderLine.getItem();
//
//        IActions.EditAction editAction = new IActions.EditAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                OrderLinePopup.this.onEditItem(item);
//            }
//        };
//        editAction.setName("Edit order item");
//
//        IActions.OpenItemDataSheetLocalAction openItemDataSheetLocalAction = new IActions.OpenItemDataSheetLocalAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                onOpenLocalDataSheet(item);
//            }
//        };
//
//        IActions.OpenItemDataSheetOnlineAction openItemDataSheetOnlineAction = new IActions.OpenItemDataSheetOnlineAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                onOpenOnlineDataSheet(item);
//            }
//        };
//
//        IActions.OrderItemAction orderItemAction = new IActions.OrderItemAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                onOrderItem(item);
//            }
//        };
//
//        IActions.ShowItemHistoryAction showItemHistoryAction = new IActions.ShowItemHistoryAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                onShowHistory(item);
//            }
//        };
//
//
//        JMenu dsMenu = new JMenu("Open data sheet");
//        dsMenu.add(new JMenuItem(openItemDataSheetOnlineAction));
//        dsMenu.add(new JMenuItem(openItemDataSheetLocalAction));
//
//        openItemDataSheetOnlineAction.setEnabled(item != null && !item.getOnlineDataSheet().isEmpty());
//        openItemDataSheetLocalAction.setEnabled(item != null && !item.getLocalDataSheet().isEmpty());
//        editAction.setEnabled(item != null);
//        showItemHistoryAction.setEnabled(item != null);
//        orderItemAction.setEnabled(item != null);
//
//        if (item != null)
//            addSeparator();
//            add(editAction);
//            add(showItemHistoryAction);
//            add(dsMenu);
//            addSeparator();
//            add(orderItemAction);
        }
    }



