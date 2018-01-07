package com.waldo.inventory.gui.dialogs.editsetdialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.actions.DeleteAction;
import com.waldo.inventory.gui.components.actions.EditAction;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialogLayout;
import com.waldo.inventory.gui.dialogs.edititemlocationdialog.EditItemLocation;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

abstract class EditSetDialogLayout extends IDialog implements IEditedListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField nameTf;
    private IComboBox<Manufacturer> manufacturerCb;

    private ITextField locationTypeTf;
    private ITextField colTf;
    private ITextField rowTf;
    private EditAction editAction;
    private DeleteAction deleteAction;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Set selectedSet;
    Set originalSet;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditSetDialogLayout(Application application, String title, Set selectedSet) {
        super(application, title);

        this.selectedSet = selectedSet;
        this.originalSet = selectedSet.createCopy();
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void updateManufacturerCbValues() {
        if (manufacturerCb != null) {
            manufacturerCb.updateList(cache().getManufacturers());
            manufacturerCb.setSelectedItem(selectedSet.getManufacturer());
        }
    }

    private void createManufacturerCb() {
        manufacturerCb = new IComboBox<>(cache().getManufacturers(), new ComparatorUtils.DbObjectNameComparator<>(), true);
        manufacturerCb.setSelectedItem(selectedSet.getManufacturer());
        manufacturerCb.addEditedListener(this, "manufacturerId");
    }

    private ActionListener createManufacturerAddListener() {
        return e -> {
            ManufacturersDialog manufacturersDialog = new ManufacturersDialog(application, "Manufacturers");
            if (manufacturersDialog.showDialog() == IDialog.OK) {
                updateManufacturerCbValues();
            }
        };
    }


    private void updateLocation(Location location) {
        if (location != null) {
            deleteAction.setEnabled(true);
            locationTypeTf.setText(location.getLocationType().toString());
            colTf.setText(Statics.Alphabet[location.getRow()]);
            rowTf.setText(String.valueOf(location.getCol()));
        } else {
            deleteAction.setEnabled(false);
            locationTypeTf.clearText();
            colTf.clearText();
            rowTf.clearText();
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("Sets.Edit.Title"));
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setEnabled(false);

        // This
        nameTf = new ITextField(this, "name");

        locationTypeTf = new ITextField(false);
        locationTypeTf.setColumns(6);
        colTf = new ITextField(false);
        colTf.setColumns(3);
        rowTf = new ITextField(false);
        rowTf.setColumns(3);
        editAction = new EditAction() {
            @Override
            public void onEdit() {
                EditItemLocation dialog;
                dialog = new EditItemLocation(application,
                        "Select",
                        selectedSet.getLocation());
                if (dialog.showDialog() == IDialog.OK) {
                    Location newLocation = dialog.getItemLocation();
                    if (newLocation != null) {
                        selectedSet.setLocationId(newLocation.getId());
                    } else {
                        selectedSet.setLocationId(DbObject.UNKNOWN_ID);
                    }
                    updateLocation(newLocation);
                    onValueChanged(
                            EditSetDialogLayout.this,
                            "locationId",
                            0,
                            0);
                }
            }
        };
        deleteAction = new DeleteAction() {
            @Override
            public void onDelete() {
                int res = JOptionPane.showConfirmDialog(
                        EditSetDialogLayout.this,
                        "Are you sure you want to delete the location?",
                        "Delete location",
                        JOptionPane.YES_NO_OPTION);

                if (res == JOptionPane.YES_OPTION) {
                    selectedSet.setLocationId(-1);
                    updateLocation(null);
                    onValueChanged(
                            EditSetDialogLayout.this,
                            "locationId",
                            0,
                            -1);
                }
            }
        };

        // Manufacturer
        createManufacturerCb();
        manufacturerCb.setName(EditItemDialogLayout.COMP_MANUFACTURER);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel locAllPnl = new JPanel(new BorderLayout());
        JPanel locPnl = new JPanel();
        locPnl.add(locationTypeTf);
        locPnl.add(rowTf);
        locPnl.add(colTf);

        JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        toolBar.add(editAction);
        toolBar.add(deleteAction);

        locAllPnl.add(locPnl, BorderLayout.CENTER);
        locAllPnl.add(toolBar, BorderLayout.EAST);

        JPanel mainPanel = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(mainPanel);

        gbc.addLine("Name: ", nameTf);
        gbc.addLine("Manufacturer: ", GuiUtils.createComboBoxWithButton(manufacturerCb, createManufacturerAddListener()));
        gbc.addLine("Location: ", locAllPnl);


        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        getContentPanel().add(mainPanel);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (selectedSet != null) {
            nameTf.setText(selectedSet.getName());
            manufacturerCb.setSelectedItem(selectedSet.getManufacturer());
            updateLocation(selectedSet.getLocation());
        }
    }
}