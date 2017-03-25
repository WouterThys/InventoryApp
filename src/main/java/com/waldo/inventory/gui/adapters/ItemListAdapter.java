package com.waldo.inventory.gui.adapters;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.SelectDataSheetDialog;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import static com.waldo.inventory.database.DbManager.dbInstance;

public class ItemListAdapter extends AbstractTableModel {

    private final String[] columnNames = {"Name", "Description", "Price", "Data sheet"};

    public ItemListAdapter() {}

    public Item getItemAt(int row) throws SQLException {
        return dbInstance().getItems().get(row);
    }

    public void tableClicked(Application application, JTable table, MouseEvent e) throws SQLException {
        int col = table.columnAtPoint(e.getPoint());
        if (col == 3) { // Data sheet column
            int row = table.rowAtPoint(e.getPoint());
            Item item = getItemAt(row);
            if (item != null) {
                String local = item.getLocalDataSheet();
                String online = item.getOnlineDataSheet();
                if (local != null && !local.isEmpty() && online != null && !online.isEmpty()) {
                    SelectDataSheetDialog.showDialog(application, online, local);
                } else if (local != null && !local.isEmpty()) {
                    try {
                        OpenUtils.openPdf(local);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(application,
                                "Error opening the file: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else if (online != null && !online.isEmpty()) {
                    try {
                        OpenUtils.browseLink(online);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(application,
                                "Error opening the file: " + e1.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    public void addItem(Item item) throws SQLException {
        if (item != null) {
            item.save();
            // TODO: CLEAN
            Item newItem = dbInstance().findItemById(item.getId());
            int row = dbInstance().getItems().indexOf(newItem);
            fireTableRowsInserted(row, row);
        }
    }

    public void deleteRow(Item item) throws SQLException {
        if (item != null) {
            int row = dbInstance().getItems().indexOf(item);
            item.delete();
            // TODO: CLEAN
            fireTableRowsDeleted(row, row);
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        try {
            return dbInstance().getItems().size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
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

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
}
