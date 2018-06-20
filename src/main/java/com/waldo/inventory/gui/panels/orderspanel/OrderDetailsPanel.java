package com.waldo.inventory.gui.panels.orderspanel;

import com.waldo.inventory.classes.dbclasses.AbstractOrder;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IOrderFlowPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.managers.OrderManager;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.OpenUtils;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Vector;

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
            int res = JOptionPane.YES_OPTION;
            if (selectedOrder.getDistributorId() > DbObject.UNKNOWN_ID) {
                res = JOptionPane.showConfirmDialog(
                        application,
                        "Changing the distributor will also change \r\n " +
                                "all the order line references and prices, do you want \r\n" +
                                "to continue?",
                        "Distributor",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
            }

            if (res == JOptionPane.YES_OPTION) {
                // TODO show dialog to select distributor
                Vector<Distributor> distributors = new Vector<>(SearchManager.sm().findDistributorsByType(selectedOrder.getDistributorType()));
                JComboBox<Distributor> distributorCb = new JComboBox<>(distributors);
                res = JOptionPane.showConfirmDialog(
                        application,
                        distributorCb,
                        "Select a distributor",
                        JOptionPane.OK_CANCEL_OPTION
                );

                if (res == JOptionPane.OK_OPTION) {
                    Distributor distributor = (Distributor) distributorCb.getSelectedItem();
                    if (distributor != null && distributor.getId() != selectedOrder.getDistributorId()) {
                        selectedOrder.setDistributorId(distributor.getId());
                        selectedOrder.save();

                        selectedOrder.updateLineReferences();
                    }
                }
            }
        }
    }

    private void editReference() {
        if (selectedOrder != null) {
            if (!selectedOrder.isLocked()) {
                String input = JOptionPane.showInputDialog(
                        application,
                        "Enter a order reference",
                        "Order reference",
                        JOptionPane.INFORMATION_MESSAGE
                );

                selectedOrder.setOrderReference(input);
                selectedOrder.save();
            } else {
                JOptionPane.showMessageDialog(
                        application,
                        "Can not edit reference when the order is locked..",
                        "Locked",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    private void deleteReference() {
        if (selectedOrder != null) {
            if (!selectedOrder.isLocked()) {
                if (!selectedOrder.getOrderReference().isEmpty()) {
                    selectedOrder.setOrderReference("");
                    selectedOrder.save();
                }
            } else {
                JOptionPane.showMessageDialog(
                        application,
                        "Can not delete reference when the order is locked..",
                        "Locked",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    private void browseReference() {
        if (selectedOrder != null) {
            String reference = selectedOrder.getOrderReference();
            if (!reference.isEmpty()) {
                if (reference.contains("www") && reference.contains(".")) {
                    try {
                        OpenUtils.browseLink(reference);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(this,
                                "Could not browse reference: " + reference,
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Reference it not a web url, reference: " + reference,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void editTracking() {
        if (selectedOrder != null) {
            if (!selectedOrder.isLocked()) {
                String input = JOptionPane.showInputDialog(
                        application,
                        "Enter a order tracking",
                        "Tracking",
                        JOptionPane.INFORMATION_MESSAGE
                );

                selectedOrder.setOrderReference(input);
                selectedOrder.save();
            } else {
                JOptionPane.showMessageDialog(
                        application,
                        "Can not edit reference when the order is locked..",
                        "Locked",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    private void deleteTracking() {
        if (selectedOrder != null) {
                if (!selectedOrder.isLocked()) {
                    if (!selectedOrder.getTrackingNumber().isEmpty()) {
                        selectedOrder.setTrackingNumber("");
                        selectedOrder.save();
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            application,
                            "Can not delete tracking when the order is locked..",
                            "Locked",
                            JOptionPane.WARNING_MESSAGE
                    );
                }

        }
    }

    private void browseTracking() {
        if (selectedOrder != null) {
            String tracking = selectedOrder.getTrackingNumber();
            if (!tracking.isEmpty()) {
                if (tracking.contains("www") && tracking.contains(".")) {
                    try {
                        OpenUtils.browseLink(tracking);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(this,
                                "Could not browse tracking: " + tracking,
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Tracking reference it not a web url, tracking: " + tracking,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
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
