package com.waldo.inventory.gui.dialogs.editcreatedpcbdialog;

import com.waldo.inventory.classes.dbclasses.CreatedPcb;
import com.waldo.inventory.classes.dbclasses.CreatedPcbLink;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialogLayout.SearchType;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;

public class EditCreatedPcbDialog extends EditCreatedPcbDialogLayout {


    public EditCreatedPcbDialog(Window window, String title, ProjectPcb projectPcb, CreatedPcb createdPcb) {
        super(window, title, projectPcb, createdPcb);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

    @Override
    void onSaveAllAction(CreatedPcb createdPcb) {
        if (createdPcb != null) {

        }
    }

    @Override
    void onAutoCalculateUsed(CreatedPcbLink link) {
        if (link != null && link.getPcbItemItemLink() != null && link.getPcbItemProjectLink() != null) {
            int desired = Math.min(link.getPcbItemProjectLink().getNumberOfItems(), link.getPcbItemItemLink().getItem().getAmount());
            usedAmountSp.setTheValue(desired);
        }
    }

    @Override
    void onEditItem(CreatedPcbLink link) {
        if (link != null && link.getPcbItemItemLink() != null) {
            EditItemDialog dialog = new EditItemDialog<>(EditCreatedPcbDialog.this, "Item", link.getPcbItemItemLink().getItem());
            dialog.showDialog();
        }
    }

    @Override
    void onSearchUsedItem(CreatedPcbLink link) {
        if (link != null) {
            AdvancedSearchDialog dialog = new AdvancedSearchDialog(
                    EditCreatedPcbDialog.this,
                    "Search",
                    SearchType.PcbItem,
                    link.getPcbItemProjectLink());

            if (dialog.showDialog() == IDialog.OK) {
                Item newUsedItem = dialog.getSelectedItem();
                if (newUsedItem != null) {
                    link.setUsedItemId(newUsedItem.getId());
                }
                updateLinkInfo(link);
                updateTable();
            }
        }
    }

    @Override
    void onDeleteUsedItem(CreatedPcbLink link) {
        if (link != null) {
            int res = JOptionPane.showConfirmDialog(
                    EditCreatedPcbDialog.this,
                    "Delete used item " + link.getUsedItem() + " from PCB?",
                    "Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (res == JOptionPane.YES_OPTION) {
                link.setUsedItemId(0);
                updateLinkInfo(link);
                updateTable();
            }
        }
    }
}
