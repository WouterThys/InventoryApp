package com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class OrderPcbItemDialogLayout extends IDialog implements
        OrderPcbItemPanel.PcbItemListener,
        OrderedPcbItemsPanel.OrderListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
   OrderPcbItemPanel pcbItemPnl;
   OrderedPcbItemsPanel orderPnl;

   private IComboBox<Order> orderCb;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Order selectedOrder;
    ProjectPcb selectedPcb;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderPcbItemDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    abstract ActionListener onChangeOrder();

    private void updateOrder(Order order) {
        // Combo box
        application.beginWait();
        try {
            selectedOrder = order;

            java.util.List<Order> planned = SearchManager.sm().findPlannedOrders();
            planned.add(Order.getUnknownOrder());
            if (!planned.contains(selectedOrder)) {
                planned.add(selectedOrder);
            }
            orderCb.updateList(planned);
            orderCb.setSelectedItem(selectedOrder);
        } finally {
            application.endWait();
        }

//        // Find items already in order
//        if (selectedOrder != null && selectedPcb != null) {
//            for (OrderItem oi : selectedOrder.getOrderItems()) {
//                for (PcbItem pcb : pcbItemPnl.pcbTableGetItemList()) {
//                    if (oi.getItemId() == pcb.getMatchedItemLink().getItemId()) {
//                        pcb.setOrderItem(oi);
//                        pcb.setOrderAmount(oi.getAmount());
//                        break;
//                    }
//                }
//            }
//            pcbItemPnl.pcbTableUpdate();
//        }
        pcbItemPnl.pcbTableUpdate();
        orderPnl.updateComponents(selectedOrder);
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setResizable(true);
        setTitleIcon(imageResource.readImage("Projects.Order.Title"));
        setTitleName(getTitle());

        // Order
        orderCb = new IComboBox<>(new ArrayList<>(), new DbObjectNameComparator<>(), true);
        orderCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && !application.isUpdating()) {
                updateOrder((Order) orderCb.getSelectedItem());
            }
        });

        // Panels
        pcbItemPnl = new OrderPcbItemPanel(this);
        orderPnl = new OrderedPcbItemsPanel(this);

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        // North
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(PanelUtils.createComboBoxWithButton(orderCb, onChangeOrder()), BorderLayout.EAST);

        // Center
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));

        pcbItemPnl.setBorder(PanelUtils.createTitleBorder("Pcb items"));
        orderPnl.setBorder(PanelUtils.createTitleBorder("To selectedOrder"));

        centerPanel.add(pcbItemPnl);
        centerPanel.add(orderPnl);

        // Add
        getContentPanel().add(northPanel, BorderLayout.NORTH);
        getContentPanel().add(centerPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            ProjectPcb pcb = (ProjectPcb) args[0];

            pcbItemPnl.updateComponents(pcb);
        }

        if (args.length > 1) {
            updateOrder((Order) args[1]);
        }
    }


}