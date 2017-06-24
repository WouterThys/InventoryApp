package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.ComponentPanel;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.EditItemStockPanel;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.EditItemOrderPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public abstract class EditItemDialogLayout extends IDialog implements
        GuiInterface,
        IEditedListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JTabbedPane tabbedPane;

    ComponentPanel componentPanel;
    private EditItemStockPanel editItemStockPanel;
    private EditItemOrderPanel editItemOrderPanel;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Item newItem;
    Item originalItem;
    boolean isNew = false;
    boolean initialized = false;

    EditItemDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        // Save button
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Tabbed pane
        tabbedPane = new JTabbedPane();

        // Panels
        componentPanel = new ComponentPanel(newItem, this);
//        componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.Y_AXIS));
        componentPanel.initializeComponents();

        editItemStockPanel = new EditItemStockPanel(newItem, this);
        editItemStockPanel.setLayout(new BoxLayout(editItemStockPanel, BoxLayout.Y_AXIS));
        editItemStockPanel.initializeComponents();

        editItemOrderPanel = new EditItemOrderPanel(newItem);
        editItemOrderPanel.setLayout(new BoxLayout(editItemOrderPanel, BoxLayout.Y_AXIS));
        editItemOrderPanel.initializeComponents();

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.Y_AXIS));

        // Component panel
        componentPanel.initializeLayouts();
        editItemStockPanel.initializeLayouts();
        editItemOrderPanel.initializeLayouts();

        componentPanel.updateComponents(null);
        editItemStockPanel.updateComponents(null);
        editItemOrderPanel.updateComponents(null);

        // Add tabs
        tabbedPane.addTab("Component  ", imageResource.readImage("EditItem.InfoIcon"), componentPanel, "Component info");
        tabbedPane.addTab("Stock  ", imageResource.readImage("EditItem.StockIcon"), editItemStockPanel, "Stock info");
        tabbedPane.addTab("Order  ", imageResource.readImage("EditItem.OrderIcon"), editItemOrderPanel, "Order info");
        //tabbedPane.setPreferredSize(new Dimension(600, 600));

        // Add
        getContentPanel().add(tabbedPane);
        pack();
    }

    @Override
    public void updateComponents(Object object) {
        try {
            initialized = false;
            application.beginWait();
            if (!newItem.getIconPath().isEmpty()) {
                try {
                    URL url = new File(newItem.getIconPath()).toURI().toURL();
                    setTitleIcon(imageResource.readImage(url, 48, 48));
                } catch (IOException e) {
                    Status().setError("Error updating components", e);
                }
            }
            setTitleName(newItem.getName().trim());

            ((GuiInterface) tabbedPane.getSelectedComponent()).updateComponents(null);
            //componentPanel.updateComponents(null);
        } finally {
            application.endWait();
            initialized = true;
        }
    }

    /*
     *                  GETTERS - SETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
}
