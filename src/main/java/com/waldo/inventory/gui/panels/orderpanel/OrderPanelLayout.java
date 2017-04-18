package com.waldo.inventory.gui.panels.orderpanel;

import com.sun.org.apache.xpath.internal.operations.Or;
import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.panels.itemdetailpanel.ItemDetailPanel;
import com.waldo.inventory.gui.panels.mainpanel.MainPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.net.URL;
import java.text.SimpleDateFormat;

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
    IItemTableModel tableModel;

    ITree ordersTree;
    IDbObjectTreeModel treeModel;
    private ItemDetailPanel itemDetailPanel;

    private ILabel toolbarDateOrdered;
    private ILabel toolbarDateReceived;
    private ILabel toolbarDateModified;
    private DefaultComboBoxModel<Distributor> distributorCbModel;
    private JComboBox<Distributor> toolbarDistributorCb;
    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyyy hh:mm");
    ResourceManager resourceManager;
    Application application;

    Item selectedItem;
    Order lastSelectedOrder;
    IdBToolBar orderToolBar;
    TopToolBar topToolBar;

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

    public void updateTable(DbObject selectedObject) {
        if (selectedObject == null || selectedObject.getName().equals("All")) {
            tableModel.setItemList(db().getOrderedItems(-1));
        } else {
//            switch (DbObject.getType(selectedObject)) {
//                case DbObject.TYPE_CATEGORY:
//                    Category c = (Category)selectedObject;
//                    tableModel.setItemList(db().getItemListForCategory(c));
//                    break;
//                case DbObject.TYPE_PRODUCT:
//                    Product p = (Product)selectedObject;
//                    tableModel.setItemList(db().getItemListForProduct(p));
//                    break;
//                case DbObject.TYPE_TYPE:
//                    Type t = (Type)selectedObject;
//                    tableModel.setItemList(db().getItemListForType(t));
//                    break;
//                default:
//                    break;
//            }
        }
    }

    public void updateToolBar(Order order) {
        if (order.getDateOrdered() != null) {
            toolbarDateOrdered.setText(sdf.format(order.getDateOrdered()));
        } else {
            toolbarDateOrdered.setText("Not ordered yet");
        }

        if (order.getDateReceived() != null) {
            toolbarDateReceived.setText(sdf.format(order.getDateReceived()));
        } else {
            toolbarDateReceived.setText("Not received yet");
        }

        if (order.getDateModified() != null) {
            toolbarDateModified.setText(sdf.format(order.getDateModified()));
        } else {
            toolbarDateModified.setText(" / ");
        }

        if (order.getDistributor() != null) {
            toolbarDistributorCb.setSelectedItem(order.getDistributor());
        }
    }

    private void createNodes(DefaultMutableTreeNode rootNode) {
        for (Order o : db().getOrders()) {
            if (o.getId() != DbObject.UNKNOWN_ID) {
                DefaultMutableTreeNode oNode = new DefaultMutableTreeNode(o); // Should be ordered on date
                rootNode.add(oNode);
            }
        }
    }

    private JPanel createOrderToolbar() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Order date
        ILabel dateLabel = new ILabel("Date ordered: ");
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(dateLabel, gbc);

        toolbarDateOrdered = new ILabel();
        toolbarDateOrdered.setEnabled(false);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(toolbarDateOrdered, gbc);

        // Received date
        ILabel receivedLabel = new ILabel("Date received: ");
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy =1 ; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(receivedLabel, gbc);

        toolbarDateReceived = new ILabel();
        toolbarDateReceived.setEnabled(false);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(toolbarDateReceived, gbc);

        // Modified date
        ILabel modifiedLabel = new ILabel("Date modified: ");
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy =2 ; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(modifiedLabel, gbc);

        toolbarDateModified = new ILabel();
        toolbarDateModified.setEnabled(false);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(toolbarDateModified, gbc);

        // Distributor
        ILabel distributorLabel = new ILabel("Order by: ");
        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(distributorLabel, gbc);

        distributorCbModel = new DefaultComboBoxModel<>();
        for (Distributor d : DbManager.db().getDistributors()) {
            distributorCbModel.addElement(d);
        }
        toolbarDistributorCb = new JComboBox<>(distributorCbModel);
        gbc.gridx = 3; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(toolbarDistributorCb, gbc);


        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Sub division tree
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new Order("All"), true);
        createNodes(rootNode);
        treeModel = new IDbObjectTreeModel(rootNode, IDbObjectTreeModel.TYPE_ORDERS);

        ordersTree = new ITree(treeModel);
        ordersTree.addTreeSelectionListener(this);
        treeModel.setTree(ordersTree);

        // Tool bars
        orderToolBar = new IdBToolBar(new IdBToolBar.IdbToolBarListener() {
            @Override
            public void onToolBarRefresh() {
                updateComponents(null);
            }

            @Override
            public void onToolBarAdd() {
                // Add order
            }

            @Override
            public void onToolBarDelete() {
                // Delete order
            }

            @Override
            public void onToolBarEdit() {
                // Edit order (if possible?)
            }
        });
        orderToolBar.setFloatable(false);
        topToolBar = new TopToolBar(application, this);
        topToolBar.getContentPane().add(createOrderToolbar());

        // Item table
        tableModel = new IItemTableModel(db().getItems());
        itemTable = new ITable(tableModel);
        itemTable.getSelectionModel().addListSelectionListener(this);
        itemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);

        // Details
        itemDetailPanel = new ItemDetailPanel();
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
    public void updateComponents(Object object) {
        // Update table if needed
        if (object != null) {
            updateTable((DbObject) object);

            updateToolBar((Order) object);
        }

        // Update detail panel
        itemDetailPanel.updateComponents(selectedItem);
    }
}
