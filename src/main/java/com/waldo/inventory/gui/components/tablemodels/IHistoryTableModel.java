package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class IHistoryTableModel extends IAbstractTableModel {

    private static final String[] columnNames = {"", "Name", "Date"}; //, "Go"};
    private static final Class[] columnClasses = {ImageIcon.class, String.class, String.class};//, JCheckBox.class};

    private List<DbObject> historyObjectList;

    public IHistoryTableModel() {
        super(columnNames, columnClasses);
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
                            return Application.imageResource.readImage("HistoryDialog.OrderIcon");
                        case 1: // Name
                            return "Ordered in " + order.getName();
                        case 2: // Date
                            if (order.isOrdered()) {
                                return DateUtils.formatDateTime(order.getDateOrdered());
                            } else {
                                return DateUtils.formatDateTime(order.getDateModified());
                            }
                        case 3: // Go to
                            return "Go";//imageResource.readImage("Common.ArrowRight");
                    }


            }
        }
        return null;
    }
}
