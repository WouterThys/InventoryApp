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

public class IItemTableModel extends IAbstractTableModel<Item> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Name", "Description", "Manufacturer", "Location"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class, String.class};

    public IItemTableModel() {
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
                    return item.getDescription();
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

    private static final ItemTableRenderer renderer = new ItemTableRenderer();
    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return renderer;
    }

    static class ItemTableRenderer extends DefaultTableCellRenderer {

        private static final ImageIcon greenBall = imageResource.readImage("Ball.green");
        private static final ImageIcon redBall = imageResource.readImage("Ball.red");
        private static final ImageIcon yellowBall = imageResource.readImage("Ball.yellow");
        private static final ImageIcon blueBall = imageResource.readImage("Ball.blue");

        private static final ITableLabel label = new ITableLabel(Color.gray, 0, false, greenBall, "");


        private boolean done = false;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Item) {
                if (!done && row == 0) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                    done = true;
                }

                Item item = (Item) value;
                String txt = String.valueOf(item.getAmount());
                label.updateBackground(component.getBackground(), row, isSelected);
                label.setText(txt);

                if (item.getOrderState() == Statics.ItemOrderStates.Ordered) {
                    label.setIcon(blueBall);
                } else if (item.getOrderState() == Statics.ItemOrderStates.Planned) {
                    label.setIcon(yellowBall);
                } else {
                    if (item.getAmount() > 0) {
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
