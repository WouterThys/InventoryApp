package com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;

import javax.swing.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class OrderPcbItemDialogLayout extends IDialog implements ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
   PcbItemOrderPanel pcbItemPnl;
   OrderedPcbItemsPanel orderPnl;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Order order;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderPcbItemDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */




    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setResizable(true);
        setTitleIcon(imageResource.readImage("Projects.Order.Title"));
        setTitleName(getTitle());
        // TODO: subtitle
        getButtonOK().setToolTipText("Order");
        getButtonOK().setEnabled(false);

        // Panels
        pcbItemPnl = new PcbItemOrderPanel(this);
        orderPnl = new OrderedPcbItemsPanel();

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.X_AXIS));

        //JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pcbItemPnl, orderPnl);

        pcbItemPnl.setBorder(PanelUtils.createTitleBorder("Pcb items"));
        orderPnl.setBorder(PanelUtils.createTitleBorder("To order"));

//        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
//        separator.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
//        getContentPanel().add(separator);
        getContentPanel().add(pcbItemPnl);
        getContentPanel().add(orderPnl);
//        separator = new JSeparator(JSeparator.HORIZONTAL);
//        separator.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
//        getContentPanel().add(separator);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            ProjectPcb pcb = (ProjectPcb) args[0];

            pcbItemPnl.updateComponents(pcb);
        }
    }


}