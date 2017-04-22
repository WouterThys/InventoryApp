package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.database.DbManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class IItemTableModel extends AbstractTableModel {

    // Names and classes
    private String[] columnNames = {"Name", "Description", "Manufacturer"};
    private Class[] columnClasses = {String.class, String.class, String.class};

    private List<Item> itemList;

    public IItemTableModel() {
        itemList = new ArrayList<>();
    }

    public IItemTableModel(List<Item> itemList) {
        this.itemList = itemList;
        itemList.sort(new Item.ItemComparator());
    }

    public IItemTableModel(String[] columnNames, Class[] columnClasses) {
        this();
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
    }

    public IItemTableModel(List<Item> itemList, String[] columnNames, Class[] columnClasses) {
        this(itemList);
        this.columnClasses = columnClasses;
        this.columnNames = columnNames;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        this.itemList.sort(new Item.ItemComparator());
        fireTableDataChanged();
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public Item getItem(int index) {
        if (index >= 0 && index < itemList.size()) {
            return itemList.get(index);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return itemList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = getItem(rowIndex);
        if (item != null) {
            switch (columnIndex) {
                case 0: // Name
                    return item.getName();
                case 1: // Description
                    return item.getDescription();
                case 2: // Manufacturer
                    Manufacturer m = DbManager.db().findManufacturerById(item.getManufacturerId());
                    if (m != null && m.getId() != DbObject.UNKNOWN_ID) {
                        return m.getName();
                    }
                    return "";
                case 3: // Amount
                    return 1;
                case 4: // Price
                    return item.getPrice();
                case 5: // Total
                    return item.getPrice();

            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 3;
    }

    public static class SpinnerEditor extends DefaultCellEditor {

        JSpinner spinner;
        JSpinner.DefaultEditor editor;
        JTextField textField;
        boolean valueSet;

        public SpinnerEditor() {
            super(new JTextField());
            spinner = new JSpinner();
            editor = ((JSpinner.DefaultEditor) spinner.getEditor());
            textField = editor.getTextField();
            textField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        if (valueSet) {
                            textField.setCaretPosition(1);
                        }
                    });
                }

                @Override
                public void focusLost(FocusEvent e) {}
            });
            textField.addActionListener(e -> stopCellEditing());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (!valueSet) {
                spinner.setValue(value);
            }
            SwingUtilities.invokeLater(() -> textField.requestFocus());
            return spinner;
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof KeyEvent) {
                KeyEvent ke = (KeyEvent) anEvent;
                textField.setText(String.valueOf(ke.getKeyChar()));
                valueSet = true;
            } else {
                valueSet = false;
            }
            return true;
        }

        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }

        @Override
        public boolean stopCellEditing() {
            try {
                editor.commitEdit();
                spinner.commitEdit();
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Invalid value, discarding");
            }
            return super.stopCellEditing();
        }
    }
}
