package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialog;
import com.waldo.inventory.gui.panels.itemdetailpanel.ItemDetailPanel;
import com.waldo.inventory.gui.panels.orderitemdetailpanel.OrderItemDetailPanel;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public abstract class OrderPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITable itemTable;
    IOrderItemTableModel tableModel;

    ITree ordersTree;
    IDbObjectTreeModel treeModel;
    ItemDetailPanel itemDetailPanel;
    OrderItemDetailPanel orderItemDetailPanel;

    private IdBToolBar orderToolBar;
    private TopToolBar topToolBar;
    private JToolBar bottomToolBar;
    private JPanel orderTbPanel;

    private ILabel tbTotalItemsLbl;
    private ILabel tbTotalPriceLbl;
    private ILabel tbDateOrderedLbl;
    private ILabel tbDateReceivedLbl;
    private ILabel tbDateModifiedLbl;
    JButton tbOrderButton;
    private JComboBox<Distributor> tbDistributorCb;
    private JPanel tbOrderFilePanel;
    JButton tbViewOrderDetailsBtn;
    JButton tbSetOrderedBtn;
    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static final SimpleDateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyy-MM-dd");
    Application application;

    OrderItem selectedOrderItem;
    Order lastSelectedOrder;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public OrderPanelLayout(Application application) {
        this.application = application;
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public Order getLastSelectedOrder() {
        return lastSelectedOrder;
    }

    public void selectOrder(Order order) {
        lastSelectedOrder = order;
        if (order != null) {
            treeModel.setSelectedObject(lastSelectedOrder);
            updateTable(lastSelectedOrder);
        }
    }

//    public void selectOrderItem(OrderItem orderItem) {
//        if (orderItem != null && lastSelectedOrder != null) {
//            List<OrderItem> orderItems = lastSelectedOrder.getOrderItems();
//            if (orderItems != null) {
//                int ndx = orderItems.indexOf(orderItem);
//                if (ndx >= 0 && ndx < orderItems.size()) {
//                    itemTable.setRowSelectionInterval(ndx, ndx);
//                }
//            }
//        }
//    }
    public void selectOrderItem(OrderItem orderItem) {
        if (orderItem != null) {
            List<OrderItem> itemList = tableModel.getItemList();
            if (itemList != null) {
                int ndx = itemList.indexOf(orderItem);
                if (ndx >= 0 && ndx < itemList.size()) {
                    itemTable.setRowSelectionInterval(ndx, ndx);
                }
            }
        }
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void updateTable(Order selectedOrder) {
        if (selectedOrder != null && !selectedOrder.getName().equals("All")) {
            tableModel.setItemList(db().getOrderedItems(selectedOrder.getId()));
        }
    }

    private void updateToolBar(Order order) {
        tbTotalItemsLbl.setText(String.valueOf(order.getOrderItems().size()));
        tbTotalPriceLbl.setText(String.valueOf(order.getTotalPrice()));

        if (order.isOrdered()) {
            tbDateOrderedLbl.setText(dateFormatShort.format(order.getDateOrdered()));
            tbSetOrderedBtn.setText("Set received");
        } else {
            tbDateOrderedLbl.setText("Not ordered");
            tbSetOrderedBtn.setText("Set ordered");
        }

        if (order.isReceived()) {
            tbDateReceivedLbl.setText(dateFormatShort.format(order.getDateReceived()));
            tbSetOrderedBtn.setVisible(false);
            //tbOrderButton.setText("Order again");
        } else {
            tbDateReceivedLbl.setText("Not received");
            tbSetOrderedBtn.setVisible(true);
            tbOrderButton.setText("Order!");
        }

        tbOrderButton.setVisible(!order.isOrdered());// || order.isReceived());

        if (order.getDateModified() != null) {
            tbDateModifiedLbl.setText(dateFormatLong.format(order.getDateModified()));
        } else {
            tbDateModifiedLbl.setText(" / ");
        }

        if (order.getDistributor() != null) {
            tbDistributorCb.setSelectedItem(order.getDistributor());
        }
    }

    private void createNodes(DefaultMutableTreeNode rootNode) {
        Order ordered = new Order("Ordered");
        Order notOrdered = new Order("Not ordered");
        ordered.setCanBeSaved(false);
        notOrdered.setCanBeSaved(false);
        DefaultMutableTreeNode orderedNode = new DefaultMutableTreeNode(ordered);
        DefaultMutableTreeNode notOrderedNode = new DefaultMutableTreeNode(notOrdered);
        rootNode.add(orderedNode);
        rootNode.add(notOrderedNode);
        for (Order o : db().getOrders()) {
            if (!o.isUnknown()) {
                DefaultMutableTreeNode oNode = new DefaultMutableTreeNode(o, false);
                if (o.isOrdered()) {
                    orderedNode.add(oNode);
                } else {
                    notOrderedNode.add(oNode);
                }
            }
        }
    }

    void recreateNodes() {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        rootNode.removeAllChildren();
        createNodes(rootNode);
    }

    private void updateEnabledComponents() {
        // Orders
        if (lastSelectedOrder == null || lastSelectedOrder.isUnknown() || !lastSelectedOrder.canBeSaved()) {
            orderToolBar.setEditActionEnabled(false);
            orderToolBar.setDeleteActionEnabled(false);
            topToolBar.setAddActionEnabled(false);
            topToolBar.setRefreshActionEnabled(false);
            tbOrderButton.setEnabled(false);
            tbDistributorCb.setEnabled(false);
        } else {
            orderToolBar.setEditActionEnabled(true);
            orderToolBar.setDeleteActionEnabled(true);
            topToolBar.setAddActionEnabled(true);
            topToolBar.setRefreshActionEnabled(true);
            tbDistributorCb.setEnabled(!lastSelectedOrder.isOrdered());
        }

        tbOrderButton.setEnabled(tableModel.getRowCount() > 0);

        // Items
        if (selectedOrderItem == null) {
            topToolBar.setEditActionEnabled(false);
            topToolBar.setDeleteActionEnabled(false);
        } else {
            topToolBar.setEditActionEnabled(true);
            topToolBar.setDeleteActionEnabled(true);
        }

    }

    private void updateVisibleComponents() {
        if (lastSelectedOrder != null) {
            boolean visible = !(lastSelectedOrder.isUnknown() || !lastSelectedOrder.canBeSaved());
            orderTbPanel.setVisible(visible);
            tbOrderFilePanel.setVisible(lastSelectedOrder.hasOrderFile());
        } else {
            orderTbPanel.setVisible(false);
        }
    }

    private void createInfoToolBar() {
        JPanel amountPanel = new JPanel(new GridBagLayout());
        JPanel datesPanel = new JPanel(new GridBagLayout());

        amountPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        datesPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Total items
        ILabel itemCntLabel = new ILabel("Item count: ");
        itemCntLabel.setHorizontalAlignment(ILabel.RIGHT);
        itemCntLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        //amountPanel.add(itemCntLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        amountPanel.add(tbTotalItemsLbl, gbc);

        // Total price
        ILabel totalPriceLabel = new ILabel("Total price: ");
        itemCntLabel.setHorizontalAlignment(ILabel.RIGHT);
        itemCntLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        //amountPanel.add(totalPriceLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        amountPanel.add(tbTotalPriceLbl, gbc);

        // Order date
        ILabel dateLabel = new ILabel("Ordered: ");
        dateLabel.setHorizontalAlignment(ILabel.RIGHT);
        dateLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        //datesPanel.add(dateLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        datesPanel.add(tbDateOrderedLbl, gbc);

        // Received date
        ILabel receivedLabel = new ILabel("Received: ");
        receivedLabel.setHorizontalAlignment(ILabel.RIGHT);
        receivedLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        //datesPanel.add(receivedLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        datesPanel.add(tbDateReceivedLbl, gbc);

        // Modified date
        ILabel modifiedLabel = new ILabel("Modified: ");
        modifiedLabel.setHorizontalAlignment(ILabel.RIGHT);
        modifiedLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        //datesPanel.add(modifiedLabel, gbc);

        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        datesPanel.add(tbDateModifiedLbl, gbc);

        // Add to toolbar
        bottomToolBar.add(datesPanel);
        bottomToolBar.add(Box.createHorizontalGlue());
        bottomToolBar.add(amountPanel);
    }

    private JPanel createOrderToolbar() {
        JPanel makeOrderPanel = new JPanel(new GridBagLayout());
        tbOrderFilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,20);

        // Distributor
        ILabel distributorLabel = new ILabel("Order by: ");
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        makeOrderPanel.add(distributorLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        makeOrderPanel.add(tbDistributorCb, gbc);

        // Order button
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        makeOrderPanel.add(tbOrderButton, gbc);

        // Order file
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        tbOrderFilePanel.add(tbViewOrderDetailsBtn, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        tbOrderFilePanel.add(tbSetOrderedBtn, gbc);

        // Create panel
        orderTbPanel = new JPanel(new BorderLayout());
        orderTbPanel.add(makeOrderPanel, BorderLayout.WEST);
        orderTbPanel.add(tbOrderFilePanel, BorderLayout.EAST);
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
        createNodes(rootNode);
        treeModel = new IDbObjectTreeModel(rootNode, IDbObjectTreeModel.TYPE_ORDERS);

        ordersTree = new ITree(treeModel);
        ordersTree.addTreeSelectionListener(this);
        treeModel.setTree(ordersTree);

        // Item table
        tableModel = new IOrderItemTableModel();
        itemTable = new ITable(tableModel);

        TableColumn tableColumn = itemTable.getColumnModel().getColumn(4);
        tableColumn.setCellEditor(new ITableEditors.SpinnerEditor() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner spinner = (JSpinner) e.getSource();
                if (selectedOrderItem != null) {
                    selectedOrderItem.setAmount((int) spinner.getValue());

                    SwingUtilities.invokeLater(() ->  {
                        selectedOrderItem.save();
                        tableModel.fireTableDataChanged();
                        updateToolBar(lastSelectedOrder);
                    });
                }
            }
        });

        itemTable.getSelectionModel().addListSelectionListener(this);
        itemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);

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

        tbDateOrderedLbl = new ILabel();
        tbDateOrderedLbl.setEnabled(false);
        tbDateOrderedLbl.setHorizontalAlignment(ILabel.LEFT);
        tbDateOrderedLbl.setVerticalAlignment(ILabel.CENTER);

        tbDateReceivedLbl = new ILabel();
        tbDateReceivedLbl.setEnabled(false);
        tbDateReceivedLbl.setHorizontalAlignment(ILabel.LEFT);
        tbDateReceivedLbl.setVerticalAlignment(ILabel.CENTER);

        tbDateModifiedLbl = new ILabel();
        tbDateModifiedLbl.setEnabled(false);
        tbDateModifiedLbl.setHorizontalAlignment(ILabel.LEFT);
        tbDateModifiedLbl.setVerticalAlignment(ILabel.CENTER);

        DefaultComboBoxModel<Distributor> distributorCbModel = new DefaultComboBoxModel<>();
        for (Distributor d : DbManager.db().getDistributors()) {
            distributorCbModel.addElement(d);
        }
        tbDistributorCb = new JComboBox<>(distributorCbModel);
        tbDistributorCb.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                if (lastSelectedOrder != null) {
                    Distributor d = (Distributor) tbDistributorCb.getSelectedItem();
                    if (lastSelectedOrder.getDistributor().getId() != d.getId()) {
                        lastSelectedOrder.setDistributor(d);
                        lastSelectedOrder.updateItemReferences();
                        lastSelectedOrder.save();
                        selectedOrderItem = null;
                        updateTable(lastSelectedOrder);
                    }
                }
            }
        });

        tbOrderButton = new JButton("Order!");

        tbSetOrderedBtn = new JButton("Set ordered");
        tbViewOrderDetailsBtn = new JButton("Order details");

        // Tool bars
        orderToolBar = new IdBToolBar(new IdBToolBar.IdbToolBarListener() {
            @Override
            public void onToolBarRefresh() {
                updateComponents(lastSelectedOrder);
            }

            @Override
            public void onToolBarAdd() {
                OrdersDialog dialog = new OrdersDialog(application, "New order", true);
                if (dialog.showDialog() == IDialog.OK) {
                    // Add order
                    Order o = dialog.getOrder();
                    o.save();
                }
            }

            @Override
            public void onToolBarDelete() {
                if (lastSelectedOrder != null) {
                    int res = JOptionPane.showConfirmDialog(OrderPanelLayout.this, "Are you sure you want to delete \"" + lastSelectedOrder.getName() + "\"?");
                    if (res == JOptionPane.OK_OPTION) {
                        lastSelectedOrder.delete();
                        lastSelectedOrder = null;
                    }
                }
            }

            @Override
            public void onToolBarEdit() {
                if (lastSelectedOrder != null) {
                    OrdersDialog dialog = new OrdersDialog(application, "New order", lastSelectedOrder);
                    if (dialog.showDialog() == IDialog.OK) {
                        // Add order
                        Order o = dialog.getOrder();
                        o.save();
                    }
                }
            }
        });
        orderToolBar.setFloatable(false);
        topToolBar = new TopToolBar(application, this);
        topToolBar.getContentPane().add(createOrderToolbar());

        // Create bottom toolbar
        bottomToolBar = new JToolBar(JToolBar.HORIZONTAL);
        bottomToolBar.setFloatable(false);
        bottomToolBar.setOpaque(false);
        bottomToolBar.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

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

        tablePanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        tablePanel.add(bottomToolBar, BorderLayout.PAGE_END);

        centerPanel.add(tablePanel, BorderLayout.CENTER);

        detailPanels.add(itemDetailPanel, BorderLayout.CENTER);
        detailPanels.add(orderItemDetailPanel, BorderLayout.EAST);
        detailPanels.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2,3,2,3),
                BorderFactory.createLineBorder(Color.GRAY, 1)
        ));

        centerPanel.add(detailPanels, BorderLayout.SOUTH);
        centerPanel.add(topToolBar, BorderLayout.PAGE_START);

        westPanel.add(new JScrollPane(ordersTree), BorderLayout.CENTER);
        westPanel.add(orderToolBar, BorderLayout.PAGE_END);

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
                if (lastSelectedOrder == null || !lastSelectedOrder.equals(object)) {
                    lastSelectedOrder = (Order) object;
                    updateTable(lastSelectedOrder);
                    updateToolBar(lastSelectedOrder);
                    selectedOrderItem = null;

                    // Search list
                    topToolBar.setSearchList(new ArrayList<>(lastSelectedOrder.getOrderItems()));
                }
            }

            //treeModel.expandNodes(0, ordersTree.getRowCount());

            updateVisibleComponents();
            updateEnabledComponents();

            // Update detail panel
            if (selectedOrderItem != null) {
                itemDetailPanel.updateComponents(selectedOrderItem.getItem());
                if (lastSelectedOrder != null && !lastSelectedOrder.isOrdered()) {
                    orderItemDetailPanel.updateComponents(selectedOrderItem);
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
