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
    ILabel titleIconLabel;
    ILabel titleNameLabel;

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
        super(application, dialog);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        // Title
        titleIconLabel = new ILabel(resourceManager.readImage("Common.UnknownIcon48"));
        titleIconLabel.setPreferredSize(new Dimension(48,48));
        titleNameLabel = new ILabel("?");
        titleNameLabel.setFontSize(36);

        // Tabbed pane
        tabbedPane = new JTabbedPane();

        // Panels
        componentPanel = new ComponentPanel(newItem);
        componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.Y_AXIS));
        componentPanel.initializeComponents();
        componentPanel.setNameTextFieldTracker(titleNameLabel);

        manufacturerPanel = new ManufacturerPanel(newItem);
        manufacturerPanel.setLayout(new BoxLayout(manufacturerPanel, BoxLayout.Y_AXIS));
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

        // Title
        JPanel titlePanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        layout.putConstraint(WEST, titleIconLabel, 5, WEST, titlePanel);
        layout.putConstraint(NORTH, titleIconLabel, 5, NORTH, titlePanel);
        layout.putConstraint(SOUTH, titleIconLabel, -5, SOUTH, titlePanel);

        layout.putConstraint(HORIZONTAL_CENTER, titleNameLabel, 0, HORIZONTAL_CENTER, titlePanel);
        layout.putConstraint(VERTICAL_CENTER, titleNameLabel, 0, VERTICAL_CENTER, titlePanel);

        titlePanel.add(titleIconLabel, BorderLayout.WEST);
        titlePanel.add(titleNameLabel, BorderLayout.CENTER);
        titlePanel.setPreferredSize(new Dimension(200, 60));
        titlePanel.setLayout(layout);

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

        // Add all
        getContentPanel().add(titlePanel);
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
                titleIconLabel.setIcon(resourceManager.readImage(url, 48,48));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        titleNameLabel.setText(newItem.getName().trim());

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
