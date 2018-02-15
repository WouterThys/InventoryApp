package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.utils.icomponents.IAbstractTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static com.waldo.inventory.gui.Application.colorResource;

public class ILinkedPcbItemTableModel extends IAbstractTableModel<PcbItemProjectLink> {

    private static final String[] COLUMN_NAMES = {"#", "Pcb item", "Item"};
    private static final Class[] COLUMN_CLASSES = {Integer.class, String.class, String.class};

    public enum AmountType {
        ItemAmount,
        OrderAmount,
        UsedAmount
    }

    private final AmountType amountType;
    private final boolean showSetValues;

    public ILinkedPcbItemTableModel(AmountType amountType, boolean showSetValues) {
        super(COLUMN_NAMES, COLUMN_CLASSES);
        this.amountType = amountType;
        this.showSetValues = showSetValues;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PcbItemProjectLink link = getItemAt(rowIndex);

        if (link != null) {
            PcbItem pcbItem = link.getPcbItem();
            switch (columnIndex) {
                case -1:
                    return link;
                case 0: // Amount
                    int amount = 0;
                    switch (amountType) {
                        case ItemAmount: amount = link.getNumberOfItems(); break;
                        case OrderAmount: amount = pcbItem.getOrderAmount(); break;
                        case UsedAmount: amount = link.getUsedCount(); break;
                    }
                    return amount;
                case 1: // Pcb item name and value
                    if (!showSetValues && link.hasMatchedItem()) {
                        return link.getPrettyName();
                    }
                case 2: // Item name
                    if (link.hasMatchedItem()) {
                        PcbItemItemLink itemLink = link.getPcbItemItemLink();
                        if (!showSetValues) {
                            return itemLink.getItem().toString();
                        }
                    }
                    return "";
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        PcbItemProjectLink link = (PcbItemProjectLink) getValueAt(rowIndex, -1);
        switch (amountType) {
            case ItemAmount:
                return false;
            case OrderAmount:
                return ((columnIndex == 0) && !link.getPcbItem().isOrdered());
            case UsedAmount:
                return ((columnIndex == 0) && !link.isUsed());
        }
        return false;
    }

    @Override
    public boolean hasTableCellRenderer() {
        return true;
    }

    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                PcbItemProjectLink link = getItemAt(row);
                PcbItem p = link.getPcbItem();

                if (!isSelected) {
                    component.setBackground(getBackground());
                    if (p.isOrdered()) {
                        component.setBackground(colorResource.readColor("Green.Light"));
                    } else {
                        if (p.getOrderAmount() > 0) {
                            component.setBackground(colorResource.readColor("Blue.Light"));
                        }
                    }
                }

                if (p.isOrdered()) {
                    component.setForeground(Color.gray);
                } else {
                    component.setForeground(Color.black);
                }

                return component;
            }
        };
    }
}