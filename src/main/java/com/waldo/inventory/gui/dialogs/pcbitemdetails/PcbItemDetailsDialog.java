package com.waldo.inventory.gui.dialogs.pcbitemdetails;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;

public class PcbItemDetailsDialog extends PcbItemDetailsDialogLayout {

    private boolean canClose = true;

    public PcbItemDetailsDialog(Application application, String title, PcbItemProjectLink itemProjectLink) {
        super(application, title, itemProjectLink);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    private void showSaveDialog() {
        if (pcbItemProjectLink != null) {
            String msg = "Edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                pcbItemProjectLink.save();
                originalProjectLink = pcbItemProjectLink.createCopy();
            }
        }
        canClose = true;
    }

    private boolean checkChange() {
        return (pcbItemProjectLink != null) && !(pcbItemProjectLink.equals(originalProjectLink));
    }

    private boolean updateItemAmount(int delta) {
        boolean canSetAmount = false;

        if ((newItemAmount + delta) >= 0) {
            newItemAmount += delta;
            setItemAmount(newItemAmount);
            canSetAmount = true;
        }

        return canSetAmount;
    }

    //
    // Dialog
    //
    @Override
    protected void onOK() {
        if (checkChange()) {
            canClose = false;
            showSaveDialog();
        }

        super.onOK();
    }

    @Override
    protected void onNeutral() {
        pcbItemProjectLink.save();
        originalProjectLink = pcbItemProjectLink.createCopy();
        getButtonNeutral().setEnabled(false);
    }

    @Override
    protected void onCancel() {
        if (pcbItemProjectLink != null && originalProjectLink != null) {
            originalProjectLink.createCopy(pcbItemProjectLink);
            pcbItemProjectLink.setCanBeSaved(true);
        }
        super.onCancel();
    }

    //
    // Edited
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        if (component.equals(usedSpinner)) {
            if (updateItemAmount((int)previousValue - (int)newValue)) {
                getButtonNeutral().setEnabled(checkChange());
            }
        } else {
            getButtonNeutral().setEnabled(checkChange());
        }
    }

    @Override
    public DbObject getGuiObject() {
        if (isShown) {
            return pcbItemProjectLink;
        }
        return null;
    }

}