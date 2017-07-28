package com.waldo.inventory.gui.dialogs.setitemdialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.setitemdialog.extra.EditSetItemDialog;
import com.waldo.inventory.gui.dialogs.setitemdialog.extra.valueparserdialog.ValueParserDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class SetItemDialog extends SetItemDialogLayout {

    private boolean canClose = true;

    public SetItemDialog(Application application, String title, Item item) {
        super(application, title, item);

        initializeComponents();
        initializeLayouts();
        updateComponents(item);
    }


    private List<SetItem> getSelectedSetItems() {
        List<SetItem> setItems = new ArrayList<>();
        int[] selectedRows = setItemTable.getSelectedRows();
        if (selectedRows.length > 0) {
            for (int row : selectedRows) {
                SetItem si = (SetItem) setItemTable.getValueAtRow(row);
                if (si != null) {
                    setItems.add(si);
                }
            }
        }

        return setItems;
    }

    private void deleteSelectedSetItems(final List<SetItem> itemsToDelete) {
        int result = JOptionPane.CANCEL_OPTION;
        if (itemsToDelete.size() == 1) {
            result = JOptionPane.showConfirmDialog(
                    SetItemDialog.this,
                    "Are you sure you want to delete " + itemsToDelete.get(0) + "?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
        } else if (itemsToDelete.size() > 1) {
            result = JOptionPane.showConfirmDialog(
                    SetItemDialog.this,
                    "Are you sure you want to delete " + itemsToDelete.size() + " items?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
        }
        if (result == JOptionPane.OK_OPTION) {
            // Delete from table
            tableModel.removeItems(itemsToDelete);
            // Delete from db
            for (SetItem item : itemsToDelete) {
                item.delete();
            }
            selectedSetItem = null;
        }

    }

    private void addSetItems(List<SetItem> itemsToAdd) {
        List<SetItem> tmp = new ArrayList<>(tableModel.getItemList());
        for (int i = itemsToAdd.size()-1; i >= 0; i--) {
            itemsToAdd.get(i).setItemId(item.getId());
            if (tmp.contains(itemsToAdd.get(i))) {
                itemsToAdd.remove(i);
            }
        }
        tableModel.addItems(itemsToAdd);
    }

    @Override
    protected void onOK() {
        if (canClose) {
            super.onOK();
        } else {
            int result = JOptionPane.showConfirmDialog(SetItemDialog.this,
                    "There are unsaved set items in the list. Return without save? ",
                    "Unsaved changes",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                super.onOK();
            }
        }
    }

    @Override
    protected void onCancel() {
        super.onCancel();
    }

    @Override
    protected void onNeutral() {
        try {
            for (SetItem si : tableModel.getItemList()) {
                si.save();
            }
            getButtonNeutral().setEnabled(false);
            canClose = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh() {
        if (!canClose) {
            int result = JOptionPane.showConfirmDialog(SetItemDialog.this,
                    "There are unsaved set items in the list, refreshing will delete this changes. Refresh anyway? ",
                    "Unsaved changes",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                updateTable();
            }
        } else {
            updateTable();
        }
    }

    @Override
    public void onToolBarAdd() {
        EditSetItemDialog itemDialog = new EditSetItemDialog(application, "Add set item", new SetItem());
        if (itemDialog.showDialog() == IDialog.OK) {
            SetItem si = itemDialog.getSetItem();

            List<SetItem> setItems = new ArrayList<>();
            setItems.add(si);
            addSetItems(setItems);
            getButtonNeutral().setEnabled(true);
            canClose = false;
        }
    }

    @Override
    public void onToolBarDelete() {
        deleteSelectedSetItems(getSelectedSetItems());
    }

    @Override
    public void onToolBarEdit() {
        if (selectedSetItem != null) {
            EditSetItemDialog itemDialog = new EditSetItemDialog(application, "Edit " + selectedSetItem.toString(), selectedSetItem);
            itemDialog.showDialog();
            getButtonNeutral().setEnabled(true);
            canClose = false;
        }
    }

    //
    // List value selected
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = setItemTable.getSelectedRow();
            if (row >= 0) {
                selectedSetItem = (SetItem) setItemTable.getValueAtRow(row);
                updateEnabledComponents();
            }
        }
    }

    //
    // Use known values button
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        ValueParserDialog dialog = new ValueParserDialog(application, "Parse values");
        if (dialog.showDialog() == IDialog.OK) {
            List<SetItem> items = dialog.getSetItems();
            if (items != null) {
                addSetItems(items);
                getButtonNeutral().setEnabled(true);
                canClose = false;
            }
        }
    }
}