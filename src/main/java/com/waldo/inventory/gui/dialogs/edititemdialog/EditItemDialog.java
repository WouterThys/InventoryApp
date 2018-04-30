package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Set;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.managers.SearchManager.sm;

public class EditItemDialog<T extends Item> extends EditItemDialogLayout {

    private boolean canClose = true;
    private boolean allowSave = true;

    public EditItemDialog(Window parent, String title, @NotNull T item)  {
        super(parent, title);
        if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            setLocationByPlatform(true);
        }
        setValues(item);
        initializeComponents();
        initializeLayouts();
        initTabChangedAction();

        updateComponents(item);
    }

    public void setValuesForSet(Set set) {
        setForSet(set);
        originalItem = selectedItem.createCopy();
    }

    private void setValues(Item item) {
        selectedItem = item;
        originalItem = selectedItem.createCopy();
    }

    private void save() {
        if (allowSave) {
            // Update location
            if (selectedItem.getLocationId() != originalItem.getLocationId()) {
                if (selectedItem.getLocation() != null) {
                    selectedItem.getLocation().updateItemList();
                }
            }
            // Update division
            if (selectedItem.getDivisionId() != originalItem.getDivisionId()) {
                if (selectedItem.getDivision() != null) {
                    selectedItem.getDivision().updateItemList();
                }
            }
            selectedItem.save();
        }
        originalItem = selectedItem.createCopy();
    }

    @Override
    protected void onOK() {
        if (verify()) {
            if (checkChange()) {
                canClose = false;
                showSaveDialog();
            }

            if (canClose) {
                dialogResult = OK;
                dispose();
            }
        }
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            save();

            getButtonNeutral().setEnabled(false);
            // Don't call update for just one component
            componentPanel.updateRating(selectedItem.getRating());
        }
    }

    @Override
    protected void onCancel() {
        originalItem.createCopy(selectedItem);
        selectedItem.setCanBeSaved(true);
        super.onCancel();
    }

    private boolean checkChange() {
        return (selectedItem != null) && (!(selectedItem.equals(originalItem)));
    }

    private void showSaveDialog() {
        if (selectedItem != null) {
            String msg = selectedItem.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    save();
                    super.onOK();
                }
            }
        } else {
            super.onOK();
        }
        canClose = true;
    }

    public Item getItem() {
        return selectedItem;
    }

    public void setAllowSave(boolean allowSave) {
        this.allowSave = allowSave;
    }

    private void initTabChangedAction() {
        tabbedPane.addChangeListener(e -> updateComponents(selectedItem));
    }

    private boolean verify() {
        boolean ok = true;

        String name = componentPanel.getNameFieldValue().trim();
        if (name.isEmpty()) {
            componentPanel.setNameFieldError("Name can not be empty");
            ok = false;
        } else {
            if (selectedItem.getId() < DbObject.UNKNOWN_ID) {
                Item check = sm().findItemByName(name);
                if (check != null) {
                    componentPanel.setNameFieldError("Name already exists in items");
                    ok = false;
                }
            }
        }
        return ok;
    }


    //
    // Values changed
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        if (fieldName.equals("Name")) {
            getTitleNameLabel().setText(String.valueOf(newValue));
        }
        getButtonNeutral().setEnabled(checkChange());
    }

    @Override
    public DbObject getGuiObject() {
        if (isShown) {
            return selectedItem;
        } else {
            return null;
        }
    }
}
