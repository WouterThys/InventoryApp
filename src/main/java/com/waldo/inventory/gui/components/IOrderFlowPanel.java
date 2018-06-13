package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.ItemOrder;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.IPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class IOrderFlowPanel extends IPanel {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel plannedIcon;
    private ILabel orderedIcon;
    private ILabel receivedIcon;

    private ILabel dateModifiedLbl;
    private ILabel dateOrderedLbl;
    private ILabel dateReceivedLbl;

    private JToolBar toOrderTb;
    private JToolBar backPlannedTb;
    private JToolBar toReceivedTb;
    private JToolBar backOrderTb;

    private IActions.MoveToOrderedAction moveToOrderedAction = new IActions.MoveToOrderedAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            moveToOrdered(itemOrder);
        }
    };

    private IActions.MoveToReceivedAction moveToReceivedAction = new IActions.MoveToReceivedAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            moveToReceived(itemOrder);
        }
    };

    private IActions.BackToOrderedAction backToOrderedAction = new IActions.BackToOrderedAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            backToOrdered(itemOrder);
        }
    };

    private IActions.BackToPlannedAction backToPlannedAction = new IActions.BackToPlannedAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            backToPlanned(itemOrder);
        }
    };

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ItemOrder itemOrder;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public IOrderFlowPanel() {

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public abstract void moveToOrdered(ItemOrder itemOrder);
    public abstract void moveToReceived(ItemOrder itemOrder);
    public abstract void backToOrdered(ItemOrder itemOrder);
    public abstract void backToPlanned(ItemOrder itemOrder);

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        plannedIcon = new ILabel(imageResource.readIcon("Orders.Flow.Planned"));
        orderedIcon = new ILabel(imageResource.readIcon("Orders.Flow.Ordered"));
        receivedIcon = new ILabel(imageResource.readIcon("Orders.Flow.Received"));

        plannedIcon.setToolTipText("Planned");
        orderedIcon.setToolTipText("Ordered");
        receivedIcon.setToolTipText("Received");

        dateModifiedLbl = new ILabel();
        dateModifiedLbl.setFontSize(9);
        dateModifiedLbl.setForeground(Color.DARK_GRAY);
        dateOrderedLbl = new ILabel();
        dateOrderedLbl.setFontSize(9);
        dateModifiedLbl.setForeground(Color.DARK_GRAY);
        dateReceivedLbl = new ILabel();
        dateReceivedLbl.setFontSize(9);
        dateModifiedLbl.setForeground(Color.DARK_GRAY);

        dateModifiedLbl.setToolTipText("Last modified");
        dateOrderedLbl.setToolTipText("ItemOrder date");
        dateReceivedLbl.setToolTipText("Received date");

        backToPlannedAction.setTooltip("Back to planned");
        moveToOrderedAction.setTooltip("Move to ordered");
        moveToReceivedAction.setTooltip("Move to received");
        backToOrderedAction.setTooltip("Back to ordered");

        setBorder(BorderFactory.createEmptyBorder(2,5,0,5));
    }

    @Override
    public void initializeLayouts() {
        JPanel plannedPnl = new JPanel(new BorderLayout());
        JPanel orderedPnl = new JPanel(new BorderLayout());
        JPanel receivedPnl = new JPanel(new BorderLayout());

        plannedPnl.add(plannedIcon, BorderLayout.CENTER);
        plannedPnl.add(dateModifiedLbl, BorderLayout.SOUTH);
        orderedPnl.add(orderedIcon, BorderLayout.CENTER);
        orderedPnl.add(dateOrderedLbl, BorderLayout.SOUTH);
        receivedPnl.add(receivedIcon, BorderLayout.CENTER);
        receivedPnl.add(dateReceivedLbl, BorderLayout.SOUTH);

        toOrderTb = GuiUtils.createNewToolbar(moveToOrderedAction);
        backPlannedTb = GuiUtils.createNewToolbar(backToPlannedAction);
        toReceivedTb = GuiUtils.createNewToolbar(moveToReceivedAction);
        backOrderTb = GuiUtils.createNewToolbar(backToOrderedAction);

        add(plannedPnl);
        add(backPlannedTb);
        add(toOrderTb);
        add(orderedPnl);
        add(backOrderTb);
        add(toReceivedTb);
        add(receivedPnl);
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null && object[0] instanceof ItemOrder) {
            itemOrder = (ItemOrder) object[0];

            switch (itemOrder.getOrderState()) {
                case Planned:
                    dateModifiedLbl.setText(DateUtils.formatDate(itemOrder.getDateModified()));
                    dateOrderedLbl.setText("");

                    backToPlannedAction.setEnabled(false);
                    moveToOrderedAction.setEnabled(true);
                    moveToReceivedAction.setEnabled(false);
                    backToOrderedAction.setEnabled(false);

                    backPlannedTb.setVisible(false);
                    toOrderTb.setVisible(true);
                    toReceivedTb.setVisible(true);
                    backOrderTb.setVisible(false);

                    plannedIcon.setEnabled(true);
                    orderedIcon.setEnabled(false);
                    receivedIcon.setEnabled(false);
                    break;

                case Ordered:
                    dateModifiedLbl.setText(DateUtils.formatDate(itemOrder.getDateModified()));
                    dateOrderedLbl.setText(DateUtils.formatDate(itemOrder.getDateOrdered()));
                    dateReceivedLbl.setText("");

                    backToPlannedAction.setEnabled(true);
                    moveToOrderedAction.setEnabled(false);
                    moveToReceivedAction.setEnabled(true);
                    backToOrderedAction.setEnabled(false);

                    backPlannedTb.setVisible(true);
                    toOrderTb.setVisible(false);
                    toReceivedTb.setVisible(true);
                    backOrderTb.setVisible(false);

                    plannedIcon.setEnabled(false);
                    orderedIcon.setEnabled(true);
                    receivedIcon.setEnabled(false);
                    break;

                case Received:
                    dateModifiedLbl.setText(DateUtils.formatDate(itemOrder.getDateModified()));
                    dateOrderedLbl.setText(DateUtils.formatDate(itemOrder.getDateOrdered()));
                    dateReceivedLbl.setText(DateUtils.formatDate(itemOrder.getDateReceived()));

                    backToPlannedAction.setEnabled(false);
                    moveToOrderedAction.setEnabled(false);
                    moveToReceivedAction.setEnabled(false);
                    backToOrderedAction.setEnabled(true);

                    backPlannedTb.setVisible(true);
                    toOrderTb.setVisible(false);
                    toReceivedTb.setVisible(false);
                    backOrderTb.setVisible(true);

                    plannedIcon.setEnabled(false);
                    orderedIcon.setEnabled(false);
                    receivedIcon.setEnabled(true);
                    break;

                default: break;
            }

        } else {
            dateModifiedLbl.setText("");
            dateOrderedLbl.setText("");
            dateReceivedLbl.setText("");

            backToPlannedAction.setEnabled(false);
            moveToOrderedAction.setEnabled(false);
            moveToReceivedAction.setEnabled(false);
            backToOrderedAction.setEnabled(false);

            backPlannedTb.setVisible(false);
            toOrderTb.setVisible(true);
            toReceivedTb.setVisible(true);
            backOrderTb.setVisible(false);

            plannedIcon.setEnabled(false);
            orderedIcon.setEnabled(false);
            receivedIcon.setEnabled(false);
        }
    }
}