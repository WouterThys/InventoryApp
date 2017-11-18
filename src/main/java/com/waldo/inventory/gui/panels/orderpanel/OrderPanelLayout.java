package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.IOrderItemTableModel;
import com.waldo.inventory.gui.components.treemodels.IDbObjectTreeModel;
import com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel.ItemDetailPanel;
import com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel.ItemDetailPanelLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;
import static com.waldo.inventory.managers.CacheManager.cache;

public abstract class OrderPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        ItemDetailPanelLayout.OnItemDetailListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITablePanel<OrderItem> orderItemTable;
    IOrderItemTableModel tableModel;

    ITree ordersTree;
    private IDbObjectTreeModel<Order> treeModel;
    ItemDetailPanel itemDetailPanel;
    //OrderItemDetailPanel orderItemDetailPanel;

    IdBToolBar treeToolBar;
    private JPanel orderTbPanel;
    IOrderFlowPanel tbOrderFlowPanel;
    private ILabel tbOrderNameLbl;
    private IComboBox<Distributor> tbDistributorCb;
    private AbstractAction orderDetailsAa;
    private JPanel tbOrderFilePanel;

    AbstractAction treeAddOrderAa;
    AbstractAction treeEditOrderAa;
    AbstractAction treeDeleteOrderAa;
    AbstractAction treeOrderDetailsAa;
    AbstractAction treeMoveToOrderedAa;
    AbstractAction treeMoveToReceivedAa;
    AbstractAction treeBackToOrderedAa;
    AbstractAction treeBackToPlannedAa;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;

    OrderItem selectedOrderItem;
    Order selectedOrder;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderPanelLayout(Application application) {
        this.application = application;
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    abstract void onTreeRightClick(MouseEvent e);
    abstract void onTableRowClicked(MouseEvent e);

    abstract void onSetOrderItemAmount(OrderItem orderItem, int amount);

    abstract void onAddOrderAa();
    abstract void onEditOrderAa(Order order);
    abstract void onDeleteOrderAa(Order order);
    abstract void onOrderDetailsAa(Order order);
    abstract void onMoveToOrderedAa(Order order);
    abstract void onMoveToReceivedAa(Order order);
    abstract void onBackToOrderedAa(Order order);
    abstract void onBackToPlannedAa(Order order);


    public Order getSelectedOrder() {
        return selectedOrder;
    }

    //
    // Tree stuff
    //
    private void treeInitializeTree(DefaultMutableTreeNode rootNode) {
        Order ordered = new Order("Ordered");
        Order planned = new Order("Planned");
        Order received = new Order("Received");

        ordered.setCanBeSaved(false);
        planned.setCanBeSaved(false);
        received.setCanBeSaved(false);

        DefaultMutableTreeNode orderedNode = new DefaultMutableTreeNode(ordered);
        DefaultMutableTreeNode plannedNode = new DefaultMutableTreeNode(planned);
        DefaultMutableTreeNode receivedNode = new DefaultMutableTreeNode(received);

        rootNode.add(receivedNode);
        rootNode.add(orderedNode);
        rootNode.add(plannedNode);

        for (Order o : cache().getOrders()) {
            if (!o.isUnknown()) {
                DefaultMutableTreeNode oNode = new DefaultMutableTreeNode(o, false);

                switch (o.getOrderState()) {
                    case Statics.ItemOrderStates.PLANNED:
                        plannedNode.add(oNode);
                        break;
                    case Statics.ItemOrderStates.ORDERED:
                        orderedNode.add(oNode);
                        break;
                    case Statics.ItemOrderStates.RECEIVED:
                        receivedNode.add(oNode);
                        break;
                    case Statics.ItemOrderStates.NONE:
                        break; // Should not happen
                }
            }
        }
    }

    void treeRecreateNodes() {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        rootNode.removeAllChildren();
        treeInitializeTree(rootNode);
    }

    void treeDeleteOrder(Order order) {
        try {
            treeModel.removeObject(order);
        } catch (Exception e) {
            Status().setError("Failed to remove order " + order.getName() + " from tree", e);
        }
    }

    long treeUpdate() {
        long orderId = -1;
        if (selectedOrder != null) {
            orderId = selectedOrder.getId();
        }
        treeModel.reload();
        treeModel.expandNodes();
        return orderId;
    }

    void treeSelectOrder(Order order) {
        treeModel.setSelectedObject(order);
    }


    //
    // Table stuff
    //
    public void tableInitialize(Order order) {
        if (order != null && !order.getName().equals("All")) {
            tableModel.setItemList(order.getOrderItems());
        }
    }

    long tableUpdate() {
        long orderItemId = -1;
        if (selectedOrderItem != null) {
            orderItemId = selectedOrderItem.getId();
        }
        tableModel.updateTable();
        return orderItemId;
    }

    void tableClear() {
        tableModel.setItemList(new ArrayList<>());
    }

    private void tableAddOrderItems(List<OrderItem> orderItems) {
        tableModel.addItems(orderItems);
    }

    void tableAddOrderItem(OrderItem orderItem) {
        List<OrderItem> orderItems = new ArrayList<>(1);
        orderItems.add(orderItem);
        tableAddOrderItems(orderItems);
    }

    public void tableSelectOrderItem(OrderItem orderItem) {
        orderItemTable.selectItem(orderItem);
    }

    OrderItem tableGetSelectedItem() {
        return orderItemTable.getSelectedItem();
    }

    List<OrderItem> tableGetAllSelectedOrderItems() {
        return orderItemTable.getAllSelectedItems();
    }


    //
    // Table tool bar stuff
    //
    void updateToolBar(Order order) {
        if (order != null) {
            tbOrderNameLbl.setText(order.getName());

            if (order.getDistributor() != null) {
                tbDistributorCb.setSelectedItem(order.getDistributor());
            }
        } else {
            tbOrderNameLbl.setText("");
            tbDistributorCb.setSelectedItem(null);
        }
    }

    void updateEnabledComponents() {
        boolean orderSelected = (selectedOrder != null && !selectedOrder.isUnknown() && selectedOrder.canBeSaved());
        boolean itemSelected = (selectedOrderItem != null && !selectedOrderItem.isUnknown());

        treeToolBar.setEditActionEnabled(orderSelected);
        treeToolBar.setDeleteActionEnabled(orderSelected);

        if (orderSelected) {
            orderItemTable.setDbToolBarEnabled(true);
            orderItemTable.setDbToolBarEditDeleteEnabled(itemSelected);
        } else {
            orderItemTable.setDbToolBarEnabled(true);
        }

        orderDetailsAa.setEnabled(orderSelected && !selectedOrder.isPlanned());
        tbDistributorCb.setEnabled(orderSelected && selectedOrder.isPlanned());

        tbOrderFlowPanel.updateComponents(selectedOrder);
    }

    void updateVisibleComponents() {
        orderTbPanel.setVisible(true);
        tbOrderFilePanel.setVisible(true);
    }

    private JPanel createOrderToolbar() {
        tbOrderFilePanel = new JPanel(new GridBagLayout());

        // Distributor
        ILabel distributorLabel = new ILabel("Order by: ", ILabel.RIGHT);
        JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setOpaque(false); toolBar.setFloatable(false);
        toolBar.setBorder(new EmptyBorder(2,2,2,2));

        JPanel makeOrderPanel = new JPanel(new BorderLayout());
        JPanel distributorPanel = new JPanel(new BorderLayout());

        distributorPanel.add(distributorLabel, BorderLayout.WEST);
        distributorPanel.add(tbDistributorCb, BorderLayout.CENTER);

        makeOrderPanel.add(tbOrderNameLbl, BorderLayout.CENTER);
        makeOrderPanel.add(toolBar, BorderLayout.EAST);
        makeOrderPanel.add(distributorPanel, BorderLayout.SOUTH);

        // Create panel
        orderTbPanel = new JPanel();
        orderTbPanel.add(makeOrderPanel);
        orderTbPanel.setVisible(false);

        return orderTbPanel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Sub division tree
        Order virtualRootOrder = new Order("All");
        virtualRootOrder.setCanBeSaved(false);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(virtualRootOrder, true);
        treeInitializeTree(rootNode);
        treeModel = new IDbObjectTreeModel<>(rootNode, (rootNode1, child) -> {
            if (child.isOrdered()) {
                return (DefaultMutableTreeNode) rootNode.getChildAt(0); // Ordered
            } else {
                return (DefaultMutableTreeNode) rootNode.getChildAt(1); // Not ordered
            }
        });

        ordersTree = new ITree(treeModel);
        ordersTree.addTreeSelectionListener(this);
        ordersTree.setCellRenderer(ITree.getOrdersRenderer());
        ordersTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = ordersTree.getClosestRowForLocation(e.getX(), e.getY());
                    ordersTree.setSelectionRow(row);
                    onTreeRightClick(e);
                }
            }
        });
        treeModel.setTree(ordersTree);

        // Item table
        tableModel = new IOrderItemTableModel();
        orderItemTable = new ITablePanel<>(tableModel, this);
        orderItemTable.setExactColumnWidth(0, 25); // Icon
        orderItemTable.setExactColumnWidth(1, 50); // Amount spinner
        orderItemTable.setDbToolBar(this);
        orderItemTable.setDbToolBarEnabled(false);
        orderItemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onTableRowClicked(e);
            }
        });
        orderItemTable.addColumnRenderer(0, new ITableEditors.OrderItemTooltipRenderer());
        orderItemTable.addColumnCellEditor(1, new ITableEditors.SpinnerEditor() {
            @Override
            public void onValueSet(int value) {
                onSetOrderItemAmount(orderItemTable.getSelectedItem(), value);
            }
        });
