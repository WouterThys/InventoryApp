package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.gui.components.tablemodels.IAbstractTableModel;
import com.waldo.inventory.gui.components.tablemodels.IOrderItemTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class ITable<T> extends JTable {

    private IAbstractTableModel model;

    public ITable(IAbstractTableModel model) {
        super();

        this.model = model;

        setModel(model);
        setRowHeight(25);

        setPreferredScrollableViewportSize(getPreferredSize());
        setAutoCreateRowSorter(true);

        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
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

        if (model instanceof IOrderItemTableModel) {
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

//        if (getModel() instanceof ILinkItemTableModel) {
//            Object o = getValueAtRow(row);
//            if (o instanceof PcbItemItemLink) {
//                PcbItemItemLink match = (PcbItemItemLink) o;
//                if (!isRowSelected(row)) {
//                    component.setBackground(getBackground());
//                    if (match.isMatched()) {
//                        component.setBackground(Color.green);
//                    } else {
//                        component.setBackground(getBackground());
//                    }
//                }
//            }
//
//        }

        return component;
    }

    public Object getValueAtRow(int row) {
        if (row >= 0) {
            return model.getValueAt(convertRowIndexToModel(row), -1);
        }
        return null;
    }

    public void selectItem(T item) {
        if (item != null) {
            int row = model.getModelIndex(item);
            int real = convertRowIndexToView(row);
            setRowSelectionInterval(real, real);
        } else {
            clearSelection();
        }
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
