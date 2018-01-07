package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.components.actions.*;

import javax.swing.*;

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

    private void init(final Item item) {

        EditAction editAction = new EditAction() {
            @Override
            public void onEdit() {
                ItemPopup.this.onEditItem();
            }
        };

        DeleteAction deleteAction = new DeleteAction() {
            @Override
            public void onDelete() {
                ItemPopup.this.onDeleteItem();
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


        JMenu dsMenu = new JMenu("Open data sheet");
        dsMenu.add(new JMenuItem(openItemDataSheetOnlineAction));
        dsMenu.add(new JMenuItem(openItemDataSheetLocalAction));

        openItemDataSheetOnlineAction.setEnabled(!item.getOnlineDataSheet().isEmpty());
        openItemDataSheetLocalAction.setEnabled(!item.getLocalDataSheet().isEmpty());

        add(editAction);
        add(deleteAction);
        addSeparator();
        add(orderItemAction);
        add(showItemHistoryAction);
        add(dsMenu);
    }
}
