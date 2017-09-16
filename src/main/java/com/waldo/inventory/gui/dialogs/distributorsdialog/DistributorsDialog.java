package com.waldo.inventory.gui.dialogs.distributorsdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.gui.Application.imageResource;

public class DistributorsDialog extends DistributorsDialogLayout {


    public static int showDialog(Application application) {
        DistributorsDialog dd = new DistributorsDialog(application, "Distributors");
        dd.setLocationRelativeTo(application);
        dd.pack();
        dd.setMinimumSize(dd.getSize());
        dd.setVisible(true);
        return dd.dialogResult;
    }

    private boolean canClose = true;


    private DistributorsDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();

        db().addOnDistributorChangedListener(this);

        updateWithFirstDistributor();
    }

    private void updateWithFirstDistributor() {
        if (db().getDistributors().size() > 0) {
            updateComponents(db().getDistributors().get(0));
            setDetails();
        } else {
            updateComponents(null);
        }
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
            selectedDistributor.save();
            originalDistributor = selectedDistributor.createCopy();
            getButtonNeutral().setEnabled(false);
        }
    }

    @Override
    protected void onCancel() {
        if (selectedDistributor != null && originalDistributor != null) {
            originalDistributor.createCopy(selectedDistributor);
            selectedDistributor.setCanBeSaved(true);
        }

        super.onCancel();
    }

    private void setDetails() {
        if (selectedDistributor != null) {
            detailName.setText(selectedDistributor.getName());
            detailWebsite.setText(selectedDistributor.getWebsite());

            if (!selectedDistributor.getIconPath().isEmpty()) {
                Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgDistributorsPath(), selectedDistributor.getIconPath());
                detailLogo.setIcon(path.toString());
            } else {
                detailLogo.setIcon(imageResource.readImage("Common.UnknownIcon48"));
            }

            // Orders
            detailOrderLink.setText(selectedDistributor.getOrderLink());
            detailOrderFileFormatCb.setSelectedItem(selectedDistributor.getOrderFileFormat());
        }
    }

    private void clearDetails() {
        detailName.setText("");
        detailWebsite.setText("");
        detailLogo.setIcon((Icon)null);
        // List
        detailOrderLink.setText("");
        detailOrderFileFormatCb.setSelectedItem(null);
    }

    private void showSaveDialog(boolean closeAfter) {
        if (selectedDistributor != null) {
            String msg = selectedDistributor.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    selectedDistributor.setName(detailName.getText());
                    selectedDistributor.setWebsite(detailWebsite.getText());
                    selectedDistributor.save();
                    originalDistributor = selectedDistributor.createCopy();
                    if (closeAfter) {
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

        return ok;
    }

    private boolean checkChange() {
        return (selectedDistributor != null) && !(selectedDistributor.equals(originalDistributor));
    }


    //
    // List selection changed
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
            if (selectedDistributor != null && !selectedDistributor.isUnknown()) {
                setDetails();
            } else {
                clearDetails();
            }
        }
    }


    //
    // Search listeners
    //
    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        Distributor dFound = (Distributor) foundObjects.get(0);
        distributorList.setSelectedValue(dFound, true);
    }

    @Override
    public void onSearchCleared() {
        distributorList.setSelectedValue(selectedDistributor, true);
    }

    @Override
    public void nextSearchObject(DbObject next) {
        distributorList.setSelectedValue(next, true);
    }

    @Override
    public void previousSearchObject(DbObject previous) {
        distributorList.setSelectedValue(previous, true);
    }


    //
    //  Distributor changed listeners
    //
    @Override
    public void onInserted(Distributor object) {
        updateComponents(object);
    }

    @Override
    public void onUpdated(Distributor newObject) {
        updateComponents(newObject);
    }

    @Override
    public void onDeleted(Distributor object) {
        updateWithFirstDistributor();
    }

    @Override
    public void onCacheCleared() {}

    //
    //  Toolbar listener
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        selectedDistributor = null;
        originalDistributor = null;
        clearDetails();
        updateWithFirstDistributor();
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        DbObjectDialog<Distributor> dialog = new DbObjectDialog<>(application, "New Distributor", new Distributor());
        if (dialog.showDialog() == DbObjectDialog.OK) {
            Distributor d = dialog.getDbObject();
            d.save();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedDistributor != null) {
            int res = JOptionPane.showConfirmDialog(DistributorsDialog.this, "Are you sure you want to delete \"" + selectedDistributor.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedDistributor.delete();
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedDistributor != null) {
            DbObjectDialog<Distributor> dialog = new DbObjectDialog<>(application, "Update " + selectedDistributor.getName(), selectedDistributor);
            if (dialog.showDialog() == DbObjectDialog.OK) {
                selectedDistributor.save();
                originalDistributor = selectedDistributor.createCopy();
            }
        }
    }

    //
    // Stuff changed
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        getButtonNeutral().setEnabled(checkChange());
    }

    @Override
    public DbObject getGuiObject() {
        return selectedDistributor;
    }
}
