package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.managers.SearchManager;

import java.util.List;

public class IOrderItemTableModel extends IAbstractTableModel<OrderItem> {

    private static final String[] COLUMN_NAMES = {"", "#", "Name", "Manufacturer", "Reference", "Price", "Total"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, Integer.class, String.class, String.class, String.class, Double.class, Double.class};

    private boolean isEditable = false;

    public IOrderItemTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public void setItemList(List<OrderItem> itemList) {
        super.setItemList(itemList);

        if (itemList.size() > 0) {
            Order order = itemList.get(0).getOrder();
            isEditable = order != null && order.isPlanned();
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
//                case 0: // State
//                    boolean amountOk = orderItem.getAmount() > 0;
//                    boolean referenceOk = orderItem.getDistributorPartId() > DbObject.UNKNOWN_ID;
//                    if (amountOk && referenceOk) {
//                        return imageOk;
//                    } else if (!referenceOk) {
//                        return imageError;
//                    }
//                    return imageWarn;
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
                        return pn.getItemRef();
                    } else {
                        return "";
                    }
                case 5: // Price
                    return orderItem.getItem().getPrice();
                case 6: // Total
                    return orderItem.getAmount() * orderItem.getItem().getPrice(); // Amount * price
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1 && isEditable;
    }
}
