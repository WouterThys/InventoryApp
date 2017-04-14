package com.waldo.inventory.gui.dialogs.ordersdialog;


import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class OrdersDialogLayout extends IDialog
        implements GuiInterface, DbObjectChangedListener<Order> {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<Order> orderList;
    private DefaultListModel<Order> orderDefaultListModel;
    IdBToolBar toolBar;
    ITextField searchField;

    ITextField detailOrderId;
    ITextField detailOrderDate;

    JList<Item> detailItemList;
    DefaultListModel<Item> detailItemDefaultListModel;

    Action searchAction;
    Action okAction;
    ListSelectionListener orderChanged;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     Order selectedOrder;


    public OrdersDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
    *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title
        setTitleIcon(resourceManager.readImage("MOrdersDialog.TitleIcon"));
        setTitleName("Orders");

        // Search
        searchField = new ITextField("Search");
        searchField.setMaximumSize(new Dimension(100,30));
        searchField.addActionListener(searchAction);

        // Order list
        orderDefaultListModel = new DefaultListModel<>();
        orderList = new JList<>(orderDefaultListModel);
        orderList.addListSelectionListener(orderChanged);

        toolBar = new IdBToolBar() {
            @Override
            protected void refresh() {
                updateComponents(null);
            }

            @Override
            protected void add() {
                DbObjectDialog<Order> dialog = new DbObjectDialog<Order>(application, "New order", new Order());
                dialog.showDialog();
            }

            @Override
            protected void delete() {

            }

            @Override
            protected void update() {

            }
        };
        toolBar.setFloatable(false);
    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object object) {

    }

    @Override
    public void onAdded(Order object) {

    }

    @Override
    public void onUpdated(Order object) {

    }

    @Override
    public void onDeleted(Order object) {

    }
}
