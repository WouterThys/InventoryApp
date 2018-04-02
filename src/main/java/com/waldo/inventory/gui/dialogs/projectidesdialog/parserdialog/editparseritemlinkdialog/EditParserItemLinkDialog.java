package com.waldo.inventory.gui.dialogs.projectidesdialog.parserdialog.editparseritemlinkdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.ParserItemLink;

import java.awt.*;

public class EditParserItemLinkDialog extends EditParserItemLinkDialogLayout {


    public EditParserItemLinkDialog(Window parent, String title, ParserItemLink link) {
        super(parent, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(link);
    }

    @Override
    protected void onOK() {

//        Category c = (Category) categoryCb.getSelectedItem();
//        Product p = (Product) productCb.getSelectedItem();
//        com.waldo.inventory.classes.dbclasses.Type t = (com.waldo.inventory.classes.dbclasses.Type) typeCb.getSelectedItem();
//
//        if (c != null) {
//            parserItemLink.setCategoryId(c.getId());
//            if (p != null) {
//                parserItemLink.setProductId(p.getId());
//                if (t != null) {
//                    parserItemLink.setTypeId(t.getId());
//                } else {
//                    parserItemLink.setTypeId(DbObject.UNKNOWN_ID);
//                }
//            } else {
//                parserItemLink.setProductId(DbObject.UNKNOWN_ID);
//                parserItemLink.setTypeId(DbObject.UNKNOWN_ID);
//            }
//        } else {
//            parserItemLink.setCategoryId(DbObject.UNKNOWN_ID);
//            parserItemLink.setProductId(DbObject.UNKNOWN_ID);
//            parserItemLink.setTypeId(DbObject.UNKNOWN_ID);
//        }

        super.onOK();
    }

    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        updateEnabledComponents();
    }

    @Override
    public DbObject getGuiObject() {
        return parserItemLink;
    }
}