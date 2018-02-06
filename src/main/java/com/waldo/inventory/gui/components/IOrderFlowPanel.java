package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.IImageButton;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.gui.Application.imageResource;

public class IOrderFlowPanel extends JPanel implements GuiUtils.GuiInterface {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel plannedIcon;
    private ILabel orderedIcon;
    private ILabel receivedIcon;

    private ILabel dateModifiedLbl;
    private ILabel dateOrderedLbl;
    private ILabel dateReceivedLbl;

    private IImageButton setOrderedBtn;
    private IImageButton setReceivedBtn;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


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
    public void addOrderClickListener(ActionListener listener) {
        setOrderedBtn.addActionListener(listener);
    }

    public void addReceivedClickListener(ActionListener listener) {
        setReceivedBtn.addActionListener(listener);
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        plannedIcon = new ILabel(imageResource.readImage("Orders.Flow.Planned"));
        orderedIcon = new ILabel(imageResource.readImage("Orders.Flow.Ordered"));
        receivedIcon = new ILabel(imageResource.readImage("Orders.Flow.Received"));

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
        dateOrderedLbl.setToolTipText("Order date");
        dateReceivedLbl.setToolTipText("Received date");

        setOrderedBtn = new IImageButton(
                imageResource.readImage("Orders.Flow.ArrowBlue"),
                imageResource.readImage("Orders.Flow.ArrowGreen"),
                imageResource.readImage("Orders.Flow.ArrowGreen"),
                imageResource.readImage("Orders.Flow.ArrowGray"));
        setReceivedBtn = new IImageButton(
                imageResource.readImage("Orders.Flow.ArrowBlue"),
                imageResource.readImage("Orders.Flow.ArrowGreen"),
                imageResource.readImage("Orders.Flow.ArrowGreen"),
                imageResource.readImage("Orders.Flow.ArrowGray"));

        setOrderedBtn.setBorder(BorderFactory.createEmptyBorder());
        setOrderedBtn.setContentAreaFilled(false);

        setReceivedBtn.setBorder(BorderFactory.createEmptyBorder());
        setReceivedBtn.setContentAreaFilled(false);

        setOrderedBtn.setToolTipText("Set ordered");
        setReceivedBtn.setToolTipText("Set Received");

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

        add(plannedPnl);
        add(setOrderedBtn);
        add(orderedPnl);
        add(setReceivedBtn);
        add(receivedPnl);
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null && object[0] instanceof Order) {
            Order order = (Order) object[0];

            switch (order.getOrderState()) {
                case Planned:
                    dateModifiedLbl.setText(DateUtils.formatDate(order.getDateModified()));
                    dateOrderedLbl.setText("");
                    dateReceivedLbl.setText("");

                    setOrderedBtn.setEnabled(true);
                    setReceivedBtn.setEnabled(false);

                    plannedIcon.setEnabled(true);
                    orderedIcon.setEnabled(false);
                    receivedIcon.setEnabled(false);
                    break;

                case Ordered:
                    dateModifiedLbl.setText(DateUtils.formatDate(order.getDateModified()));
                    dateOrderedLbl.setText(DateUtils.formatDate(order.getDateOrdered()));
                    dateReceivedLbl.setText("");

                    setOrderedBtn.setEnabled(false);
                    setReceivedBtn.setEnabled(true);

                    plannedIcon.setEnabled(false);
                    orderedIcon.setEnabled(true);
                    receivedIcon.setEnabled(false);
                    break;

                case Received:
                    dateModifiedLbl.setText(DateUtils.formatDate(order.getDateModified()));
                    dateOrderedLbl.setText(DateUtils.formatDate(order.getDateOrdered()));
                    dateReceivedLbl.setText(DateUtils.formatDate(order.getDateReceived()));

                    setOrderedBtn.setEnabled(false);
                    setReceivedBtn.setEnabled(false);

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

            setOrderedBtn.setEnabled(false);
            setReceivedBtn.setEnabled(false);

            plannedIcon.setEnabled(false);
            orderedIcon.setEnabled(false);
            receivedIcon.setEnabled(false);
        }
    }
}