package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.gui.components.actions.AddDivisionAction;
import com.waldo.inventory.gui.components.actions.DeleteDivisionAction;
import com.waldo.inventory.gui.components.actions.EditDivisionAction;

import javax.swing.*;

public abstract class DivisionPopup extends JPopupMenu {

    public enum DivisionType {
        Root,
        Category,
        Product,
        Type
    }

    protected DivisionPopup(DbObject division, boolean isRoot) {
        super();

        switch (DbObject.getType(division)) {
            case DbObject.TYPE_CATEGORY:
                if (isRoot) {
                    init(DivisionType.Root);
                } else {
                    init(DivisionType.Category);
                }
                break;
            case DbObject.TYPE_PRODUCT:
                init(DivisionType.Product);
                break;
            case DbObject.TYPE_TYPE:
                init(DivisionType.Type);
                break;
        }
    }

    public abstract void onAddDivision();
    public abstract void onEditDivision();
    public abstract void onDeleteDivision();

    private void init(DivisionType divisionType) {

        AddDivisionAction addDivisionAction = new AddDivisionAction() {
            @Override
            public void onAddDivision() {
                DivisionPopup.this.onAddDivision();
            }
        };

        EditDivisionAction editDivisionAction = new EditDivisionAction() {
            @Override
            public void onEditDivision() {
                DivisionPopup.this.onEditDivision();
            }
        };

        DeleteDivisionAction deleteDivisionAction = new DeleteDivisionAction() {
            @Override
            public void onDeleteDivision() {
                DivisionPopup.this.onDeleteDivision();
            }
        };

        switch (divisionType) {
            case Root:
                addDivisionAction.putValue(AbstractAction.NAME, "Add category");
                add(addDivisionAction);
                break;
            case Category:
                addDivisionAction.putValue(AbstractAction.NAME, "Add product");
                editDivisionAction.putValue(AbstractAction.NAME, "Edit category");
                deleteDivisionAction.putValue(AbstractAction.NAME, "Delete category");
                add(addDivisionAction);
                add(editDivisionAction);
                add(deleteDivisionAction);
                break;
            case Product:
                addDivisionAction.putValue(AbstractAction.NAME, "Add type");
                editDivisionAction.putValue(AbstractAction.NAME, "Edit product");
                deleteDivisionAction.putValue(AbstractAction.NAME, "Delete product");
                add(addDivisionAction);
                add(editDivisionAction);
                add(deleteDivisionAction);
                break;
            case Type:
                editDivisionAction.putValue(AbstractAction.NAME, "Edit type");
                deleteDivisionAction.putValue(AbstractAction.NAME, "Delete type");
                add(editDivisionAction);
                add(deleteDivisionAction);
                break;
        }

    }
}
