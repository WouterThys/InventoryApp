package com.waldo.inventory.gui.panels.orderspanel;

import com.waldo.inventory.classes.dbclasses.AbstractOrder;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IOrderFlowPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.managers.OrderManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class OrderDetailsPanel extends IPanel implements IdBToolBar.IdbToolBarListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IOrderFlowPanel orderFlowPanel;

    private ITextField orderNameTf;
    private ITextField distributorTf;

    private ITextField orderReferenceTf;
    private ITextField trackingLinkTf;

    private IdBToolBar ordersToolBar;

    // Actions
    private IActions.EditAction editDistributorAction;

    private IActions.EditAction editReferenceAction;
    private IActions.DeleteAction deleteReferenceAction;
    private IActions.BrowseWebAction browseReferenceAction;

    private IActions.EditAction editTrackingAction;
    private IActions.DeleteAction deleteTrackingAction;
    private IActions.BrowseWebAction browseTrackingAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private AbstractOrder selectedOrder;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public OrderDetailsPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
    }


    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        boolean enabled = selectedOrder != null;
        boolean locked = enabled && selectedOrder.isLocked();

        boolean hasReference = enabled && !selectedOrder.getOrderReference().isEmpty();
        boolean hasTracking = enabled && !selectedOrder.getTrackingNumber().isEmpty();

        ordersToolBar.setEditActionEnabled(enabled);
        ordersToolBar.setDeleteActionEnabled(enabled);

        editDistributorAction.setEnabled(!locked);
        editReferenceAction.setEnabled(!locked);
        deleteReferenceAction.setEnabled(!locked && hasReference);
        browseReferenceAction.setEnabled(hasReference);
        editTrackingAction.setEnabled(!locked);
        deleteTrackingAction.setEnabled(!locked && hasTracking);
        browseTrackingAction.setEnabled(hasTracking);
    }

    private void editDistributor() {
        if (selectedOrder != null && !selectedOrder.isLocked()) {

        }
    }

    private void editReference() {
        if (selectedOrder != null && !selectedOrder.isLocked()) {

        }
    }

    private void deleteReference() {
        if (selectedOrder != null && !selectedOrder.isLocked()) {

        }
    }

    private void browseReference() {
        if (selectedOrder != null && !selectedOrder.isLocked()) {

        }
    }

    private void editTracking() {
        if (selectedOrder != null && !selectedOrder.isLocked()) {

        }
    }

    private void deleteTracking() {
        if (selectedOrder != null && !selectedOrder.isLocked()) {

        }
    }

    private void browseTracking() {
        if (selectedOrder != null && !selectedOrder.isLocked()) {

        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {

        orderFlowPanel = new IOrderFlowPanel() {
            @Override
            public void moveToOrdered(AbstractOrder order) {
                if (order != null) {
                    OrderManager.moveToOrdered(order);
                }
            }

            @Override
            public void moveToReceived(AbstractOrder order) {
                if (order != null) {
                    OrderManager.moveToReceived(order);
                }
            }

            @Override
            public void backToOrdered(AbstractOrder order) {
                if (order != null) {
                    OrderManager.backToOrdered(order);
                }
            }

            @Override
            public void backToPlanned(AbstractOrder order) {
                if (order != null) {
                    OrderManager.backToPlanned(order);
                }
            }
        };

        orderNameTf = new ITextField(false);
        distributorTf = new ITextField(false);

        orderReferenceTf = new ITextField(false);
        trackingLinkTf = new ITextField(false);

        ordersToolBar = new IdBToolBar(this, false, false, true, true);

        editDistributorAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> editDistributor());
            }
        };
        editReferenceAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> editReference());
            }
        };
        deleteReferenceAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> deleteReference());
            }
        };
        browseReferenceAction = new IActions.BrowseWebAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> browseReference());
            }
        };
        editTrackingAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> editTracking());
            }
        };
        deleteTrackingAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> deleteTracking());
            }
        };
        browseTrackingAction = new IActions.BrowseWebAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> browseTracking());
            }
        };
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(infoPanel);
        gbc.addLine("Order name", imageResource.readIcon("Actions.Tag"), orderNameTf);
        gbc.addLine("Distributor", imageResource.readIcon("Distributors.Menu"), GuiUtils.createComponentWithActions(distributorTf, editDistributorAction)); // TODO label
        gbc.addLine("Order reference", imageResource.readIcon("Orders.LineReference"), GuiUtils.createComponentWithActions(orderReferenceTf, editReferenceAction, deleteReferenceAction, browseReferenceAction)); // TODO edit/delete
        gbc.addLine("Tracking reference", imageResource.readIcon("Orders.Table.Ordered"), GuiUtils.createComponentWithActions(trackingLinkTf, editTrackingAction, deleteTrackingAction, browseTrackingAction)); // TODO actions


        Box box = Box.createVerticalBox();
        box.add(orderFlowPanel);
        box.add(infoPanel);

        add(ordersToolBar, BorderLayout.PAGE_START);
        add(box, BorderLayout.CENTER);

    }

    @Override
    public void updateComponents(Object... objects) {
        if (objects != null && objects.length > 0) {
            selectedOrder = (AbstractOrder) objects[0];
        }

        if (selectedOrder != null) {

            orderNameTf.setText(selectedOrder.toString());
            if (selectedOrder.getDistributor() != null) {
                distributorTf.setText(selectedOrder.getDistributor().toString());
            } else {
                distributorTf.setText("");
            }

            orderReferenceTf.setText(selectedOrder.getOrderReference());
            trackingLinkTf.setText(selectedOrder.getTrackingNumber());
            orderFlowPanel.updateComponents(selectedOrder);

            updateEnabledComponents();

            setVisible(true);
        } else {
            setVisible(false);
        }
    }


    @Override
    public void onToolBarRefresh(IdBToolBar source) {

    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {

    }
}
