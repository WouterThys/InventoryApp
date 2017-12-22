package com.waldo.inventory.gui.dialogs.pcbitemdetails;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialogLayout.SearchType;

public class PcbItemDetailsDialog extends PcbItemDetailsDialogLayout {


    public PcbItemDetailsDialog(Application application, String title, PcbItemProjectLink itemProjectLink) {
        super(application, title, itemProjectLink);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    @Override
    void onSelectNewItem() {

        AdvancedSearchDialog dialog = new AdvancedSearchDialog(
                application,
                "Search item",
                SearchType.PcbItem,
                pcbItemProjectLink);
        if (dialog.showDialog() == IDialog.OK) {
            PcbItem pcbItem = pcbItemProjectLink.getPcbItem();
            DbObject newMatch = dialog.getSelectedItem();
            if (newMatch != null) {
                // TODO #24
                //updateMatchedItemPanel(newMatch.getName(), currentLink.getAmount());
            }
        }
    }

    @Override
    void onDeleteMatchedItem() {
        PcbItem pcbItem = pcbItemProjectLink.getPcbItem();
        // TODO #24
//        PcbItemItemLink itemItemLink = pcbItem.getMatchedItemLink();
//        if (itemItemLink != null) {
//            pcbItem.setMatchedItem(null);
//            itemItemLink.delete();
//            pcbItem.save();
//            clearMatchedItemPanel();
//        }
    }

    @Override
    void onSelectNewOrder() {

    }

    private void showSaveDialog() {
        if (pcbItemProjectLink != null) {
            String msg = "Edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                pcbItemProjectLink.save();
                originalProjectLink = pcbItemProjectLink.createCopy();
            }
        }
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