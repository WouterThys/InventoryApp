package com.waldo.inventory.gui;

import com.waldo.inventory.gui.dialogs.distributorsdialog.DistributorsDialog;
import com.waldo.inventory.gui.dialogs.importfromcsvdialog.ReadCsvDialog;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;
import com.waldo.inventory.gui.dialogs.packagedialog.PackageTypeDialog;
import com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialog;

import javax.swing.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.gui.Application.imageResource;

public class MenuBar extends JMenuBar {

    private final Application application;

    public MenuBar(Application application) {
        this.application = application;

        initializeComponents();
    }

    private void initializeComponents() {
        // File menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open", imageResource.readImage("MenuBar.OpenIcon"));
        JMenuItem closeItem = new JMenuItem(("Exit"));

        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(closeItem);


        // Database menu
        JMenu dbMenu = new JMenu("Database");

        JMenuItem subDivisions = new JMenuItem("Sub divisions", imageResource.readImage("MenuBar.EditSubDivisionsIcon"));
        subDivisions.addActionListener(subDivisionsSelected());

        JMenuItem manufacturers = new JMenuItem("Manufacturers", imageResource.readImage("MenuBar.EditManufacturers"));
        manufacturers.addActionListener(manufacturersSelected());

        JMenuItem distributors = new JMenuItem("Distributors", imageResource.readImage("MenuBar.EditDistributors"));
        distributors.addActionListener(distributorsSelected());

        JMenuItem packages = new JMenuItem("Packages", imageResource.readImage("MenuBar.Packages"));
        packages.addActionListener(packagesSelected());

        dbMenu.add(subDivisions);
        dbMenu.addSeparator();
        dbMenu.add(manufacturers);
        dbMenu.add(distributors);
        dbMenu.add(packages);

        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");

        JMenuItem importFromCsv = new JMenuItem("Import from csv", imageResource.readImage("MenuBar.Import"));
        importFromCsv.addActionListener(importFromCsvSelected());

        toolsMenu.add(importFromCsv);


        // Add menus
        add(fileMenu);
        add(dbMenu);
        add(toolsMenu);
    }

    private ActionListener subDivisionsSelected() {
        return e -> {
            SubDivisionsDialog divisionsDialog = new SubDivisionsDialog(application, "Sub divisions");
            divisionsDialog.showDialog();
        };
    }

    private ActionListener manufacturersSelected() {
        return (e -> {
            ManufacturersDialog dialog = new ManufacturersDialog(application, "Manufacturers");
            dialog.showDialog();
        });
    }

    private ActionListener packagesSelected() {
        return (e -> {
            PackageTypeDialog dialog = new PackageTypeDialog(application, "Package Types");
            dialog.showDialog();
        });
    }

    private ActionListener distributorsSelected() { return  e -> DistributorsDialog.showDialog(application); }

    private ActionListener importFromCsvSelected() {
        return (e -> {
            ReadCsvDialog dialog = new ReadCsvDialog(application, "Import csv");
            dialog.showDialog();
        });
    }

//    private ActionListener ordersSelected() {
//        OrdersDialog dialog = new OrdersDialog(application, "Orders");
//        return e -> dialog.setVisible(true);
//    }
//
//    private ActionListener locationsSelected() {
//        return null;
//    }
}
