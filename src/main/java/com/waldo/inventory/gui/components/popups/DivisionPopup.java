package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.gui.components.actions.AddAction;
import com.waldo.inventory.gui.components.actions.DeleteAction;
import com.waldo.inventory.gui.components.actions.EditAction;
import com.waldo.inventory.gui.components.actions.WizardAction;

import javax.swing.*;

public abstract class DivisionPopup extends JPopupMenu {

    private enum DivisionType {
        RootItem,
        RootSet,
        Category,
        Product,
        Type,
        Set
    }

    protected DivisionPopup(DbObject division) {
        super();

        switch (DbObject.getType(division)) {
            case DbObject.TYPE_ITEM:
                init(DivisionType.RootItem);
                break;
            case DbObject.TYPE_CATEGORY:
                init(DivisionType.Category);
                break;
            case DbObject.TYPE_PRODUCT:
                init(DivisionType.Product);
                break;
            case DbObject.TYPE_TYPE:
                init(DivisionType.Type);
                break;
            case DbObject.TYPE_SET:
                if (division.canBeSaved()) {
                    init(DivisionType.Set);
                } else {
                    init(DivisionType.RootSet);
                }
                break;
        }
    }

    public abstract void onAddDivision();
    public abstract void onEditDivision();
    public abstract void onDeleteDivision();
    public abstract void onSetWizardAction();

    private void init(DivisionType divisionType) {

        AddAction addDivisionAction = new AddAction() {
            @Override
            public void onAdd() {
                onAddDivision();
            }
        };

        EditAction editDivisionAction = new EditAction() {
            @Override
            public void onEdit() {
                onEditDivision();
            }
        };

        DeleteAction deleteDivisionAction = new DeleteAction() {
            @Override
            public void onDelete() {
                onDeleteDivision();
            }
        };

        WizardAction setItemsWizardAction = new WizardAction() {
            @Override
            public void onMagic() {
                onSetWizardAction();
            }
        };

        switch (divisionType) {
            case RootItem:
                addDivisionAction.setName("Add category");
                add(addDivisionAction);
                break;
            case RootSet:
                addDivisionAction.setName("Add set");
                add(addDivisionAction);
                break;
            case Category:
                addDivisionAction.setName("Add product");
                editDivisionAction.setName("Edit category");
                deleteDivisionAction.setName("Delete category");
                add(addDivisionAction);
                add(editDivisionAction);
                add(deleteDivisionAction);
                break;
            case Product:
                addDivisionAction.setName("Add type");
                editDivisionAction.setName("Edit product");
                deleteDivisionAction.setName("Delete product");
                add(addDivisionAction);
                add(editDivisionAction);
                add(deleteDivisionAction);
                break;
            case Type:
                editDivisionAction.setName("Edit type");
                deleteDivisionAction.setName("Delete type");
                add(editDivisionAction);
                add(deleteDivisionAction);
                break;
            case Set:
                editDivisionAction.setName("Edit set");
                deleteDivisionAction.setName("Delete set");
                setItemsWizardAction.setName("Set item wizard");
                add(editDivisionAction);
                add(deleteDivisionAction);
                addSeparator();
                add(setItemsWizardAction);
                break;
        }

    }
}
