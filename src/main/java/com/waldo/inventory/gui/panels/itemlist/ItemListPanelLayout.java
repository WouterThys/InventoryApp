package com.waldo.inventory.gui.panels.itemlist;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.database.ItemsChangedListener;
import com.waldo.inventory.gui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;

import static com.waldo.inventory.database.DbManager.dbInstance;

public abstract class ItemListPanelLayout extends JPanel implements GuiInterface, ItemsChangedListener {

    private static final Logger LOG = LoggerFactory.getLogger(ItemListPanelLayout.class);

    private static final String[] columnNames = {"Name", "Description", "Price", "Data sheet"};

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JTable itemTable;
    private AbstractTableModel tableModel;
    Item selectedItem;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ResourceManager resourceManager;

    MouseAdapter mouseClicked;
    ListSelectionListener itemSelectedListener;
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

        tableModel = new AbstractTableModel() {

            @Override
            public int getRowCount() {
                try {
                    return dbInstance().getItems().size();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return 0;
                }
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                final Item item;
                try {
                    item = getItemAt(rowIndex);
                    switch (columnIndex) {
                        case 0: // Name
                            return item.getName();
                        case 1: // Description
                            return item.getDescription();
                        case 2: // Price
                            return item.getPrice();
                        case 3: // Data sheet
                            boolean hasLocal = (item.getLocalDataSheet() != null && !item.getLocalDataSheet().isEmpty());
                            boolean hasOnline = (item.getOnlineDataSheet() != null && !item.getOnlineDataSheet().isEmpty());
                            if (hasLocal || hasOnline) {
                                return "Open";
                            } else {
                                return "";
                            }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        itemTable = new JTable(tableModel);
        itemTable.addMouseListener(mouseClicked); //

        itemTable.setColumnSelectionAllowed(false);
        itemTable.getSelectionModel().addListSelectionListener(itemSelectedListener);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        add(new JScrollPane(itemTable), BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {

    }

    @Override
    public void onItemAdded(Item item) {
        tableModel.fireTableDataChanged();
        selectedItem = item;
        LOG.debug("Item list updated: added item " + item.getName());
    }

    @Override
    public void onItemUpdated(Item item) {
        tableModel.fireTableDataChanged();
        selectedItem = item;
        LOG.debug("Item list updated: updated item " + item.getName());
    }

    @Override
    public void onItemDeleted(Item item) {
        tableModel.fireTableDataChanged();
        selectedItem = null;
        LOG.debug("Item list updated: deleted item " + item.getName());
    }
}
