package com.waldo.inventory.gui.dialogs.inventorydialog;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class InventoryDialog extends InventoryDialogLayout {


    public InventoryDialog(Window window, String title, LocationType locationType) {
        super(window, title, locationType);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    //
    // Dialog
    //


    //
    // Location map panel
    @Override
    public void onLocationClicked(ActionEvent e, Location location) {
        if (location != null && !location.equals(currentLocation)) {
            setCurrentLocation(location);
        }
    }

    //
    // Item toolbar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {

    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (currentLocation != null) {
            AdvancedSearchDialog dialog = new AdvancedSearchDialog(InventoryDialog.this, true);
            if (dialog.showDialog() == IDialog.OK) {
                Item item = dialog.getSelectedItem();
                if (item != null) {
                    currentLocation.updateItemList();
                    item.setLocationId(currentLocation.getId());
                    item.save();

                    tableModel.setItemList(currentLocation.getItems());
                }
            }
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (currentLocation != null && currentItem != null) {
            int res = JOptionPane.showConfirmDialog(
                    InventoryDialog.this,
                    "Do you realy want to delete " + currentItem + " from this location?",
                    "Delete item",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (res == JOptionPane.YES_OPTION) {
                currentItem.setLocationId(0);
                currentLocation.updateItemList();
                currentItem.save();

                tableModel.setItemList(currentLocation.getItems());
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (currentItem != null) {
            EditItemDialog dialog = new EditItemDialog<>(InventoryDialog.this, "Edit item", currentItem);
            dialog.showDialog();
            SwingUtilities.invokeLater(() -> tableModel.updateTable());
        }
    }

    //
    // Item table
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            currentItem = itemTable.getSelectedItem();
            updateEnabledComponents();
        }
    }
}