package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDbObjectTreeModel;
import com.waldo.inventory.gui.components.IItemTableModel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.inventory.gui.panels.mainpanel.detailpanel.MainDetailPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

import static com.waldo.inventory.database.DbManager.db;

public abstract class OrderPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        ListSelectionListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITable itemTable;
    IItemTableModel tableModel;

    ITree ordersTree;
    IDbObjectTreeModel treeModel;
    private MainDetailPanel detailPanel;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ResourceManager resourceManager;

    Item selectedItem;
    DbObject lastSelectedOrder;

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

    private void createNodes(DefaultMutableTreeNode rootNode) {
        for (Order o : db().getOrders()) {
            if (o.getId() != DbObject.UNKNOWN_ID) {
                DefaultMutableTreeNode oNode = new DefaultMutableTreeNode(o); // Should be ordered on date
                rootNode.add(oNode);
            }
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Sub division tree
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new Category("All"), true);
        createNodes(rootNode);
        treeModel = new IDbObjectTreeModel(rootNode, IDbObjectTreeModel.TYPE_ORDERS);

        ordersTree = new ITree(treeModel);
        ordersTree.addTreeSelectionListener(this);
        treeModel.setTree(ordersTree);

        // Item table
        tableModel = new IItemTableModel(db().getItems());
        itemTable = new ITable(tableModel);
        itemTable.getSelectionModel().addListSelectionListener(this);
        //itemTable.addFocusListener(this);
        itemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);

        // Details
        detailPanel = new MainDetailPanel();
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Panel them together
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        panel.add(detailPanel, BorderLayout.SOUTH);

        // Add
        add(new JScrollPane(ordersTree), BorderLayout.WEST);
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {
        // Update table if needed
        itemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);
        if (object != null) {
            updateTable((DbObject) object);
        }

        // Update detail panel
        detailPanel.updateComponents(selectedItem);
    }
}
