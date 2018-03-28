package com.waldo.inventory.gui.dialogs.editdistributorpartlinkdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.DistributorPartLink;
import com.waldo.inventory.gui.dialogs.distributorsdialog.DistributorsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditDistributorPartLinkDialog extends EditDistributorPartLinkDialogLayout {


    public EditDistributorPartLinkDialog(Window parent, String title, DistributorPartLink distributorPartLink) {
        super(parent, title);

        initializeComponents();
        initializeLayouts();

        updateComponents(distributorPartLink);
    }

    private boolean verify() {
        boolean ok = true;
        if (distributorPartLink.getDistributorId() <= DbObject.UNKNOWN_ID) {
            JOptionPane.showMessageDialog(this,
                    "Distributor can not be empty..",
                    "Empty distributor",
                    JOptionPane.ERROR_MESSAGE);
            ok = false;
        }

        if (ok) {
            if (distributorPartLink.getItemRef().isEmpty()) {
                referenceTf.setError("Reference can not be empty..");
                ok = false;
            }
        }

        return ok;
    }

    public void enableDistributor(boolean enable) {
        enableDistributorSelect(enable);
    }

    //
    // Dialog
    //
    @Override
    protected void onOK() {
        if (verify()) {
            setPrice(distributorPartLink);
            super.onOK();
        }
    }

    @Override
    protected void onCancel() {
        originalPartLink.createCopy(distributorPartLink);
        distributorPartLink.setCanBeSaved(true);
        super.onCancel();
    }

    //
    // Edited listener
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {

    }

    @Override
    public DbObject getGuiObject() {
        if (isShown) {
            return distributorPartLink;
        }
        return null;
    }

    //
    // Add distributor
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        DistributorsDialog distributorsDialog = new DistributorsDialog(EditDistributorPartLinkDialog.this);
        distributorsDialog.showDialog();

        updateDistributorCb(distributorPartLink);
    }


}