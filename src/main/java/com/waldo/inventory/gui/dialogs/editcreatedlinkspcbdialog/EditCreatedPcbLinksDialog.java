package com.waldo.inventory.gui.dialogs.editcreatedlinkspcbdialog;

import com.waldo.inventory.classes.dbclasses.CreatedPcb;
import com.waldo.inventory.classes.dbclasses.CreatedPcbLink;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialogLayout.SearchType;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EditCreatedPcbLinksDialog extends EditCreatedPcbLinksDialogLayout implements CacheChangedListener<CreatedPcbLink>{


    public EditCreatedPcbLinksDialog(Window window, String title, ProjectPcb projectPcb, CreatedPcb createdPcb) {
        super(window, title, projectPcb, createdPcb);

        addCacheListener(CreatedPcbLink.class, this);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

    //
    // Cache listener
    //
    @Override
    public void onInserted(CreatedPcbLink link) {
        //updateLinkInfo(link);
        updateTable();
    }

    @Override
    public void onUpdated(CreatedPcbLink link) {
        //updateLinkInfo(link);
        updateTable();
    }

    @Override
    public void onDeleted(CreatedPcbLink link) {
        // Should not happen
    }

    @Override
    public void onCacheCleared() {
        // Don't care
    }

    //
    // Gui events
    //
    @Override
    void onSaveAllAction(CreatedPcb createdPcb) {
        List<CreatedPcbLink> linkList = getDisplayList();
        if (linkList != null && linkList.size() > 0) {
            for (CreatedPcbLink link : linkList) {
                link.save();
            }
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
            EditItemDialog dialog = new EditItemDialog<>(EditCreatedPcbLinksDialog.this, "Item", link.getPcbItemItemLink().getItem());
            dialog.showDialog();
        }
    }

    @Override
    void onSearchUsedItem(CreatedPcbLink link) {
        if (link != null) {
            AdvancedSearchDialog dialog = new AdvancedSearchDialog(
                    EditCreatedPcbLinksDialog.this,
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
                    EditCreatedPcbLinksDialog.this,
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

    //
    // Edited
    //
    @Override
    public void onValueChanged(Component component, String s, Object o, Object o1) {
        updateTable();
    }

    @Override
    public Object getGuiObject() {
        if (isShown) {
            return getSelectedLink();
        }
        return null;
    }
}
