package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Item;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.EventObject;

public class ITableEditors {

    public abstract static class SpinnerEditor extends DefaultCellEditor implements ChangeListener {

        JSpinner spinner;
        JSpinner.DefaultEditor editor;
        JTextField textField;
        boolean valueSet;

        public SpinnerEditor() {
            super(new JTextField());
            SpinnerModel model = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
            spinner = new JSpinner(model);
            spinner.addChangeListener(this);
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

    public static class AmountRenderer extends DefaultTableCellRenderer {

        ResourceManager resourceManager = new ResourceManager(IItemTableModel.class.getResource("/settings/IconSettings.properties").getPath());

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (column == 0) {
                Item item = (Item) value;
                ILabel lblText = new ILabel(String.valueOf(item.getAmount()));
                lblText.setForeground(Color.WHITE);
                Font f = lblText.getFont();
                lblText.setFont(new Font(f.getName(), Font.BOLD, f.getSize()-5));
                ILabel lblIcon;
                if (item.getOrderState() == Statics.ItemOrderState.ORDERED) {
                    lblIcon = new ILabel(resourceManager.readImage("Ball.blue"));
                } else if (item.getOrderState() == Statics.ItemOrderState.PLANNED) {
                    lblIcon = new ILabel(resourceManager.readImage("Ball.yellow"));
                } else {
                    if (item.getAmount() > 0) {
                        lblIcon = new ILabel(resourceManager.readImage("Ball.green"));
                    } else {
                        lblIcon = new ILabel(resourceManager.readImage("Ball.red"));
                    }
                }

                lblIcon.setLayout(new GridBagLayout());
                lblIcon.add(lblText);
                return lblIcon;
            } else {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }
    }
}
