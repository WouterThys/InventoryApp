package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.*;
import com.waldo.inventory.gui.components.treetable.AbstractTreeTableModel;
import com.waldo.inventory.gui.components.treetable.FileSystemModel;
import com.waldo.inventory.gui.components.treetable.ItemTableModel;
import com.waldo.inventory.gui.components.treetable.JTreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;

import static com.waldo.inventory.database.DbManager.dbInstance;

public abstract class ItemListPanelLayout extends JPanel implements GuiInterface {

    private static final Logger LOG = LoggerFactory.getLogger(ItemListPanelLayout.class);

    private static final String[] columnNames = {"Name"};//, "Description", "Price", "Data sheet"};

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JTreeTable itemTable;
    AbstractTreeTableModel tableModel;

    JList<Category> categoryList;
    DefaultListModel<Category> categoryListModel;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ResourceManager resourceManager;

    Item selectedItem;
    Category selectedCategory;

    MouseAdapter mouseClicked;
    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Item getItemAt(int row) throws SQLException {
        return dbInstance().getItems().get(row);
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Categories
        categoryListModel = new DefaultListModel<>();
        categoryList = new JList<>(categoryListModel);

        // Items
        try {
            itemTable = new JTreeTable(new ItemTableModel());
        } catch (SQLException e) {
           e.printStackTrace();
        }

    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        add(new JScrollPane(itemTable), BorderLayout.CENTER);
        //add(new JScrollPane(categoryList), BorderLayout.WEST);
    }

    @Override
    public void updateComponents(Object object) {
        // Get all categories
        categoryListModel.removeAllElements();
        try {
            for (Category c : dbInstance().getCategories()) {
                categoryListModel.addElement(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Get all items
        //...
    }


}
