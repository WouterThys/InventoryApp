package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;

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

    private final AmountType amountType;
    private PcbItemTableModelListener modelListener;
    private final boolean showSetValues;

    public ILinkedPcbItemTableModel(AmountType amountType, boolean showSetValues) {
        super(COLUMN_NAMES, COLUMN_CLASSES);
        this.amountType = amountType;
        this.showSetValues = showSetValues;
    }

    public ILinkedPcbItemTableModel(AmountType amountType, boolean showSetValues, PcbItemTableModelListener modelListener) {
        this(amountType, showSetValues);
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
                        case ItemAmount: amount = 0; break; //TODO#24 pcbItem.getReferences().size(); break;
                        case OrderAmount: amount = pcbItem.getOrderAmount(); break;
                        case UsedAmount:
                            if (modelListener != null) {
                                amount = modelListener.onGetLink(pcbItem).getUsedCount();
                            }
                            break;
                    }
                    return amount;
                case 1: // Pcb item name and value
                    if (!showSetValues && pcbItem.getMatchedItemLink().isSetItem()) {
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
                    PcbItemItemLink link = pcbItem.getMatchedItemLink();
                    if (!showSetValues || !link.isSetItem()) {
                        return link.getItem().toString();
                    } else {
                        return link.getSetItem().toString();
                    }

            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        PcbItem item = (PcbItem) getValueAt(rowIndex, -1);
        switch (amountType) {
            case ItemAmount:
                return false;
            case OrderAmount:
                return ((columnIndex == 0) && !item.isOrdered());
            case UsedAmount:
                return ((columnIndex == 0) && !modelListener.onGetLink(item).isUsed());
        }
        return false;
    }
}