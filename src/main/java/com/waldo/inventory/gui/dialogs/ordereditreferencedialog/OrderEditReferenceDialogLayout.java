package com.waldo.inventory.gui.dialogs.ordereditreferencedialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.DistributorPartLink;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.*;

public abstract class OrderEditReferenceDialogLayout extends IDialog implements IEditedListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel distributorLbl;
    private ITextField referenceTf;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     OrderItem orderItem;
     DistributorPartLink selectedDistributorPartLink;
     DistributorPartLink originalDistributorPartLink;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderEditReferenceDialogLayout(Application application, String title) {
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
        showTitlePanel(false);
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // This
        distributorLbl = new ILabel();
        referenceTf = new ITextField(this, "itemRef");
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new GridBagLayout());

        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(getContentPanel());
        gbc.addLine("Distributor: ", distributorLbl);
        gbc.addLine("Reference: ", referenceTf);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        selectedDistributorPartLink = null;
        originalDistributorPartLink = null;
        if (args.length > 0 && args[0] != null) {
            orderItem = (OrderItem) args[0];
            if (orderItem.getDistributorPartId() > DbObject.UNKNOWN_ID) {
                selectedDistributorPartLink = orderItem.getDistributorPartLink();
            } else {
                selectedDistributorPartLink = new DistributorPartLink(
                        orderItem.getOrder().getDistributorId(),
                        orderItem.getItemId()
                );
            }
            originalDistributorPartLink = selectedDistributorPartLink.createCopy();

            referenceTf.setText(selectedDistributorPartLink.getItemRef());
            distributorLbl.setText(orderItem.getOrder().getDistributor().toString());
        }
    }
}