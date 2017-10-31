package com.waldo.inventory.gui.dialogs.editdistributorpartlinkdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.DistributorPartLink;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.distributorsdialog.DistributorsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditDistributorPartLinkDialog extends EditDistributorPartLinkDialogLayout {


    public EditDistributorPartLinkDialog(Application application, String title, DistributorPartLink distributorPartLink) {
        super(application, title);

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

    //
    // Dialog
    //
    @Override
    protected void onOK() {
        if (verify()) {
            super.onOK();
        }
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
        DistributorsDialog distributorsDialog = new DistributorsDialog(application, "Distributors");
        distributorsDialog.showDialog();

        updateComboBox(distributorPartLink);
    }


}