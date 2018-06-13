package com.waldo.inventory.gui.dialogs.addtoorderdialog;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.DistributorType;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.ItemOrder;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.gui.components.actions.IActions.GoAction;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static com.waldo.inventory.gui.components.actions.IActions.AddAction;
import static com.waldo.inventory.managers.CacheManager.cache;

abstract class AddToOrderCacheDialogLayout extends ICacheDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    // New or existing order
    private ILabel selectOrderLbl;
    IComboBox<ItemOrder> orderCb;
    private AddAction addNewOrderAa;
    private GoAction addNowAa;

    // Pending
    private ILabel pendingOrderLbl;
    IComboBox<Distributor> distributorCb;
    private GoAction addPendingAa;

    private boolean pendingOption;
    DistributorType distributorType;

    /*
    *                  CONSTRUCTOR
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    AddToOrderCacheDialogLayout(Window parent, String title, boolean pendingOption, Statics.DistributorType distributorType) {
        super(parent, title);
        this.pendingOption = pendingOption;
        this.distributorType = distributorType;
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

        DefaultComboBoxModel<ItemOrder> orderCbModel = new DefaultComboBoxModel<>();
        List<ItemOrder> plannedItemOrders = SearchManager.sm().findPlannedOrders(distributorType);
        for (ItemOrder o : plannedItemOrders) {
            orderCbModel.addElement(o);
        }

        orderCb.setModel(orderCbModel);
        if (object.length != 0 && object[0] != null) {
            orderCb.setSelectedItem(object[0]);
        }
    }
}
