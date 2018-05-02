package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.managers.CacheManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class ItemPopup extends JPopupMenu {

    protected ItemPopup(Item item) {
        super();

        init(item);
    }

    public abstract void onEditItem();
    public abstract void onDeleteItem();
    public abstract void onOpenLocalDataSheet(Item item);
    public abstract void onOpenOnlineDataSheet(Item item);
    public abstract void onOrderItem(Item item);
    public abstract void onShowHistory(Item item);
    public abstract void onAddToSet(Set set, Item item);
    public abstract void onPrint(Item item);

    private void init(final Item item) {

        IActions.EditAction editAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditItem();
            }
        };
        editAction.setName("Edit item");

        IActions.DeleteAction deleteAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteItem();
            }
        };
        deleteAction.setName("Delete item");

        IActions.OpenItemDataSheetLocalAction openItemDataSheetLocalAction = new IActions.OpenItemDataSheetLocalAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOpenLocalDataSheet(item);
            }
        };

        IActions.OpenItemDataSheetOnlineAction openItemDataSheetOnlineAction = new IActions.OpenItemDataSheetOnlineAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOpenOnlineDataSheet(item);
            }
        };

        IActions.OrderItemAction orderItemAction = new IActions.OrderItemAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOrderItem(item);
            }
        };

        IActions.ShowItemHistoryAction showItemHistoryAction = new IActions.ShowItemHistoryAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onShowHistory(item);
            }
        };

        IActions.PrintAction printAction = new IActions.PrintAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPrint(item);
            }
        };

        // Data sheets
        JMenu dsMenu = new JMenu("Open data sheet");
        dsMenu.add(new JMenuItem(openItemDataSheetOnlineAction));
        dsMenu.add(new JMenuItem(openItemDataSheetLocalAction));

        openItemDataSheetOnlineAction.setEnabled(!item.getOnlineDataSheet().isEmpty());
        openItemDataSheetLocalAction.setEnabled(!item.getLocalDataSheet().isEmpty());

        // Sets
        JMenu setMenu = new JMenu("Add to set");
        for (Set set : CacheManager.cache().getSets()) {
            setMenu.add(new JMenuItem(new IActions.AddItemToSetAction(set) {
                @Override
                public void onAddToSet(ActionEvent e, Set set) {
                    ItemPopup.this.onAddToSet(set, item);
                }
            }));
        }

        // Add
        add(editAction);
        add(deleteAction);
        addSeparator();
        add(orderItemAction);
        add(showItemHistoryAction);
        add(dsMenu);
        addSeparator();
        add(setMenu);
        addSeparator();
        add(printAction);
    }
}
