package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.AbstractOrderLine;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.components.actions.IActions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public abstract class OrderLinePopup extends JPopupMenu {

    protected OrderLinePopup(List<AbstractOrderLine> orderLineList) {
        super();

        init(orderLineList);
    }

    // Order line
    public abstract void onDeleteOrderLines(List<AbstractOrderLine> orderLineList);

    public abstract void onOrderOrderLines(List<AbstractOrderLine> orderLineList);

    public abstract void onEditReference(AbstractOrderLine orderLine);

    // Ordered line
    public abstract void onEditLine(AbstractOrderLine orderLine);

    // When item order
    public abstract void onOpenLocalDataSheet(Item item);

    public abstract void onOpenOnlineDataSheet(Item item);

    public abstract void onShowHistory(Item item);

    private void init(List<AbstractOrderLine> orderLineList) {
        // Delete
        IActions.DeleteAction deleteAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onDeleteOrderLines(orderLineList));
            }
        };
        if (orderLineList.size() > 1) {
            deleteAction.setName("Delete order lines");
        } else {
            deleteAction.setName("Delete order line");
        }
        add(deleteAction);

        // Order again
        IActions.OrderItemAction orderAction = new IActions.OrderItemAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onOrderOrderLines(orderLineList));
            }
        };
        orderAction.setName("Order again");
        add(orderAction);

        IActions.EditAction editAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onEditLine(orderLineList.get(0)));
            }
        };
        editAction.setName("Edit " + orderLineList.get(0).getLine());
        add(editAction);


        IActions.EditReferenceAction editReferenceAction = new IActions.EditReferenceAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onEditReference(orderLineList.get(0)));
            }
        };
        add(editReferenceAction);

        boolean multiple = orderLineList.size() > 1;
        boolean locked = orderLineList.get(0).isLocked();

        editReferenceAction.setEnabled(!locked && !multiple);
        editAction.setEnabled(!multiple);

        if (orderLineList.size() == 1 && orderLineList.get(0).getLine() instanceof Item) {

            Item item = (Item) orderLineList.get(0).getLine();

            IActions.OpenItemDataSheetLocalAction openItemDataSheetLocalAction = new IActions.OpenItemDataSheetLocalAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(() -> onOpenLocalDataSheet(item));
                }
            };

            IActions.OpenItemDataSheetOnlineAction openItemDataSheetOnlineAction = new IActions.OpenItemDataSheetOnlineAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(() -> onOpenOnlineDataSheet(item));
                }
            };

            IActions.ShowItemHistoryAction showItemHistoryAction = new IActions.ShowItemHistoryAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(() -> onShowHistory(item));
                }
            };


            JMenu dsMenu = new JMenu("Open data sheet");
            dsMenu.add(new JMenuItem(openItemDataSheetOnlineAction));
            dsMenu.add(new JMenuItem(openItemDataSheetLocalAction));

            openItemDataSheetOnlineAction.setEnabled(item != null && !item.getOnlineDataSheet().isEmpty());
            openItemDataSheetLocalAction.setEnabled(item != null && !item.getLocalDataSheet().isEmpty());
            showItemHistoryAction.setEnabled(item != null);


            if (item != null) {
                addSeparator();
                add(showItemHistoryAction);
                add(dsMenu);
            }
        }
    }

}



