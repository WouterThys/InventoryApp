package com.waldo.inventory.gui.dialogs.editdivisiondialog;

import com.waldo.inventory.Utils.Statics.IconDisplayType;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.gui.components.IObjectDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.selectdivisiondialog.SelectDivisionDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ICheckBox;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

public class EditDivisionDialog extends IObjectDialog<Division> {

    private ITextField nameTf;
    private ITextField parentTf;
    private ICheckBox canHaveValueCb;
    private IComboBox<IconDisplayType> displayTypeCb;

    private IActions.EditAction editParentAction;

    private Division parentDivision;

    public EditDivisionDialog(Window parent, Division division, Division parentDivision) {
        super(parent, "Divisions", division, Division.class);
        this.parentDivision = parentDivision;

        initializeComponents();
        initializeLayouts();
        updateComponents();
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
        parentTf = new ITextField(false);

        editParentAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectDivisionDialog dialog = new SelectDivisionDialog(EditDivisionDialog.this, getObject());
                if (dialog.showDialog() == ICacheDialog.OK) {
                    Division newParent = dialog.getSelectedDivision();
                    if (newParent != null && !newParent.equals(parentDivision)) {
                        getObject().setParentDivisionId(newParent.getId());
                        parentTf.setText(newParent.getName());
                        onValueChanged(null, "parentDivisionId", null, null);
                    }
                }
            }
        };

        canHaveValueCb = new ICheckBox("Components can have value");
        canHaveValueCb.addEditedListener(this, "canHaveValue");

        displayTypeCb = new IComboBox<>(IconDisplayType.values());
        displayTypeCb.addItemListener(e -> {
            if (EditDivisionDialog.this.isShown && e.getStateChange() == ItemEvent.SELECTED) {
                if (getObject() != null) {
                    getObject().setDisplayType((IconDisplayType) displayTypeCb.getSelectedItem());
                    onValueChanged(displayTypeCb, "displayType", null, null);
                }
            }
        });
    }

    @Override
    public void initializeLayouts() {
        JPanel panel = new JPanel();

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(panel);
        gbc.addLine("Name: ", nameTf);
        gbc.addLine("Parent: ", GuiUtils.createComponentWithActions(parentTf, editParentAction));
        gbc.addLine("Display type: ", displayTypeCb);
        gbc.addLine("", canHaveValueCb);

        panel.setBorder(BorderFactory.createEmptyBorder(5,10,20,10));

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(panel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... objects) {
        if (getObject() != null) {
            nameTf.setText(getObject().getName());
            canHaveValueCb.setSelected(getObject().isCanHaveValue());
            displayTypeCb.setSelectedItem(getObject().getDisplayType());
        }

        if (parentDivision != null && parentDivision.canBeSaved()) {
            parentTf.setText(parentDivision.getName());
        }
    }
}
