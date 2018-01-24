package com.waldo.inventory.gui.components;

import com.waldo.inventory.gui.components.tablemodels.IAbstractTableModel;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ITable<T> extends JXTable {

    private final IAbstractTableModel<T> model;
    private final boolean autoAdaptHeight;

    public ITable(IAbstractTableModel<T> model) {
        super(model);

        this.model = model;
        this.autoAdaptHeight = false;

        setModel(model);
        setRowHeight(25);

        setPreferredScrollableViewportSize(getPreferredSize());
        setAutoCreateRowSorter(true);

        if (model.hasTableCellRenderer()) {
            setDefaultRenderer(Object.class, model.getTableCellRenderer());
        }
        //setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
    }

    public ITable(IAbstractTableModel<T> model, boolean autoSetHeight) {
        super(model);

        this.model = model;
        this.autoAdaptHeight = autoSetHeight;

        setModel(model);
        setRowHeight(25);

        setPreferredScrollableViewportSize(getPreferredSize());
        setAutoCreateRowSorter(true);
        //setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        if (autoAdaptHeight) {
            return new Dimension(super.getPreferredSize().width, getRowHeight() * getRowCount());
        } else {
            return super.getPreferredScrollableViewportSize();
        }
    }

    @SuppressWarnings("unchecked")
    public T getValueAtRow(int row) {
        if (row >= 0) {
            return (T) model.getValueAt(convertRowIndexToModel(row), -1);
        }
        return null;
    }

    public void selectItem(T item) {
        if (item != null) {
            int row = model.getModelIndex(item);
            if (row >= 0) {
                final int real = convertRowIndexToView(row);
                setRowSelectionInterval(real, real);
                SwingUtilities.invokeLater(() -> scrollRectToVisible(new Rectangle(getCellRect(real, 0, true))));
            }
        } else {
            clearSelection();
        }
    }

    public T getSelectedItem() {
        int row = getSelectedRow();
        return getValueAtRow(row);
    }

    public List<T> getSelectedItems() {
        List<T> list = new ArrayList<>();
        int[] selectedRows = getSelectedRows();
        if (selectedRows.length > 0) {
            for (int row : selectedRows) {
                T t = getValueAtRow(row);
                if (t != null) {
                    list.add(t);
                }
            }
        }
        return list;
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

    public void setExactColumnWidth(int column, int width) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        if (tableColumn != null) {
            tableColumn.setMaxWidth(width);
            tableColumn.setMinWidth(width);
        }
    }

    public void resizeColumns() {
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        for (int column = 0; column < getColumnCount(); column++) {
            TableColumn tableColumn = getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();
            if (maxWidth > 500) {
                maxWidth = 500;
            }

            for (int row = 0; row < getRowCount(); row++) {
                TableCellRenderer cellRenderer = getCellRenderer(row, column);
                Component c = prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);

                //  We've exceeded the maximum width, no need to check other rows
                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth( preferredWidth );
        }
    }
}
