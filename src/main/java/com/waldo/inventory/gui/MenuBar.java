package com.waldo.inventory.gui;

import com.waldo.inventory.Main;
import com.waldo.inventory.gui.dialogs.distributorsdialog.DistributorsDialog;
import com.waldo.inventory.gui.dialogs.importfromcsvdialog.ReadCsvDialog;
import com.waldo.inventory.gui.dialogs.kicadparserdialog.KiCadDialog;
import com.waldo.inventory.gui.dialogs.locationtypedialog.LocationTypeDialog;
import com.waldo.inventory.gui.dialogs.logsdialog.LogsDialog;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;
import com.waldo.inventory.gui.dialogs.packagedialog.PackageTypeDialog;
import com.waldo.inventory.gui.dialogs.projecttypesdialog.ProjectTypesDialog;
import com.waldo.inventory.gui.dialogs.querydialog.QueryDialog;
import com.waldo.inventory.gui.dialogs.settingsdialog.SettingsDialog;
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
        JMenu fileMenu = new JMenu("Inventory");

        JMenuItem settingsItem = new JMenuItem("Settings", imageResource.readImage("MenuBar.SettingsIcon"));
        settingsItem.addActionListener(settingsSelected());
        JMenuItem logItem = new JMenuItem("Logs", imageResource.readImage("MenuBar.LogsIcon"));
        logItem.addActionListener(logSelected());
        JMenuItem closeItem = new JMenuItem(("Exit"));
        closeItem.addActionListener(e -> Main.closeApplication(1));

        fileMenu.add(settingsItem);
        fileMenu.add(logItem);
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

        JMenuItem projectTypes = new JMenuItem("Project types", imageResource.readImage("MenuBar.Project"));
        projectTypes.addActionListener(projectTypesSelected());

        JMenuItem locationTypes = new JMenuItem("Locations", imageResource.readImage("MenuBar.Location"));
        locationTypes.addActionListener(locationTypesSelected());

        dbMenu.add(subDivisions);
        dbMenu.addSeparator();
        dbMenu.add(locationTypes);
        dbMenu.add(manufacturers);
        dbMenu.add(distributors);
        dbMenu.add(packages);
        dbMenu.addSeparator();
        dbMenu.add(projectTypes);

        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");

        JMenuItem importFromCsv = new JMenuItem("Import from csv", imageResource.readImage("MenuBar.Import"));
        importFromCsv.addActionListener(importFromCsvSelected());

        JMenuItem queries = new JMenuItem("Query panel", imageResource.readImage("QueryDialog.TitleIcon", 16));
        queries.addActionListener(showQuerySelected());

        JMenuItem kicadParser = new JMenuItem("KiCad parser", imageResource.readImage("Parser.KiCad", 16));
        kicadParser.addActionListener(kicadParserSelected());

        toolsMenu.add(importFromCsv);
        toolsMenu.add(queries);
        toolsMenu.add(kicadParser);

        // Add menus
        add(fileMenu);
        add(dbMenu);
        add(toolsMenu);
    }

    private ActionListener settingsSelected() {
        return e -> {
            SettingsDialog dialog = new SettingsDialog(application, "Settings");
            dialog.showDialog();
        };
    }

    private ActionListener logSelected() {
        return e -> {
            LogsDialog dialog = new LogsDialog(application, "Logs");
            dialog.showDialog();
        };
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

    private ActionListener showQuerySelected() {
        return (e -> {
            QueryDialog dialog = new QueryDialog(application, "Queries");
            dialog.showDialog();
        });
    }

    private ActionListener kicadParserSelected() {
        return (e -> {
           KiCadDialog dialog = new KiCadDialog(application, "KiCad parser");
           dialog.showDialog();
        });
    }

    private ActionListener projectTypesSelected() {
        return (e -> {
           ProjectTypesDialog dialog = new ProjectTypesDialog(application, "Project types");
           dialog.showDialog();
        });
    }


    private ActionListener locationTypesSelected() {
        return (e -> {
            LocationTypeDialog dialog = new LocationTypeDialog(application, "Locations");
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
