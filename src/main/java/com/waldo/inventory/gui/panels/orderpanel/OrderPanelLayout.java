package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.IOrderItemTableModel;
import com.waldo.inventory.gui.components.treemodels.IDbObjectTreeModel;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialog;
import com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel.ItemDetailPanel;
import com.waldo.inventory.gui.panels.orderpanel.orderitemdetailpanel.OrderItemDetailPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
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
    private IDbObjectTreeModel treeModel;
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
    private JComboBox<Distributor> tbDistributorCb;
    private JPanel tbOrderFilePanel;
    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static final SimpleDateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyy-MM-dd");
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

    public void treeAddOrder(Order order) {
        try {
            treeModel.addObject(order);
        } catch (Exception e) {
            Status().setError("Failed to add order " + order.getName() + " to tree", e);
        }
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

    public void treeSelectOrder(Order order) {
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

    public long tableUpdate() {
        long orderItemId = -1;
        if (selectedOrderItem != null) {
            orderItemId = selectedOrderItem.getId();
        }
        tableModel.updateTable();
        return orderItemId;
    }

    public void tableClear() {
        tableModel.setItemList(new ArrayList<>());
    }

    public void tableAddOrderItems(List<OrderItem> orderItems) {
        tableModel.addItems(orderItems);
    }

    public void tableAddOrderItem(OrderItem orderItem) {
        List<OrderItem> orderItems = new ArrayList<>(1);
        orderItems.add(orderItem);
        tableAddOrderItems(orderItems);
    }

    public void tableDeleteOrderItems(List<OrderItem> orderItems) {
        tableModel.removeItems(orderItems);
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
        } else  {
            tbTotalItemsLbl.setText("");
            tbTotalPriceLbl.setText("");
            tbOrderNameLbl.setText("");
            tbDistributorCb.setSelectedItem(null);
        }
    }

    void updateEnabledComponents() {
        // Orders
        if (selectedOrder == null || selectedOrder.isUnknown() || !selectedOrder.canBeSaved()) {
            treeToolBar.setEditActionEnabled(false);
            treeToolBar.setDeleteActionEnabled(false);
            tableToolBar.setAddActionEnabled(false);
            tableToolBar.setRefreshActionEnabled(false);
            tbDistributorCb.setEnabled(false);
        } else {
            treeToolBar.setEditActionEnabled(true);
            treeToolBar.setDeleteActionEnabled(true);
            tableToolBar.setAddActionEnabled(true);
            tableToolBar.setRefreshActionEnabled(true);
            tbDistributorCb.setEnabled(!selectedOrder.isOrdered());
        }

        // Items
        if (selectedOrderItem == null) {
            tableToolBar.setEditActionEnabled(false);
            tableToolBar.setDeleteActionEnabled(false);
        } else {
            tableToolBar.setEditActionEnabled(true);
            tableToolBar.setDeleteActionEnabled(true);
        }

        tbOrderFlowPanel.updateComponents(selectedOrder);
    }

    void updateVisibleComponents() {
//        if (selectedOrder != null) {
//            boolean visible = !(selectedOrder.isUnknown() || !selectedOrder.canBeSaved());
//            orderTbPanel.setVisible(visible);
//            tbOrderFilePanel.setVisible(selectedOrder.hasOrderFile());
//        } else {
//            orderTbPanel.setVisible(false);
//        }
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
        ILabel itemCntLabel = new ILabel("Item count: ");
        itemCntLabel.setHorizontalAlignment(ILabel.RIGHT);
        itemCntLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.gridy = 0;
        gbc.weighty = 0;
        //amountPanel.add(itemCntLabel, gbc);

        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.gridy = 1;
        gbc.weighty = 0;
        amountPanel.add(tbTotalItemsLbl, gbc);

        // Total price
        ILabel totalPriceLabel = new ILabel("Total price: ");
        itemCntLabel.setHorizontalAlignment(ILabel.RIGHT);
        itemCntLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.gridy = 0;
        gbc.weighty = 0;
        //amountPanel.add(totalPriceLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.gridy = 1;
        gbc.weighty = 0;
        amountPanel.add(tbTotalPriceLbl, gbc);

        // Add to toolbar
        bottomToolBar.add(datesPanel);
        bottomToolBar.add(Box.createHorizontalGlue());
        bottomToolBar.add(amountPanel);
    }

    private JPanel createOrderToolbar() {
        JPanel makeOrderPanel = new JPanel(new GridBagLayout());
        tbOrderFilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(-10, 2, 0, 20);

        // Order name
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        makeOrderPanel.add(tbOrderNameLbl, gbc);

        // Distributor
        ILabel distributorLabel = new ILabel("Order by: ");
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        makeOrderPanel.add(distributorLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        makeOrderPanel.add(tbDistributorCb, gbc);

        // Create panel
        orderTbPanel = new JPanel(new BorderLayout());
        orderTbPanel.add(makeOrderPanel, BorderLayout.WEST);
        orderTbPanel.add(tbOrderFlowPanel, BorderLayout.EAST);
        //orderTbPanel.add(tbOrderFilePanel, BorderLayout.EAST);
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
        treeModel = new IDbObjectTreeModel(rootNode, IDbObjectTreeModel.TYPE_ORDERS);

        ordersTree = new ITree(treeModel);
        ordersTree.addTreeSelectionListener(this);
        treeModel.setTree(ordersTree);

        // Item table
        tableModel = new IOrderItemTableModel();
        orderItemTable = new ITable(tableModel);

        orderItemTable.getSelectionModel().addListSelectionListener(this);
        orderItemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);

        // Details
        itemDetailPanel = new ItemDetailPanel(application);
        //itemDetailPanel.setOrderButtonVisible(false);

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
        DefaultComboBoxModel<Distributor> distributorCbModel = new DefaultComboBoxModel<>();
        for (Distributor d : DbManager.db().getDistributors()) {
            distributorCbModel.addElement(d);
        }
        tbDistributorCb = new JComboBox<>(distributorCbModel);
        tbDistributorCb.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                if (selectedOrder != null) {
                    application.beginWait();
                    try {
                        Distributor d = (Distributor) tbDistributorCb.getSelectedItem();
                        if (selectedOrder.getDistributorId() != d.getId()) {
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
                OrdersDialog dialog = new OrdersDialog(application, "New order", true);
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
                        selectedOrder.delete(); // Cascaded delete will delete order items too
                        selectedOrder = null;
                        selectedOrderItem = null;
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
        treeToolBar.setFloatable(false);
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

        // Add
        add(westPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) { // Has last selected order
        if (application.isUpdating()) {
            return;
        }
        try {
            application.beginWait();

            // Update table if needed
            if (object != null) {
                if (selectedOrder == null || !selectedOrder.equals(object)) {
                    selectedOrder = (Order) object;
                    tableInitialize(selectedOrder);

                    // Search list
                    tableToolBar.setSearchList(new ArrayList<>(selectedOrder.getOrderItems()));
                }
            } else {
                selectedOrder = null;
            }

            treeModel.expandNodes(0, ordersTree.getRowCount());

            updateToolBar(selectedOrder);
            updateVisibleComponents();
            updateEnabledComponents();

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
                itemDetailPanel.updateComponents(null);
                orderItemDetailPanel.updateComponents(null);
            }
        } finally {
            application.endWait();
        }
    }
}
