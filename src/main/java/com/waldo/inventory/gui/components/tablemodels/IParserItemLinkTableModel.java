package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.ParserItemLink;
import com.waldo.utils.icomponents.IAbstractTableModel;

public class IParserItemLinkTableModel extends IAbstractTableModel<ParserItemLink> {

    private static final String[] COLUMN_NAMES = {"Link name", "Division"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class};

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
                case 0: // Link name
                    return link.getPcbItemName();
                case 1: // Division
                    if (link.getDivisionId() > DbObject.UNKNOWN_ID) {
                        return link.getDivision().toString();
                    } else {
                        return "";
                    }
            }
        }
        return null;
    }
}
