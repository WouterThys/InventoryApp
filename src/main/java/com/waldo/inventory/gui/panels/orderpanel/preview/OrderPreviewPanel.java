package com.waldo.inventory.gui.panels.orderpanel.preview;

import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.ItemOrder;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.editordersdialog.EditOrdersDialog;
import com.waldo.inventory.gui.dialogs.orderdetailsdialog.OrderDetailsCacheDialog;
import com.waldo.inventory.gui.dialogs.ordersearchitemdialog.OrderSearchItemsDialog;
import com.waldo.inventory.gui.dialogs.pendingordersdialog.PendingOrdersCacheDialog;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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

    private ItemOrder selectedItemOrder;


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public OrderPreviewPanel(Application application) {
        this.application = application;
        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        boolean orderSelected = selectedItemOrder != null;
        boolean locked = orderSelected && selectedItemOrder.isLocked();

        orderTb.setEditActionEnabled(!locked);
        orderTb.setDeleteActionEnabled(!locked);
        orderDetailsAa.setEnabled(orderSelected);
    }

    private void setDetails(ItemOrder itemOrder) {
        if (itemOrder != null) {
//            totalItemsTf.setText(String.valueOf(itemOrder.getItemOrderLines().size()));
//            totalPriceTf.setText(String.valueOf(itemOrder.getTotalPrice()));
//            if (itemOrder.getDistributorId() > DbObject.UNKNOWN_ID) {
//                orderByTf.setText(itemOrder.getDistributor().toString());
//            } else {
//                orderByTf.setText("");
//            }
        }
    }

    public void addOrder() {
        EditOrdersDialog dialog = new EditOrdersDialog(application, new ItemOrder(), null, true);
        dialog.showDialog();
    }

    public void editOrder(ItemOrder itemOrder) {
        if (itemOrder != null) {
            Distributor distributor = itemOrder.getDistributor();
            EditOrdersDialog dialog;
            if (distributor != null) {
                dialog = new EditOrdersDialog(application, itemOrder,distributor.getDistributorType(),  true);
            } else {
                dialog = new EditOrdersDialog(application, itemOrder, null, true);
            }
            dialog.showDialog();
        }
    }

    public void deleteOrder(ItemOrder itemOrder) {
        if (itemOrder != null && itemOrder.canBeSaved()) {
            int res = JOptionPane.showConfirmDialog(
                    application,
                    "Are you sure you want to delete \"" + itemOrder.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                SwingUtilities.invokeLater(() -> {
//                    List<ItemOrderLine> orderItems = selectedItemOrder.getItemOrderLines();
//                    itemOrder.delete(); // Cascaded delete will delete itemOrder items too
//
//                    // Do this after delete: items will not be updated in change listener for orders
//                    for (ItemOrderLine orderItem : orderItems) {
//                        orderItem.updateOrderState();
//                    }
                });
            }
        }
    }

    public void viewOrderDetails(ItemOrder itemOrder) {
        if (itemOrder != null && itemOrder.canBeSaved()) {
            OrderDetailsCacheDialog dialog = new OrderDetailsCacheDialog(application, "Confirm receive", itemOrder); // TODO
            if (itemOrder.isReceived()) {
                dialog.showDialog(OrderDetailsCacheDialog.TAB_ORDER_DETAILS, null);
            } else {
                dialog.showDialog();
            }
        }
    }

    private void viewPendingOrders() {
        PendingOrdersCacheDialog dialog = new PendingOrdersCacheDialog(application, "Pending orders");
        dialog.showDialog();
    }

    private void searchOrderItems() {
        OrderSearchItemsDialog dialog = new OrderSearchItemsDialog(application);
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
        setDetails(selectedItemOrder);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        addOrder();
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        deleteOrder(selectedItemOrder);
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        editOrder(selectedItemOrder);
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
                viewOrderDetails(selectedItemOrder);
            }
        };
        orderDetailsAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Details");

//        AbstractAction pendingOrderAa = new AbstractAction("Pending orders", imageResource.readIcon("Actions.M.Pending")) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                viewPendingOrders();
//            }
//        };
//        pendingOrderAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Pending orders");

        IActions.SearchAction searchAction = new IActions.SearchAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchOrderItems();
            }
        };
        searchAction.setIcon(imageResource.readIcon("Actions.M.Search"));

        orderTb = new IdBToolBar(this);
        orderTb.addSeparateAction(orderDetailsAa);
        //orderTb.addAction(pendingOrderAa);
        orderTb.addAction(searchAction);
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
        gbc.addLine("ItemOrder by", imageResource.readIcon("Distributors.Menu"), orderByTf);

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
        add(southPnl, BorderLayout.SOUTH);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length == 0 || args[0] == null) {
            selectedItemOrder = null;
        } else {
            if (args[0] instanceof ItemOrder) {
                selectedItemOrder = (ItemOrder) args[0];
            }
            setDetails(selectedItemOrder);
            updateEnabledComponents();
        }
    }
}
