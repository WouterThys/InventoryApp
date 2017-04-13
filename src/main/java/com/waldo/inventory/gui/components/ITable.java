package com.waldo.inventory.gui.components;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class ITable extends JTable {

    public ITable(TableModel model) {
        super();

        setModel(model);
        setRowHeight(25);
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

}
