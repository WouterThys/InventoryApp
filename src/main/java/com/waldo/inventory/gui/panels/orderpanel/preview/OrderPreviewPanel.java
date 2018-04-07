package com.waldo.inventory.gui.panels.orderpanel.preview;

import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.OrderLine;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.editordersdialog.EditOrdersDialog;
import com.waldo.inventory.gui.dialogs.orderconfirmdialog.OrderDetailsDialog;
import com.waldo.inventory.gui.dialogs.pendingordersdialog.PendingOrdersDialog;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class OrderPreviewPanel extends IPanel implements IdBToolBar.IdbToolBarListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private ITextField totalItemsTf;
    private ITextField totalPriceTf;
    private ITextField orderByTf;
    private IdBToolBar orderTb;

    private AbstractAction orderDetailsAa;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private final Order rootOrder;

    private Order selectedOrder;


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public OrderPreviewPanel(Application application, Order rootOrder) {
        this.application = application;
        this.rootOrder = rootOrder;
        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        boolean orderSelected = selectedOrder != null;
        boolean locked = orderSelected && selectedOrder.isLocked();

        orderTb.setEditActionEnabled(!locked);
        orderTb.setDeleteActionEnabled(!locked);
        orderDetailsAa.setEnabled(orderSelected);
    }

    private void setDetails(Order order) {
        if (order != null) {
            totalItemsTf.setText(String.valueOf(order.getOrderLines().size()));
            totalPriceTf.setText(String.valueOf(order.getTotalPrice()));
            orderByTf.setText(order.getDistributor().toString());
        }
    }

    public void addOrder() {
        EditOrdersDialog dialog = new EditOrdersDialog(application, new Order(), true);
        dialog.showDialog();
    }

    public void editOrder(Order order) {
        if (order != null) {
            EditOrdersDialog dialog = new EditOrdersDialog(application, order, true);
            dialog.showDialog();
        }
    }

    public void deleteOrder(Order order) {
        if (order != null && order.canBeSaved()) {
            int res = JOptionPane.showConfirmDialog(
                    application,
                    "Are you sure you want to delete \"" + order.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                SwingUtilities.invokeLater(() -> {
                    List<OrderLine> orderItems = selectedOrder.getOrderLines();
                    order.delete(); // Cascaded delete will delete order items too

                    // Do this after delete: items will not be updated in change listener for orders
                    for (OrderLine orderItem : orderItems) {
                        orderItem.updateOrderState();
                    }
                });
            }
        }
    }

    public void viewOrderDetails(Order order) {
        if (order != null && order.canBeSaved()) {
            OrderDetailsDialog dialog = new OrderDetailsDialog(application, "Confirm receive", order); // TODO
            if (order.isReceived()) {
                dialog.showDialog(OrderDetailsDialog.TAB_ORDER_DETAILS, null);
            } else {
                dialog.showDialog();
            }
        }
    }

    void viewPendingOrders() {
        PendingOrdersDialog dialog = new PendingOrdersDialog(application, "Pending orders");
        dialog.showDialog();
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        setDetails(selectedOrder);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        addOrder();
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        deleteOrder(selectedOrder);
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        editOrder(selectedOrder);
    }

    //
    // Gui
    //
    @Override
    public void initializeComponents() {
        totalItemsTf = new ITextField(false, 7);
        totalPriceTf = new ITextField(false, 7);
        orderByTf = new ITextField(false);
        orderTb = new IdBToolBar(this);

        orderDetailsAa = new AbstractAction("Details", imageResource.readIcon("Orders.Flow.Details")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewOrderDetails(selectedOrder);
            }
        };
        orderDetailsAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Details");

        AbstractAction pendingOrderAa = new AbstractAction("Pending orders", imageResource.readIcon("Actions.M.Pending")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPendingOrders();
            }
        };
        pendingOrderAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Pending orders");

        orderTb = new IdBToolBar(this);
        orderTb.addSeparateAction(orderDetailsAa);
        orderTb.addAction(pendingOrderAa);
    }

    @Override
    public void initializeLayouts() {
        GuiUtils.GridBagHelper gbc;

        JPanel totalItemsPnl = new JPanel();
        gbc = new GuiUtils.GridBagHelper(totalItemsPnl);
        gbc.addLine("Total items", imageResource.readIcon("Preview.Amount"), totalItemsTf);

        JPanel totalPricePnl = new JPanel();
        gbc = new GuiUtils.GridBagHelper(totalPricePnl);
        gbc.addLine("Total price", imageResource.readIcon("Preview.Price"), totalPriceTf);

        JPanel orderByPnl = new JPanel();
        gbc = new GuiUtils.GridBagHelper(orderByPnl);
        gbc.addLine("Order by", imageResource.readIcon("Distributors.Menu"), orderByTf);

        JPanel infoPnl = new JPanel();
        infoPnl.add(totalItemsPnl);
        infoPnl.add(totalPricePnl);


        JPanel centerPnl = new JPanel(new BorderLayout());
        JPanel southPnl = new JPanel(new BorderLayout());

        centerPnl.add(infoPnl, BorderLayout.NORTH);
        centerPnl.add(orderByPnl, BorderLayout.CENTER);
        southPnl.add(orderTb, BorderLayout.WEST);

        setLayout(new BorderLayout());
        add(centerPnl, BorderLayout.CENTER);
        add(southPnl, BorderLayout.NORTH);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length == 0 || args[0] == null) {
            selectedOrder = null;
        } else {
            if (args[0] instanceof Order) {
                selectedOrder = (Order) args[0];
            }
            setDetails(selectedOrder);
            updateEnabledComponents();
        }
    }
}
