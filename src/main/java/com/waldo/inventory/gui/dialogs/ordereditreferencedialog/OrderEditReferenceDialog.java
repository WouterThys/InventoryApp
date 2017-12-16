package com.waldo.inventory.gui.dialogs.ordereditreferencedialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.DistributorPartLink;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;

public class OrderEditReferenceDialog extends OrderEditReferenceDialogLayout
        implements CacheChangedListener<DistributorPartLink> {

    private boolean canClose = true;

    public OrderEditReferenceDialog(Application application, String title, OrderItem orderItem) {
        super(application, title);

       addCacheListener(DistributorPartLink.class,this);

        initializeComponents();
        initializeLayouts();
        updateComponents(orderItem);

    }

    private void saveLink() {
        selectedDistributorPartLink.save();
        originalDistributorPartLink = selectedDistributorPartLink.createCopy();
    }

    private void showSaveDialog() {
        if (selectedDistributorPartLink != null) {
            String msg = selectedDistributorPartLink.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                saveLink();
                dialogResult = OK;
                dispose();
            } else {
                onCancel();
            }
        } else {
            dialogResult = OK;
            dispose();
        }
        canClose = true;
    }

    private boolean checkChange() {
        return selectedDistributorPartLink != null &&
                (!selectedDistributorPartLink.getItemRef().equals(
                        originalDistributorPartLink.getItemRef()));
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

        if (canClose) {
            dialogResult = OK;
            dispose();
        }
    }

    @Override
    protected void onNeutral() {
        saveLink();
        getButtonNeutral().setEnabled(false);
    }

    @Override
    protected void onCancel() {
        if (selectedDistributorPartLink != null && originalDistributorPartLink != null) {
            originalDistributorPartLink.createCopy(selectedDistributorPartLink);
            selectedDistributorPartLink.setCanBeSaved(true);
        }
        super.onCancel();
    }

    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        getButtonNeutral().setEnabled(checkChange());
    }

    @Override
    public DbObject getGuiObject() {
        if (isShown) {
            return selectedDistributorPartLink;
        }
        return null;
    }


    //
    // Listener
    //
    @Override
    public void onInserted(DistributorPartLink link) {
        orderItem.updateDistributorPart();
        orderItem.save();
    }

    @Override
    public void onUpdated(DistributorPartLink link) {
        orderItem.updateDistributorPart();
        orderItem.save();
    }

    @Override
    public void onDeleted(DistributorPartLink link) {
        // Can not happen
    }

    @Override
    public void onCacheCleared() {
        // Don't care
    }
}