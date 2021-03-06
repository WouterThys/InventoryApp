package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class IWizardSetItemsTableModel extends IAbstractTableModel<Item> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Name", "Value", "Manufacturer", "Location"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class, String.class};

    public IWizardSetItemsTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    public void setItemList(List<Item> itemList) {
        super.setItemList(itemList);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = getItemAt(rowIndex);

        if (item != null) {
            switch (columnIndex) {
                case -1:
                    return item;
                case 0: // Amount label
                    return item;
                case 1: // Name
                    return item.toString();
                case 2: // Description
                    return item.getValue().toString();
                case 3: // Manufacturer
                    Manufacturer m = item.getManufacturer();
                    if (m != null && !m.isUnknown()) {
                        return m.toString();
                    }
                    return "";
                case 4: // Location
                    Location l = item.getLocation();
                    if (l != null && !l.isUnknown()) {
                        return l.getPrettyString();
                    }
                    return "";
            }
        }
        return null;
    }


    @Override
    public boolean hasTableCellRenderer() {
        return true;
    }

    private static final IRenderer renderer = new IRenderer();
    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return renderer;
    }

    private static class IRenderer extends DefaultTableCellRenderer {

        private static final ImageIcon greenBall = imageResource.readIcon("Ball.green");
        private static final ImageIcon redBall = imageResource.readIcon("Ball.red");
        private static final ImageIcon yellowBall = imageResource.readIcon("Ball.yellow");
        private static final ImageIcon blueBall = imageResource.readIcon("Ball.blue");

        private static final ITableLabel label = new ITableLabel(Color.gray, 0, false, greenBall, "");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Item) {
                if (row == 0) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                }

                label.updateBackground(component.getBackground(), row, isSelected);
                Item item = (Item) value;
                String txt = String.valueOf(item.getAmount());
                label.setText(txt);

                if (item.getOrderState() == Statics.OrderStates.Ordered) {
                    label.setIcon(blueBall);
                } else if (item.getOrderState() == Statics.OrderStates.Planned) {
                    label.setIcon(yellowBall);
                } else {
                    if (item.getAmount() > item.getMinimum()) {
                        label.setIcon(greenBall);
                    } else {
                        label.setIcon(redBall);
                    }
                }

                return label;
            }
            return component;
        }
    }

}
