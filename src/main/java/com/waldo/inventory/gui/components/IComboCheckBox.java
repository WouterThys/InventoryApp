package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.DbObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class IComboCheckBox<E extends DbObject> extends JComboBox<JCheckBox> {

    private static final JCheckBox allBox = new JCheckBox("All", true);
    private static final JCheckBox separatorBox =  new JCheckBox();

    private final boolean allOption;
    private final List<E> itemList;

    public IComboCheckBox(List<E> itemList, boolean allOption) {
        super();

        this.itemList = itemList;
        this.allOption = allOption;

        setRenderer();

        DefaultComboBoxModel<JCheckBox> model = new DefaultComboBoxModel<>();
        if (allOption) {
            model.addElement(allBox);
            model.addElement(separatorBox);
        }
        for (E e : itemList) {
            model.addElement(new JCheckBox(e.getName(), false));
        }
        setModel(model);

        addActionListener(new ComboBoxListener(this));
        allSelected(true);
    }

    private void setRenderer() {
        setRenderer(new ComboBoxRenderer());
        addActionListener(ae -> itemSelected());
    }

    private void itemSelected() {
        if (getSelectedItem() instanceof JCheckBox) {
            JCheckBox jcb = (JCheckBox) getSelectedItem();
            jcb.setSelected(!jcb.isSelected());
            SwingUtilities.invokeLater(this::showPopup);
        }
    }

    private void allSelected(boolean selected) {
        allBox.setSelected(selected);
        int a = allOption ? 2 : 0;
        for (int i = a; i < getModel().getSize(); i++) {
            JCheckBox box = getModel().getElementAt(i);
            box.setSelected(!selected);
            box.setEnabled(selected);
        }
    }

    public List<E> getSelectedElements() {
        List<E> list = new ArrayList<>();

        if (allOption && allBox.isSelected()) {
            list = itemList;
        } else {
            int a = allOption ? 2 : 0;
            for (int i = a; i < getModel().getSize(); i++) {
                JCheckBox box = getItemAt(i);
                if (box.isSelected()) {
                    list.add(itemList.get(i - a));
                }
            }
        }
        return list;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
    }

    class ComboBoxRenderer implements ListCellRenderer<JCheckBox> {

        int maxWidth = 0;
        final JSeparator separator;

        ComboBoxRenderer() {
            setOpaque(true);
            separator = new JSeparator(JSeparator.HORIZONTAL);
        }

        private JLabel defaultLabel;

        @Override
        public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value == null) {
                if (defaultLabel == null) {
                    defaultLabel = new JLabel();
                }
                return defaultLabel;
            }

            if (value.equals(separatorBox)) {
                return separator;
            }

            Dimension dimension = value.getPreferredSize();
            if (dimension.width > maxWidth) {
                maxWidth = dimension.width;
                setMinimumSize(dimension);
                revalidate();
                repaint();
            }

            if (isSelected && value.isEnabled()) {
                value.setBackground(list.getSelectionBackground());
                value.setForeground(list.getSelectionForeground());
            } else {
                value.setBackground(list.getBackground());
                value.setForeground(list.getForeground());
            }
            return value;

        }
    }

    class ComboBoxListener implements ActionListener {
        final JComboBox comboBox;
        Object currentObject;

        ComboBoxListener(JComboBox comboBox) {
            this.comboBox = comboBox;
            comboBox.setSelectedIndex(0);
            currentObject = comboBox.getSelectedItem();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (separatorBox.equals(comboBox.getSelectedItem())) {
                comboBox.setSelectedItem(currentObject);
            } else {
                currentObject = comboBox.getSelectedItem();
            }

            if (currentObject != null && currentObject.equals(allBox)) {
                allSelected(allBox.isSelected());
            } else {
                JCheckBox currentBox = (JCheckBox) currentObject;
                if (currentBox != null && currentBox.isEnabled() && allBox.isSelected()) {
                    boolean selected = currentBox.isSelected();
                    allSelected(false);
                    currentBox.setSelected(selected);
                }
            }
        }
    }
}