package com.waldo.inventory.gui.dialogs.addtoorderdialog;

import com.waldo.inventory.Utils.Statics.DistributorType;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;

import static com.waldo.inventory.gui.components.actions.IActions.AddAction;

abstract class AddToOrderDialogLayout<T extends Orderable> extends ICacheDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    // New or existing order
    private ILabel selectOrderLbl;
    IComboBox<AbstractOrder<T>> orderCb;
    private AddAction addNewOrderAa;
    DistributorType distributorType;

    /*
    *                  CONSTRUCTOR
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    AddToOrderDialogLayout(Window parent, String title) {
        super(parent, title);
    }

    abstract void addNewOrder();

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        selectOrderLbl = new ILabel("Select an order, or add a new one.");
        addNewOrderAa = new AddAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewOrder();
            }
        };
        orderCb = new IComboBox<>();
        orderCb.addItemListener(e -> {
            SwingUtilities.invokeLater(() -> {
                AbstractOrder selected = (AbstractOrder) orderCb.getSelectedItem();
                getButtonOK().setEnabled(selected != null && selected.getId() > DbObject.UNKNOWN_ID);
            });
        });
    }

    @Override
    public void initializeLayouts() {
        GuiUtils.GridBagHelper gbc;

        JPanel addNowPanel = new JPanel();
        gbc = new GuiUtils.GridBagHelper(addNowPanel, 0);
        gbc.addLine("", selectOrderLbl);
        gbc.addLine("", GuiUtils.createComponentWithActions(orderCb, addNewOrderAa));

        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.Y_AXIS));
        getContentPanel().add(addNowPanel);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {

        Vector<AbstractOrder<T>> plannedOrders = new Vector<>();
        switch (distributorType) {
            default:
            case Items:
                for (ItemOrder o : SearchManager.sm().findPlannedItemOrders()) {
                    plannedOrders.add((AbstractOrder<T>) o);
                }
                break;
            case Pcbs:
                for (PcbOrder o : SearchManager.sm().findPlannedPcbOrders()) {
                    plannedOrders.add((AbstractOrder<T>) o);
                }
                break;
        }

        DefaultComboBoxModel<AbstractOrder<T>> orderCbModel = new DefaultComboBoxModel<>(plannedOrders);

        orderCb.setModel(orderCbModel);
        if (object.length != 0 && object[0] != null) {
            orderCb.setSelectedItem(object[0]);
            getButtonOK().setEnabled(true);
        }
    }
}
