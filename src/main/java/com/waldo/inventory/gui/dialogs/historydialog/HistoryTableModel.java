package com.waldo.inventory.gui.dialogs.historydialog;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.gui.components.ILabel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryTableModel extends AbstractTableModel {

    private static final String[] columnNames = {"", "Name", "Date", "Go"};
    private static final Class[] columnClasses = {ImageIcon.class, String.class, String.class, JCheckBox.class};
    private static final SimpleDateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private List<DbObject> historyObjectList;
    private ResourceManager resourceManager;

    public HistoryTableModel() {
        super();
        URL url = HistoryTableModel.class.getResource("/settings/IconSettings.properties");
        resourceManager = new ResourceManager(url.getPath());
        historyObjectList = new ArrayList<>();
    }

    public void setHistoryObjectList(List<DbObject> list) {
        this.historyObjectList = list;
        fireTableDataChanged();
    }

    public List<DbObject> getHistoryObjectList() {
        return historyObjectList;
    }

    public DbObject getDbObject(int index) {
        return historyObjectList.get(index);
    }



    @Override
    public int getRowCount() {
        return historyObjectList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DbObject dbObject = getDbObject(rowIndex);
        if (dbObject != null) {
            switch (DbObject.getType(dbObject)) {
                case DbObject.TYPE_ORDER:
                    Order order = (Order) dbObject;
                    switch (columnIndex) {
                        case 0: // Icon
                            return resourceManager.readImage("HistoryDialog.OrderIcon");
                        case 1: // Name
                            return order.getName();
                        case 2: // Date
                            if (order.isOrdered()) {
                                return dateFormatLong.format(order.getDateOrdered());
                            } else {
                                return dateFormatLong.format(order.getDateModified());
                            }
                        case 3: // Go to
                            return "Go";//resourceManager.readImage("Common.ArrowRight");
                    }


            }
        }
        return null;
    }
}
