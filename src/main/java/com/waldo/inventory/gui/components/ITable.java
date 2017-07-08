package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.gui.components.tablemodels.IOrderItemTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;

public class ITable extends JTable {

    public ITable(TableModel model) {
        super();

        setModel(model);
        setRowHeight(25);

        setPreferredScrollableViewportSize(getPreferredSize());
        setAutoCreateRowSorter(true);
    }

    public void resizeColumns(Integer[] widths) {
        int tableWidth = getWidth();

        TableColumn column;
        TableColumnModel columnModel = getColumnModel();

        int columnCnt = columnModel.getColumnCount();
        for (int i = 0; i < columnCnt; i++) {
            column = columnModel.getColumn(i);
            int width = Math.round(widths[i]*tableWidth);
            column.setPreferredWidth(width);
        }
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {

        // Width
        Component component = super.prepareRenderer(renderer, row, column);
        int rendererWidth = component.getPreferredSize().width;
        TableColumn tableColumn = getColumnModel().getColumn(column);
        if (renderer instanceof ITableEditors.AmountRenderer) {
            tableColumn.setMaxWidth(36);
        } else {
            tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
        }

        if (getModel() instanceof IOrderItemTableModel) {
            Object o = getValueAtRow(row);

            if (o instanceof OrderItem) {
                OrderItem orderItem = (OrderItem) o;
                if (!isRowSelected(row)) {
                    component.setBackground(getBackground());
                    if (orderItem.getItem().isDiscourageOrder()) {
                        component.setBackground(Color.orange);
                    } else {
                        component.setBackground(getBackground());
                    }
                }
            }
        }

        return component;
    }

    public Object getValueAtRow(int row) {
        if (row >= 0) {
            return getModel().getValueAt(convertRowIndexToModel(row), -1);
        }
        return null;
    }
//
//    public java.util.List<DbObject> getSelectedObjects() {
//        java.util.List<DbObject> selectedObjects = new ArrayList<>();
//        int[] selectedRows = getSelectedRows();
//        if (selectedRows.length > 0) {
//            for (int row : selectedRows) {
//
//            }
//        }
//
//        return selectedObjects;
//    }
}
