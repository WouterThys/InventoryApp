package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.IOrderItemTableModel;
import com.waldo.inventory.gui.components.treemodels.IDbObjectTreeModel;
import com.waldo.inventory.gui.dialogs.orderconfirmdialog.OrderConfirmDialog;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialog;
import com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel.ItemDetailPanel;
import com.waldo.inventory.gui.panels.orderpanel.orderitemdetailpanel.OrderItemDetailPanel;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.gui.Application.colorResource;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public abstract class OrderPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITable orderItemTable;
    IOrderItemTableModel tableModel;

    ITree ordersTree;
    private IDbObjectTreeModel<Order> treeModel;
    ItemDetailPanel itemDetailPanel;
    OrderItemDetailPanel orderItemDetailPanel;

    private IdBToolBar treeToolBar;
    TopToolBar tableToolBar;
    private JToolBar bottomToolBar;
    private JPanel orderTbPanel;
    IOrderFlowPanel tbOrderFlowPanel;
    private ILabel tbTotalItemsLbl;
    private ILabel tbTotalPriceLbl;
    private ILabel tbOrderNameLbl;
    private IComboBox<Distributor> tbDistributorCb;
    private AbstractAction tbOrderDetailsAa;
    private JPanel tbOrderFilePanel;
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

        for (Order o : db().getOrders()) {
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
        if (orderItem != null) {
            int modelNdx = tableModel.getItemList().indexOf(orderItem);
            if (modelNdx >= 0) {
                int tableNdx = orderItemTable.convertRowIndexToView(modelNdx);
                orderItemTable.setRowSelectionInterval(tableNdx, tableNdx);
                orderItemTable.scrollRectToVisible(new Rectangle(orderItemTable.getCellRect(tableNdx, 0, true)));
            }
        } else {
            orderItemTable.clearSelection();
        }
    }


    //
    // Table tool bar stuff
    //
    void updateToolBar(Order order) {
        if (order != null) {
            tbTotalItemsLbl.setText(String.valueOf(order.getOrderItems().size()));
            tbTotalPriceLbl.setText(String.valueOf(order.getTotalPrice()));
            tbOrderNameLbl.setText(order.getName());

            if (order.getDistributor() != null) {
                tbDistributorCb.setSelectedItem(order.getDistributor());
            }
        } else {
            tbTotalItemsLbl.setText("");
            tbTotalPriceLbl.setText("");
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
            tableToolBar.setEnabled(true);
            tableToolBar.setEditActionEnabled(itemSelected);
            tableToolBar.setDeleteActionEnabled(itemSelected);
        } else {
            tableToolBar.setEnabled(false);
        }

        tbOrderDetailsAa.setEnabled(orderSelected && !selectedOrder.isPlanned());
        tbDistributorCb.setEnabled(orderSelected && selectedOrder.isPlanned());

        tbOrderFlowPanel.updateComponents(selectedOrder);
    }

    void updateVisibleComponents() {
        orderTbPanel.setVisible(true);
        tbOrderFilePanel.setVisible(true);
    }

    private void createInfoToolBar() {
        JPanel amountPanel = new JPanel(new GridBagLayout());
        JPanel datesPanel = new JPanel(new GridBagLayout());

        amountPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        datesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Total items
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        amountPanel.add(tbTotalItemsLbl, gbc);

        // Total price
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        amountPanel.add(tbTotalPriceLbl, gbc);

        // Add to toolbar
        bottomToolBar.add(datesPanel);
        bottomToolBar.add(Box.createHorizontalGlue());
        bottomToolBar.add(amountPanel);
    }

    private JPanel createOrderToolbar() {
        tbOrderFilePanel = new JPanel(new GridBagLayout());

        // Distributor
        ILabel distributorLabel = new ILabel("Order by: ", ILabel.RIGHT);
        JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setOpaque(false); toolBar.setFloatable(false);
        toolBar.setBorder(new EmptyBorder(2,2,2,2));
        toolBar.add(tbOrderDetailsAa);

        JPanel makeOrderPanel = new JPanel(new BorderLayout());
        JPanel distributorPanel = new JPanel(new BorderLayout());

        distributorPanel.add(distributorLabel, BorderLayout.WEST);
        distributorPanel.add(tbDistributorCb, BorderLayout.CENTER);

        makeOrderPanel.add(tbOrderNameLbl, BorderLayout.CENTER);
        makeOrderPanel.add(toolBar, BorderLayout.EAST);
        makeOrderPanel.add(distributorPanel, BorderLayout.SOUTH);

        // Create panel
        orderTbPanel = new JPanel(new BorderLayout());
        orderTbPanel.add(makeOrderPanel, BorderLayout.WEST);
        orderTbPanel.add(tbOrderFlowPanel, BorderLayout.EAST);
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
        treeModel.setTree(ordersTree);

        // Item table
        tableModel = new IOrderItemTableModel();
        orderItemTable = new ITable<OrderItem>(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                OrderItem o = (OrderItem) getValueAtRow(row);

                if (!isRowSelected(row)) {
                    component.setBackground(getBackground());
                    if (o.getItem().isDiscourageOrder()) {
                        component.setBackground(colorResource.readColor("Red.Light"));
                    } else {
                        component.setBackground(getBackground());
                    }
                }

                return component;
            }
        };

        orderItemTable.getSelectionModel().addListSelectionListener(this);
        orderItemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);

        // Details
        itemDetailPanel = new ItemDetailPanel(application);
        orderItemDetailPanel = new OrderItemDetailPanel(application);

        // Tool bar
        tbTotalPriceLbl = new ILabel();
        tbTotalPriceLbl.setEnabled(false);
        tbTotalPriceLbl.setHorizontalAlignment(ILabel.RIGHT);
        tbTotalPriceLbl.setVerticalAlignment(ILabel.CENTER);

        tbTotalItemsLbl = new ILabel();
        tbTotalItemsLbl.setEnabled(false);
        tbTotalItemsLbl.setHorizontalAlignment(ILabel.LEFT);
        tbTotalItemsLbl.setVerticalAlignment(ILabel.CENTER);

        tbOrderNameLbl = new ILabel();
        Font f = tbOrderNameLbl.getFont();
        tbOrderNameLbl.setFont(new Font(f.getName(), Font.BOLD, 20));
        tbDistributorCb = new IComboBox<>(DbManager.db().getDistributors(), new DbObjectNameComparator<>(), true);
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

        tbOrderDetailsAa = new AbstractAction("Details", imageResource.readImage("Orders.Flow.Details")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrderConfirmDialog dialog = new OrderConfirmDialog(application, "Confirm receive", selectedOrder);
                if (selectedOrder.isReceived()) {
                    dialog.showDialog(OrderConfirmDialog.TAB_ORDER_DETAILS, null);
                } else {
                    dialog.showDialog();
                }
            }
        };

        tbOrderFlowPanel = new IOrderFlowPanel(application);

        // Tool bars
        treeToolBar = new IdBToolBar(new IdBToolBar.IdbToolBarListener() {
            @Override
            public void onToolBarRefresh(IdBToolBar source) {

                treeRecreateNodes();
                final long orderId = treeUpdate();
                final long orderItemId = tableUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedOrder = SearchManager.sm().findOrderById(orderId);
                    treeSelectOrder(selectedOrder);
                    selectedOrderItem = SearchManager.sm().findOrderItemById(orderItemId);
                    tableSelectOrderItem(selectedOrderItem);
                });
            }

            @Override
            public void onToolBarAdd(IdBToolBar source) {
                OrdersDialog dialog = new OrdersDialog(application, "New order", new Order(), true);
                if (dialog.showDialog() == IDialog.OK) {
                    Order o = dialog.getOrder();
                    o.save();
                }
            }

            @Override
            public void onToolBarDelete(IdBToolBar source) {
                if (selectedOrder != null) {
                    int res = JOptionPane.showConfirmDialog(OrderPanelLayout.this, "Are you sure you want to delete \"" + selectedOrder.getName() + "\"?");
                    if (res == JOptionPane.OK_OPTION) {
                        SwingUtilities.invokeLater(() -> {
                            List<OrderItem> orderItems = selectedOrder.getOrderItems();

                            selectedOrder.delete(); // Cascaded delete will delete order items too
                            selectedOrder = null;
                            selectedOrderItem = null;

                            // Do this after delete: items will not be updated in change listener for orders
                            for (OrderItem orderItem : orderItems) {
                                orderItem.getItem().setOrderState(Statics.ItemOrderStates.NONE);
                                orderItem.getItem().save();
                            }
                        });
                    }
                }
            }

            @Override
            public void onToolBarEdit(IdBToolBar source) {
                if (selectedOrder != null) {
                    OrdersDialog dialog = new OrdersDialog(application, "Edit order", selectedOrder);
                    if (dialog.showDialog() == IDialog.OK) {
                        Order o = dialog.getOrder();
                        o.save();
                    }
                }
            }
        });
        tableToolBar = new TopToolBar(application, this);
        tableToolBar.getContentPane().add(createOrderToolbar());

        // Create bottom toolbar
        bottomToolBar = new JToolBar(JToolBar.HORIZONTAL);
        bottomToolBar.setFloatable(false);
        bottomToolBar.setOpaque(false);
        bottomToolBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        createInfoToolBar();
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
        tablePanel.add(bottomToolBar, BorderLayout.PAGE_END);

        centerPanel.add(tablePanel, BorderLayout.CENTER);

        detailPanels.add(itemDetailPanel, BorderLayout.CENTER);
        detailPanels.add(orderItemDetailPanel, BorderLayout.EAST);
        detailPanels.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 3, 2, 3),
                BorderFactory.createLineBorder(Color.GRAY, 1)
        ));

        centerPanel.add(detailPanels, BorderLayout.SOUTH);
        centerPanel.add(tableToolBar, BorderLayout.PAGE_START);

        ordersTree.setPreferredSize(new Dimension(300, 200));
        JScrollPane pane = new JScrollPane(ordersTree);
        westPanel.add(pane, BorderLayout.CENTER);
        westPanel.add(treeToolBar, BorderLayout.PAGE_END);
        westPanel.setMinimumSize(new Dimension(200, 200));

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

                    // Search list
                    tableToolBar.setSearchList(new ArrayList<>(selectedOrder.getOrderItems()));
                }
            }

            treeModel.expandNodes(0, ordersTree.getRowCount());

            updateToolBar(selectedOrder);

            // Update detail panel
            if (selectedOrderItem != null) {
                itemDetailPanel.updateComponents(selectedOrderItem.getItem());
                if (selectedOrder != null && !selectedOrder.isOrdered()) {
                    orderItemDetailPanel.updateComponents(selectedOrderItem);
                    itemDetailPanel.setRemarksPanelVisible(false);
                } else {
                    itemDetailPanel.setRemarksPanelVisible(true);
                }
            } else {
                itemDetailPanel.updateComponents();
                orderItemDetailPanel.updateComponents();
            }
            updateVisibleComponents();
            updateEnabledComponents();
        } finally {
            application.endWait();
        }
    }
}
