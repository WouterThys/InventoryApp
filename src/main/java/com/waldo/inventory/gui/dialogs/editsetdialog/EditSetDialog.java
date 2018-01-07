package com.waldo.inventory.gui.dialogs.editsetdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import java.awt.*;

public class EditSetDialog extends EditSetDialogLayout {

    private boolean canClose = true;


    public EditSetDialog(Application application, String title, Set set) {
        super(application, title, set);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

    private void showSaveDialog() {
        if (selectedSet != null) {
            String msg = selectedSet.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    selectedSet.save();
                    originalSet = selectedSet.createCopy();
                    super.onOK();
                }
            }
        } else {
             super.onOK();
        }
        canClose = true;
    }

    private boolean verify() {
        boolean ok = true;
        if (nameTf.getText().isEmpty()) {
            nameTf.setError("Name can't be empty..");
            ok = false;
        } else {
            if (selectedSet.getId() < DbObject.UNKNOWN_ID) {
                Set foundSet = SearchManager.sm().findSetByName(nameTf.getText());
                if (foundSet != null) {
                    nameTf.setError("Name already exists..");
                    ok = false;
                }
            }
        }

        return ok;
    }

    private boolean checkChange() {
        return (selectedSet != null) && !(selectedSet.equals(originalSet));
    }

    @Override
    protected void onOK() {
        if (checkChange()) {
            canClose = false;
            showSaveDialog();
        }

        if (canClose) {
            super.onOK();
        }
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            selectedSet.save();
            originalSet = selectedSet.createCopy();
            getButtonNeutral().setEnabled(false);
        }

    }

    @Override
    protected void onCancel() {
        if (selectedSet != null && originalSet != null) {
            originalSet.createCopy(selectedSet);
            selectedSet.setCanBeSaved(true);
        }
        super.onCancel();
    }

    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        getButtonNeutral().setEnabled(checkChange());
    }

    @Override
    public DbObject getGuiObject() {
        if (isShown)  {
            return selectedSet;
        }
        return null;
    }
}