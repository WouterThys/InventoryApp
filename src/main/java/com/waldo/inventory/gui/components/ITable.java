package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.gui.components.tablemodels.IAbstractTableModel;
import com.waldo.inventory.gui.components.tablemodels.IOrderItemTableModel;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ITable<T> extends JTable {

    private IAbstractTableModel<T> model;

    public ITable(IAbstractTableModel<T> model) {
        super(model);

        this.model = model;

        setModel(model);
        setRowHeight(25);

        setPreferredScrollableViewportSize(getPreferredSize());
        setAutoCreateRowSorter(true);

        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
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

    @Override
    protected JTableHeader createDefaultTableHeader() {
        IAbstractTableModel model = (IAbstractTableModel) getModel();
        if ((model != null) &&
                (model.getColumnHeaderToolTips() != null) &&
                (model.getColumnHeaderToolTips().length == model.getColumnCount())) {
             return new JTableHeader(columnModel) {
                 @Override
                 public String getToolTipText(MouseEvent event) {
                     Point p = event.getPoint();
                     int ndx = columnModel.getColumnIndexAtX(p.x);
                     int realNdx = columnModel.getColumn(ndx).getModelIndex();
                     return model.getColumnHeaderToolTips()[realNdx];
                 }
             };
        }
        return super.createDefaultTableHeader();
    }
}
