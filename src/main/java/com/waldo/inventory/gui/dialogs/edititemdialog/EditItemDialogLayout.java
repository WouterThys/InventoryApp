package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.waldo.inventory.Utils.ImageUtils;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.ComponentPanel;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.LocationPanel;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.ManufacturerPanel;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.OrderPanel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Vector;

import static com.waldo.inventory.Utils.PanelUtils.createFieldConstraints;
import static com.waldo.inventory.database.DbManager.dbInstance;
import static javax.swing.SpringLayout.*;
import static javax.swing.SpringLayout.HORIZONTAL_CENTER;
import static javax.swing.SpringLayout.VERTICAL_CENTER;

public abstract class EditItemDialogLayout extends IDialogPanel implements GuiInterface {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JTabbedPane tabbedPane;

    ComponentPanel componentPanel;
    ManufacturerPanel manufacturerPanel;
    LocationPanel locationPanel;
    OrderPanel orderPanel;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    static Item newItem;
    boolean isNew = false;

    Action createAction;
    Action cancelAction;

    public EditItemDialogLayout(Application application, JDialog dialog) {
        super(application, dialog, true);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        // Tabbed pane
        tabbedPane = new JTabbedPane();

        // Panels
        componentPanel = new ComponentPanel(newItem);
        componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.Y_AXIS));
        componentPanel.initializeComponents();
        componentPanel.setNameTextFieldTracker(getTitleNameLabel());

        manufacturerPanel = new ManufacturerPanel(newItem);
        //manufacturerPanel.setLayout(new BoxLayout(manufacturerPanel, BoxLayout.Y_AXIS));
        manufacturerPanel.initializeComponents();

        locationPanel = new LocationPanel(newItem);
        locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.Y_AXIS));
        locationPanel.initializeComponents();

        orderPanel = new OrderPanel(newItem);
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.initializeComponents();

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.Y_AXIS));

        // Component panel
        componentPanel.initializeLayouts();
        manufacturerPanel.initializeLayouts();
        locationPanel.initializeLayouts();
        orderPanel.initializeLayouts();

        // Add tabs
        tabbedPane.addTab("Component", resourceManager.readImage("EditItem.InfoIcon"), componentPanel, "Component info");
        tabbedPane.addTab("Manufacturer", resourceManager.readImage("EditItem.ManufacturerIcon"), manufacturerPanel, "Manufacturer info");
        tabbedPane.addTab("Location", resourceManager.readImage("EditItem.LocationIcon"), locationPanel, "Location info");
        tabbedPane.addTab("Order", resourceManager.readImage("EditItem.OrderIcon"), orderPanel, "Order info");
        tabbedPane.setPreferredSize(new Dimension(600, 600));

        // Add
        getContentPanel().add(tabbedPane);

        // Buttons
        String txt  = isNew ? "Create" : "Save";
        setPositiveButton(txt).addActionListener(createAction);
        setNegativeButton("Cancel").addActionListener(cancelAction);
    }

    @Override
    public void updateComponents(Object object) {
        if (!newItem.getIconPath().isEmpty()) {
            try {
                URL url = new File(newItem.getIconPath()).toURI().toURL();
                setTitleIcon(resourceManager.readImage(url, 48,48));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setTitleName(newItem.getName().trim());

        // Component panel
        switch (tabbedPane.getSelectedIndex()) {
            case 0:
        }

        ((GuiInterface)tabbedPane.getSelectedComponent()).updateComponents(null);
        //componentPanel.updateComponents(null);
    }

    /*
     *                  GETTERS - SETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
}
