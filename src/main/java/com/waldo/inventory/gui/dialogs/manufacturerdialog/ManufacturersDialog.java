package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import static com.waldo.inventory.database.DbManager.dbInstance;

public class ManufacturersDialog extends ManufacturersDialogLayout {

    private static final Logger LOG = LoggerFactory.getLogger(SubDivisionsDialog.class);

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
        //dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
    }

    private ManufacturersDialog(Application application, JDialog dialog) {
        super(application, dialog);
        initActions();
        initializeComponents();
        initializeLayouts();

        dbInstance().addOnManufacturerChangedListener(this);

        updateComponents(null);
    }

    private void initActions() {
        initManufacturerChanged();
    }


    private void initManufacturerChanged() {
        manufacturerChanged = e -> {
            if (!e.getValueIsAdjusting()) {
                JList list = (JList) e.getSource();
                selectedManufacturer = (Manufacturer) list.getSelectedValue();
                if (selectedManufacturer != null && selectedManufacturer.getId() != DbObject.UNKNOWN_ID) {
                    setDetails();
                } else {
                    clearDetails();
                }
            }
        };
    }

    private void setDetails() {
        if (selectedManufacturer != null) {
            detailName.setText(selectedManufacturer.getName());
            detailWebsite.setText(selectedManufacturer.getWebsite());

            if (!selectedManufacturer.getIconPath().isEmpty()) {
                try {
                    URL url = new File(selectedManufacturer.getIconPath()).toURI().toURL();
                    detailLogo.setIcon(resourceManager.readImage(url, 48,48));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                detailLogo.setIcon(resourceManager.readImage("Common.UnknownIcon48"));
            }

            detailItemDefaultListModel.removeAllElements();
            try {
                for (Item item : dbInstance().getItemsForManufacturer(selectedManufacturer.getId())) {
                    detailItemDefaultListModel.addElement(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearDetails() {
        detailName.setText("");
        detailWebsite.setText("");
        detailLogo.setIcon(null);
        detailItemDefaultListModel.removeAllElements();
    }
}
