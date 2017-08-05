package com.waldo.inventory.gui.dialogs.kccomponentorderdialog.extras;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.tablemodels.IKcOrderItemTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.List;

public class KcOrderItemPanel extends JPanel implements GuiInterface {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IKcOrderItemTableModel tableModel;
    private ITable<OrderItem> itemTable;

    private ITextField descriptionTf;
    private ITextField footprintTf;



    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public KcOrderItemPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void addListSelectionListener(ListSelectionListener listSelectionListener) {
        itemTable.getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    public void addOrderItem(OrderItem orderItem) {
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        tableModel.addItems(orderItems);
    }

    public void removeOrderItem(OrderItem orderItem) {
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        tableModel.removeItems(orderItems);
    }

    public void setSelectedOrderItem(OrderItem orderItem) {
        itemTable.selectItem(orderItem);
    }

    public OrderItem getSelectedOrderItem() {
        int row = itemTable.getSelectedRow();
        if (row >= 0) {
            return (OrderItem) itemTable.getValueAtRow(row);
        }
        return null;
    }

    public void updateSelectedValueData(OrderItem orderItem) {
        if (orderItem != null) {
            Item item = orderItem.getItem();
            descriptionTf.setText(item.getDescription());
            if (item.getDimensionType() != null) {
                footprintTf.setText(item.getDimensionType().getName());
            } else {
                if (item.getPackage() != null && item.getPackage().getPackageType() != null) {
                    footprintTf.setText(item.getPackage().getPackageType().getName());
                } else {
                    footprintTf.setText("");
                }
            }
        } else {
            descriptionTf.clearText();
            footprintTf.clearText();
        }
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Table
        tableModel = new IKcOrderItemTableModel();
        itemTable = new ITable<>(tableModel);

    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object object) {

    }
}