package com.waldo.inventory.gui.dialogs.editcreatedlinkspcbdialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.SolderItemState;
import com.waldo.inventory.classes.Price;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.editremarksdialog.EditRemarksDialog;
import com.waldo.inventory.gui.dialogs.messageprogressdialog.MessageProgressDialog;
import com.waldo.inventory.gui.dialogs.pcbitemdetails.PcbItemDetailsDialog;
import com.waldo.inventory.gui.dialogs.solderiteminfodialog.SolderItemInfoDialog;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class EditCreatedPcbLinksDialog extends EditCreatedPcbLinksDialogLayout implements CacheChangedListener<CreatedPcbLink> {

    private final Window parent;
    private boolean creatingLinks = false;
    private MessageProgressDialog logDialog;

    public EditCreatedPcbLinksDialog(Window window, String title, ProjectPcb projectPcb, CreatedPcb createdPcb) {
        super(window, title, projectPcb, createdPcb);
        this.parent = window;
        addCacheListener(CreatedPcbLink.class, this);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    private void createPcb(CreatedPcb pcb) {
        if (pcb != null) {
            if (!pcb.isSoldered()) {
                if (hasErrors(pcb)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "There are errors in the PCB items, solve them before creating the PCB..",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                boolean canGoOn = true;
                if (hasUnsaved(pcb)) {
                    int res = JOptionPane.showConfirmDialog(
                            this,
                            "There are still unsaved links in the PCB items, are you sure you want to create the PCB?",
                            "Unsaved",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    canGoOn = (res == JOptionPane.YES_OPTION);
                }
                if (canGoOn && hasWarnings(pcb)) {
                    int res = JOptionPane.showConfirmDialog(
                            this,
                            "There are warnings in the PCB items, are you sure you want to create the PCB?",
                            "Warnings",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    canGoOn = (res == JOptionPane.YES_OPTION);
                }

                if (canGoOn) {
//                    for (CreatedPcbLink link : pcb.getCreatedPcbLinks()) {
//                        if (link.getUsedItemId() > DbObject.UNKNOWN_ID) {
//                            Item usedItem = link.getUsedItem();
//                            usedItem.setAmount(usedItem.getAmount() - link.getUsedAmount());
//                            usedItem.save();
//                        }
//                    } TODO
                    pcb.setDateSoldered(DateUtils.now());
                    pcb.save();

                    saveComponents();

                    //updateComponents();
                }
            }
        }
    }

    private boolean hasUnsaved(CreatedPcb pcb) {
        if (pcb != null) {
            for (CreatedPcbLink cpl : pcb.getCreatedPcbLinks()) {
                if (cpl.getState() == Statics.CreatedPcbLinkState.NotSaved) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasErrors(CreatedPcb pcb) {
        if (pcb != null) {
            for (CreatedPcbLink cpl : pcb.getCreatedPcbLinks()) {
                if (cpl.getState() == Statics.CreatedPcbLinkState.Error) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasWarnings(CreatedPcb pcb) {
        if (pcb != null) {
            for (CreatedPcbLink cpl : pcb.getCreatedPcbLinks()) {
                if (cpl.getState() == Statics.CreatedPcbLinkState.Warning) {
                    return true;
                }
            }
        }
        return false;
    }


    //
    // Cache listener
    //
    @Override
    public void onInserted(CreatedPcbLink link) {
        //updateLinkInfo(link);
        if (!creatingLinks) {
            selectedLink = link;
            updateLinkInfo(link);
            updateEnabledComponents();
            updateLinkTable();
        }
    }

    @Override
    public void onUpdated(CreatedPcbLink link) {
        //updateLinkInfo(link);
        if (!creatingLinks) {
            selectedLink = link;
            updateLinkInfo(link);
            updateEnabledComponents();
            updateLinkTable();
        }
    }

    @Override
    public void onDeleted(CreatedPcbLink link) {
        // Should not happen
    }

    @Override
    public void onCacheCleared() {
        // Don't care
    }

    private void saveComponents() {
        if (createdPcb != null) {
            List<CreatedPcbLink> linkList = createdPcb.getCreatedPcbLinks();
            if (linkList != null && linkList.size() > 0) {
                for (CreatedPcbLink link : linkList) {
                    link.save();
                }

                JOptionPane.showMessageDialog(
                        parent,
                        "Saved!"
                );
            }
        }
    }

    private void logMainProgress(int progress) {
        if (logDialog != null) {
            logDialog.setMainProgress(progress);
        }
    }

    private void logSubProgress(int progress) {
        if (logDialog != null) {
            logDialog.setSubProgress(progress);
        }
    }

    private void logText(String text) {
        if (logDialog != null && text != null) {
            logDialog.appendText(text);
        }
    }

    private void logWarn(String text) {
        if (logDialog != null && text != null) {
            logDialog.appendWarning(text);
        }
    }

    private void logError(String text) {
        if (logDialog != null && text != null) {
            logDialog.appendError(text);
        }
    }

    private void logProgress(int progress) {
        if (logDialog != null) {
            logDialog.setMainProgress(progress);
        }
    }


    //
    // Dialog custom events
    //
    @Override
    void onLinkTableDoubleClicked() {
        if (selectedLink != null) {
            PcbItemDetailsDialog itemDetailsDialog = new PcbItemDetailsDialog(
                    EditCreatedPcbLinksDialog.this,
                    "Pcb item",
                    selectedLink.getPcbItemProjectLink()
            );
            if (itemDetailsDialog.showDialog() == IDialog.OK) {
                updateLinkInfo(selectedLink);
            }
        }
    }

    @Override
    void onSolderTableDoubleClicked() {
        SolderItem solderItem = getSelectedSolderItem();
        if (solderItem != null && solderItem.getUsedItemId() > DbObject.UNKNOWN_ID) {
            EditItemDialog dialog = new EditItemDialog<>(
                    EditCreatedPcbLinksDialog.this,
                    "Item",
                    solderItem.getUsedItem()
            );
            if (dialog.showDialog() == IDialog.OK) {
                updateSolderTable();
            }
        }
    }

    @Override
    void onRecreateSolderItems(CreatedPcbLink link) {
        if (link != null) {
            int res = JOptionPane.showConfirmDialog(
                    this,
                    "Recreating solder items for " + link.getPcbItemProjectLink().getPrettyName() + " " +
                            "will discard all previous work. Do you want to go on?",
                    "Recreate",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (res == JOptionPane.YES_OPTION) {
                for (SolderItem solderItem : link.getSolderItems()) {
                    solderItem.delete();
                }
                link.updateSolderItems();
                cache().getSolderItems().clear();

                PcbItemProjectLink projectLink = link.getPcbItemProjectLink();
                for (String reference : projectLink.getReferences()) {
                    SolderItem solderItem = new SolderItem(reference, link);
                    solderItem.save();
                }
                link.updateSolderItems();
                initSolderItemTable(link);
                updateEnabledComponents();
            }
        }
    }

    @Override
    void onSolderWizard(CreatedPcbLink link) {
        if (link != null) {
            List<SolderItem> selectedItems = getSelectedSolderItems();
            if (selectedItems != null && selectedItems.size() > 0) {

                JCheckBox overwriteStateCb = new JCheckBox("Overwrite 'Not Used' items ", true);
                JCheckBox overwriteLinkCb = new JCheckBox("Overwrite if item already has link ", false);
                String message = "This wizard will copy all links to the used item field, and set them soldered. " +
                        "\nSelect options: ";
                Object[] params = {message, overwriteStateCb, overwriteLinkCb};
                int res = JOptionPane.showConfirmDialog(
                        EditCreatedPcbLinksDialog.this,
                        params,
                        "Wizard options",
                        JOptionPane.OK_CANCEL_OPTION);
                if (res == JOptionPane.OK_OPTION) {
                    logDialog = new MessageProgressDialog(EditCreatedPcbLinksDialog.this);
                    logDialog.initMainProgress(0, 2);
                    logDialog.initSubProgress(0, selectedItems.size());
                    logDialog.showDialog();

                    final boolean overwrite = overwriteStateCb.isSelected();
                    SwingUtilities.invokeLater(() -> {
                        logDialog.appendHeader("Copying links into used items");
                        logDialog.setMainProgress(0);
                        onCopyLink(getSelectedSolderItems(), link, overwriteStateCb.isSelected(), overwriteLinkCb.isSelected(), true);

                        logDialog.appendHeader("Soldering items");
                        logDialog.setMainProgress(1);
                        onSetSoldered(getSelectedSolderItems(), overwrite, true);

                        logDialog.setMainProgress(2);
                        logDialog.setDone(false);
                    });
                }

            }
        }
    }

    @Override
    void onEditPcbItem(CreatedPcbLink link) {
        if (link != null) {
            PcbItemDetailsDialog itemDetailsDialog = new PcbItemDetailsDialog(
                    EditCreatedPcbLinksDialog.this,
                    "Pcb item",
                    link.getPcbItemProjectLink()
            );
            if (itemDetailsDialog.showDialog() == IDialog.OK) {
                updateLinkInfo(link);
                updateEnabledComponents();
            }
        }
    }

    @Override
    void onEditLinkedItem(CreatedPcbLink link) {
        if (link != null && link.getPcbItemItemLink() != null) {
            EditItemDialog dialog = new EditItemDialog<>(this, "Edit item", link.getPcbItemItemLink().getItem());
            dialog.showDialog();
            updateLinkInfo(link);
        }
    }

    @Override
    void onSearchUsedItem(CreatedPcbLink link) {
        if (link != null) {
            List<SolderItem> selectedItems = getSelectedSolderItems();
            if (selectedItems != null && selectedItems.size() > 0) {
                AdvancedSearchDialog dialog = new AdvancedSearchDialog(EditCreatedPcbLinksDialog.this, false);
                dialog.searchPcbItem(link.getPcbItemProjectLink());
                if (dialog.showDialog() == IDialog.OK) {
                    Item newUsedItem = dialog.getSelectedItem();
                    if (newUsedItem != null) {
                        for (SolderItem solderItem : selectedItems) {
                            if (solderItem.getUsedItemId() != newUsedItem.getId()) {
                                solderItem.setState(SolderItemState.None);
                            }
                            solderItem.setUsedItemId(newUsedItem.getId());
                            solderItem.save();
                        }
                        updateEnabledComponents();
                        updateSolderTable();
                    }
                }
            }
        }
    }

    @Override
    void onOrderInfo(SolderItem solderItem) {
        if (solderItem != null) {
            SolderItemInfoDialog dialog = new SolderItemInfoDialog(this, solderItem);
            dialog.showDialog();
        }
    }

    @Override
    void onCopyLink(List<SolderItem> selectedItems, CreatedPcbLink link, boolean overwriteIfNotUsed, boolean overwriteIfAlreadyHasItem, boolean logging) {
        if (link != null) {
            if (link.getPcbItemItemLink() == null) {
                if (logging) logWarn("No linked item available");
            } else {
                if (selectedItems != null && selectedItems.size() > 0) {
//                    SynchronousDbSaver<SolderItem> synchronousDbSaver = new SynchronousDbSaver<>(
//                            SolderItem.class,
//                            selectedItems,
//                            new SynchronousDbSaver.SynchronousSaveListener<SolderItem>() {
//                                @Override
//                                public void beforeSave(SolderItem solderItem) {
//                                    int p = 0;
//
//                                    if (logging) logText("Linking item " + solderItem);
//                                    if (overwriteIfNotUsed || (!solderItem.getState().equals(SolderItemState.NotUsed))) {
//                                        long usedItemId = solderItem.getUsedItemId();
//                                        long linkItemId = link.getPcbItemItemLink().getItemId();
//                                        if (linkItemId <= DbObject.UNKNOWN_ID) {
//                                            if (logging) logWarn("No linked item available");
//                                        } else {
//                                            if (overwriteIfAlreadyHasItem || !(usedItemId > DbObject.UNKNOWN_ID)) {
//                                                solderItem.setUsedItemId(link.getPcbItemItemLink().getItemId());
//                                                solderItem.save();
//                                                if (logging) logText("\tCopied linked item into used item!");
//                                            } else {
//                                                if (logging) logWarn("\tAlready used item defined, not overwriting");
//                                            }
//                                        }
//                                    } else {
//                                        if (logging) logWarn("\tDid not link item with state 'Not used'");
//                                    }
//                                    p++;
//                                    if (logging) logSubProgress(p);
//                                }
//
//                                @Override
//                                public void saved(SolderItem saveObject) {
//
//                                }
//
//                                @Override
//                                public void done() {
//                                    updateSolderTable();
//                                    updateEnabledComponents();
//                                }
//                            }
//                    );
//                    synchronousDbSaver.startSaving();

                    int p = 0;
                    for (SolderItem solderItem : selectedItems) {
                        if (logging) logText("Linking item " + solderItem);
                        if (overwriteIfNotUsed || (!solderItem.getState().equals(SolderItemState.NotUsed))) {
                            long usedItemId = solderItem.getUsedItemId();
                            long linkItemId = link.getPcbItemItemLink().getItemId();
                            if (linkItemId <= DbObject.UNKNOWN_ID) {
                                if (logging) logWarn("No linked item available");
                            } else {
                                if (overwriteIfAlreadyHasItem || !(usedItemId > DbObject.UNKNOWN_ID)) {
                                    solderItem.setUsedItemId(link.getPcbItemItemLink().getItemId());
                                    solderItem.save();
                                    if (logging) logText("\tCopied linked item into used item!");
                                } else {
                                    if (logging) logWarn("\tAlready used item defined, not overwriting");
                                }
                            }
                        } else {
                            if (logging) logWarn("\tDid not link item with state 'Not used'");
                        }
                        p++;
                        if (logging) logSubProgress(p);
                    }
                    updateSolderTable();
                    updateEnabledComponents();
                } else {
                    if (logging) logError("No solder items found to solder");
                }
            }
        }
    }

    @Override
    void onDeleteUsedItem() {
        List<SolderItem> selectedItems = getSelectedSolderItems();
        if (selectedItems != null && selectedItems.size() > 0) {
            int res = JOptionPane.showConfirmDialog(
                    EditCreatedPcbLinksDialog.this,
                    "Delete used item(s)?",
                    "Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (res == JOptionPane.YES_OPTION) {
                for (SolderItem solderItem : selectedItems) {
                    solderItem.setUsedItemId(0);
                    solderItem.setState(SolderItemState.None);
                    solderItem.save();
                }
                updateEnabledComponents();
                updateSolderTable();
            }
        }
    }

    @Override
    void onNotUsed() {
        List<SolderItem> selectedItems = getSelectedSolderItems();
        if (selectedItems != null && selectedItems.size() > 0) {
            int res = JOptionPane.showConfirmDialog(
                    EditCreatedPcbLinksDialog.this,
                    "Set not used for these item(s)?",
                    "Not used",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (res == JOptionPane.YES_OPTION) {
                for (SolderItem solderItem : selectedItems) {
                    solderItem.setState(SolderItemState.NotUsed);
                    solderItem.save();
                }
                updateEnabledComponents();
                updateSolderTable();
            }
        }
    }

    @Override
    void onSetSoldered(List<SolderItem> selectedItems, boolean overwriteIfNotUsed, boolean logging) {
        if (selectedItems != null && selectedItems.size() > 0) {
            int p = 0;
            for (SolderItem solderItem : selectedItems) {
                if (logging) logText("Soldering item " + solderItem);
                if (solderItem.getUsedItemId() > DbObject.UNKNOWN_ID) {
                    if (overwriteIfNotUsed || (!solderItem.getState().equals(SolderItemState.NotUsed))) {
                        solderItem.setState(SolderItemState.Soldered);
                        solderItem.setNumTimesSoldered(solderItem.getNumTimesSoldered() + 1);
                        solderItem.setSolderDate(DateUtils.now());
                        solderItem.save();
                        // TODO -> substract from item
                        if (logging) logText("\tSoldered item!");
                    } else {
                        if (logging) logWarn("\tDid not solder item with state 'Not used'");
                    }
                } else {
                    if (logging) logWarn("\tCannot solder when there is no used item");
                }
                p++;
                if (logging) logSubProgress(p);
            }
            updateEnabledComponents();
            updateSolderTable();
        } else {
            if (logging) logError("No solder items found to solder");
        }
    }

    @Override
    void onDesoldered() {
        List<SolderItem> selectedItems = getSelectedSolderItems();
        if (selectedItems != null && selectedItems.size() > 0) {
            int res = JOptionPane.showConfirmDialog(
                    EditCreatedPcbLinksDialog.this,
                    "Desolder these item(s)?",
                    "Desolder",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (res == JOptionPane.YES_OPTION) {
                for (SolderItem solderItem : selectedItems) {
                    solderItem.setState(SolderItemState.Desoldered);
                    solderItem.setNumTimesDesoldered(solderItem.getNumTimesDesoldered() + 1);
                    solderItem.setDesolderDate(DateUtils.now());
                    solderItem.save();
                }
                updateEnabledComponents();
                updateSolderTable();
            }
        }
    }

    @Override
    void onCreatePcb(CreatedPcb createdPcb) {
        createPcb(createdPcb);
    }

    @Override
    void onDestroyPcb(CreatedPcb createdPcb) {
        if (createdPcb != null && !createdPcb.isDestroyed()) {
            int res = JOptionPane.showConfirmDialog(
                    this,
                    "Destroy PCB?",
                    "Destroy",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (res == JOptionPane.YES_OPTION) {
                createdPcb.setDateDestroyed(DateUtils.now());
                createdPcb.save();
            }
        }
    }

    @Override
    void onCalculatePrice(CreatedPcb createdPcb) {
        if (createdPcb != null && createdPcb.isSoldered() && createdPcb.getCreatedPcbLinks().size() > 0) {
            beginWait();
            try {

                Price totalPrice;
                Price orderPrice = new Price(0, Statics.PriceUnits.Euro);
                Price itemsPrice = new Price(0, Statics.PriceUnits.Euro);

                // Price for order
                Order order = createdPcb.getOrder();
                if (order != null && order.getOrderLines().size() > 0) {
                    orderPrice = order.getOrderLines().get(0).getPrice();
                }

//                for (CreatedPcbLink link : createdPcb.getCreatedPcbLinks()) {
//                    int amount = link.getUsedAmount();
//                    Item usedItem = link.getUsedItem();
//
//                    if (amount > 0 && usedItem != null) {
//                        List<DistributorPartLink> distributorPartLinks = SearchManager.sm().findDistributorPartLinksForItem(usedItem.getId());
//                        if (distributorPartLinks.size() > 0) {
//                            double v = distributorPartLinks.get(0).getPrice().getValue();
//                            itemsPrice = Price.add(itemsPrice, v * amount);
//                        }
//                    }
//                } TODO

                totalPrice = Price.add(itemsPrice, orderPrice);

                final String message = "Order price: " + orderPrice.toString() + "\n" +
                        "Items price: " + itemsPrice.toString() + "\n" +
                        "Total price: " + totalPrice.toString();

                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        EditCreatedPcbLinksDialog.this,
                        message
                ));

            } finally {
                endWait();
            }
        }
    }

    @Override
    void onEditRemark(SolderItem solderItem) {
        if (solderItem != null) {
            EditRemarksDialog dialog = new EditRemarksDialog(parent, "Edit remarks", solderItem.getRemarksFile());
            if (dialog.showDialog() == IDialog.OK) {
                solderItem.setRemarksFile(dialog.getFile());
                updateSolderInfo(solderItem);
            }
        }
    }

    @Override
    void onSolderAllWizard(CreatedPcb createdPcb) {
        if (createdPcb != null) {

            JCheckBox overwriteStateCb = new JCheckBox("Overwrite 'Not Used' items ", true);
            JCheckBox overwriteLinkCb = new JCheckBox("Overwrite if item already has link ", false);
            String message = "This wizard will copy all links to the used item field, and set them soldered for al pcb items on this PCB " +
                    "\nSelect options: ";
            Object[] params = {message, overwriteStateCb, overwriteLinkCb};
            int res = JOptionPane.showConfirmDialog(
                    EditCreatedPcbLinksDialog.this,
                    params,
                    "Wizard options",
                    JOptionPane.OK_CANCEL_OPTION);

            if (res == JOptionPane.OK_OPTION) {

                logDialog = new MessageProgressDialog(EditCreatedPcbLinksDialog.this);
                logDialog.initMainProgress(0, createdPcb.getCreatedPcbLinks().size());
                logDialog.showDialog();

                int p = 0;

                for (CreatedPcbLink link : createdPcb.getCreatedPcbLinks()) {
                    List<SolderItem> selectedItems = getSelectedSolderItems();

                    if (selectedItems != null && selectedItems.size() > 0) {

                        logDialog.initSubProgress(0, selectedItems.size());
                        final boolean overwrite = overwriteStateCb.isSelected();
                        //SwingUtilities.invokeLater(() -> {
                            logDialog.appendHeader("Copying links into used items");
                            onCopyLink(link.getSolderItems(), link, overwriteStateCb.isSelected(), overwriteLinkCb.isSelected(), true);
                            logDialog.appendHeader("Soldering items");
                            onSetSoldered(link.getSolderItems(), overwrite, true);
                        //});
                    }

                    p++;
                    logMainProgress(p);
                }
            }
        }

    }

    @Override
    void onRemoveAll(CreatedPcb createdPcb) {
        if (createdPcb != null && createdPcb.isSoldered()) {

            JCheckBox setBackAmountCb = new JCheckBox("Add used amounts back to item amount ");
            String message = "This will remove all used items and set the used items back to zero, all data will be removed from the database.";
            Object[] params = {message, setBackAmountCb};
            int res = JOptionPane.showConfirmDialog(
                    EditCreatedPcbLinksDialog.this,
                    params,
                    "Remove",
                    JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                boolean setBackAmount = setBackAmountCb.isSelected();
                List<CreatedPcbLink> toDelete = new ArrayList<>(createdPcb.getCreatedPcbLinks());
//                for (CreatedPcbLink cpl : toDelete) {
//                    if (setBackAmount && cpl.getUsedItemId() > DbObject.UNKNOWN_ID) {
//                        Item usedItem = cpl.getUsedItem();
//                        usedItem.setAmount(usedItem.getAmount() + cpl.getUsedAmount());
//                        usedItem.save();
//                    }
//                    cpl.delete();
//                } TODO
                createdPcb.updateCreatedPcbLinks();
                createdPcb.setDateSoldered((Date) null);
                createdPcb.save();
                updateComponents();
            }
        }
    }

    //
    // Start up
    //
    @Override
    public void windowActivated(WindowEvent e) {
        super.windowActivated(e);

        if (createdPcb != null && createdPcb.getCreatedPcbLinks().size() == 0) {
            int res = JOptionPane.showConfirmDialog(
                    this,
                    "There are no components created for this PCB, create them now?",
                    "Create",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (res == JOptionPane.YES_OPTION) {
                creatingLinks = true;
                try {
                    createdPcb.createPcbLinks();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    creatingLinks = false;
                    updateComponents();
                }
            } else {
                onCancel();
            }
        }
    }

    //
    // Edited
    //
    @Override
    public void onValueChanged(Component component, String s, Object o, Object o1) {
        if (selectedLink != null) {
            selectedLink.save();
        }
        //updateLinkTable();
    }

    @Override
    public Object getGuiObject() {
        if (isShown) {
            return getSelectedLink();
        }
        return null;
    }
}
