package com.waldo.inventory.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class IComboCheckBox extends JComboBox {

    private JCheckBox[] checkBoxes;

    public IComboCheckBox() { addStuff(); }
    public IComboCheckBox(JCheckBox[] items) {
        super(items);
        addStuff();
        this.checkBoxes = items;

    }
    public IComboCheckBox(Vector items) { super(items); addStuff(); }
    public IComboCheckBox(ComboBoxModel aModel) { super(aModel); addStuff(); }


    public JCheckBox[] getCheckBoxes() {
        return checkBoxes;
    }

    private void addStuff() {
        setRenderer(new ComboBoxRenderer());
        addActionListener(ae -> itemSelected());
    }
    private void itemSelected() {
        if (getSelectedItem() instanceof JCheckBox) {
            JCheckBox jcb = (JCheckBox)getSelectedItem();
            jcb.setSelected(!jcb.isSelected());
            SwingUtilities.invokeLater(this::showPopup);
        }
    }

    class ComboBoxRenderer implements ListCellRenderer {
        private JLabel defaultLabel;
        public ComboBoxRenderer() { setOpaque(true); }
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            if (value == null) {
                if (defaultLabel == null) {
                    defaultLabel = new JLabel();
                }
                return defaultLabel;
            }
            if (value instanceof Component) {
                Component c = (Component)value;
                if (isSelected) {
                    c.setBackground(list.getSelectionBackground());
                    c.setForeground(list.getSelectionForeground());
                } else {
                    c.setBackground(list.getBackground());
                    c.setForeground(list.getForeground());
                }
                return c;
            } else {
                if (defaultLabel==null) defaultLabel = new JLabel(value.toString());
                else defaultLabel.setText(value.toString());
                return defaultLabel;
            }
        }
    }
}