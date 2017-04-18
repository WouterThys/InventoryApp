package com.waldo.inventory.gui.dialogs.distributorsdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

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

        updateComponents(null);
    }

    @Override
    protected void onOK() {
        if (detailWebsite.isEdited()) {
            canClose = false;
            showSaveDialog();
        }

        if (canClose) {
            dialogResult = OK;
            dispose();
        }
    }

    private void setDetails() {
        if (selectedDistributor != null) {
            detailName.setTextBeforeEdit(selectedDistributor.getName());
            detailWebsite.setTextBeforeEdit(selectedDistributor.getWebsite());

            if (!selectedDistributor.getIconPath().isEmpty()) {
                detailLogo.setIcon(selectedDistributor.getIconPath(), 48,48);
            } else {
                detailLogo.setIcon(resourceManager.readImage("Common.UnknownIcon48"));
            }

            // List?
        }
    }

    private void clearDetails() {
        detailName.setText("");
        detailWebsite.setText("");
        detailLogo.setIcon((Icon)null);
        // List
    }

    private void showSaveDialog() {
        if (selectedDistributor != null) {
            String msg = selectedDistributor.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    selectedDistributor.setName(detailName.getText());
                    selectedDistributor.setWebsite(detailWebsite.getText());
                    selectedDistributor.save();
                    dispose();
                }
            }
        } else {
            dispose();
        }
    }

    private boolean verify() {
        boolean ok = true;
        if (detailName.getText().isEmpty()) {
            detailName.setError("Name can't be empty");
            ok = false;
        }

        return ok;
    }


    //
    // List selection changed
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            JList list = (JList) e.getSource();
            selectedDistributor = (Distributor) list.getSelectedValue();
            if (selectedDistributor != null && selectedDistributor.getId() != DbObject.UNKNOWN_ID) {
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


    //
    //  Distributor changed listeners
    //
    @Override
    public void onAdded(Distributor object) {
        updateComponents(object);
    }

    @Override
    public void onUpdated(Distributor newObject, Distributor oldObject) {
        updateComponents(newObject);
    }

    @Override
    public void onDeleted(Distributor object) {
        updateComponents(null);
    }

    //
    //  Toolbar listener
    //
    @Override
    public void onToolBarRefresh() {
        updateComponents(null);
    }

    @Override
    public void onToolBarAdd() {
        DbObjectDialog<Distributor> dialog = new DbObjectDialog<>(application, "New Distributor", new Distributor());
        if (dialog.showDialog() == DbObjectDialog.OK) {
            Distributor d = dialog.getDbObject();
            d.save();
        }
    }

    @Override
    public void onToolBarDelete() {
        if (selectedDistributor != null) {
            int res = JOptionPane.showConfirmDialog(DistributorsDialog.this, "Are you sure you want to delete \"" + selectedDistributor.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedDistributor.delete();
                selectedDistributor = null;
            }
        }
    }

    @Override
    public void onToolBarEdit() {
        if (selectedDistributor != null) {
            DbObjectDialog<Distributor> dialog = new DbObjectDialog<>(application, "Update " + selectedDistributor.getName(), selectedDistributor);
            if (dialog.showDialog() == DbObjectDialog.OK) {
                selectedDistributor.save();
            }
        }
    }
}
