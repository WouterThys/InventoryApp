package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.*;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.inventory.gui.panels.mainpanel.detailpanel.MainDetailPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

import static com.waldo.inventory.database.DbManager.db;

public abstract class MainPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        ListSelectionListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITable itemTable;
    ItemTableModel tableModel;

    ITree subDivisionTree;
    //DivisionTreeModel treeModel;
    IDbObjectTreeModel treeModel;
    DefaultMutableTreeNode rootNode;

    MainDetailPanel detailPanel;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ResourceManager resourceManager;

    Item selectedItem;
    DbObject lastSelectedDivision;

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
        rootNode = new DefaultMutableTreeNode(new Category("All"), true);
        createNodes(rootNode);
        treeModel = new IDbObjectTreeModel(rootNode);

        subDivisionTree = new ITree(treeModel);
        subDivisionTree.addTreeSelectionListener(this);
        treeModel.setTree(subDivisionTree);

        // Item table
        tableModel = new ItemTableModel(db().getItems());
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

    private void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width +1 , width);
            }
            if(width > 300)
                width=300;
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }
}
