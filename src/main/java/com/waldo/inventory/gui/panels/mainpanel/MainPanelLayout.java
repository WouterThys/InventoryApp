package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ITablePanel;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.popups.TableOptionsPopup;
import com.waldo.inventory.gui.components.tablemodels.IItemTableModel;
import com.waldo.inventory.gui.components.treemodels.IDbObjectTreeModel;
import com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel.ItemDetailPanel;
import com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel.ItemDetailPanelLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

abstract class MainPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        ItemDetailPanelLayout.OnItemDetailListener,
        TableOptionsPopup.TableOptionsListener {

    private static final String TREE_ITEMS = "Items";
    private static final String TREE_SETS = "Sets";

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITablePanel<Item> itemTable;
    IItemTableModel tableModel;

    ITree selectionTree;
    IDbObjectTreeModel<DbObject> treeModel;
    IdBToolBar selectionTb;

    ItemDetailPanel detailPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final Application application;

    Item selectedItem;
    DbObject selectedDivision; // Category, Product, Type or Set

    private final Category invisibleRoot = new Category("");
    private final Item itemRoot = new Item(TREE_ITEMS);
    private final Set setRoot = new Set(TREE_SETS);
    private final DefaultMutableTreeNode iRoot = new DefaultMutableTreeNode(itemRoot, true);
    private final DefaultMutableTreeNode sRoot = new DefaultMutableTreeNode(setRoot, true);

    boolean showSets = true;
    boolean showSetItems = false;

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

        // Divisions
        enabled = selectedDivision != null && selectedDivision.canBeSaved();
        selectionTb.setEditActionEnabled(enabled);
        selectionTb.setDeleteActionEnabled(enabled);
        if (enabled) {
            if (DbObject.getType(selectedDivision) == DbObject.TYPE_TYPE) {
                selectionTb.setAddActionEnabled(false);
            } else {
                selectionTb.setAddActionEnabled(true);
            }
        }
    }

    abstract void onTreeRightClick(MouseEvent e);
    abstract void onTableRowClicked(MouseEvent e);

    //
    // Table stuff
    //
    public void tableInitialize(DbObject selectedObject) {
        java.util.List<Item> itemList = new ArrayList<>();
        if (selectedObject == null || selectedObject.getName().equals(TREE_ITEMS)) {
            itemList = filterItems(cache().getItems(), showSets, showSetItems);
        } else if (selectedObject.getName().equals(TREE_SETS)) {
            for (Set set : cache().getSets()) {
                itemList.addAll(set.getSetItems());
            }
        } else {
            switch (DbObject.getType(selectedObject)) {
                case DbObject.TYPE_CATEGORY:
                    Category c = (Category)selectedObject;
                    itemList = filterItems(sm().findItemListForCategory(c), showSets, showSetItems);
                    break;
                case DbObject.TYPE_PRODUCT:
                    Product p = (Product)selectedObject;
                    itemList = filterItems(sm().findItemListForProduct(p), showSets, showSetItems);
                    break;
                case DbObject.TYPE_TYPE:
                    Type t = (Type)selectedObject;
                    itemList = filterItems(sm().findItemListForType(t), showSets, showSetItems);
                    break;
                case DbObject.TYPE_SET:
                    itemList = ((Set) selectedObject).getSetItems();
                default:
                    break;
            }
        }
        tableModel.setItemList(itemList);
    }

    private List<Item> filterItems(List<Item> itemList, boolean showSets, boolean showSetItems) {
        List<Item> filtered = new ArrayList<>();
        if (itemList != null && itemList.size() > 0) {
            if (showSets && showSetItems) { // Both true
                filtered = new ArrayList<>(itemList);
            } else if (!showSets && !showSetItems){ // Both false
                for (Item item : itemList) {
                    if (!item.isSet() && !item.isSetItem()) {
                        filtered.add(item);
                    }
                }
            } else { // One is true
                if (!showSets) {
                    for (Item item : itemList) {
                        if (!item.isSet()) {
                            filtered.add(item);
                        }
                    }
                }
                if (!showSetItems) {
                    for (Item item : itemList) {
                        if (!item.isSetItem()) {
                            filtered.add(item);
                        }
                    }
                }
            }
        }
        return filtered;
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

    void tableSelectItem(int row) {
        if (row >= 0) {
            this.selectedItem = (Item) tableModel.getValueAt(row, -1);
            itemTable.selectItem(selectedItem);
        }
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

    void treeSelectDivision(DbObject division) {
        if (division != null) {
            treeModel.setSelectedObject(division);
        }
    }

    private void createNodes(DefaultMutableTreeNode rootNode) {
        rootNode.add(iRoot);
        rootNode.add(sRoot);

        // Divisions
        cache().getCategories().sort(new ComparatorUtils.DbObjectNameComparator<>());
        List<Category> categories = cache().getCategories();
        for (Category category : categories) {
            if (!category.isUnknown()) {
                DefaultMutableTreeNode cNode = new DefaultMutableTreeNode(category, true);
                iRoot.add(cNode);

                List<Product> productList = sm().findProductListForCategory(category.getId());
                productList.sort(new ComparatorUtils.DbObjectNameComparator<>());
                for (Product product : productList) {
                    DefaultMutableTreeNode pNode = new DefaultMutableTreeNode(product, true);
                    cNode.add(pNode);

                    List<Type> typeList = sm().findTypeListForProduct(product.getId());
                    typeList.sort(new ComparatorUtils.DbObjectNameComparator<>());
                    for (Type type : typeList) {
                        DefaultMutableTreeNode tNode = new DefaultMutableTreeNode(type, false);
                        pNode.add(tNode);
                    }
                }
            }
        }

        // Sets
        List<Set> setList = cache().getSets();
        if (setList.size() > 0) {
            setList.sort(new ComparatorUtils.DbObjectNameComparator<>());
            for (Set set : cache().getSets()) {
                DefaultMutableTreeNode sNode = new DefaultMutableTreeNode(set, false);
                sRoot.add(sNode);
            }
        }
    }

    void treeRecreateNodes() {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        rootNode.removeAllChildren();
        createNodes(rootNode);
    }

    DbObject treeGetItemRoot() {
        return itemRoot;
    }

    // Other

    boolean setsSelected() {
        return (selectedDivision != null && selectedDivision instanceof Set);
    }

    private JPanel createItemDivisionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane pane = new JScrollPane(selectionTree);
        pane.setPreferredSize(new Dimension(300, 400));
        panel.add(pane, BorderLayout.CENTER);
        panel.add(selectionTb, BorderLayout.PAGE_END);
        panel.setMinimumSize(new Dimension(200,200));


        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Divisions
        invisibleRoot.setCanBeSaved(false);
        itemRoot.setCanBeSaved(false);
        setRoot.setCanBeSaved(false);

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(invisibleRoot, true);
        createNodes(rootNode);
        treeModel = new IDbObjectTreeModel<>(rootNode, (rootNode1, child) -> {
            switch (DbObject.getType(child)) {
                case DbObject.TYPE_CATEGORY:
                    return iRoot;

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

                case DbObject.TYPE_SET:
                    return sRoot;
            }
            return null;
        });

        selectionTree = new ITree(treeModel);
        selectionTree.addTreeSelectionListener(this);
        selectionTree.setCellRenderer(ITree.getItemsRenderer());
        selectionTree.setExpandsSelectedPaths(true);
        selectionTree.setScrollsOnExpand(true);
        selectionTree.setRootVisible(false);
        selectionTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = selectionTree.getClosestRowForLocation(e.getX(), e.getY());
                    selectionTree.setSelectionRow(row);
                    onTreeRightClick(e);
                }
            }
        });
        treeModel.setTree(selectionTree);
        treeModel.expandNode(iRoot);
        treeModel.expandNode(sRoot);

        selectionTb = new IdBToolBar(this, IdBToolBar.HORIZONTAL);

        // Items
        tableModel = new IItemTableModel();
        itemTable = new ITablePanel<>(tableModel, this, true);
        itemTable.setDbToolBar(this);
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onTableRowClicked(e);
            }
        });
        itemTable.addSortOption(new ComparatorUtils.ItemDivisionComparator(), null);
        itemTable.addSortOption(new ComparatorUtils.DbObjectNameComparator(), null);
        itemTable.addSortOption(new ComparatorUtils.ItemManufacturerComparator(), null);
        itemTable.addSortOption(new ComparatorUtils.ItemLocationComparator(), null);
        itemTable.addTableOptionsListener(this);
        tableInitialize(null);

        // Details
        detailPanel = new ItemDetailPanel(application, this);

        // Preview
        //previewPanel = new ItemPreviewPanel(application);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel divisionPanel = createItemDivisionPanel();

        // Panel them together
        centerPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        centerPanel.add(detailPanel, BorderLayout.SOUTH);
        //panel.add(previewPanel, BorderLayout.EAST);

        // Add
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, divisionPanel, centerPanel);
        centerSplitPane.setOneTouchExpandable(true);
        add(centerSplitPane, BorderLayout.CENTER);
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
