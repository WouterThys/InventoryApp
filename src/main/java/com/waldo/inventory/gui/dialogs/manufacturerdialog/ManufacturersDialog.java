package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.dbInstance;

public class ManufacturersDialog extends ManufacturersDialogLayout {

    public static void showDialog(Application parent) {
        JDialog dialog = new JDialog(parent, "Sub Divisions", true);
        final ManufacturersDialog md = new ManufacturersDialog(parent, dialog);
        dialog.getContentPane().add(md);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dbInstance().removeOnManufacturersChangedListener(md);
                super.windowClosing(e);
            }
        });
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
    }


    private boolean canClose = true;

    private ManufacturersDialog(Application application, JDialog dialog) {
        super(application, dialog);
        initializeComponents();
        initializeLayouts();
        initActions();

        dbInstance().addOnManufacturerChangedListener(this);

        updateComponents(null);
    }

    private void initActions() {
        manufacturerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                JList list = (JList) e.getSource();
                selectedManufacturer = (Manufacturer) list.getSelectedValue();
                if (selectedManufacturer != null && selectedManufacturer.getId() != DbObject.UNKNOWN_ID) {
                    setDetails();
                } else {
                    clearDetails();
                }
            }
        });


        getPositiveButton().addActionListener(e -> {
            if (detailWebsite.isEdited()) {
                canClose = false;
                showSaveDialog();
            }

            if (canClose) {
                close();
            }
        });
    }


    private void setDetails() {
        if (selectedManufacturer != null) {
            detailName.setTextBeforeEdit(selectedManufacturer.getName());
            detailWebsite.setTextBeforeEdit(selectedManufacturer.getWebsite());

            if (!selectedManufacturer.getIconPath().isEmpty()) {
                detailLogo.setIcon(selectedManufacturer.getIconPath(), 48,48);
            } else {
                detailLogo.setIcon(resourceManager.readImage("Common.UnknownIcon48"));
            }

            detailItemDefaultListModel.removeAllElements();
            for (Item item : dbInstance().getItemsForManufacturer(selectedManufacturer.getId())) {
                detailItemDefaultListModel.addElement(item);
            }
        }
    }

    private void clearDetails() {
        detailName.setText("");
        detailWebsite.setText("");
        detailLogo.setIcon((Icon) null);
        detailItemDefaultListModel.removeAllElements();
    }

    private void showSaveDialog() {
        if (selectedManufacturer != null) {
            String msg = selectedManufacturer.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    selectedManufacturer.setName(detailName.getText());
                    selectedManufacturer.setWebsite(detailWebsite.getText());
                    selectedManufacturer.save();
                    close();
                }
            }
        } else {
            close();
        }
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

    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {

    }

    @Override
    public void onSearchCleared() {

    }
}
