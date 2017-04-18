package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.*;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.panels.itemdetailpanel.ItemDetailPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.net.URL;

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
    private ItemDetailPanel detailPanel;
    TopToolBar topToolBar;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ResourceManager resourceManager;
    Application application;

    Item selectedItem;
    DbObject lastSelectedDivision;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public MainPanelLayout(Application application) {
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
            tableModel.setItemList(db().getItems());
        } else {
            switch (DbObject.getType(selectedObject)) {
                case DbObject.TYPE_CATEGORY:
                    Category c = (Category)selectedObject;
                    tableModel.setItemList(db().getItemListForCategory(c));
                    break;
                case DbObject.TYPE_PRODUCT:
                    Product p = (Product)selectedObject;
                    tableModel.setItemList(db().getItemListForProduct(p));
                    break;
                case DbObject.TYPE_TYPE:
                    Type t = (Type)selectedObject;
                    tableModel.setItemList(db().getItemListForType(t));
                    break;
                default:
                    break;
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
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new Category("All"), true);
        createNodes(rootNode);
        treeModel = new IDbObjectTreeModel(rootNode, IDbObjectTreeModel.TYPE_DIVISIONS);

        subDivisionTree = new ITree(treeModel);
        subDivisionTree.addTreeSelectionListener(this);
        treeModel.setTree(subDivisionTree);

        // Tool bar
        topToolBar = new TopToolBar(application, this);

        // Item table
        tableModel = new IItemTableModel(db().getItems());
        itemTable = new ITable(tableModel);
        itemTable.getSelectionModel().addListSelectionListener(this);
        //itemTable.addFocusListener(this);
        itemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);

        // Details
        detailPanel = new ItemDetailPanel();
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Panel them together
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        panel.add(detailPanel, BorderLayout.SOUTH);
        panel.add(topToolBar, BorderLayout.PAGE_START);

        // Add
        add(new JScrollPane(subDivisionTree), BorderLayout.WEST);
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
