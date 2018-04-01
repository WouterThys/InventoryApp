package com.waldo.inventory.gui.dialogs.editdivisiondialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.gui.components.IObjectDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ITextField;

import java.awt.*;

public class EditDivisionDialog extends IObjectDialog <Division> {

    private ITextField nameTf;
    private IComboBox<Division> parentCb;

    private Division parentDivision;

    public EditDivisionDialog(Window parent, Division division) {
        super(parent, "Edit " + division.getName(), division, Division.class);
        this.parentDivision = division.getParentDivision();
    }

    public EditDivisionDialog(Window parent, Division division, Division parentDivision) {
        super(parent, "Add division", division, Division.class);
        this.parentDivision = parentDivision;
    }

    @Override
    public VerifyState verify(Division toVerify) {
        VerifyState ok = VerifyState.Ok;
        String name = nameTf.getText();
        if (name == null || name.isEmpty()) {
            nameTf.setError("Name can not be empty..");
            ok = VerifyState.Error;
        } else {
            if (getObject().getId() <= DbObject.UNKNOWN_ID) {
                Division foundDivision = SearchManager.sm().findDivisionByName(name);
                if (foundDivision != null) {
                    nameTf.setError("Division already exists..");
                    ok = VerifyState.Error;
                }
            }
        }
        return ok;
    }

    @Override
    public void initializeComponents() {


        nameTf = new ITextField(this, "name");
    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object... objects) {

    }
}
