package com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.gui.components.ITableEditors;
import com.waldo.inventory.gui.components.tablemodels.IPcbItemOrderTableModel;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

class OrderedPcbItemsPanel extends JPanel implements GuiUtils.GuiInterface {

    interface OrderListener {
        void onDoOrder();
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IPcbItemOrderTableModel orderTableModel;
    private ITable<OrderItem> orderTable;

    private JButton doOrderBtn;

    private AbstractAction addOneAa;
    private AbstractAction remOneAa;
    private AbstractAction remAllAa;
    private AbstractAction refreshAa;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final List<Order> orderList = new ArrayList<>();
    private Order selectedOrder;

    private final OrderListener orderListener;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public OrderedPcbItemsPanel(OrderListener orderListener) {

        this.orderListener = orderListener;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean hasOrder = selectedOrder != null;
        OrderItem selectedOrderItem = orderTableGetSelectedItem();
        boolean selected = hasOrder &&  selectedOrderItem!= null && selectedOrderItem.getId() < DbObject.UNKNOWN_ID;

        addOneAa.setEnabled(selected);
        remOneAa.setEnabled(selected);

        refreshAa.setEnabled(hasOrder);
        remAllAa.setEnabled(hasOrder);

        doOrderBtn.setEnabled(hasOrder);
    }

    void orderTableUpdate() {
        orderTableModel.updateTable();
    }

    OrderItem orderTableGetSelectedItem() {
        return orderTable.getSelectedItem();
    }

    //
    // Actions
    //
    private void onAddOne(OrderItem orderItem) {
        if (orderItem != null && orderItem.getId() < DbObject.UNKNOWN_ID) {
            orderItem.setAmount(orderItem.getAmount() + 1);
        }
    }

    private void onRemOne(OrderItem orderItem) {
        if (orderItem != null && orderItem.getId() < DbObject.UNKNOWN_ID) {
            if (orderItem.getAmount() > 0) {
                orderItem.setAmount(orderItem.getAmount() - 1);
            }
        }
    }

    private void onRemAll() {
        for (OrderItem orderItem : selectedOrder.getTempOrderItems()) {
            orderItem.setAmount(0);
        }
    }

    private void onRefresh() {
        List<Order> orders = new ArrayList<>(orderList);
        for (Order order : orders) {
            // Check items
            List<OrderItem> itemList = new ArrayList<>(order.getTempOrderItems());
            for (OrderItem item : itemList ) {
                if (item.getId() < DbObject.UNKNOWN_ID && item.getAmount() == 0) {
                    order.removeItemFromList(item);
                }
            }

            // Check order
            if (order.getTempOrderItems().size() == 0) {
                orderList.remove(order);
            }
        }
    }

    //
    // Table
    //
    void orderTableInit(List<Order> orderList) {
        if (orderList.size() > 0) {
            List<OrderItem> orderItems = new ArrayList<>();
            for (Order order : orderList) {
                orderItems.addAll(order.getOrderItems());
                orderItems.addAll(order.getTempOrderItems());
            }
            orderTableModel.setItemList(orderItems);
        } else {
            orderTableModel.clearItemList();
        }
    }

    //
    // Methods
    //
    private JToolBar createOrderToolBar() {
        JToolBar pcbToolBar = new JToolBar(JToolBar.HORIZONTAL);
        pcbToolBar.setFloatable(false);
        pcbToolBar.add(addOneAa);
        pcbToolBar.add(remOneAa);
        pcbToolBar.addSeparator();
        pcbToolBar.add(remAllAa);
        pcbToolBar.addSeparator();
        pcbToolBar.add(refreshAa);

        return pcbToolBar;
    }

    private void onDoOrder() {
        if (orderListener != null) {
            orderListener.onDoOrder();
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Actions
        addOneAa = new AbstractAction("AddOne", imageResource.readImage("Projects.Order.AddOne")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddOne(orderTable.getSelectedItem());
                orderTableUpdate();
                updateEnabledComponents();
            }
        };
        remOneAa = new AbstractAction("RemOne", imageResource.readImage("Projects.Order.RemOne")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemOne(orderTable.getSelectedItem());
                orderTableUpdate();
                updateEnabledComponents();
            }
        };
        remAllAa = new AbstractAction("RemAll", imageResource.readImage("Projects.Order.RemAll")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemAll();
                orderTableUpdate();
                updateEnabledComponents();
            }
        };

        refreshAa = new AbstractAction("Refresh", imageResource.readImage("Projects.Order.Refresh")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRefresh();
                orderTableInit(orderList);
                updateEnabledComponents();
            }
        };

        // Table
        orderTableModel = new IPcbItemOrderTableModel();
        orderTable = new ITable<OrderItem>(orderTableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                OrderItem oi = getValueAtRow(row);

                if (oi.getId() > DbObject.UNKNOWN_ID) {
                    component.setForeground(Color.gray);
                } else {
                    component.setForeground(Color.black);
                }

                return component;
            }
        };
        orderTable.getSelectionModel().addListSelectionListener(e -> updateEnabledComponents());
        orderTable.setExactColumnWidth(2, 60);
        TableColumn tableColumn = orderTable.getColumnModel().getColumn(2);
        tableColumn.setCellEditor(new ITableEditors.SpinnerEditor() {
            @Override
            public void onValueSet(int value) {
                OrderItem item = orderTable.getSelectedItem();
                if (item != null) {
                    item.setAmount(value);
                }
            }
        });

        // Button
        doOrderBtn = new JButton(imageResource.readImage("Projects.Order.DoOrder"));
        doOrderBtn.addActionListener(e -> onDoOrder());
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Extra
        JScrollPane pane = new JScrollPane(orderTable);
        pane.setPreferredSize(new Dimension(600, 300));
        JPanel removeOrderPnl = new JPanel(new BorderLayout());
        removeOrderPnl.add(doOrderBtn, BorderLayout.EAST);

        add(createOrderToolBar(), BorderLayout.PAGE_START);
        add(pane, BorderLayout.CENTER);
        add(removeOrderPnl, BorderLayout.PAGE_END);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            selectedOrder = (Order) args[0];

            if (!orderList.contains(selectedOrder)) {
                orderList.add(selectedOrder);
            }

            orderTableInit(orderList);
        }
        updateEnabledComponents();
    }
}