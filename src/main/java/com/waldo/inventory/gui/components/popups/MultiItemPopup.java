package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.components.actions.AddItemToSetAction;
import com.waldo.inventory.gui.components.actions.DeleteAction;
import com.waldo.inventory.gui.components.actions.OrderItemAction;
import com.waldo.inventory.managers.CacheManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public abstract class MultiItemPopup extends JPopupMenu {

    protected MultiItemPopup(List<Item> itemList) {
        super();

        init(itemList);
    }

    public abstract void onDeleteItems(List<Item> itemList);
    public abstract void onOrderItems(List<Item> itemList);
    public abstract void onAddToSet(Set set, List<Item> itemList);

    private void init(final List<Item> itemList) {

        DeleteAction deleteAction = new DeleteAction("Delete items") {
            @Override
            public void onDelete() {
                onDeleteItems(itemList);
            }
        };

        OrderItemAction orderItemAction = new OrderItemAction("Order items") {
            @Override
            public void onOrderItem() {
                onOrderItems(itemList);
            }
        };

        // Sets
        JMenu setMenu = new JMenu("Add to set");
        for (Set set : CacheManager.cache().getSets()) {
            setMenu.add(new JMenuItem(new AddItemToSetAction(set) {
                @Override
                public void onAddToSet(ActionEvent e, Set set) {
                    MultiItemPopup.this.onAddToSet(set, itemList);
                }
            }));
        }

        // Add
        add(deleteAction);
        add(orderItemAction);
        addSeparator();
        add(setMenu);
    }
}

