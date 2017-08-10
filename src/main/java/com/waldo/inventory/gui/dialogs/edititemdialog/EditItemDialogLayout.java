package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Package;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.ITabbedPane;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.ComponentPanel;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.EditItemOrderPanel;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.EditItemStockPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public abstract class EditItemDialogLayout extends IDialog implements IEditedListener {

    protected static final int COMPONENT_TAB = 0;
    protected static final int STOCK_TAB = 1;
    protected static final int MANUFACTURER_TAB = 2;
    protected static final int ORDER_TAB = 3;

    public static final String COMP_NAME = "C_Name";
    public static final String COMP_DIVISION = "C_Division";
    public static final String COMP_MANUFACTURER = "C_Manufacturer";
    public static final String COMP_DESCRIPTION = "C_Description";
    public static final String COMP_RATING = "C_Rating";
    public static final String COMP_DISCOURAGE = "C_Discourage";
    public static final String COMP_REMARK = "C_Remark";

    public static final String TAB_COMPONENTS = "T_Comp";
    public static final String TAB_COMP_DETAILS = "T_Detail";
    public static final String TAB_ORDERS = "T_Order";
    public static final String TAB_STOCK = "T_Stock";

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITabbedPane tabbedPane;

    ComponentPanel componentPanel;
    private EditItemStockPanel editItemStockPanel;
    EditItemOrderPanel editItemOrderPanel;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Item newItem;
    Item originalItem;

    Package newPackage;
    Package originalPackage;

    boolean isNew = false;
    boolean initialized = false;

    EditItemDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Save button
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Tabbed pane
        tabbedPane = new ITabbedPane(JTabbedPane.LEFT);
        ITabbedPane.AbstractTabRenderer renderer = (ITabbedPane.AbstractTabRenderer) tabbedPane.getTabRenderer();
        renderer.setPrototypeText("go banana");
        renderer.setHorizontalTextAlignment(SwingConstants.TRAILING);

        // Panels
        componentPanel = new ComponentPanel(application, newItem, this);
//        componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.Y_AXIS));
        componentPanel.initializeComponents();

        editItemStockPanel = new EditItemStockPanel(application, newItem, this);
        editItemStockPanel.setLayout(new BoxLayout(editItemStockPanel, BoxLayout.Y_AXIS));
        editItemStockPanel.initializeComponents();

        editItemOrderPanel = new EditItemOrderPanel(application, newItem, this);
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
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
        getContentPanel().add(separator);
        getContentPanel().add(tabbedPane);
        separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
        getContentPanel().add(separator);
        pack();
    }

    @Override
    public void updateComponents(Object object) {
        try {
            initialized = false;
            application.beginWait();
            if (!newItem.getIconPath().isEmpty()) {
                try {
                    Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgItemsPath(), newItem.getIconPath());
                    URL url = path.toUri().toURL();
                    setTitleIcon(imageResource.readImage(url, 64, 64));
                } catch (Exception e) {
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
     *                  OVERWRITE
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    protected void setFocusTab(String focusTab) {
        switch (focusTab) {
            case TAB_COMPONENTS:
                tabbedPane.setSelectedIndex(COMPONENT_TAB);
                componentPanel.setSelectedTab(0);
                break;
            case TAB_COMP_DETAILS:
                tabbedPane.setSelectedIndex(COMPONENT_TAB);
                componentPanel.setSelectedTab(1);
                break;
            case TAB_ORDERS: tabbedPane.setSelectedIndex(ORDER_TAB); break;
            case TAB_STOCK: tabbedPane.setSelectedIndex(STOCK_TAB); break;
            default: break;
        }
    }
}
