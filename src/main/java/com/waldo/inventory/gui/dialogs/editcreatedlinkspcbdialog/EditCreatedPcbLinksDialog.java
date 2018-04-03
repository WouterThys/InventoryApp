package com.waldo.inventory.gui.dialogs.editcreatedlinkspcbdialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.editremarksdialog.EditRemarksDialog;
import com.waldo.inventory.gui.dialogs.filechooserdialog.ImageFileChooser;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class EditCreatedPcbLinksDialog extends EditCreatedPcbLinksDialogLayout implements CacheChangedListener<CreatedPcbLink> {

    private final Window parent;

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
            if (!pcb.isCreated()) {
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
                    for (CreatedPcbLink link : pcb.getCreatedPcbLinks()) {
                        if (link.getUsedItemId() > DbObject.UNKNOWN_ID) {
                            Item usedItem = link.getUsedItem();
                            usedItem.setAmount(usedItem.getAmount() - link.getUsedAmount());
                            usedItem.save();
                        }
                    }
                    pcb.setDateCreated(DateUtils.now());
                    pcb.save();

                    updateComponents();
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
    void onSaveAll(CreatedPcb createdPcb) {
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
            AdvancedSearchDialog dialog = new AdvancedSearchDialog(EditCreatedPcbLinksDialog.this, false);
            dialog.searchPcbItem(link.getPcbItemProjectLink());
            if (dialog.showDialog() == IDialog.OK) {
                Item newUsedItem = dialog.getSelectedItem();
                if (newUsedItem != null) {
                    link.setUsedItemId(newUsedItem.getId());
                }
                updateLinkInfo(link);
                updateEnabledComponents();
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
                updateEnabledComponents();
                updateTable();
            }
        }
    }

    @Override
    void onCreatePcb(CreatedPcb createdPcb) {
        createPcb(createdPcb);
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
            if (createdPcb.isCreated()) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Can not do this on a PCB that is already created..",
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
                    EditCreatedPcbLinksDialog.this,
                    params,
                    "Wizzard options",
                    JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                if (usedItemsCb.isSelected()) {
                    for (CreatedPcbLink cpl : createdPcb.getCreatedPcbLinks()) {
                        if (cpl.getUsedItemId() <= DbObject.UNKNOWN_ID) {
                            PcbItemItemLink itemItemLink = cpl.getPcbItemItemLink();
                            if (itemItemLink != null) {
                                cpl.setUsedItemId(itemItemLink.getItemId());
                            }
                        }
                    }
                }

                if (usedAmountCb.isSelected()) {
                    for (CreatedPcbLink cpl : createdPcb.getCreatedPcbLinks()) {
                        Item usedItem = cpl.getUsedItem();
                        if (usedItem != null && cpl.getPcbItemProjectLinkId() > DbObject.UNKNOWN_ID) {
                            cpl.setUsedAmount(Math.min(usedItem.getAmount(), cpl.getPcbItemProjectLink().getNumberOfItems()));
                        }
                    }
                }

                JOptionPane.showMessageDialog(
                        EditCreatedPcbLinksDialog.this,
                        "Done!",
                        "Done",
                        JOptionPane.INFORMATION_MESSAGE
                );

                updateComponents();
            }
        }
    }

    @Override
    void onRemoveAll(CreatedPcb createdPcb) {
        if (createdPcb != null && createdPcb.isCreated()) {

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
                for (CreatedPcbLink cpl : toDelete) {
                    if (setBackAmount && cpl.getUsedItemId() > DbObject.UNKNOWN_ID) {
                        Item usedItem = cpl.getUsedItem();
                        usedItem.setAmount(usedItem.getAmount() + cpl.getUsedAmount());
                        usedItem.save();
                    }
                    cpl.delete();
                }
                createdPcb.updateCreatedPcbLinks();
                createdPcb.setDateCreated((Date)null);
                createdPcb.save();
                updateComponents();
            }
        }
    }

    @Override
    void onImageIconDoubleClicked(CreatedPcb createdPcb, ILabel imageLabel) {
        JFileChooser fileChooser = ImageFileChooser.getFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showDialog(parent, "Open") == JFileChooser.APPROVE_OPTION) {
            String iconPath = fileChooser.getSelectedFile().getPath();
            if (!iconPath.isEmpty()) {
                createdPcb.setIconPath(iconPath);
                createdPcb.save();
                try {
                    Path path = Paths.get(createdPcb.getIconPath());
                    URL url = path.toUri().toURL();
                    pcbImageLbl.setIcon(imageResource.readImage(url));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
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
