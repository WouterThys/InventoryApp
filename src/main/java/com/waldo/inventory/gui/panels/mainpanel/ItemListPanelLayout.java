package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.*;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.util.*;

import static com.waldo.inventory.database.DbManager.dbInstance;

public abstract class ItemListPanelLayout extends JPanel implements
        GuiInterface,
        TreeModelListener,
        TreeSelectionListener {

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
    Item getItemAt(int row) throws SQLException {
        return dbInstance().getItems().get(row);
    }

    void updateTable(DbObject selectedObject) throws SQLException {
        if (selectedObject.getName().equals("All*")) {
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
        try {
            tableModel = new ItemTableModel(dbInstance().getItems());
            itemTable = new ITable(tableModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        add(new JScrollPane(itemTable), BorderLayout.CENTER);
        add(new JScrollPane(subDivisionTree), BorderLayout.WEST);
    }

    @Override
    public void updateComponents(Object object) {

    }
}
