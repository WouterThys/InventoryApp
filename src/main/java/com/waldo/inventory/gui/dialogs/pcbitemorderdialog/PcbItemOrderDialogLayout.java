package com.waldo.inventory.gui.dialogs.pcbitemorderdialog;

import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.tablemodels.ILinkKiCadTableModel;
import com.waldo.inventory.gui.dialogs.linkitemdialog.extras.LinkPcbPanel;
import com.waldo.inventory.gui.dialogs.pcbitemorderdialog.extras.PcbItemPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class PcbItemOrderDialogLayout extends IDialog implements ActionListener, PcbItemPanel.AmountChangeListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     LinkPcbPanel pcbPanel;
     PcbItemPanel orderPanel;

     OrderAction addToOrder;
     OrderAction removeFromOrder;
     OrderAction addAll;
     OrderAction addAllCheckStock;

     PcbItem selectedComponent;
     OrderItem selectedOrderItem;
     Order selectedOrder;

     private ILabel infoLbl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    List<PcbItem> pcbItemList;

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
        boolean enable = selectedComponent != null;
        addToOrder.setEnabled(enable);
        removeFromOrder.setEnabled(enable);

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
        orderPanel = new PcbItemPanel(application);
        orderPanel.addOnAmountChangedListener(this);

        // Actions
        addToOrder =
                new OrderAction(this,
                        imageResource.readImage("Projects.Order.AddToBtn"),
                        "Add to order",
                        false);
        removeFromOrder =
                new OrderAction(this,
                        imageResource.readImage("Projects.Order.RemoveFromBtn"),
                        "Remove from order",
                        false);
        addAll =
                new OrderAction(this,
                        imageResource.readImage("Projects.Order.AddAllBtn"),
                        "Add all",
                        true);
        addAllCheckStock =
                new OrderAction(this,
                        imageResource.readImage("Projects.Order.AddAllCheckStockBtn"),
                        "Add items short in stock",
                        true);

        // Info label
        infoLbl = new ILabel();
        infoLbl.setForeground(Color.gray);
        infoLbl.setText(" ");
    }

    private class OrderAction extends AbstractAction {

        ActionListener listener;
        String toolTip;

        OrderAction(ActionListener listener, ImageIcon actionIcon, String toolTip, boolean enabled) {
            super("", actionIcon);
            this.listener = listener;
            this.toolTip = toolTip;
            setEnabled(enabled);


        }

        @Override
        public void actionPerformed(ActionEvent e) {
            e.setSource(this);
            listener.actionPerformed(e);
        }

        String getToolTip() {
            return toolTip;
        }
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        JToolBar buttonPanel = new JToolBar(JToolBar.VERTICAL);

        buttonPanel.setFloatable(false);
        buttonPanel.add(addToOrder);
        buttonPanel.add(removeFromOrder);
        buttonPanel.addSeparator();
        buttonPanel.add(addAll);
        buttonPanel.add(addAllCheckStock);

        mainPanel.add(pcbPanel);
        mainPanel.add(buttonPanel);
        mainPanel.add(orderPanel);

        getContentPanel().add(mainPanel, BorderLayout.CENTER);
        getContentPanel().add(infoLbl, BorderLayout.SOUTH);
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,5,10,5));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null && object[0] instanceof List) {
            pcbItemList = (List<PcbItem>) object[0];
            pcbPanel.setItemList(pcbItemList);
        }
    }
}