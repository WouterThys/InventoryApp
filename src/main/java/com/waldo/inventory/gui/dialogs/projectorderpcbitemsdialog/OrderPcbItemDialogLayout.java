package com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.DbObject;
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

public abstract class OrderPcbItemDialogLayout extends IDialog implements ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
   PcbItemOrderPanel pcbItemPnl;
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


        // TODO: find items in order

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
        getButtonOK().setToolTipText("Order");
        getButtonOK().setEnabled(false);

        // Order
        orderCb = new IComboBox<>(new ArrayList<>(), new DbObject.DbObjectNameComparator<>(), true);
        orderCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && !application.isUpdating()) {
                updateOrder((Order) orderCb.getSelectedItem());
            }
        });

        // Panels
        pcbItemPnl = new PcbItemOrderPanel(this);
        orderPnl = new OrderedPcbItemsPanel();

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