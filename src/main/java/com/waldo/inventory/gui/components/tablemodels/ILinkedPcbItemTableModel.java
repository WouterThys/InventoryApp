package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.classes.PcbItemProjectLink;

public class ILinkedPcbItemTableModel extends IAbstractTableModel<PcbItem> {

    private static final String[] COLUMN_NAMES = {"#", "Pcb item", "Item"};
    private static final Class[] COLUMN_CLASSES = {Integer.class, String.class, String.class};

    public enum AmountType {
        ItemAmount,
        OrderAmount,
        UsedAmount
    }

    public interface PcbItemTableModelListener {
        PcbItemProjectLink onGetLink(PcbItem pcbItem);
    }

    private AmountType amountType;
    private PcbItemTableModelListener modelListener;

    public ILinkedPcbItemTableModel(AmountType amountType) {
        super(COLUMN_NAMES, COLUMN_CLASSES);
        this.amountType = amountType;
    }

    public ILinkedPcbItemTableModel(AmountType amountType, PcbItemTableModelListener modelListener) {
        this(amountType);
        this.modelListener = modelListener;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PcbItem pcbItem = getItemAt(rowIndex);
        if (pcbItem != null) {
            switch (columnIndex) {
                case -1:
                    return pcbItem;
                case 0: // Amount
                    int amount = 0;
                    switch (amountType) {
                        case ItemAmount: amount = pcbItem.getReferences().size(); break;
                        case OrderAmount: amount = pcbItem.getOrderAmount(); break;
                        case UsedAmount:
                            if (modelListener != null) {
                                amount = modelListener.onGetLink(pcbItem).getUsedCount();
                            }
                            break;
                    }
                    return amount;
                case 1: // Pcb item name and value
                    if (pcbItem.getMatchedItemLink().isSetItem()) {
                        return pcbItem.getPartName() + " (Set)";
                    } else {
                        String name = pcbItem.getPartName();
                        String value = pcbItem.getValue();
                        if (name.equals(value)) {
                            return name;
                        } else {
                            return name + " - " + value;
                        }
                    }
                case 2: // Item name
                    return pcbItem.getMatchedItemLink().getItem().toString();
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        PcbItem item = (PcbItem) getValueAt(rowIndex, -1);
        return !item.isOrdered() && (columnIndex == 0);
    }
}