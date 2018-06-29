package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.components.IImagePanel;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.ComponentPanel;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.EditItemOrderPanel;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.EditItemStockPanel;
import com.waldo.utils.icomponents.IEditedListener;
import com.waldo.utils.icomponents.ITabbedPane;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class EditItemDialogLayout<T extends Item> extends iDialog implements IEditedListener {

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

    ComponentPanel<T> componentPanel;
    private EditItemStockPanel<T> editItemStockPanel;
    private EditItemOrderPanel<T> editItemOrderPanel;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    T selectedItem;
    T originalItem;

    EditItemDialogLayout(Window parent, String title) {
        super(parent, title);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void setForSet(Set set) {
        if (set != null) {
            componentPanel.setValuesForSet(set);
            editItemStockPanel.setValuesForSet(set);
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setResizable(true);
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        setTitleImage(new IImagePanel(EditItemDialogLayout.this, Statics.ImageType.ItemImage, selectedItem, new Dimension(64,64), this));

        // Tabbed pane
        tabbedPane = new ITabbedPane(JTabbedPane.LEFT);
        ITabbedPane.AbstractTabRenderer renderer = (ITabbedPane.AbstractTabRenderer) tabbedPane.getTabRenderer();
        renderer.setPrototypeText("go banana");
        renderer.setHorizontalTextAlignment(SwingConstants.TRAILING);

        // Panels
        componentPanel = new ComponentPanel<>(this, selectedItem,this);
//        componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.Y_AXIS));
        componentPanel.initializeComponents();

        editItemStockPanel = new EditItemStockPanel<>(this, selectedItem,this);
        editItemStockPanel.setLayout(new BoxLayout(editItemStockPanel, BoxLayout.Y_AXIS));
        editItemStockPanel.initializeComponents();

        editItemOrderPanel = new EditItemOrderPanel<>(this, selectedItem, this);
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

        // Add tabs
        tabbedPane.addTab("Component  ", imageResource.readIcon("Tag.S"), componentPanel, "Component info");
        tabbedPane.addTab("Stock  ", imageResource.readIcon("Stock.S"), editItemStockPanel, "Stock info");
        tabbedPane.addTab("Order  ", imageResource.readIcon("Order.S"), editItemOrderPanel, "Order info");

        // Add
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
        separator.setMaximumSize(new Dimension(1000000,1));
        getContentPanel().add(separator);
        getContentPanel().add(tabbedPane);
        separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
        separator.setMaximumSize(new Dimension(1000000,1));
        getContentPanel().add(separator);

        //getContentPanel().setPreferredSize(new Dimension(600, 500));
        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        beginWait();
        try {
            setTitleIcon(selectedItem);

            if (selectedItem.isSet()) {
                setInfoIcon(imageResource.readIcon("Components.L"));
            }
            setTitleName(selectedItem.getName().trim());

            ((GuiUtils.GuiInterface) tabbedPane.getSelectedComponent()).updateComponents();
        } finally {
            endWait();
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
                componentPanel.setSelectedTab(ComponentPanel.TAB_BASIC);
                break;
            case TAB_COMP_DETAILS:
                tabbedPane.setSelectedIndex(COMPONENT_TAB);
                componentPanel.setSelectedTab(ComponentPanel.TAB_DETAILS);
                break;
            case TAB_ORDERS: tabbedPane.setSelectedIndex(ORDER_TAB); break;
            case TAB_STOCK: tabbedPane.setSelectedIndex(STOCK_TAB); break;
            default: break;
        }
    }
}
