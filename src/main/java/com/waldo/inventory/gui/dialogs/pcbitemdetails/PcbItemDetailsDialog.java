package com.waldo.inventory.gui.dialogs.pcbitemdetails;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;
import com.waldo.inventory.gui.dialogs.alllinkeditemsdialog.AllLinkedItemsDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialogLayout.SearchType;

public class PcbItemDetailsDialog extends PcbItemDetailsDialogLayout implements CacheChangedListener<PcbItemItemLink> {


    public PcbItemDetailsDialog(Window parent, String title, PcbItemProjectLink itemProjectLink) {
        super(parent, title, itemProjectLink);

        addCacheListener(PcbItemItemLink.class, this);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    @Override
    void onSelectNewItem() {
        AdvancedSearchDialog dialog = new AdvancedSearchDialog(
                this,
                "Search item",
                SearchType.PcbItem,
                pcbItemProjectLink);
        if (dialog.showDialog() == IDialog.OK) {
            updatePcbItemLink(pcbItemProjectLink, dialog.getSelectedItem());
        }
    }

    private void updatePcbItemLink(PcbItemProjectLink projectLink, Item newMatch) {
        if (projectLink != null) {
            PcbItem pcbItem = projectLink.getPcbItem();
            PcbItemItemLink itemLink = projectLink.getPcbItemItemLink();
            if (pcbItem != null && newMatch != null) {
                if (itemLink == null) {
                    // Create new
                    itemLink = new PcbItemItemLink(pcbItem, newMatch);
                } else {
                    // Update old
                    itemLink.setMatchedItem(newMatch);
                }
                itemLink.save();
            }
        }
    }

    private void updateOtherPcbItems(PcbItemItemLink itemLink) {
        for (PcbItemProjectLink itemProjectLink : SearchManager.sm().findPcbItemProjectLinksWithPcbItem(pcbItemProjectLink.getPcbItemId())) {
            if (itemProjectLink.getId() != pcbItemProjectLink.getId() && pcbItemProjectLink.getValue().equals(itemProjectLink.getValue())) {
                itemProjectLink.setPcbItemItemLinkId(itemLink.getId());
                itemProjectLink.save();
            }
        }
    }

    @Override
    void onDeleteMatchedItem() {
        if (pcbItemProjectLink != null && pcbItemProjectLink.getPcbItemItemLink() != null) {
            String msg = "Delete link with " + pcbItemProjectLink.getPcbItemItemLink().getLinkedItemName() + "?";
            if (JOptionPane.showConfirmDialog(this, msg, "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                PcbItemItemLink itemItemLink = pcbItemProjectLink.getPcbItemItemLink();
                if (itemItemLink != null) {
                    itemItemLink.delete(); // Should also delete other pcb item links
                    clearMatchedItemPanel();
                    updateEnabledComponents();
                }
            }
        }
    }

    @Override
    void onViewAllItemLinks() {
        if (pcbItemProjectLink != null) {
            AllLinkedItemsDialog dialog = new AllLinkedItemsDialog(this, pcbItemProjectLink.getPcbItem());
            dialog.showDialog();
        }
    }

    @Override
    void onSelectNewOrder() {

    }

    private void savePcbItemProjectLink(PcbItemProjectLink projectLink) {
        if (projectLink != null) {
            pcbItemProjectLink.save();
            originalProjectLink = pcbItemProjectLink.createCopy();
            getButtonNeutral().setEnabled(false);
            if (pcbItemProjectLink.hasMatchedItem()) {
                SwingUtilities.invokeLater(() -> updateOtherPcbItems(pcbItemProjectLink.getPcbItemItemLink()));
            }
        }
    }

    private void showSaveDialog() {
        if (pcbItemProjectLink != null) {
            String msg = "Edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                savePcbItemProjectLink(pcbItemProjectLink);
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
        savePcbItemProjectLink(pcbItemProjectLink);
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
        getButtonNeutral().setEnabled(checkChange());
        updateEnabledComponents();
    }

    @Override
    public DbObject getGuiObject() {
        if (isShown) {
            return pcbItemProjectLink;
        }
        return null;
    }

    //
    // Item link changed
    //
    @Override
    public void onInserted(PcbItemItemLink itemLink) {
        if (pcbItemProjectLink != null) {
            pcbItemProjectLink.setPcbItemItemLinkId(itemLink.getId());
            updateMatchedItemPanel(itemLink.getLinkedItemName(), itemLink.getLinkedItemAmount());
            onValueChanged(this, "pcbItemProjectLinkId", 0, itemLink.getId());
        }
    }

    @Override
    public void onUpdated(PcbItemItemLink itemLink) {
        if (pcbItemProjectLink != null) {
            pcbItemProjectLink.setPcbItemItemLinkId(itemLink.getId());
            updateMatchedItemPanel(itemLink.getLinkedItemName(), itemLink.getLinkedItemAmount());
            onValueChanged(this, "pcbItemProjectLinkId", 0, itemLink.getId());
        }
    }

    @Override
    public void onDeleted(PcbItemItemLink itemLink) {
        if (pcbItemProjectLink != null) {
            pcbItemProjectLink.setPcbItemItemLinkId(0);
            onValueChanged(this, "pcbItemProjectLinkId", itemLink.getId(), 0);
        }
    }

    @Override
    public void onCacheCleared() {

    }
}