package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialog;
import com.waldo.inventory.gui.panels.itemdetailpanel.ItemDetailPanel;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import static com.waldo.inventory.database.DbManager.db;

public abstract class OrderPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        ActionListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITable itemTable;
    IOrderItemTableModel tableModel;

    ITree ordersTree;
    IDbObjectTreeModel treeModel;
    ItemDetailPanel itemDetailPanel;

    private ILabel toolbarTotalItemsLbl;
    private ILabel toolbarTotalPriceLbl;
    private ILabel toolbarDateOrderedLbl;
    private ILabel toolbarDateReceivedLbl;
    private ILabel toolbarDateModifiedLbl;
    private JButton toolbarOrderButton;
    private JComboBox<Distributor> toolbarDistributorCb;
    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static final SimpleDateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyy-MM-dd");
    Application application;

    OrderItem selectedItem;
    Order lastSelectedOrder;
    private IdBToolBar orderToolBar;
    private TopToolBar topToolBar;
    private JPanel orderTbPanel;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public OrderPanelLayout(Application application) {
        this.application = application;
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void updateTable(Order selectedOrder) {
        if (selectedOrder != null && !selectedOrder.getName().equals("All")) {
            selectedOrder.updateItemReferences();
            tableModel.setItemList(db().getOrderedItems(selectedOrder.getId()));
        }
    }

    private void updateToolBar(Order order) {
        toolbarTotalItemsLbl.setText(String.valueOf(order.getOrderItems().size()));
        toolbarTotalPriceLbl.setText(String.valueOf(order.getTotalPrice()));

        if (order.getDateOrdered() != null) {
            toolbarDateOrderedLbl.setText(dateFormatShort.format(order.getDateOrdered()));
        } else {
            toolbarDateOrderedLbl.setText("Not ordered yet");
        }

        if (order.getDateReceived() != null) {
            toolbarDateReceivedLbl.setText(dateFormatShort.format(order.getDateReceived()));
        } else {
            toolbarDateReceivedLbl.setText("Not received yet");
        }

        if (order.getDateModified() != null) {
            toolbarDateModifiedLbl.setText(dateFormatLong.format(order.getDateModified()));
        } else {
            toolbarDateModifiedLbl.setText(" / ");
        }

        if (order.getDistributor() != null) {
            toolbarDistributorCb.setSelectedItem(order.getDistributor());
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

    private void updateEnabledComponents() {
        // Orders
        if (lastSelectedOrder == null || lastSelectedOrder.isUnknown() || !lastSelectedOrder.canBeSaved()) {
            orderToolBar.setEditActionEnabled(false);
            orderToolBar.setDeleteActionEnabled(false);
            topToolBar.setAddActionEnabled(false);
            topToolBar.setRefreshActionEnabled(false);
            toolbarOrderButton.setEnabled(false);
            toolbarDistributorCb.setEnabled(false);
        } else {
            orderToolBar.setEditActionEnabled(true);
            orderToolBar.setDeleteActionEnabled(true);
            topToolBar.setAddActionEnabled(true);
            topToolBar.setRefreshActionEnabled(true);
            toolbarDistributorCb.setEnabled(true);
        }

        toolbarOrderButton.setEnabled(tableModel.getRowCount() > 0);

        // Items
        if (selectedItem == null) {
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
        } else {
            orderTbPanel.setVisible(false);
        }
    }

    private JPanel createOrderToolbar() {
        JPanel westPanel = new JPanel(new GridBagLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel eastPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        // Total items
        ILabel itemCntLabel = new ILabel("Item count: ");
        itemCntLabel.setHorizontalAlignment(ILabel.RIGHT);
        itemCntLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        westPanel.add(itemCntLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,20);
        westPanel.add(toolbarTotalItemsLbl, gbc);

        // Total price
        ILabel totalPriceLabel = new ILabel("Total price: ");
        itemCntLabel.setHorizontalAlignment(ILabel.RIGHT);
        itemCntLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        westPanel.add(totalPriceLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,20);
        westPanel.add(toolbarTotalPriceLbl, gbc);

        // Order date
        ILabel dateLabel = new ILabel("Ordered: ");
        dateLabel.setHorizontalAlignment(ILabel.RIGHT);
        dateLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(dateLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,20);
        centerPanel.add(toolbarDateOrderedLbl, gbc);

        // Received date
        ILabel receivedLabel = new ILabel("Date received: ");
        receivedLabel.setHorizontalAlignment(ILabel.RIGHT);
        receivedLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy =1 ; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,2);
        centerPanel.add(receivedLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,20);
        centerPanel.add(toolbarDateReceivedLbl, gbc);

        // Modified date
        ILabel modifiedLabel = new ILabel("Date modified: ");
        modifiedLabel.setHorizontalAlignment(ILabel.RIGHT);
        modifiedLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy =2 ; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,2);
        centerPanel.add(modifiedLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,20);
        centerPanel.add(toolbarDateModifiedLbl, gbc);

        // Distributor
        ILabel distributorLabel = new ILabel("Order by: ");
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        eastPanel.add(distributorLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        eastPanel.add(toolbarDistributorCb, gbc);

        // Order button
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        eastPanel.add(toolbarOrderButton, gbc);

        // Create panel
        orderTbPanel = new JPanel(new BorderLayout());
        orderTbPanel.add(westPanel, BorderLayout.WEST);
        orderTbPanel.add(centerPanel, BorderLayout.CENTER);
        orderTbPanel.add(eastPanel, BorderLayout.EAST);
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
                if (selectedItem != null) {
                    selectedItem.setAmount((int) spinner.getValue());

                    SwingUtilities.invokeLater(() ->  {
                        selectedItem.save();
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

        // Tool bar
        toolbarTotalPriceLbl = new ILabel();
        toolbarTotalPriceLbl.setEnabled(false);
        toolbarTotalPriceLbl.setHorizontalAlignment(ILabel.LEFT);
        toolbarTotalPriceLbl.setVerticalAlignment(ILabel.CENTER);

        toolbarTotalItemsLbl = new ILabel();
        toolbarTotalItemsLbl.setEnabled(false);
        toolbarTotalItemsLbl.setHorizontalAlignment(ILabel.LEFT);
        toolbarTotalItemsLbl.setVerticalAlignment(ILabel.CENTER);

        toolbarDateOrderedLbl = new ILabel();
        toolbarDateOrderedLbl.setEnabled(false);
        toolbarDateOrderedLbl.setHorizontalAlignment(ILabel.LEFT);
        toolbarDateOrderedLbl.setVerticalAlignment(ILabel.CENTER);

        toolbarDateReceivedLbl = new ILabel();
        toolbarDateReceivedLbl.setEnabled(false);
        toolbarDateReceivedLbl.setHorizontalAlignment(ILabel.LEFT);
        toolbarDateReceivedLbl.setVerticalAlignment(ILabel.CENTER);

        toolbarDateModifiedLbl = new ILabel();
        toolbarDateModifiedLbl.setEnabled(false);
        toolbarDateModifiedLbl.setHorizontalAlignment(ILabel.LEFT);
        toolbarDateModifiedLbl.setVerticalAlignment(ILabel.CENTER);

        DefaultComboBoxModel<Distributor> distributorCbModel = new DefaultComboBoxModel<>();
        for (Distributor d : DbManager.db().getDistributors()) {
            distributorCbModel.addElement(d);
        }
        toolbarDistributorCb = new JComboBox<>(distributorCbModel);
//        toolbarDistributorCb.addItemListener(event -> {
//            if (event.getStateChange() == ItemEvent.SELECTED) {
//                if (lastSelectedOrder != null) {
//                    lastSelectedOrder.setDistributor((Distributor) toolbarDistributorCb.getSelectedItem());
//                    lastSelectedOrder.updateItemReferences();
//                    SwingUtilities.invokeLater(() -> {
//                        lastSelectedOrder.save();
//                        tableModel.fireTableDataChanged();
//                    });
//                }
//            }
//        });

        toolbarOrderButton = new JButton("Order!");
        toolbarOrderButton.addActionListener(this);

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
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Panel them together
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        centerPanel.add(itemDetailPanel, BorderLayout.SOUTH);
        centerPanel.add(topToolBar, BorderLayout.PAGE_START);

        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.add(new JScrollPane(ordersTree), BorderLayout.CENTER);
        westPanel.add(orderToolBar, BorderLayout.PAGE_END);

        // Add
        add(westPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) { // Has last selected order
        // Update table if needed
        if (object != null) {
            if (lastSelectedOrder == null || !lastSelectedOrder.equals(object)) {
                lastSelectedOrder = (Order) object;
                updateTable(lastSelectedOrder);
                updateToolBar(lastSelectedOrder);
            }
        }

        treeModel.expandNodes(0, ordersTree.getRowCount());

        updateVisibleComponents();
        updateEnabledComponents();

        // Update detail panel
        if (selectedItem != null) {
            itemDetailPanel.updateComponents(selectedItem.getItem());
        } else {
            itemDetailPanel.updateComponents(null);
        }
    }
}
