package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.DistributorPartLink;

public class IDistributorPartTableModel extends IAbstractTableModel<DistributorPartLink> {
    private static final String[] COLUMN_NAMES = {"Distributor", "Reference"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, String.class};

    public IDistributorPartTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DistributorPartLink distributorPartLink = getItemAt(rowIndex);
        if (distributorPartLink != null) {
            switch (columnIndex) {
                case -1:
                    return distributorPartLink;
                case 0: // Distributor
                    return distributorPartLink.getDistributor().toString();
                case 1: // Reference
                    return distributorPartLink.toString();
            }
        }
        return null;
    }
}
