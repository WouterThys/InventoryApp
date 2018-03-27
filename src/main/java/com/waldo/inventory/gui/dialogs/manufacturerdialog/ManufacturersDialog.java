package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class ManufacturersDialog extends ManufacturersDialogLayout implements CacheChangedListener<Manufacturer> {

    private boolean canClose = true;

    public ManufacturersDialog(Window parent, String title) {
        super(parent, title);
        initializeComponents();
        initializeLayouts();

        addCacheListener(Manufacturer.class, this);

        updateComponents();
    }

    @Override
    protected void onOK() {
        if (checkChange()) {
            canClose = false;
            showSaveDialog(true);
        }

        if (canClose) {
            super.onOK();
        }
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            selectedManufacturer.save();
            originalManufacturer = selectedManufacturer.createCopy();
            getButtonNeutral().setEnabled(false);
        }

    }

    @Override
    protected void onCancel() {
        if (selectedManufacturer != null && originalManufacturer != null) {
            originalManufacturer.createCopy(selectedManufacturer);
            selectedManufacturer.setCanBeSaved(true);
        }
        super.onCancel();
    }

    private void setDetails() {
        if (selectedManufacturer != null) {
            detailName.setText(selectedManufacturer.getName());
            browsePanel.setText(selectedManufacturer.getWebsite());

            if (!selectedManufacturer.getIconPath().isEmpty()) {
                Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgManufacturersPath(), selectedManufacturer.getIconPath());
                try {
                    detailLogo.setIcon(imageResource.readImage(path, 48,48));
                } catch (Exception e) {
                    detailLogo.setIcon(imageResource.readImage("Common.UnknownIcon48"));
                }
            } else {
                detailLogo.setIcon(imageResource.readImage("Common.UnknownIcon48"));
            }

            detailItemDefaultListModel.removeAllElements();
            for (Item item : SearchManager.sm().getItemsForManufacturer(selectedManufacturer.getId())) {
                detailItemDefaultListModel.addElement(item);
            }
        }
    }

    private void clearDetails() {
        detailName.setText("");
        browsePanel.clearText();
        detailLogo.setIcon(null);
        detailItemDefaultListModel.removeAllElements();
    }

    private void showSaveDialog(boolean closeAfter) {
        if (selectedManufacturer != null) {
            String msg = selectedManufacturer.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    selectedManufacturer.save();
                    originalManufacturer = selectedManufacturer.createCopy();
                    if (closeAfter) {
                        super.onOK();
                    }
                }
            }
        } else {
            if (closeAfter) {
                dialogResult = OK;
                dispose();
            }
        }
        canClose = true;
    }

    private boolean verify() {
        boolean ok = true;
        if (detailName.getText().isEmpty()) {
            detailName.setError("Name can't be empty");
            ok = false;
        }

        // Valid website??
        //...
        return ok;
    }

    private boolean checkChange() {
        return (selectedManufacturer != null) && !(selectedManufacturer.equals(originalManufacturer));
    }

    private void selectManufacturer(Manufacturer manufacturer) {
        if (selectedManufacturer != null && originalManufacturer != null) {
            if (checkChange()) {
                showSaveDialog(false);
            }
        }
        getButtonNeutral().setEnabled(false);
        selectedManufacturer = manufacturer;
        if (selectedManufacturer != null && !selectedManufacturer.isUnknown()) {
            originalManufacturer = selectedManufacturer.createCopy();
            manufacturerList.setSelectedValue(selectedManufacturer, true);
            setDetails();
        } else {
            clearDetails();
        }
        updateEnabledComponents();
    }

    //
    // Update listener
    //

    @Override
    public void updateComponents(Object... args) {
        if (isUpdating()) {
            return;
        }
        beginWait();
        try {
            // Get all menus
            List<Manufacturer> manufacturers = cache().getManufacturers();
            manufacturers.sort(new ComparatorUtils.DbObjectNameComparator<>());
            setManufacturerList(manufacturers);

            Manufacturer m = null;

            if (args.length > 0) {
                m = (Manufacturer) args[0];
            }
            if (m == null) {
                m = manufacturers.get(1); // Unknown is 0
            }

            selectManufacturer(m);
            updateEnabledComponents();
        } finally {
            endWait();
        }
    }


    //
    // Search listener
    //
    @Override
    public void onObjectsFound(List<Manufacturer> foundObjects) {
        if (foundObjects != null && foundObjects.size() > 0) {
            beginWait();
            try {
                setManufacturerList(foundObjects);
                Manufacturer m = foundObjects.get(0);
                manufacturerList.setSelectedValue(m, true);
                searchPanel.setCurrentObject(m);
            } finally {
                endWait();
            }
        } else {
            setManufacturerList(new ArrayList<>());
        }
    }

    @Override
    public void onNextObjectSelected(Manufacturer next) {
        manufacturerList.setSelectedValue(next, true);
    }

    @Override
    public void onPreviousObjectSelected(Manufacturer previous) {
        manufacturerList.setSelectedValue(previous, true);
    }

    @Override
    public void onSearchCleared() {
        updateComponents(selectedManufacturer);
    }

    //
    // Manufacturer listener
    //
    @Override
    public void onInserted(Manufacturer manufacturer) {
        updateComponents(manufacturer);
    }

    @Override
    public void onUpdated(Manufacturer newManufacturer) {
        updateComponents(newManufacturer);
    }

    @Override
    public void onDeleted(Manufacturer manufacturer) {
        updateComponents();
    }

    @Override
    public void onCacheCleared() {}

    //
    // List selection listener
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !isUpdating()) {
            JList list = (JList) e.getSource();
            Object selected = list.getSelectedValue();

            selectManufacturer((Manufacturer)selected);
        }
    }

    //
    // Tool bar
    //

    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        updateComponents(selectedManufacturer);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        DbObjectDialog<Manufacturer> dialog = new DbObjectDialog<>(ManufacturersDialog.this, "New Manufacturer", new Manufacturer());
        if (dialog.showDialog() == DbObjectDialog.OK) {
            Manufacturer m = dialog.getDbObject();
            m.save();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedManufacturer != null) {
            int res = JOptionPane.showConfirmDialog(ManufacturersDialog.this, "Are you sure you want to delete \"" + selectedManufacturer.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedManufacturer.delete();
                selectedManufacturer = null;
                originalManufacturer = null;
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedManufacturer != null) {
            DbObjectDialog<Manufacturer> dialog = new DbObjectDialog<>(ManufacturersDialog.this, "Update " + selectedManufacturer.getName(), selectedManufacturer);
            if (dialog.showDialog() == DbObjectDialog.OK) {
                selectedManufacturer.save();
                originalManufacturer = selectedManufacturer.createCopy();
            }
        }
    }

    //
    // Web site changed
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        getButtonNeutral().setEnabled(checkChange());
    }

    @Override
    public DbObject getGuiObject() {
        if (isShown) {
            return selectedManufacturer;
        }
        return null;
    }
}