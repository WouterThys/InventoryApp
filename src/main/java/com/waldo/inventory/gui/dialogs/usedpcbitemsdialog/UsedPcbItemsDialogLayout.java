package com.waldo.inventory.gui.dialogs.usedpcbitemsdialog;

import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.tablemodels.ILinkKiCadTableModel;
import com.waldo.inventory.gui.dialogs.linkitemdialog.extras.LinkPcbPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class UsedPcbItemsDialogLayout extends IDialog implements ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LinkPcbPanel pcbPanel;
    //PcbItemOrderPanel usedPanel;

    UsedAction addToUsed;
    UsedAction removeFromUsed;
    UsedAction addAll;
    UsedAction addAllCheck;

    PcbItem selectedComponent;
    OrderItem selectedOrderItem;
    Order selectedOrder;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     List<PcbItem> pcbItemList;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    UsedPcbItemsDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean enable = selectedComponent != null;
        addToUsed.setEnabled(enable);
        removeFromUsed.setEnabled(enable);

        //getButtonOK().setEnabled(usedPanel.hasOrderItems());
    }

    void addTableListeners(ListSelectionListener kcListListener, ListSelectionListener orderListListener) {
        pcbPanel.addListSelectionListener(kcListListener);
        //usedPanel.addListSelectionListener(orderListListener);
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("Projects.Used.Title"));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setEnabled(false);

        // Panels
        pcbPanel = new LinkPcbPanel(application, ILinkKiCadTableModel.ORDER_COMPONENTS);
        //usedPanel = new PcbItemOrderPanel(application);
        //usedPanel.addOnAmountChangedListener(this);

        // Actions
        addToUsed =
                new UsedAction(this,
                        imageResource.readImage("Projects.Order.AddToBtn"),
                        false);
        removeFromUsed =
                new UsedAction(this,
                        imageResource.readImage("Projects.Order.RemoveFromBtn"),
                        false);
        addAll =
                new UsedAction(this,
                        imageResource.readImage("Projects.Order.AddAllBtn"),
                        true);
        addAllCheck =
                new UsedAction(this,
                        imageResource.readImage("Projects.Order.AddAllCheckStockBtn"),
                        true);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        JToolBar buttonPanel = new JToolBar(JToolBar.VERTICAL);

        buttonPanel.setFloatable(false);
        buttonPanel.add(addToUsed);
        buttonPanel.add(removeFromUsed);
        buttonPanel.addSeparator();
        buttonPanel.add(addAll);
        buttonPanel.add(addAllCheck);

        mainPanel.add(pcbPanel);
        mainPanel.add(buttonPanel);
        //mainPanel.add(usedPanel);

        getContentPanel().add(mainPanel, BorderLayout.CENTER);
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,5,10,5));

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        pcbPanel.setItemList(pcbItemList);
    }

    private class UsedAction extends AbstractAction {

        ActionListener listener;

        UsedAction(ActionListener listener, ImageIcon actionIcon, boolean enabled) {
            super("", actionIcon);
            this.listener = listener;
            setEnabled(enabled);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            e.setSource(this);
            listener.actionPerformed(e);
        }
    }
}