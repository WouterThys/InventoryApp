package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.IItemTableModel;
import com.waldo.inventory.gui.components.treemodels.IDbObjectTreeModel;
import com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel.ItemDetailPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.managers.SearchManager.sm;

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
    IDbObjectTreeModel<DbObject> treeModel;
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
    void updateEnabledComponents() {
        if (selectedItem == null || selectedItem.isUnknown() || !selectedItem.canBeSaved()) {
            topToolBar.setDeleteActionEnabled(false);
            topToolBar.setEditActionEnabled(false);
        } else {
            topToolBar.setDeleteActionEnabled(true);
            topToolBar.setEditActionEnabled(true);
        }
    }

    Item getItemAt(int row)  {
        return (Item) itemTable.getModel().getValueAt(itemTable.convertRowIndexToModel(row), 0);
        //return tableModel.getItem(row);
    }

    public void updateTable(DbObject selectedObject) {
        java.util.List<Item> itemList = new ArrayList<>();
        if (selectedObject == null || selectedObject.getName().equals("All")) {
            itemList = db().getItems();
            for (Item item : itemList) {
                if (item.getId() == DbObject.UNKNOWN_ID) {
                    itemList.remove(item);
                    break;
                }
            }
        } else {
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
        }
        tableModel.setItemList(itemList);
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

//    public void scrollToVisible() {
//        if (selectedItem != null) {
//            List<Item> itemList = tableModel.getItemList();
//            if (itemList != null) {
//                int ndx = itemList.indexOf(selectedItem);
//                if (ndx >= 0 && ndx < itemList.size()) {
//                    itemTable.scrollRectToVisible(new Rectangle(itemTable.getCellRect(ndx, 0, true)));
//                }
//            }
//        }
//    }

    private void createNodes(DefaultMutableTreeNode rootNode) {
        for (Category category : db().getCategories()) {
            if (!category.isUnknown()) {
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
    }

    void recreateNodes() {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        rootNode.removeAllChildren();
        createNodes(rootNode);
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
        treeModel = new IDbObjectTreeModel<>(rootNode, (rootNode1, child) -> {
            switch (DbObject.getType(child)) {
                case DbObject.TYPE_CATEGORY:
                    return rootNode;

                case DbObject.TYPE_PRODUCT: {
                    Product p = (Product) child;
                    Category c = sm().findCategoryById(p.getCategoryId()); // The parent object
                    return treeModel.findNode(c);
                }

                case DbObject.TYPE_TYPE: {
                    Type t = (Type) child;
                    Product p = sm().findProductById(t.getProductId()); // The parent object
                    return treeModel.findNode(p);
                }
            }
            return null;
        });

        subDivisionTree = new ITree(treeModel);
        subDivisionTree.addTreeSelectionListener(this);
        subDivisionTree.setExpandsSelectedPaths(true);
        subDivisionTree.setScrollsOnExpand(true);
        treeModel.setTree(subDivisionTree);

        // Tool bar
        topToolBar = new TopToolBar(application, this);

        // Item table
        tableModel = new IItemTableModel();
        itemTable = new ITable<>(tableModel);
        itemTable.getSelectionModel().addListSelectionListener(this);
        itemTable.setDefaultRenderer(ILabel.class, new ITableEditors.AmountRenderer());
        itemTable.setOpaque(true);
        updateTable(null);

        // Details
        detailPanel = new ItemDetailPanel(application);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        //subDivisionTree.setPreferredSize(new Dimension(300,200));
        JScrollPane pane = new JScrollPane(subDivisionTree);
        pane.setMinimumSize(new Dimension(200, 200));
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.add(pane);

        // Panel them together
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        panel.add(detailPanel, BorderLayout.SOUTH);
        panel.add(topToolBar, BorderLayout.PAGE_START);

        // Add
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pane, panel);
        splitPane.setOneTouchExpandable(true);

        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {
        application.beginWait();
        try {
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
