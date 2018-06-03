package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class IOrderLineTableModel extends IAbstractTableModel<OrderLine> {

    private static final String[] COLUMN_NAMES = {"", "#", "Name", "Manufacturer", "Reference", "Price", "Total"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, Integer.class, String.class, String.class, String.class, String.class, String.class};

    private boolean isEditable = false;

    public IOrderLineTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public void setItemList(List<OrderLine> itemList) {
        super.setItemList(itemList);

        if (itemList.size() > 0) {
            Order order = itemList.get(0).getOrder();
            isEditable = order != null && (order.isPlanned() || !order.isLocked());
        }
    }

    @Override
    public void clearItemList() {
        super.clearItemList();
        isEditable = false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OrderLine line = getItemAt(rowIndex);
        if (line != null) {
            switch (columnIndex) {
                case 0:
                case -1: // Reference to object itself
                    return line;
                case 1: // Amount
                    return line.getAmount();
                case 2: // Name
                    return line.toString();
                case 3: // Manufacturer
                    if (line.getItem() != null) {
                        Manufacturer m = line.getItem().getManufacturer();
                        if (m != null && m.getId() != DbObject.UNKNOWN_ID) {
                            return m.toString();
                        }
                    } else {
                        if (line.getPcb() != null) {
                            Project p = line.getPcb().getProject();
                            if (p != null && p.getId() != DbObject.UNKNOWN_ID) {
                                return p.toString();
                            }
                        }
                    }
                    return "";
                case 4: // Reference
                    DistributorPartLink pn = line.getDistributorPartLink();
                    if (pn != null) {
                        return pn.toString();
                    } else {
                        return "";
                    }
                case 5: // Price
                    return line.getPrice();
                case 6: // Total
                    return line.getTotalPrice(); // Amount * price
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1 && isEditable;
    }

    @Override
    public boolean hasTableCellRenderer() {
        return true;
    }

    private static final Renderer renderer = new Renderer();
    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return renderer;
    }

    static class Renderer extends DefaultTableCellRenderer {

        private static final ImageIcon imageOk = imageResource.readIcon("Orders.Table.Ok");
        private static final ImageIcon imageWarn = imageResource.readIcon("Orders.Table.Warning");
        private static final ImageIcon imageError = imageResource.readIcon("Orders.Table.Error");

        private static final ITableLabel label = new ITableLabel(Color.gray, 0, false, imageOk, "");

        private boolean done = false;
        private Color defaultBackground;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof OrderLine) {
                if (!done && row == 0) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                    done = true;
                }

                if (defaultBackground == null && !isSelected) {
                    defaultBackground = c.getBackground();
                }

                c.setBackground(defaultBackground);
                label.updateBackground(c.getBackground(), row, isSelected);
                OrderLine orderLine = (OrderLine) value;

                if (orderLine.getItem() != null) {
                    Item item = orderLine.getItem();
                    if (item.isDiscourageOrder()) {
                        label.setBackground(Color.ORANGE);
                        c.setBackground(Color.ORANGE);
                    }
                }

                boolean amountOk = orderLine.getAmount() > 0;
                boolean referenceOk = orderLine.getDistributorPartId() > DbObject.UNKNOWN_ID;
                if (amountOk && referenceOk) {
                    label.setIcon(imageOk);
                    label.setToolTipText(null);
                } else if (!referenceOk) {
                    label.setIcon(imageError);
                    label.setToolTipText("Reference is not set..");
                } else {
                    label.setIcon(imageWarn);
                    label.setToolTipText("Amount is 0..");
                }

                return label;
            }
            return c;
        }
    }
}
