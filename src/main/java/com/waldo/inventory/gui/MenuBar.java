package com.waldo.inventory.gui;

import com.waldo.inventory.Main;
import com.waldo.inventory.gui.dialogs.distributorsdialog.DistributorsDialog;
import com.waldo.inventory.gui.dialogs.graphsdialog.GraphsDialog;
import com.waldo.inventory.gui.dialogs.kicadparserdialog.KiCadDialog;
import com.waldo.inventory.gui.dialogs.locationtypedialog.LocationTypeDialog;
import com.waldo.inventory.gui.dialogs.logsdialog.LogsDialog;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;
import com.waldo.inventory.gui.dialogs.packagedialog.PackageTypeDialog;
import com.waldo.inventory.gui.dialogs.projectidesdialog.ProjectIDEDialog;
import com.waldo.inventory.gui.dialogs.querydialog.QueryDialog;
import com.waldo.inventory.gui.dialogs.settingsdialog.SettingsDialog;

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

        JMenuItem settingsItem = new JMenuItem("Settings", imageResource.readIcon("Settings.Menu"));
        settingsItem.addActionListener(settingsSelected());
        JMenuItem logItem = new JMenuItem("Logs", imageResource.readIcon("Log.Menu"));
        logItem.addActionListener(logSelected());
        JMenuItem dbHistoryItem = new JMenuItem("Statistics", imageResource.readIcon("Statistics.Menu"));
        dbHistoryItem.addActionListener(dbHistorySelected());
        JMenuItem closeItem = new JMenuItem("Exit", imageResource.readIcon("MainMenu.Quit"));
        closeItem.addActionListener(e -> Main.closeApplication(1));

        fileMenu.add(settingsItem);
        fileMenu.add(logItem);
        fileMenu.add(dbHistoryItem);
        fileMenu.addSeparator();
        fileMenu.add(closeItem);


        // Database menu
        JMenu dbMenu = new JMenu("Resources");

        JMenuItem manufacturers = new JMenuItem("Manufacturers", imageResource.readIcon("Manufacturers.Menu"));
        manufacturers.addActionListener(manufacturersSelected());

        JMenuItem distributors = new JMenuItem("Distributors", imageResource.readIcon("Distributors.Menu"));
        distributors.addActionListener(distributorsSelected());

        JMenuItem packages = new JMenuItem("Packages", imageResource.readIcon("Packages.Menu"));
        packages.addActionListener(packagesSelected());

        JMenuItem projectTypes = new JMenuItem("Project IDEs", imageResource.readIcon("Ides.Menu"));
        projectTypes.addActionListener(projectTypesSelected());

        JMenuItem locationTypes = new JMenuItem("Locations", imageResource.readIcon("Locations.Menu"));
        locationTypes.addActionListener(locationTypesSelected());

        dbMenu.add(locationTypes);
        dbMenu.add(manufacturers);
        dbMenu.add(distributors);
        dbMenu.add(packages);
        dbMenu.addSeparator();
        dbMenu.add(projectTypes);

        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");

        JMenuItem queries = new JMenuItem("Query panel", imageResource.readIcon("QueryDialog.TitleIcon"));
        queries.addActionListener(showQuerySelected());

        JMenuItem kicadParser = new JMenuItem("KiCad parser", imageResource.readIcon("Parser.KiCad"));
        kicadParser.addActionListener(kicadParserSelected());

        toolsMenu.add(queries);
        toolsMenu.add(kicadParser);

        // Add menus
        add(fileMenu);
        add(dbMenu);
        add(toolsMenu);
    }

    private ActionListener settingsSelected() {
        return e -> {
            SettingsDialog dialog = new SettingsDialog(application, "Settings", false);
            dialog.showDialog();
        };
    }

    private ActionListener logSelected() {
        return e -> {
            LogsDialog dialog = new LogsDialog(application, "Logs");
            dialog.showDialog();
        };
    }

    private ActionListener dbHistorySelected() {
        return e -> {
            GraphsDialog dialog = new GraphsDialog(application, "Statistics");
            dialog.showDialog();
        };
    }

    private ActionListener manufacturersSelected() {
        return (e -> {
            ManufacturersDialog dialog = new ManufacturersDialog(application);
            dialog.showDialog();
        });
    }

    private ActionListener packagesSelected() {
        return (e -> {
            PackageTypeDialog dialog = new PackageTypeDialog(application, "Packages");
            dialog.showDialog();
        });
    }

    private ActionListener distributorsSelected() {
        //return  e -> DistributorsDialog.showDialog(application);
        return (e -> {
            DistributorsDialog dialog = new DistributorsDialog(application);
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
           ProjectIDEDialog dialog = new ProjectIDEDialog(application, "Project IDEs");
           dialog.showDialog();
        });
    }


    private ActionListener locationTypesSelected() {
        return (e -> {
            LocationTypeDialog dialog = new LocationTypeDialog(application);
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
