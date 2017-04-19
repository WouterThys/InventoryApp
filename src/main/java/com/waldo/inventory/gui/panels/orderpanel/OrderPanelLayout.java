package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialog;
import com.waldo.inventory.gui.panels.itemdetailpanel.ItemDetailPanel;
import com.waldo.inventory.gui.panels.mainpanel.MainPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
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
    IItemTableModel tableModel;

    ITree ordersTree;
    IDbObjectTreeModel treeModel;
    private ItemDetailPanel itemDetailPanel;

    private ILabel toolbarDateOrdered;
    private ILabel toolbarDateReceived;
    private ILabel toolbarDateModified;
    private JButton toolbarOrderButton;
    private JComboBox<Distributor> toolbarDistributorCb;
    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static final SimpleDateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyy-MM-dd");
    ResourceManager resourceManager;
    Application application;

    Item selectedItem;
    Order lastSelectedOrder;
    IdBToolBar orderToolBar;
    TopToolBar topToolBar;
    JPanel orderTbPanel;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public OrderPanelLayout(Application application) {
        this.application = application;

        URL url = MainPanel.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Item getItemAt(int row)  {
        return tableModel.getItem(row);
    }

    public void updateTable(Order selectedOrder) {
        if (selectedOrder != null && !selectedOrder.getName().equals("All")) {
            tableModel.setItemList(db().getOrderedItems(selectedOrder.getId()));
        }
    }

    public void updateToolBar(Order order) {
        if (order.getDateOrdered() != null) {
            toolbarDateOrdered.setText(dateFormatShort.format(order.getDateOrdered()));
        } else {
            toolbarDateOrdered.setText("Not ordered yet");
        }

        if (order.getDateReceived() != null) {
            toolbarDateReceived.setText(dateFormatShort.format(order.getDateReceived()));
        } else {
            toolbarDateReceived.setText("Not received yet");
        }

        if (order.getDateModified() != null) {
            toolbarDateModified.setText(dateFormatLong.format(order.getDateModified()));
        } else {
            toolbarDateModified.setText(" / ");
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
        if (lastSelectedOrder == null || lastSelectedOrder.isUnknown() || !lastSelectedOrder.canBeSaved() || lastSelectedOrder.isOrdered()) {
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
            toolbarOrderButton.setEnabled(tableModel.getRowCount() > 0);
            toolbarDistributorCb.setEnabled(true);
        }

        // Items
        if (selectedItem == null) {
            topToolBar.setEditActionEnabled(false);
            topToolBar.setDeleteActionEnabled(false);
        } else {
            topToolBar.setEditActionEnabled(true);
            topToolBar.setDeleteActionEnabled(true);
        }

        topToolBar.setSearchEnabled(tableModel.getRowCount() > 0);

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
        JPanel eastPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        // Order date
        ILabel dateLabel = new ILabel("Date ordered: ");
        dateLabel.setHorizontalAlignment(ILabel.RIGHT);
        dateLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        westPanel.add(dateLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,20);
        westPanel.add(toolbarDateOrdered, gbc);

        // Received date
        ILabel receivedLabel = new ILabel("Date received: ");
        receivedLabel.setHorizontalAlignment(ILabel.RIGHT);
        receivedLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy =1 ; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,2);
        westPanel.add(receivedLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,20);
        westPanel.add(toolbarDateReceived, gbc);

        // Modified date
        ILabel modifiedLabel = new ILabel("Date modified: ");
        modifiedLabel.setHorizontalAlignment(ILabel.RIGHT);
        modifiedLabel.setVerticalAlignment(ILabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy =2 ; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,2);
        westPanel.add(modifiedLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,20);
        westPanel.add(toolbarDateModified, gbc);

        // Distributor
        ILabel distributorLabel = new ILabel("Order by: ");
        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        eastPanel.add(distributorLabel, gbc);

        gbc.gridx = 3; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        eastPanel.add(toolbarDistributorCb, gbc);

        // Order button
        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        eastPanel.add(toolbarOrderButton, gbc);

        // Create panel
        orderTbPanel = new JPanel(new BorderLayout());
        orderTbPanel.add(westPanel, BorderLayout.WEST);
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
        tableModel = new IItemTableModel();
        itemTable = new ITable(tableModel);
        itemTable.getSelectionModel().addListSelectionListener(this);
        itemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);

        // Details
        itemDetailPanel = new ItemDetailPanel(application);

        // Tool bar
        toolbarDateOrdered = new ILabel();
        toolbarDateOrdered.setEnabled(false);
        toolbarDateOrdered.setHorizontalAlignment(ILabel.LEFT);
        toolbarDateOrdered.setVerticalAlignment(ILabel.CENTER);

        toolbarDateReceived = new ILabel();
        toolbarDateReceived.setEnabled(false);
        toolbarDateReceived.setHorizontalAlignment(ILabel.LEFT);
        toolbarDateReceived.setVerticalAlignment(ILabel.CENTER);

        toolbarDateModified = new ILabel();
        toolbarDateModified.setEnabled(false);
        toolbarDateModified.setHorizontalAlignment(ILabel.LEFT);
        toolbarDateModified.setVerticalAlignment(ILabel.CENTER);

        DefaultComboBoxModel<Distributor> distributorCbModel = new DefaultComboBoxModel<>();
        for (Distributor d : DbManager.db().getDistributors()) {
            distributorCbModel.addElement(d);
        }
        toolbarDistributorCb = new JComboBox<>(distributorCbModel);

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
                // Edit order (if possible?)
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
        lastSelectedOrder = (Order) object;
        if (lastSelectedOrder != null) {
            updateTable(lastSelectedOrder);
            updateToolBar(lastSelectedOrder);
        }

        treeModel.expandNodes(0, ordersTree.getRowCount());

        updateVisibleComponents();
        updateEnabledComponents();

        // Update detail panel
        itemDetailPanel.updateComponents(selectedItem);
    }
}
