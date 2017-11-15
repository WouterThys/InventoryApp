package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class ManufacturersDialog extends ManufacturersDialogLayout {

    private boolean canClose = true;

    public ManufacturersDialog(Application application, String title) {
        super(application, title);
        initializeComponents();
        initializeLayouts();

        cache().addOnManufacturerChangedListener(this);

        updateWithFirstManufacturer();
    }

    private void updateWithFirstManufacturer() {
        if (cache().getManufacturers().size() > 1) {
            updateComponents(cache().getManufacturers().get(1)); // 0 is unknown
            setDetails();
        } else {
            updateComponents();
        }
    }

    @Override
    protected void onOK() {
        if (checkChange()) {
            canClose = false;
            showSaveDialog(true);
        }

        if (canClose) {
            dialogResult = OK;
            dispose();
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
                detailLogo.setIcon(path.toString(), 48,48);
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
        detailLogo.setIcon((Icon) null);
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
                        dialogResult = OK;
                        dispose();
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

    //
    // Update listener
    //

    @Override
    public void updateComponents(Object... object) {
        try {
            application.beginWait();
            // Get all menus
            manufacturerDefaultListModel.removeAllElements();
            for (Manufacturer m : cache().getManufacturers()) {
                if (!m.isUnknown()) {
                    manufacturerDefaultListModel.addElement(m);
                }
            }

            selectedManufacturer = (Manufacturer) object[0];
            updateEnabledComponents();

            if (selectedManufacturer != null) {
                originalManufacturer = selectedManufacturer.createCopy();
                manufacturerList.setSelectedValue(selectedManufacturer, true);
                setDetails();
            } else {
                originalManufacturer = null;
            }
        } finally {
            application.endWait();
        }
    }


    //
    // Search listener
    //

    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        Manufacturer mFound = (Manufacturer) foundObjects.get(0);
        manufacturerList.setSelectedValue(mFound, true);
    }

    @Override
    public void onSearchCleared() {
        manufacturerList.setSelectedValue(selectedManufacturer, true);
    }

    @Override
    public void nextSearchObject(DbObject next) {
        manufacturerList.setSelectedValue(next, true);
    }

    @Override
    public void previousSearchObject(DbObject previous) {
        manufacturerList.setSelectedValue(previous, true);
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
        updateWithFirstManufacturer();
    }

    @Override
    public void onCacheCleared() {}

    //
    // List selection listener
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !application.isUpdating()) {
            JList list = (JList) e.getSource();
            Object selected = list.getSelectedValue();

            if (checkChange()) {
                showSaveDialog(false);
            }
            getButtonNeutral().setEnabled(false);
            updateComponents(selected);
            if (selectedManufacturer != null && !selectedManufacturer.isUnknown()) {
                setDetails();
            } else {
                clearDetails();
            }
        }
    }

    //
    // Tool bar
    //

    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        updateWithFirstManufacturer();
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        DbObjectDialog<Manufacturer> dialog = new DbObjectDialog<>(application, "New Manufacturer", new Manufacturer());
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
            DbObjectDialog<Manufacturer> dialog = new DbObjectDialog<>(application, "Update " + selectedManufacturer.getName(), selectedManufacturer);
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
        return selectedManufacturer;
    }
}