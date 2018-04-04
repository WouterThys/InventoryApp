package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IOrderFlowPanel;
import com.waldo.inventory.gui.components.ITablePanel;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.tablemodels.IOrderItemTableModel;
import com.waldo.inventory.gui.components.treemodels.IDbObjectTreeModel;
import com.waldo.inventory.gui.panels.mainpanel.AbstractDetailPanel;
import com.waldo.inventory.gui.panels.mainpanel.preview.itemdetailpanel.ItemDetailPanel;
import com.waldo.inventory.gui.panels.mainpanel.ItemDetailListener;
import com.waldo.inventory.gui.panels.mainpanel.OrderDetailListener;
import com.waldo.inventory.gui.panels.mainpanel.preview.ItemPreviewPanel;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableEditors;

import javax.swing.*;
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

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;
import static com.waldo.inventory.managers.CacheManager.cache;

public abstract class OrderPanelLayout extends JPanel implements
        GuiUtils.GuiInterface,
        TreeSelectionListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        ItemDetailListener,
        OrderDetailListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITablePanel<OrderItem> orderItemTable;
    IOrderItemTableModel tableModel;

    ITree ordersTree;
    private IDbObjectTreeModel<Order> treeModel;
    AbstractDetailPanel detailPanel;
    //OrderItemDetailPanel orderItemDetailPanel;

    IdBToolBar treeToolBar;
    private JPanel orderTbPanel;
    IOrderFlowPanel tbOrderFlowPanel;
    private ILabel tbOrderNameLbl;
    private IComboBox<Distributor> tbDistributorCb;
    private AbstractAction orderDetailsAa;
    private AbstractAction pendingOrderAa;
    private JPanel tbOrderFilePanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final Application application;

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

    abstract void onAddOrder();
    abstract void onEditOrder(Order order);
    abstract void onDeleteOrder(Order order);
    abstract void onOrderDetails(Order order);
    abstract void onViewPendingOrders();

    abstract void onMoveToOrdered(Order order);
    abstract void onMoveToReceived(Order order);
    abstract void onBackToOrdered(Order order);
    abstract void onBackToPlanned(Order order);

    abstract void onDeleteOrderItem(OrderItem orderItem);
    abstract void onEditItem(OrderItem orderItem);


    Order getSelectedOrder() {
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
                    case Planned:
                        plannedNode.add(oNode);
                        break;
                    case Ordered:
                        orderedNode.add(oNode);
                        break;
                    case Received:
                        receivedNode.add(oNode);
                        break;
                    case NoOrder:
                        break; // Should not happen
                }
            }
        }
    }


    public DefaultMutableTreeNode sort(DefaultMutableTreeNode node) {
        //sort alphabetically
        for(int i = 0; i < node.getChildCount() - 1; i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            String nt = child.getUserObject().toString();

            for(int j = i + 1; j <= node.getChildCount() - 1; j++) {
                DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) node.getChildAt(j);
                String np = prevNode.getUserObject().toString();

                System.out.println(nt + " " + np);
                if(nt.compareToIgnoreCase(np) > 0) {
                    node.insert(child, j);
                    node.insert(prevNode, i);
                }
            }
            if(child.getChildCount() > 0) {
                sort(child);
            }
        }

        //put folders first - normal on Windows and some flavors of Linux but not on Mac OS X.
        for(int i = 0; i < node.getChildCount() - 1; i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            for(int j = i + 1; j <= node.getChildCount() - 1; j++) {
                DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) node.getChildAt(j);

                if(!prevNode.isLeaf() && child.isLeaf()) {
                    node.insert(child, j);
                    node.insert(prevNode, i);
                }
            }
        }

        return node;

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
            orderItemTable.resizeColumns();
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

    void tableSelectOrderItem(OrderItem orderItem) {
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

            if (order.getOrderState() != Statics.ItemOrderStates.Planned && !order.isLocked()) {
                orderItemTable.setHeaderPanelBackground(Color.red);
            } else {
                orderItemTable.setHeaderPanelBackground(null);
            }
        } else {
            tbOrderNameLbl.setText("");
            tbDistributorCb.setSelectedItem(null);
            orderItemTable.setHeaderPanelBackground(null);
        }
    }

    void updateEnabledComponents() {
        boolean orderSelected = (selectedOrder != null && !selectedOrder.isUnknown() && selectedOrder.canBeSaved());
        boolean itemSelected = (selectedOrderItem != null && !selectedOrderItem.isUnknown());
        boolean locked = orderSelected && selectedOrder.isLocked();

        treeToolBar.setEditActionEnabled(!locked);
        treeToolBar.setDeleteActionEnabled(!locked);

        if (orderSelected) {
            orderItemTable.setDbToolBarEnabled(true);
            orderItemTable.setDbToolBarEditDeleteEnabled(itemSelected && !locked);
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
        JToolBar toolBar = GuiUtils.createNewToolbar();

        distributorLabel.setOpaque(false);
        toolBar.setOpaque(false);

        JPanel makeOrderPanel = new JPanel(new BorderLayout());
        JPanel distributorPanel = new JPanel(new BorderLayout());
        distributorPanel.setOpaque(false);
        makeOrderPanel.setOpaque(false);

        distributorPanel.add(distributorLabel, BorderLayout.WEST);
        distributorPanel.add(tbDistributorCb, BorderLayout.CENTER);

        makeOrderPanel.add(tbOrderNameLbl, BorderLayout.CENTER);
        makeOrderPanel.add(toolBar, BorderLayout.EAST);
        makeOrderPanel.add(distributorPanel, BorderLayout.SOUTH);

        // Create panel
        orderTbPanel = new JPanel();
        orderTbPanel.add(makeOrderPanel);
        orderTbPanel.setVisible(false);

        tbOrderFilePanel.setOpaque(false);
        orderTbPanel.setOpaque(false);

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

        // Preview
        boolean vertical = settings().getGeneralSettings().getGuiDetailsView() == Statics.GuiDetailsView.VerticalSplit;
        if (vertical) {
            detailPanel = new ItemPreviewPanel(this, this) {
                @Override
                public void onToolBarDelete(IdBToolBar source) {
                    OrderPanelLayout.this.onToolBarDelete(source);
                }

                @Override
                public void onToolBarEdit(IdBToolBar source) {
                    OrderPanelLayout.this.onToolBarEdit(source);
                }
            };
            detailPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(2, -1, -1, -1),
                    BorderFactory.createLineBorder(Color.lightGray, 1)
            ));
        }

        // Item table
        tableModel = new IOrderItemTableModel();
        orderItemTable = new ITablePanel<>(tableModel, detailPanel, this, false);
        orderItemTable.setExactColumnWidth(1, 50); // Amount spinner
        orderItemTable.setDbToolBar(this, true, true, false, false);
        orderItemTable.setDbToolBarEnabled(false);
        orderItemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onTableRowClicked(e);
            }
        });
        orderItemTable.addColumnCellEditor(1, new ITableEditors.SpinnerEditor() {
            @Override
            public void onValueSet(int value) {
                onSetOrderItemAmount(orderItemTable.getSelectedItem(), value);
            }
        });

        if (!vertical) {
            detailPanel = new ItemDetailPanel(this);
        }

        // Tool bar
        tbOrderNameLbl = new ILabel();
        Font f = tbOrderNameLbl.getFont();
        tbOrderNameLbl.setFont(new Font(f.getName(), Font.BOLD, 20));
        tbDistributorCb = new IComboBox<>(cache().getDistributors(), new DbObjectNameComparator<>(), true);
        tbDistributorCb.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                if (selectedOrder != null) {
                    Application.beginWait(OrderPanelLayout.this);
                    try {
                        Distributor d = (Distributor) tbDistributorCb.getSelectedItem();
                        if (d != null && selectedOrder.getDistributorId() != d.getId()) {
                            selectedOrder.setDistributorId(d.getId());
                            selectedOrder.updateItemReferences();
                            SwingUtilities.invokeLater(() -> selectedOrder.save());
                        }
                    } finally {
                        Application.endWait(OrderPanelLayout.this);
                    }
                }
            }
        });

        orderDetailsAa = new AbstractAction("Details", imageResource.readIcon("Orders.Flow.Details")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOrderDetails(selectedOrder);
            }
        };
        orderDetailsAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Details");

        pendingOrderAa = new AbstractAction("Pending orders", imageResource.readIcon("Actions.M.Pending")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onViewPendingOrders();
            }
        };
        pendingOrderAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Pending orders");

        tbOrderFlowPanel = new IOrderFlowPanel();

        // Tool bars
        treeToolBar = new IdBToolBar(this);
        treeToolBar.addSeparateAction(orderDetailsAa);
        treeToolBar.addAction(pendingOrderAa);

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

        //detailPanels.add(itemDetailPanel, BorderLayout.CENTER);
        //detailPanels.add(orderItemDetailPanel, BorderLayout.EAST);

        boolean vertical = settings().getGeneralSettings().getGuiDetailsView() == Statics.GuiDetailsView.VerticalSplit;
        if (!vertical) {
            detailPanels.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 3, 2, 3),
                BorderFactory.createLineBorder(Color.GRAY, 1)
                ));
            centerPanel.add(detailPanel, BorderLayout.SOUTH);
        }

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
        if (Application.isUpdating(OrderPanelLayout.this)) {
            return;
        }
        Application.beginWait(OrderPanelLayout.this);
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
                detailPanel.updateComponents(selectedOrderItem.getItem());
            } else {
                detailPanel.updateComponents();
            }
            updateVisibleComponents();
            updateEnabledComponents();
        } finally {
            Application.endWait(OrderPanelLayout.this);
        }
    }
}
