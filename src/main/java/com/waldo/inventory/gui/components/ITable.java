package com.waldo.inventory.gui.components;

import javax.swing.*;
import javax.swing.table.TableModel;

public class ITable extends JTable {

    public ITable(TableModel model) {
        super();

        setModel(model);

    }

}
