package com.waldo.inventory.gui.panels.orderspanel;

import com.waldo.inventory.Utils.Statics.OrderStates;
import com.waldo.inventory.classes.dbclasses.AbstractOrder;
import com.waldo.inventory.classes.dbclasses.AbstractOrderLine;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ITablePanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.tablemodels.IOrderLinesTableModel;
import com.waldo.inventory.gui.components.tablemodels.IOrderTableModel;
import com.waldo.inventory.gui.components.trees.IOrdersTree;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class OrdersPanelLayout extends IPanel implements IdBToolBar.IdbToolBarListener{


    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITablePanel<AbstractOrder> orderTablePanel;
    IOrderTableModel orderTableModel;

    private ITablePanel<AbstractOrderLine> linesTablePanel;
    IOrderLinesTableModel linesTableModel;

    private IOrdersTree ordersTree;

    OrderDetailsPanel orderDetailsPanel;
    OrderLineDetailsPanel lineDetailsPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final Application application;

    AbstractOrderLine selectedOrderLine;
    AbstractOrder selectedOrder;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrdersPanelLayout(Application application) {
        this.application = application;
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    abstract void onTreeRightClick(MouseEvent e);
    abstract void onOrderDoubleClick(MouseEvent e);
    abstract void onOrderRightClick(MouseEvent e);
    abstract void onLineRightClick(MouseEvent e);

    abstract void onOrderSelected(AbstractOrder order);
    abstract void onLineSelected(AbstractOrderLine line);
    abstract void onTreeSelected(OrderStates states, int year);

    //
    // Tree stuff
    //

    void treeDeleteOrder(AbstractOrder order) {
        ordersTree.removeOrder(order);
    }

    void treeReload() {
        ordersTree.structureChanged();
    }

    long treeUpdate() {
        long orderId = -1;
        if (selectedOrder != null) {
            orderId = selectedOrder.getId();
        }
        ordersTree.updateTree();
        ordersTree.expandAll();
        return orderId;
    }

    void treeSelectOrder(AbstractOrder order) {
        ordersTree.setSelectedItem(order);
    }


    //
    // Table stuff
    //
    void orderTableInitialize(OrderStates orderState, int year) {
        List<AbstractOrder> orderList = SearchManager.sm().findOrdersForStateAndYear(orderState, year);
        orderTableModel.setItemList(orderList);

        selectedOrderLine = null;
        if (orderList.contains(selectedOrder)) {
            onOrderSelected(selectedOrder);
        } else if (orderList.size() > 0) {
            onOrderSelected(orderList.get(0));
        } else {
            onOrderSelected(null);
        }
    }

    long orderTableUpdate() {
        long orderId = -1;
        if (selectedOrder != null) {
            orderId = selectedOrder.getId();
        }
        orderTableModel.updateTable();
        return orderId;
    }

    void orderTableClear() {
        orderTableModel.setItemList(new ArrayList<>());
    }

    void orderTableAdd(AbstractOrder order) {
        orderTableModel.addItem(order);
    }

    void orderTableRemove(AbstractOrder order) {
        orderTableModel.removeItem(order);
    }

    void orderTableSelect(AbstractOrder order) {
        orderTablePanel.selectItem(order);
    }

    AbstractOrder orderTableGetSelected() {
        return orderTablePanel.getSelectedItem();
    }

    List<AbstractOrder> orderTableGetAllSelected() {
        return orderTablePanel.getAllSelectedItems();
    }


    public void lineTableInitialize(AbstractOrder order) {
        if (order != null) {
            linesTableModel.setItemList(order.getOrderLines());
        } else {
            lineTableClear();
        }
        selectedOrderLine = null;
    }

    long lineTableUpdate() {
        long orderItemId = -1;
        if (selectedOrderLine != null) {
            orderItemId = selectedOrderLine.getId();
        }
        linesTableModel.updateTable();
        return orderItemId;
    }

    void lineTableClear() {
        linesTableModel.setItemList(new ArrayList<>());
    }

    void lineTableAdd(AbstractOrderLine line) {
        linesTableModel.addItem(line);
    }

    void lineTableSelect(AbstractOrderLine line) {
        linesTablePanel.selectItem(line);
    }

    AbstractOrderLine lineTableGetSelected() {
        return linesTablePanel.getSelectedItem();
    }

    List<AbstractOrderLine> lineTableGetAllSelected() {
        return linesTablePanel.getAllSelectedItems();
    }


    //
    // Table tool bar stuff
    //

    void updateEnabledComponents() {
        boolean orderSelected = (selectedOrder != null && !selectedOrder.isUnknown() && selectedOrder.canBeSaved());
        boolean lineSelected = (selectedOrderLine != null && !selectedOrderLine.isUnknown());
        boolean locked = orderSelected && selectedOrder.isLocked();

        if (orderSelected) {
            linesTablePanel.setDbToolBarEnabled(true);
            linesTablePanel.setDbToolBarEditDeleteEnabled(lineSelected && !locked);
        } else {
            linesTablePanel.setDbToolBarEnabled(false);
        }

        // TODO tbOrderFlowPanel.updateComponents(selectedOrder);
        // TODO previewPanel.updateComponents(selectedOrder);
    }



    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {

        // Details
        orderDetailsPanel = new OrderDetailsPanel(application) {
            @Override
            public void onToolBarDelete(IdBToolBar source) {
                OrdersPanelLayout.this.onToolBarDelete(source);
            }

            @Override
            public void onToolBarEdit(IdBToolBar source) {
                OrdersPanelLayout.this.onToolBarEdit(source);
            }
        };
        orderDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, -1, -1, -1),
                BorderFactory.createLineBorder(Color.lightGray, 1)
        ));


        lineDetailsPanel = new OrderLineDetailsPanel(application);

        // Tree
        ordersTree = new IOrdersTree(IOrdersTree.defaultRoot);
        ordersTree.addTreeSelectionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                IOrdersTree.OrderTreeNode node = ordersTree.getSelectedItem();
                if (node != null) {
                    onTreeSelected(node.getOrderState(), node.getYear());
                }
            });
        });

        // Tables
        orderTableModel = new IOrderTableModel();
        orderTablePanel = new ITablePanel<>(orderTableModel, orderDetailsPanel, e -> {
            if (!e.getValueIsAdjusting()) {
                SwingUtilities.invokeLater(() -> onOrderSelected(orderTableGetSelected()));
            }
        }, false);
        orderTablePanel.setDbToolBar(this, true, true, false, false);
        orderTablePanel.setHeaderPanelVisible(true);
        orderTablePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    SwingUtilities.invokeLater(() -> onOrderDoubleClick(e));
                } else {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        SwingUtilities.invokeLater(() -> onOrderRightClick(e));
                    }
                }
            }
        });

        linesTableModel = new IOrderLinesTableModel();
        linesTablePanel = new ITablePanel<>(linesTableModel, lineDetailsPanel, e -> {
            if (!e.getValueIsAdjusting()) {
                SwingUtilities.invokeLater(() -> onLineSelected(lineTableGetSelected()));
            }
        }, false);
        linesTablePanel.setHeaderPanelVisible(false);
        linesTablePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    SwingUtilities.invokeLater(() -> onLineRightClick(e));
                }
            }
        });


    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, orderTablePanel, linesTablePanel);
        centerSplitPane.setResizeWeight(0.3);

        JPanel treePanel = new JPanel(new BorderLayout());
        JScrollPane pane = new JScrollPane(ordersTree);
        pane.setPreferredSize(new Dimension(300, 400));
        treePanel.add(pane);
        // TODO: preview treePanel.add(pane, BorderLayout.CENTER);
        // treePanel.add(divisionPreviewPanel, BorderLayout.SOUTH);
        // treePanel.setMinimumSize(new Dimension(200,200));


        JSplitPane orderSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, centerSplitPane);
        orderSplitPane.setOneTouchExpandable(true);

        add(orderSplitPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... objects) {

    }
}
