package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.*;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.util.*;

import static com.waldo.inventory.database.DbManager.dbInstance;

public abstract class ItemListPanelLayout extends JPanel implements
        GuiInterface,
        TreeModelListener,
        TreeSelectionListener,
        ListSelectionListener{

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITable itemTable;
    ItemTableModel tableModel;

    ITree subDivisionTree;
    DivisionTreeModel treeModel;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ResourceManager resourceManager;

    Item selectedItem;
    DbObject lastSelectedDivision;

    MouseAdapter mouseClicked;
    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Item getItemAt(int row)  {
        return tableModel.getItem(row);
    }

    public void updateTable(DbObject selectedObject) throws SQLException {
        if (selectedObject == null || selectedObject.getName().equals("All")) {
            tableModel.setItemList(dbInstance().getItems());
        } else {
            switch (DbObject.getType(selectedObject)) {
                case DbObject.TYPE_CATEGORY:
                    Category c = (Category)selectedObject;
                    tableModel.setItemList(dbInstance().getItemListForCategory(c));
                    break;
                case DbObject.TYPE_PRODUCT:
                    Product p = (Product)selectedObject;
                    tableModel.setItemList(dbInstance().getItemListForProduct(p));
                    break;
                case DbObject.TYPE_TYPE:
                    Type t = (Type)selectedObject;
                    tableModel.setItemList(dbInstance().getItemListForType(t));
                    break;
                default:
                    break;
            }
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Sub division tree
        treeModel = new DivisionTreeModel();
        treeModel.addTreeModelListener(this);

        subDivisionTree = new ITree(treeModel);
        subDivisionTree.addTreeSelectionListener(this);

        // Item table
        tableModel = new ItemTableModel(dbInstance().getItems());
        itemTable = new ITable(tableModel);
        itemTable.getSelectionModel().addListSelectionListener(this);
        itemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        add(new JScrollPane(itemTable), BorderLayout.CENTER);
        add(new JScrollPane(subDivisionTree), BorderLayout.WEST);
    }

    @Override
    public void updateComponents(Object object) {
        resizeColumnWidth(itemTable);
    }

    public void resizeColumnWidth(JTable table) {
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
