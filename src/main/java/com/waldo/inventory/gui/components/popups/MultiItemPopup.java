package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.components.actions.IActions;
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

        IActions.DeleteAction deleteAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteItems(itemList);
            }
        };
        deleteAction.setName("Delete items");

        IActions.OrderItemAction orderItemAction = new IActions.OrderItemAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOrderItems(itemList);
            }
        };
        orderItemAction.setName("ItemOrder item");

        // Sets
        JMenu setMenu = new JMenu("Add to set");
        for (Set set : CacheManager.cache().getSets()) {
            setMenu.add(new JMenuItem(new IActions.AddItemToSetAction(set) {
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

