package com.waldo.inventory.gui.dialogs.orderitemdialog;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.actions.IActions.GoAction;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;

import static com.waldo.inventory.gui.components.actions.IActions.AddAction;
import static com.waldo.inventory.managers.CacheManager.cache;

abstract class OrderItemDialogLayout extends IDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    // New or existing order
    private ILabel selectOrderLbl;
    IComboBox<Order> orderCb;
    private AddAction addNewOrderAa;
    private GoAction addNowAa;

    // Pending
    private ILabel pendingOrderLbl;
    IComboBox<Distributor> distributorCb;
    private GoAction addPendingAa;

    private boolean pendingOption;

    /*
    *                  CONSTRUCTOR
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderItemDialogLayout(Window parent, String title, boolean pendingOption) {
        super(parent, title);
        this.pendingOption = pendingOption;
    }

    abstract void addNewOrder();
    abstract void addToOrder();
    abstract void addToPending();

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        getButtonOK().setVisible(false);

        selectOrderLbl = new ILabel("Select an order, or add a new one.");
        addNewOrderAa = new AddAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewOrder();
            }
        };
        addNowAa = new GoAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToOrder();
            }
        };
        orderCb = new IComboBox<>();

        pendingOrderLbl = new ILabel("Add to pending orders, and order later.");
        addPendingAa = new GoAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToPending();
            }
        };
        distributorCb = new IComboBox<>(cache().getDistributors(), new DbObjectNameComparator<>(), false);
    }

    @Override
    public void initializeLayouts() {
        GuiUtils.GridBagHelper gbc;

        JPanel addNowPanel = new JPanel();
        gbc = new GuiUtils.GridBagHelper(addNowPanel, 0);
        gbc.addLine("", selectOrderLbl);
        gbc.addLine("", GuiUtils.createComponentWithActions(orderCb, addNewOrderAa, addNowAa));

        JPanel addLaterPanel = new JPanel();
        gbc = new GuiUtils.GridBagHelper(addLaterPanel, 0);
        gbc.addLine("", pendingOrderLbl);
        gbc.addLine("", GuiUtils.createComponentWithActions(distributorCb, addPendingAa));

        addNowPanel.setBorder(GuiUtils.createTitleBorder("Add now"));
        addLaterPanel.setBorder(GuiUtils.createTitleBorder("Add later"));

        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.Y_AXIS));
        getContentPanel().add(addNowPanel);
        if (pendingOption) {
            getContentPanel().add(addLaterPanel);
        }

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        Vector<Order> orders = new Vector<>();
        for (Order o : cache().getOrders()) {
            if (!o.isUnknown() && !o.isOrdered()) {
                orders.addElement(o);
            }
        }
        orders.sort(new Order.SortUnordered());
        DefaultComboBoxModel<Order> orderCbModel = new DefaultComboBoxModel<>(orders);
        orderCb.setModel(orderCbModel);
        if (object.length != 0 && object[0] != null) {
            orderCb.setSelectedItem(object);
        }
    }
}
