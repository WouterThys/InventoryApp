package com.waldo.inventory.gui.panels.orderspanel;

import com.waldo.inventory.classes.dbclasses.AbstractOrder;
import com.waldo.inventory.classes.dbclasses.ItemOrder;
import com.waldo.inventory.gui.components.IOrderFlowPanel;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

public class OrderDetailsPanel extends IPanel {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IOrderFlowPanel orderFlowPanel;

    private ITextField orderNameTf;
    private ITextField distributorTf;

    private ITextField orderReferenceTf;
    private ITextField trackingLinkTf;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {

        orderFlowPanel = new IOrderFlowPanel() {
            @Override
            public void moveToOrdered(ItemOrder itemOrder) {

            }

            @Override
            public void moveToReceived(ItemOrder itemOrder) {

            }

            @Override
            public void backToOrdered(ItemOrder itemOrder) {

            }

            @Override
            public void backToPlanned(ItemOrder itemOrder) {

            }
        };

        orderNameTf = new ITextField(false);
        distributorTf = new ITextField(false);

        orderReferenceTf = new ITextField(false);
        trackingLinkTf = new ITextField(false);


    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object... objects) {
        AbstractOrder order = null;
        if (objects != null && objects.length > 0) {
            order = (AbstractOrder) objects[0];
        }

        if (order != null) {

            orderNameTf.setText(order.toString());
            if (order.getDistributor() != null) {
                distributorTf.setText(order.getDistributor().toString());
            } else {
                distributorTf.setText("");
            }

            orderReferenceTf.setText(order.getOrderReference());
            trackingLinkTf.setText(order.getTrackingNumber());

            setVisible(true);
        } else {
            setVisible(false);
        }
    }
}
