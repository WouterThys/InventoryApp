package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.Error;
import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.gui.dialogs.distributorsdialog.DistributorsDialog;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;
import com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialog;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.net.URL;

public class MenuBar extends JMenuBar {

    private ResourceManager resourceManager;
    private final Application application;

    public MenuBar(Application application) {
        this.application = application;
        URL url = Error.class.getResource("/settings/IconSettings.properties");
        resourceManager = new ResourceManager(url.getPath());

        initializeComponents();
    }

    private void initializeComponents() {
        // File menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open", resourceManager.readImage("MenuBar.OpenIcon"));
        JMenuItem closeItem = new JMenuItem(("Exit"));

        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(closeItem);


        // Database menu
        JMenu dbMenu = new JMenu("Database");

        JMenuItem subDivisions = new JMenuItem("Sub divisions", resourceManager.readImage("MenuBar.EditSubDivisionsIcon"));
        subDivisions.addActionListener(subDivisionsSelected());

        JMenuItem manufacturers = new JMenuItem("Manufacturers", resourceManager.readImage("MenuBar.EditManufacturers"));
        manufacturers.addActionListener(manufacturersSelected());

        JMenuItem distributors = new JMenuItem("Distributors", resourceManager.readImage("MenuBar.EditDistributors"));
        distributors.addActionListener(distributorsSelected());

//        JMenuItem orders = new JMenuItem("Orders", resourceManager.readImage("MenuBar.EditOrders"));
//        orders.addActionListener(ordersSelected());
//
//        JMenuItem locations = new JMenuItem("Locations", resourceManager.readImage("MenuBar.EditLocations"));
//        locations.addActionListener(locationsSelected());

        dbMenu.add(subDivisions);
        dbMenu.addSeparator();
        dbMenu.add(manufacturers);
        dbMenu.add(distributors);
//        dbMenu.add(orders);
//        dbMenu.add(locations);


        // Add menus
        add(fileMenu);
        add(dbMenu);
    }

    private ActionListener subDivisionsSelected() {
        return e -> {
            SubDivisionsDialog divisionsDialog = new SubDivisionsDialog(application, "Sub divisions");
            divisionsDialog.showDialog();
        };
    }

    private ActionListener manufacturersSelected() {
        return e -> ManufacturersDialog.showDialog(application);
    }

    private ActionListener distributorsSelected() { return  e -> DistributorsDialog.showDialog(application); }

//    private ActionListener ordersSelected() {
//        OrdersDialog dialog = new OrdersDialog(application, "Orders");
//        return e -> dialog.setVisible(true);
//    }
//
//    private ActionListener locationsSelected() {
//        return null;
//    }
}
