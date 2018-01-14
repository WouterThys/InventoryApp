package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.components.actions.*;
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

    private void init(final Item item) {

        EditAction editAction = new EditAction("Edit item") {
            @Override
            public void onEdit(ActionEvent e) {
                onEditItem();
            }
        };

        DeleteAction deleteAction = new DeleteAction("Delete item") {
            @Override
            public void onDelete() {
                onDeleteItem();
            }
        };

        OpenItemDataSheetLocalAction openItemDataSheetLocalAction = new OpenItemDataSheetLocalAction() {
            @Override
            public void onOpenLocalDataSheet() {
                ItemPopup.this.onOpenLocalDataSheet(item);
            }
        };

        OpenItemDataSheetOnlineAction openItemDataSheetOnlineAction = new OpenItemDataSheetOnlineAction() {
            @Override
            public void onOpenOnlineDataSheet() {
                ItemPopup.this.onOpenOnlineDataSheet(item);
            }
        };

        OrderItemAction orderItemAction = new OrderItemAction() {
            @Override
            public void onOrderItem() {
                ItemPopup.this.onOrderItem(item);
            }
        };

        ShowItemHistoryAction showItemHistoryAction = new ShowItemHistoryAction() {
            @Override
            public void onShowHistory() {
                ItemPopup.this.onShowHistory(item);
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
            setMenu.add(new JMenuItem(new AddItemToSetAction(set) {
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
    }
}
