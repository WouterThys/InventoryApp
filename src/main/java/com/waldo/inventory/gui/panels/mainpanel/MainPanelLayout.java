package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ITableEditors;
import com.waldo.inventory.gui.components.ITablePanel;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.tablemodels.IItemTableModel;
import com.waldo.inventory.gui.components.treemodels.IDbObjectTreeModel;
import com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel.ItemDetailPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public abstract class MainPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITablePanel<Item> itemTable;
    IItemTableModel tableModel;

    ITree subDivisionTree;
    IDbObjectTreeModel<DbObject> treeModel;
    ItemDetailPanel detailPanel;

    AbstractAction treeAddDivision;
    AbstractAction treeEditDivision;
    AbstractAction treeDeleteDivision;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;

    Item selectedItem;
    DbObject selectedDivision;

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
        boolean enabled =  !(selectedItem == null || selectedItem.isUnknown() || !selectedItem.canBeSaved());
        itemTable.setDbToolBarEditDeleteEnabled(enabled);
    }

    abstract void onTreeRightClick(MouseEvent e);

    abstract void onAddDivision();
    abstract void onEditDivision();
    abstract void onDeleteDivision();

    //
    // Table stuff
    //
    public void tableInitialize(DbObject selectedObject) {
        java.util.List<Item> itemList = new ArrayList<>();
        if (selectedObject == null || selectedObject.getName().equals("All")) {
            itemList = new ArrayList<>(cache().getItems());
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
                    itemList = sm().findItemListForCategory(c);
                    break;
                case DbObject.TYPE_PRODUCT:
                    Product p = (Product)selectedObject;
                    itemList = sm().findItemListForProduct(p);
                    break;
                case DbObject.TYPE_TYPE:
                    Type t = (Type)selectedObject;
                    itemList = sm().findItemListForType(t);
                    break;
                default:
                    break;
            }
        }
        tableModel.setItemList(itemList);
    }

    long tableUpdate() {
        long itemId = -1;
        if (selectedItem != null) {
            itemId = selectedItem.getId();
        }
        tableModel.updateTable();
        return itemId;
    }

    void tableRemoveItem(Item item) {
        tableModel.removeItem(item);
    }

    void tableAddItem(Item item) {
        tableModel.addItem(item);
    }

    public void tableSelectItem(Item item) {
        this.selectedItem = item;
        itemTable.selectItem(item);
    }
    //
    // Tree stuff
    //
    void treeSelectDivisionForItem(Item item) {
        if (item.getTypeId() > DbObject.UNKNOWN_ID) {
            selectedDivision = sm().findTypeById(item.getTypeId());
        } else {
            if (item.getProductId() > DbObject.UNKNOWN_ID) {
                selectedDivision = sm().findProductById(item.getProductId());
            } else {
                if (item.getCategoryId() > DbObject.UNKNOWN_ID) {
                    selectedDivision = sm().findCategoryById(item.getCategoryId());
                } else {
                    selectedDivision = null;
                }
            }
        }
        treeModel.setSelectedObject(selectedDivision);
    }

    private void createNodes(DefaultMutableTreeNode rootNode) {
        for (Category category : cache().getCategories()) {
            if (!category.isUnknown()) {
                DefaultMutableTreeNode cNode = new DefaultMutableTreeNode(category, true);
                rootNode.add(cNode);

                for (Product product : sm().findProductListForCategory(category.getId())) {
                    DefaultMutableTreeNode pNode = new DefaultMutableTreeNode(product, true);
                    cNode.add(pNode);

                    for (Type type : sm().findTypeListForProduct(product.getId())) {
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
        subDivisionTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = subDivisionTree.getClosestRowForLocation(e.getX(), e.getY());
                    subDivisionTree.setSelectionRow(row);
                    onTreeRightClick(e);
                }
            }
        });
        treeModel.setTree(subDivisionTree);

        // Tree actions
        treeAddDivision = new AbstractAction("Add", imageResource.readImage("Items.Tree.Add")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddDivision();
            }
        };
        treeEditDivision = new AbstractAction("Edit", imageResource.readImage("Items.Tree.Edit")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditDivision();
            }
        };
        treeDeleteDivision = new AbstractAction("Delete", imageResource.readImage("Items.Tree.Delete")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteDivision();
            }
        };

        // Item table
        tableModel = new IItemTableModel();
        itemTable = new ITablePanel<>(tableModel, this, new ITableEditors.AmountRenderer());
        itemTable.setExactColumnWidth(0, 36);
        itemTable.setDbToolBar(this);
        tableInitialize(null);

        // Details
        detailPanel = new ItemDetailPanel(application);

        // Preview
        //previewPanel = new ItemPreviewPanel(application);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JScrollPane pane = new JScrollPane(subDivisionTree);
        pane.setMinimumSize(new Dimension(200, 200));
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.add(pane);

//
        // Panel them together
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        panel.add(detailPanel, BorderLayout.SOUTH);
        //panel.add(previewPanel, BorderLayout.EAST);

        // Add
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pane, panel);
        splitPane.setOneTouchExpandable(true);

        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... object) {
        application.beginWait();
        try {
            // Update table if needed
            if (object.length != 0 && object[0] != null) {
                if (selectedDivision == null || !selectedDivision.equals(object[0])) {
                    selectedDivision = (DbObject) object[0];
                    tableInitialize((DbObject) object[0]);
                }
            }

            // Enabled components
            updateEnabledComponents();

            // Update detail panel
            detailPanel.updateComponents(selectedItem);
            //previewPanel.updateComponents(selectedItem);
        } finally {
            application.endWait();
        }
    }
}
