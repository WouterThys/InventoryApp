package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.Error;
import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class MenuBar extends JMenuBar {

    private ResourceManager resourceManager;
    private final Application application;

    public MenuBar(Application application) {
        this.application = application;
        URL url = Error.class.getResource("/settings/Settings.properties");
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

        JMenuItem subDivisions = new JMenuItem("Edit sub divisions", resourceManager.readImage("MenuBar.EditSubDivisionsIcon"));
        subDivisions.addActionListener(subDivisionsSelected());

        dbMenu.add(subDivisions);


        // Add menus
        add(fileMenu);
        add(dbMenu);
    }

    private ActionListener subDivisionsSelected() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SubDivisionsDialog.showDialog(application);
            }
        };
    }
}
