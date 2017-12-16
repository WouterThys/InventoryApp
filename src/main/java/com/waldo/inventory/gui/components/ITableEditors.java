package com.waldo.inventory.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.EventObject;

public class ITableEditors {

    public interface SpinnerChangedListener {
        void onValueSet(int value);
    }

    public abstract static class SpinnerEditor extends DefaultCellEditor implements SpinnerChangedListener {

        final JSpinner spinner;
        final JSpinner.DefaultEditor editor;
        final JTextField textField;
        boolean valueSet;

        protected SpinnerEditor() {
            super(new JTextField());
            SpinnerModel model = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
            spinner = new JSpinner(model);
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
                public void focusLost(FocusEvent e) {
                }
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
            int value = (int) spinner.getValue();

            onValueSet(value);

            return value;
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
