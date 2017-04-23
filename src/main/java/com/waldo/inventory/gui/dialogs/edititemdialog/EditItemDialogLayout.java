package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.ComponentPanel;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.EditItemLocationPanel;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.EditItemManufacturerPanel;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.EditItemOrderPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public abstract class EditItemDialogLayout extends IDialog implements GuiInterface {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JTabbedPane tabbedPane;

    ComponentPanel componentPanel;
    EditItemManufacturerPanel editItemManufacturerPanel;
    EditItemLocationPanel editItemLocationPanel;
    EditItemOrderPanel editItemOrderPanel;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Item newItem;
    boolean isNew = false;

    EditItemDialogLayout(Application application, String title) {
        super(application, title);
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

        editItemManufacturerPanel = new EditItemManufacturerPanel(newItem);
        editItemManufacturerPanel.initializeComponents();

        editItemLocationPanel = new EditItemLocationPanel(newItem);
        editItemLocationPanel.setLayout(new BoxLayout(editItemLocationPanel, BoxLayout.Y_AXIS));
        editItemLocationPanel.initializeComponents();

        editItemOrderPanel = new EditItemOrderPanel(newItem);
        editItemOrderPanel.setLayout(new BoxLayout(editItemOrderPanel, BoxLayout.Y_AXIS));
        editItemOrderPanel.initializeComponents();

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.Y_AXIS));

        // Component panel
        componentPanel.initializeLayouts();
        editItemManufacturerPanel.initializeLayouts();
        editItemLocationPanel.initializeLayouts();
        editItemOrderPanel.initializeLayouts();

        componentPanel.updateComponents(null);
        editItemManufacturerPanel.updateComponents(null);
        editItemLocationPanel.updateComponents(null);
        editItemOrderPanel.updateComponents(null);

        // Add tabs
        tabbedPane.addTab("Component", resourceManager.readImage("EditItem.InfoIcon"), componentPanel, "Component info");
        tabbedPane.addTab("Manufacturer", resourceManager.readImage("EditItem.ManufacturerIcon"), editItemManufacturerPanel, "Manufacturer info");
        tabbedPane.addTab("Location", resourceManager.readImage("EditItem.LocationIcon"), editItemLocationPanel, "Location info");
        tabbedPane.addTab("Order", resourceManager.readImage("EditItem.OrderIcon"), editItemOrderPanel, "Order info");
        tabbedPane.setPreferredSize(new Dimension(600, 600));

        // Add
        getContentPanel().add(tabbedPane);

        // Buttons
        String txt  = isNew ? "Create" : "Save";
        getButtonOK().setText(txt);
    }

    @Override
    public void updateComponents(Object object) {
        try {
            application.beginWait();
            if (!newItem.getIconPath().isEmpty()) {
                try {
                    URL url = new File(newItem.getIconPath()).toURI().toURL();
                    setTitleIcon(resourceManager.readImage(url, 48, 48));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            setTitleName(newItem.getName().trim());

            ((GuiInterface) tabbedPane.getSelectedComponent()).updateComponents(null);
            //componentPanel.updateComponents(null);
        } finally {
            application.endWait();
        }
    }

    /*
     *                  GETTERS - SETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
}
