package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class IOrderItemTableModel extends IAbstractTableModel<OrderItem> {

    private static final String[] COLUMN_NAMES = {"", "#", "Name", "Manufacturer", "Reference", "Price", "Total"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, Integer.class, String.class, String.class, String.class, String.class, String.class};

    private boolean isEditable = false;

    public IOrderItemTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public void setItemList(List<OrderItem> itemList) {
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
        OrderItem orderItem = getItemAt(rowIndex);
        if (orderItem != null) {
            switch (columnIndex) {
                case 0:
                case -1: // Reference to object itself
                    return orderItem;
                case 1: // Amount
                    return orderItem.getAmount();
                case 2: // Name
                    return orderItem.getItem().toString();
                case 3: // Manufacturer
                    Manufacturer m = SearchManager.sm().findManufacturerById(orderItem.getItem().getManufacturerId());
                    if (m != null && m.getId() != DbObject.UNKNOWN_ID) {
                        return m.toString();
                    }
                    return "";
                case 4: // Reference
                    DistributorPartLink pn = orderItem.getDistributorPartLink();
                    if (pn != null) {
                        return pn.toString();
                    } else {
                        return "";
                    }
                case 5: // Price
                    return orderItem.getPrice();
                case 6: // Total
                    return orderItem.getTotalPrice(); // Amount * price
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
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof OrderItem) {
                if (!done && row == 0) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                    done = true;
                }

                label.updateBackground(c.getBackground(), row, isSelected);
                OrderItem orderItem = (OrderItem) value;

                boolean amountOk = orderItem.getAmount() > 0;
                boolean referenceOk = orderItem.getDistributorPartId() > DbObject.UNKNOWN_ID;
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
