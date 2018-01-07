package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITablePanel;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.inventory.gui.components.IdBToolBar;
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

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

abstract class MainPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        ItemDetailPanelLayout.OnItemDetailListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITablePanel<Item> itemTable;
    IItemTableModel tableModel;

    ITree subDivisionTree;
    IDbObjectTreeModel<DbObject> treeModel;
    IdBToolBar divisionTb;

    DefaultListModel<Set> setListModel;
    JList<Set> setList; // TODO: make table or tree with divisions?
    IdBToolBar setTb;

    ItemDetailPanel detailPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final Application application;

    Item selectedItem;
    DbObject selectedDivision;
    Set selectedSet;

    Category virtualRoot;

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
        divisionTb.setEditActionEnabled(enabled);
        divisionTb.setDeleteActionEnabled(enabled);
        if (enabled) {
            if (DbObject.getType(selectedDivision) == DbObject.TYPE_TYPE) {
                divisionTb.setAddActionEnabled(false);
            } else {
                divisionTb.setAddActionEnabled(true);
            }
        }

        // Sets
        enabled = selectedSet != null;
        setTb.setEditActionEnabled(enabled);
        setTb.setDeleteActionEnabled(enabled);
    }

    abstract void onTreeRightClick(MouseEvent e);
    abstract void onTableRowClicked(MouseEvent e);

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

    private void createNodes(DefaultMutableTreeNode rootNode) {
        cache().getCategories().sort(new ComparatorUtils.DbObjectNameComparator<>());
        for (Category category : cache().getCategories()) {
            if (!category.isUnknown()) {
                DefaultMutableTreeNode cNode = new DefaultMutableTreeNode(category, true);
                rootNode.add(cNode);

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
    }

    void treeRecreateNodes() {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        rootNode.removeAllChildren();
        createNodes(rootNode);
    }

    // Sets stuff
    void initializeSets() {
        setListModel.clear();
        for (Set set : cache().getSets()) {
            setListModel.addElement(set);
        }
    }

    private JPanel createItemDivisionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // Title
        JPanel itemTitlePnl = new JPanel(new BorderLayout());
        ILabel itemTitleIcon = new ILabel(imageResource.readImage("Items.SmallTitle"));
        ILabel itemTitleLbl = new ILabel("Items");
        itemTitleLbl.setFont(25, Font.BOLD);
        itemTitleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        itemTitleLbl.setVerticalAlignment(SwingConstants.CENTER);

        itemTitlePnl.add(itemTitleIcon, BorderLayout.WEST);
        itemTitlePnl.add(itemTitleLbl, BorderLayout.CENTER);
        itemTitlePnl.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JScrollPane pane = new JScrollPane(subDivisionTree);
        pane.setPreferredSize(new Dimension(300, 400));
        panel.add(itemTitlePnl, BorderLayout.PAGE_START);
        panel.add(pane, BorderLayout.CENTER);
        panel.add(divisionTb, BorderLayout.PAGE_END);
        panel.setMinimumSize(new Dimension(200,200));


        return panel;
    }

    private JPanel createSetPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // Title
        JPanel itemTitlePnl = new JPanel(new BorderLayout());
        ILabel itemTitleIcon = new ILabel(imageResource.readImage("Sets.SmallTitle"));
        ILabel itemTitleLbl = new ILabel("Sets");
        itemTitleLbl.setFont(25, Font.BOLD);
        itemTitleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        itemTitleLbl.setVerticalAlignment(SwingConstants.CENTER);

        itemTitlePnl.add(itemTitleIcon, BorderLayout.WEST);
        itemTitlePnl.add(itemTitleLbl, BorderLayout.CENTER);
        itemTitlePnl.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JScrollPane pane = new JScrollPane(setList);
        pane.setPreferredSize(new Dimension(300, 200));
        panel.add(itemTitlePnl, BorderLayout.PAGE_START);
        panel.add(pane, BorderLayout.CENTER);
        panel.add(setTb, BorderLayout.PAGE_END);
        panel.setMinimumSize(new Dimension(200,200));

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Divisions
        virtualRoot = new Category("All");
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
        subDivisionTree.setCellRenderer(ITree.getItemsRenderer());
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
        divisionTb = new IdBToolBar(this, IdBToolBar.HORIZONTAL);

        // Sets
        setListModel = new DefaultListModel<>();
        setList = new JList<>(setListModel);
        setList.addListSelectionListener(this);
        setTb = new IdBToolBar(this, IdBToolBar.HORIZONTAL);


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
        itemTable.addSortOption(new ComparatorUtils.ItemDivisionComparator());
        itemTable.addSortOption(new ComparatorUtils.DbObjectNameComparator());
        itemTable.addSortOption(new ComparatorUtils.ItemManufacturerComparator());
        itemTable.addSortOption(new ComparatorUtils.ItemLocationComparator());
        tableInitialize(null);

        // Details
        detailPanel = new ItemDetailPanel(application, this);

        // Preview
        //previewPanel = new ItemPreviewPanel(application);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel westPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel divisionPanel = createItemDivisionPanel();
        JPanel setPanel = createSetPanel();

        // Panel them together
        centerPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        centerPanel.add(detailPanel, BorderLayout.SOUTH);
        //panel.add(previewPanel, BorderLayout.EAST);

        JSplitPane westSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, divisionPanel, setPanel);
        westSplitPane.setResizeWeight(1);

        westSplitPane.setOneTouchExpandable(true);


        // Add
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westSplitPane, centerPanel);
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

            // Sets
           initializeSets();

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