//        { TODO: in new ITablePanel something to override the prepareRenderer method
//            @Override
//            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
//                Component component = super.prepareRenderer(renderer, row, column);
//                OrderItem o = (OrderItem) getValueAtRow(row);
//
//                if (!isRowSelected(row)) {
//                    component.setBackground(getBackground());
//                    if (o.getItem().isDiscourageOrder()) {
//                        component.setBackground(colorResource.readColor("Red.Light"));
//                    } else {
//                        component.setBackground(getBackground());
//                    }
//                }
//
//                return component;
//            }
//        };

        // Details
        itemDetailPanel = new ItemDetailPanel(application, this);
        //orderItemDetailPanel = new OrderItemDetailPanel(application);

        // Tool bar
        tbOrderNameLbl = new ILabel();
        Font f = tbOrderNameLbl.getFont();
        tbOrderNameLbl.setFont(new Font(f.getName(), Font.BOLD, 20));
        tbDistributorCb = new IComboBox<>(cache().getDistributors(), new DbObjectNameComparator<>(), true);
        tbDistributorCb.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                if (selectedOrder != null) {
                    application.beginWait();
                    try {
                        Distributor d = (Distributor) tbDistributorCb.getSelectedItem();
                        if (d != null && selectedOrder.getDistributorId() != d.getId()) {
                            selectedOrder.setDistributorId(d.getId());
                            selectedOrder.updateItemReferences();
                            SwingUtilities.invokeLater(() -> selectedOrder.save());
                        }
                    } finally {
                        application.endWait();
                    }
                }
            }
        });

        orderDetailsAa = new AbstractAction("Details", imageResource.readImage("Orders.Flow.Details")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOrderDetailsAa(selectedOrder);
            }
        };
        tbOrderFlowPanel = new IOrderFlowPanel(application);

        // Actions
        treeAddOrderAa = new AbstractAction("Add order", imageResource.readImage("Orders.Tree.AddOrder")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddOrderAa();
            }
        };
        treeEditOrderAa = new AbstractAction("Edit order", imageResource.readImage("Orders.Tree.EditOrder")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditOrderAa(getSelectedOrder());
            }
        };
        treeDeleteOrderAa = new AbstractAction("Delete order", imageResource.readImage("Orders.Tree.DeleteOrder")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteOrderAa(getSelectedOrder());
            }
        };
        treeOrderDetailsAa = new AbstractAction("Order details", imageResource.readImage("Orders.Tree.OrderDetails")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOrderDetailsAa(getSelectedOrder());
            }
        };
        treeMoveToOrderedAa = new AbstractAction("Order ordered", imageResource.readImage("Orders.Tree.MoveToOrdered")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onMoveToOrderedAa(getSelectedOrder());
            }
        };
        treeMoveToReceivedAa = new AbstractAction("Order received", imageResource.readImage("Orders.Tree.MoveToReceived")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onMoveToReceivedAa(getSelectedOrder());
            }
        };
        treeBackToOrderedAa = new AbstractAction("Back to ordered", imageResource.readImage("Orders.Tree.BackToOrdered")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBackToOrderedAa(getSelectedOrder());
            }
        };
        treeBackToPlannedAa = new AbstractAction("Back to planned", imageResource.readImage("Orders.Tree.BackToPlanned")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBackToPlannedAa(getSelectedOrder());
            }
        };

        // Tool bars
        treeToolBar = new IdBToolBar(this);
        treeToolBar.addSeparateAction(orderDetailsAa);

    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Panel them together
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel tablePanel = new JPanel(new BorderLayout());
        JPanel detailPanels = new JPanel(new BorderLayout());
        JPanel westPanel = new JPanel(new BorderLayout());

        tablePanel.add(new JScrollPane(orderItemTable), BorderLayout.CENTER);

        centerPanel.add(tablePanel, BorderLayout.CENTER);

        detailPanels.add(itemDetailPanel, BorderLayout.CENTER);
        //detailPanels.add(orderItemDetailPanel, BorderLayout.EAST);
        detailPanels.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 3, 2, 3),
                BorderFactory.createLineBorder(Color.GRAY, 1)
        ));

        centerPanel.add(detailPanels, BorderLayout.SOUTH);
        orderItemTable.getTitlePanel().add(createOrderToolbar(), BorderLayout.CENTER);

        ordersTree.setPreferredSize(new Dimension(300, 200));
        JScrollPane pane = new JScrollPane(ordersTree);
        westPanel.add(tbOrderFlowPanel, BorderLayout.PAGE_START);
        westPanel.add(pane, BorderLayout.CENTER);
        westPanel.add(treeToolBar, BorderLayout.PAGE_END);
        westPanel.setMinimumSize(new Dimension(280, 200));

        // Add
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westPanel, centerPanel);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... object) { // Has last selected order
        if (application.isUpdating()) {
            return;
        }
        application.beginWait();
        try {
            // Update table if needed
            if (object.length != 0 && object[0] != null) {
                if (selectedOrder == null || !selectedOrder.equals(object[0])) {
                    selectedOrder = (Order) object[0];
                    tableInitialize(selectedOrder);
                }
            }

            treeModel.expandNodes(0, ordersTree.getRowCount());

            updateToolBar(selectedOrder);

            // Update detail panel
            if (selectedOrderItem != null) {
                itemDetailPanel.updateComponents(selectedOrderItem.getItem());
            } else {
                itemDetailPanel.updateComponents();
            }
            updateVisibleComponents();
            updateEnabledComponents();
        } finally {
            application.endWait();
        }
    }
}
