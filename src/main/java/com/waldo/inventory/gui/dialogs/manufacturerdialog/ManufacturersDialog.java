package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
    }

    private void initActions() {
    }

}
