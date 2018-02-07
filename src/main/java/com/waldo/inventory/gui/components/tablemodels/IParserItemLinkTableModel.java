package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.ParserItemLink;
import com.waldo.utils.icomponents.IAbstractTableModel;

public class IParserItemLinkTableModel extends IAbstractTableModel<ParserItemLink> {

    private static final String[] COLUMN_NAMES = {"Component name", "Category", "Product", "Type"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, String.class, String.class};

    public IParserItemLinkTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ParserItemLink link = getItemAt(rowIndex);
        if (link != null) {
            switch (columnIndex) {
                case -1: // Reference to object itself
                    return link;
                case 0: // Component name
                    return link.getPcbItemName();
                case 1: // Category
                    if (link.getCategoryId() > DbObject.UNKNOWN_ID) {
                        return link.getCategory().toString();
                    } else {
                        return "";
                    }
                case 2: // Product
                    if (link.getProductId() > DbObject.UNKNOWN_ID) {
                        return link.getProduct().toString();
                    } else {
                        return "";
                    }
                case 3: // Type
                    if (link.getTypeId() > DbObject.UNKNOWN_ID) {
                        return link.getType().toString();
                    } else {
                        return "";
                    }

            }
        }
        return null;
    }
}
