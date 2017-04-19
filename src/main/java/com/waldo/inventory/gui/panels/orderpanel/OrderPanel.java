package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IItemTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;

import static com.waldo.inventory.database.DbManager.db;

public class OrderPanel extends OrderPanelLayout {

    private DbObjectChangedListener<Item> itemsChanged;
    private DbObjectChangedListener<Order> ordersChanged;
    private DbObjectChangedListener<OrderItem> orderItemsChanged;

    public OrderPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();
        initActions();
        initializeListeners();

        db().addOnItemsChangedListener(itemsChanged);
        db().addOnOrdersChangedListener(ordersChanged);
        db().addOnOrderItemsChangedListener(orderItemsChanged);

        updateComponents(null);
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public DbObject getLastSelectedOrder() {
        return lastSelectedOrder;
    }

    public IItemTableModel getTableModel() {
        return tableModel;
    }

    public void addItemToOrder(Item item, Order order) {
        // Add to data base
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setOrderId(order.getId());
        orderItem.setName(item.toString() + " - " + order.toString());

        orderItem.save();
    }


    private void initActions() {
        initMouseClicked();
    }

    private void initMouseClicked() {
        itemTable.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // TODO: go to orders tab?
                    Item selectedItem = application.getSelectedItem();
                    EditItemDialog dialog = new EditItemDialog(application, "Item", selectedItem);
                    if (dialog.showDialog() == EditItemDialog.OK) {
                        dialog.getItem().save();
                    }
                }
            }
        });
    }

    private void initializeListeners() {
        setItemsChangedListener();
        setOrdersChangedListener();
        setOrderItemsChangedListener();
    }

    private void setItemsChangedListener() {
        itemsChanged = new DbObjectChangedListener<Item>() {
            @Override
            public void onAdded(Item item) {
                selectedItem = item;
                updateItems();
            }

            @Override
            public void onUpdated(Item newItem, Item oldItem) {
                selectedItem = newItem;
                updateItems();
            }

            @Override
            public void onDeleted(Item item) {
                selectedItem = null;
                updateItems();
            }
        };
    }

    private void updateItems() {
        if (lastSelectedOrder == null) {
            lastSelectedOrder = (Order) treeModel.getRoot();
        }
        updateComponents(lastSelectedOrder);
    }

    private void setOrdersChangedListener() {
        ordersChanged = new DbObjectChangedListener<Order>() {
            @Override
            public void onAdded(Order order) {
                treeModel.addObject(order);
                updateComponents(order);
            }

            @Override
            public void onUpdated(Order newOrder, Order oldOrder) {
                treeModel.updateObject(newOrder, oldOrder);
                updateComponents(newOrder);
            }

            @Override
            public void onDeleted(Order order) {
                treeModel.removeObject(order);
                updateComponents(null);
            }
        };
    }

    private void setOrderItemsChangedListener() {
        orderItemsChanged = new DbObjectChangedListener<OrderItem>() {
            @Override
            public void onAdded(OrderItem orderItem) {
                Order order = DbManager.db().findOrderById(orderItem.getOrderId());
                Item item = DbManager.db().findItemById(orderItem.getItemId());

                order.getOrderItems().add(item);
                order.setDateModified(new Date(System.currentTimeMillis()));

                updateComponents(order);
            }

            @Override
            public void onUpdated(OrderItem newOrderItem, OrderItem oldOrderItem) {

            }

            @Override
            public void onDeleted(OrderItem orderItem) {

            }
        };
    }


    //
    // Tree selection interface
    //

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) ordersTree.getLastSelectedPathComponent();

        if (node == null) {
            lastSelectedOrder = null;
            return; // Nothing selected
        }

        selectedItem = null;
        lastSelectedOrder = (Order) node.getUserObject();
        application.clearSearch();

        updateComponents(lastSelectedOrder);
    }

    //
    // Table selection changed
    //

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = itemTable.getSelectedRow();
            if (row >= 0) {
                selectedItem = getItemAt(itemTable.getSelectedRow());
                updateComponents(null);
            }
        }
    }

    //
    //  Tool bar listener
    //

    @Override
    public void onToolBarRefresh() {
        updateComponents(lastSelectedOrder);
    }

    @Override
    public void onToolBarAdd() {

    }

    @Override
    public void onToolBarDelete() {

    }

    @Override
    public void onToolBarEdit() {

    }
}
