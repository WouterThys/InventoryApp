package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.managers.SearchManager;

public class IOrderItemTableModel extends IAbstractTableModel<OrderItem> {

    private static final String[] COLUMN_NAMES = {"Name", "Description", "Manufacturer", "Reference", "Amount", "Price", "Total"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, String.class, String.class, Number.class, Double.class, Double.class};

    public IOrderItemTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OrderItem orderItem = getItemAt(rowIndex);
        if (orderItem != null) {
            switch (columnIndex) {
                case -1: // Reference to object itself
                    return orderItem;
                case 0: // Name
                    return orderItem.getItem().toString();
                case 1: // Description
                    return orderItem.getItem().getDescription();
                case 2: // Manufacturer
                    Manufacturer m = SearchManager.sm().findManufacturerById(orderItem.getItem().getManufacturerId());
                    if (m != null && m.getId() != DbObject.UNKNOWN_ID) {
                        return m.toString();
                    }
                    return "";
                case 3: // Reference
                    DistributorPartLink pn = orderItem.getDistributorPartLink();
                    if (pn != null) {
                        return pn.getItemRef();
                    } else {
                        return "";
                    }
                case 4: // Amount
                    return orderItem.getAmount();
                case 5: // Price
                    return orderItem.getItem().getPrice();
                case 6: // Total
                    return orderItem.getAmount() * orderItem.getItem().getPrice(); // Amount * price
            }
        }
        return null;
    }
}
