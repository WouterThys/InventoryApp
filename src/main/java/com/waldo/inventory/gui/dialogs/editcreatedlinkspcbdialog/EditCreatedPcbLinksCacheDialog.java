package com.waldo.inventory.gui.dialogs.editcreatedlinkspcbdialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Price;
import com.waldo.inventory.classes.dbclasses.CreatedPcb;
import com.waldo.inventory.classes.dbclasses.CreatedPcbLink;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.editremarksdialog.EditRemarksDialog;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class EditCreatedPcbLinksCacheDialog extends EditCreatedPcbLinksCacheDialogLayout implements CacheChangedListener<CreatedPcbLink> {

    private final Window parent;
    private boolean creatingLinks = false;

    public EditCreatedPcbLinksCacheDialog(Window window, String title, ProjectPcb projectPcb, CreatedPcb createdPcb) {
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
            updateTable();
        }
    }

    @Override
    public void onUpdated(CreatedPcbLink link) {
        //updateLinkInfo(link);
        if (!creatingLinks) {
            selectedLink = link;
            updateLinkInfo(link);
            updateEnabledComponents();
            updateTable();
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

    @Override
    void onAutoCalculateUsed(CreatedPcbLink link) {
//        if (link != null && link.getUsedItem() != null && link.getPcbItemProjectLink() != null) {
//            int desired = Math.min(link.getPcbItemProjectLink().getNumberOfReferences(), link.getUsedItem().getAmount());
//            usedAmountSp.setTheValue(desired);
//        } TODO
    }

    @Override
    void onEditItem(CreatedPcbLink link) {
        if (link != null && link.getPcbItemItemLink() != null) {
            EditItemDialog dialog = new EditItemDialog<>(EditCreatedPcbLinksCacheDialog.this, "Item", link.getPcbItemItemLink().getItem());
            dialog.showDialog();
        }
    }

    @Override
    void onSearchUsedItem(CreatedPcbLink link) {
        if (link != null) {
            AdvancedSearchDialog dialog = new AdvancedSearchDialog(EditCreatedPcbLinksCacheDialog.this, false);
            dialog.searchPcbItem(link.getPcbItemProjectLink());
            if (dialog.showDialog() == IDialog.OK) {
//                Item newUsedItem = dialog.getSelectedItem();
//                if (newUsedItem != null) {
//                    link.setUsedItemId(newUsedItem.getId());
//                }
//                link.setUsedAmount(0);
//                link.save(); TODO
            }
        }
    }

    @Override
    void onDeleteUsedItem(CreatedPcbLink link) {
        if (link != null) {
//            int res = JOptionPane.showConfirmDialog(
//                    EditCreatedPcbLinksCacheDialog.this,
//                    "Delete used item " + link.getUsedItem() + " from PCB?",
//                    "Delete",
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.WARNING_MESSAGE
//            );
//
//            if (res == JOptionPane.YES_OPTION) {
//                link.setUsedItemId(0);
//                link.setUsedAmount(0);
//                link.save();
//            } TODO
        }
    }

    @Override
    void onNotUsed(CreatedPcbLink link) {
        if (link != null) {

//            if (link.getUsedItemId() > DbObject.UNKNOWN_ID) {
//                onDeleteUsedItem(link);
//            }
//
//            if (link.getUsedItemId() <= DbObject.UNKNOWN_ID) {
//                link.setUsedAmount(CreatedPcbLink.NOT_USED);
//                link.save();
//            } TODO
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
                        EditCreatedPcbLinksCacheDialog.this,
                        message
                ));

            } finally {
                endWait();
            }
        }
    }

    @Override
    void onEditRemark(CreatedPcbLink link) {
        if (link != null) {
            EditRemarksDialog dialog = new EditRemarksDialog(parent, "Edit remarks", link.getRemarksFile());
            if (dialog.showDialog() == IDialog.OK) {
                link.setRemarksFile(dialog.getFile());
                updateLinkInfo(link);
            }
        }
    }

    @Override
    void onMagicWizard(CreatedPcb createdPcb) {
        if (createdPcb != null) {
            if (createdPcb.isSoldered()) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Can not do this on a PCB that is already soldered..",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            JCheckBox usedItemsCb = new JCheckBox("Fill in all used items from linked items ", true);
            JCheckBox usedAmountCb = new JCheckBox("Enter all used amounts from PCB references ", true);
            String message = "Select options, this will overwrite all the previously selected work..";
            Object[] params = {message, usedItemsCb, usedAmountCb};
            int res = JOptionPane.showConfirmDialog(
                    EditCreatedPcbLinksCacheDialog.this,
                    params,
                    "Wizzard options",
                    JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                if (usedItemsCb.isSelected()) {
//                    for (CreatedPcbLink cpl : createdPcb.getCreatedPcbLinks()) {
//                        if (cpl.getUsedItemId() <= DbObject.UNKNOWN_ID) {
//                            PcbItemItemLink itemItemLink = cpl.getPcbItemItemLink();
//                            if (itemItemLink != null) {
//                                cpl.setUsedItemId(itemItemLink.getItemId());
//                            }
//                        }
//                    } TODO
                }

                if (usedAmountCb.isSelected()) {
//                    for (CreatedPcbLink cpl : createdPcb.getCreatedPcbLinks()) {
//                        Item usedItem = cpl.getUsedItem();
//                        if (usedItem != null && cpl.getPcbItemProjectLinkId() > DbObject.UNKNOWN_ID) {
//                            cpl.setUsedAmount(Math.min(usedItem.getAmount(), cpl.getPcbItemProjectLink().getNumberOfReferences()));
//                        }
//                    } TODO
                }

                JOptionPane.showMessageDialog(
                        EditCreatedPcbLinksCacheDialog.this,
                        "Done!",
                        "Done",
                        JOptionPane.INFORMATION_MESSAGE
                );

                saveComponents();
                updateComponents();
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
                    EditCreatedPcbLinksCacheDialog.this,
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
                createdPcb.setDateSoldered((Date)null);
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
        //updateTable();
    }

    @Override
    public Object getGuiObject() {
        if (isShown) {
            return getSelectedLink();
        }
        return null;
    }
}
