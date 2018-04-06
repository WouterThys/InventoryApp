package com.waldo.inventory.gui.panels.orderpanel.preview;

import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.editordersdialog.EditOrdersDialog;
import com.waldo.inventory.gui.panels.orderpanel.OrderPanel;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;
import org.apache.xpath.operations.Or;

import javax.swing.*;
import java.awt.*;
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
        boolean enabled = selectedOrder != null;
        orderTb.setEditActionEnabled(enabled);
        orderTb.setDeleteActionEnabled(enabled);
    }

    private void setDetails(Order order) {
        if (order != null) {
            totalItemsTf.setText(String.valueOf(order.getOrderItems().size()));
            totalPriceTf.setText(String.valueOf(order.getTotalPrice()));
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
            int res = JOptionPane.showConfirmDialog(OrderPreviewPanel.this, "Are you sure you want to delete \"" + order.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                SwingUtilities.invokeLater(() -> {
                    List<OrderItem> orderItems = selectedOrder.getOrderItems();
                    order.delete(); // Cascaded delete will delete order items too

                    // Do this after delete: items will not be updated in change listener for orders
                    for (OrderItem orderItem : orderItems) {
                        orderItem.updateOrderState();
                    }
                });
            }
        }
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
        addOrder(selectedOrder);
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
        totalItemsTf = new ITextField(false, 8);
        totalPriceTf = new ITextField(false, 8);
        orderByTf = new ITextField(false);
        orderTb = new IdBToolBar(this);
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

        JPanel centerPnl = new JPanel(new BorderLayout());
        JPanel southPnl = new JPanel(new BorderLayout());

        centerPnl.add(totalItemsPnl, BorderLayout.EAST);
        centerPnl.add(divisionPanel, BorderLayout.CENTER);
        southPnl.add(setTb, BorderLayout.WEST);

        setLayout(new BorderLayout());
        add(centerPnl, BorderLayout.CENTER);
        add(southPnl, BorderLayout.NORTH);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length == 0 || args[0] == null) {
            setVisible(false);
            selectedOrder = null;
        } else {
            setVisible(true);
            if (args[0] instanceof Order) {
                selectedOrder = (Order) args[0];
            }
            setDetails(selectedOrder);
            updateEnabledComponents();
        }
    }
}
