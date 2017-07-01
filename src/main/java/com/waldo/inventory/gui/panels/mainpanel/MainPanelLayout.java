package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel.ItemDetailPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public abstract class MainPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITable itemTable;
    IItemTableModel tableModel;

    ITree subDivisionTree;
    IDbObjectTreeModel treeModel;
    ItemDetailPanel detailPanel;
    TopToolBar topToolBar;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;

    Item selectedItem;
    DbObject lastSelectedDivision;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    MainPanelLayout(Application application) {
        this.application = application;
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        if (selectedItem == null || selectedItem.isUnknown() || !selectedItem.canBeSaved()) {
            topToolBar.setDeleteActionEnabled(false);
            topToolBar.setEditActionEnabled(false);
        } else {
            topToolBar.setDeleteActionEnabled(true);
            topToolBar.setEditActionEnabled(true);
        }
    }

    Item getItemAt(int row)  {
        return tableModel.getItem(row);
    }

    public void updateTable(DbObject selectedObject) {
        if (selectedObject == null || selectedObject.getName().equals("All")) {
            tableModel.setItemList(db().getItems());
        } else {
            java.util.List<Item> itemList = new ArrayList<>();
            switch (DbObject.getType(selectedObject)) {
                case DbObject.TYPE_CATEGORY:
                    Category c = (Category)selectedObject;
                    itemList = db().getItemListForCategory(c);
                    break;
                case DbObject.TYPE_PRODUCT:
                    Product p = (Product)selectedObject;
                    itemList = db().getItemListForProduct(p);
                    break;
                case DbObject.TYPE_TYPE:
                    Type t = (Type)selectedObject;
                    itemList = db().getItemListForType(t);
                    break;
                default:
                    break;
            }

            tableModel.setItemList(itemList);
        }
    }

    public void selectItem(Item selectedItem) {
        if (selectedItem != null) {
            List<Item> itemList = tableModel.getItemList();
            if (itemList != null) {
                int ndx = itemList.indexOf(selectedItem);
                if (ndx >= 0 && ndx < itemList.size()) {
                    itemTable.setRowSelectionInterval(ndx, ndx);
                    itemTable.scrollRectToVisible(new Rectangle(itemTable.getCellRect(ndx, 0, true)));
                }
            }
        }
    }

    private void createNodes(DefaultMutableTreeNode rootNode) {
        for (Category category : db().getCategories()) {
            DefaultMutableTreeNode cNode = new DefaultMutableTreeNode(category, true);
            rootNode.add(cNode);

            for (Product product : db().getProductListForCategory(category.getId())) {
                DefaultMutableTreeNode pNode = new DefaultMutableTreeNode(product, true);
                cNode.add(pNode);

                for (Type type : db().getTypeListForProduct(product.getId())) {
                    DefaultMutableTreeNode tNode = new DefaultMutableTreeNode(type, false);
                    pNode.add(tNode);
                }
            }
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Sub division tree
        Category virtualRoot = new Category("All");
        virtualRoot.setCanBeSaved(false);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(virtualRoot, true);
        createNodes(rootNode);
        treeModel = new IDbObjectTreeModel(rootNode, IDbObjectTreeModel.TYPE_DIVISIONS);

        subDivisionTree = new ITree(treeModel);
        subDivisionTree.addTreeSelectionListener(this);
        subDivisionTree.setExpandsSelectedPaths(true);
        subDivisionTree.setScrollsOnExpand(true);
        treeModel.setTree(subDivisionTree);

        // Tool bar
        topToolBar = new TopToolBar(application, this);

        // Item table
        tableModel = new IItemTableModel(db().getItems());
        itemTable = new ITable(tableModel);
        itemTable.getSelectionModel().addListSelectionListener(this);
        itemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);
        itemTable.setDefaultRenderer(ILabel.class, new ITableEditors.AmountRenderer());
        itemTable.setOpaque(true);

        // Details
        detailPanel = new ItemDetailPanel(application);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        subDivisionTree.setPreferredSize(new Dimension(300,200));
        JScrollPane pane = new JScrollPane(subDivisionTree);

        // Panel them together
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        panel.add(detailPanel, BorderLayout.SOUTH);
        panel.add(topToolBar, BorderLayout.PAGE_START);

        // Add
        add(pane, BorderLayout.WEST);
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {
        try {
            application.beginWait();

            // Update table if needed
            if (object != null) {
                if (lastSelectedDivision == null || !lastSelectedDivision.equals(object)) {
                    lastSelectedDivision = (DbObject) object;
                    updateTable((DbObject) object);
                }
            }

            // Enabled components
            updateEnabledComponents();

            // Update detail panel
            detailPanel.updateComponents(selectedItem);
        } finally {
            application.endWait();
        }
    }
}
