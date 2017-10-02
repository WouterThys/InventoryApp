package com.waldo.inventory.gui.dialogs.pcbitemorderdialog;

import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.tablemodels.ILinkKiCadTableModel;
import com.waldo.inventory.gui.dialogs.pcbitemorderdialog.extras.KcOrderItemPanel;
import com.waldo.inventory.gui.dialogs.linkitemdialog.extras.LinkPcbPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionListener;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class PcbItemOrderDialogLayout extends IDialog implements ActionListener, KcOrderItemPanel.AmountChangeListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     LinkPcbPanel pcbPanel;
     KcOrderItemPanel orderPanel;

    JButton addToOrderBtn;
    JButton removeFromOrderBtn;

     PcbItem selectedComponent;
     OrderItem selectedOrderItem;
     Order selectedOrder;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PcbItemOrderDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        addToOrderBtn.setEnabled(selectedComponent != null);
        removeFromOrderBtn.setEnabled(selectedOrderItem != null);

        getButtonOK().setEnabled(orderPanel.hasOrderItems());
    }

    void addTableListeners(ListSelectionListener kcListListener, ListSelectionListener orderListListener) {
        pcbPanel.addListSelectionListener(kcListListener);
        orderPanel.addListSelectionListener(orderListListener);
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("Projects.Order.Title"));
        setTitleName(getTitle());
        getButtonOK().setText("Order");
        getButtonOK().setEnabled(false);

        // Panels
        pcbPanel = new LinkPcbPanel(application, ILinkKiCadTableModel.ORDER_COMPONENTS);
        orderPanel = new KcOrderItemPanel(application);
        orderPanel.addOnAmountChangedListener(this);

        // Button
        addToOrderBtn = new JButton(imageResource.readImage("Projects.Order.AddToBtn"));
        addToOrderBtn.setToolTipText("Add to order");
        addToOrderBtn.setEnabled(false);
        addToOrderBtn.addActionListener(this);

        removeFromOrderBtn = new JButton(imageResource.readImage("Projects.Order.RemoveFromBtn"));
        removeFromOrderBtn.setToolTipText("Remove from order");
        removeFromOrderBtn.setEnabled(false);
        removeFromOrderBtn.addActionListener(this);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.X_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(addToOrderBtn);
        buttonPanel.add(removeFromOrderBtn);

        getContentPanel().add(pcbPanel);
        getContentPanel().add(buttonPanel);
        getContentPanel().add(orderPanel);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,5,10,5));

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null && object instanceof List) {
            pcbPanel.setItemList((List<PcbItem>)object);
        }
    }
}