package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.SimpleDateFormat;

import static com.waldo.inventory.gui.Application.imageResource;

public class IOrderFlowPanel extends JPanel implements GuiInterface {

    private static final SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyy-MM-dd");

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
    Application application;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public IOrderFlowPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
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
        plannedIcon = new ILabel(imageResource.readImage("OrderFlow.Planned", 48));
        orderedIcon = new ILabel(imageResource.readImage("OrderFlow.Ordered", 48));
        receivedIcon = new ILabel(imageResource.readImage("OrderFlow.Received", 40));

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
                imageResource.readImage("Common.RArrow", 32),
                imageResource.readImage("Common.RArrowGreen", 32),
                imageResource.readImage("Common.RArrowGreen", 32),
                imageResource.readImage("Common.RArrowGray", 32));
        setReceivedBtn = new IImageButton(
                imageResource.readImage("Common.RArrow", 32),
                imageResource.readImage("Common.RArrowGreen", 32),
                imageResource.readImage("Common.RArrowGreen", 32),
                imageResource.readImage("Common.RArrowGray", 32));

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
    public void updateComponents(Object object) {
        if (object != null && object instanceof Order) {
            Order order = (Order) object;

            switch (order.getOrderState()) {
                case Statics.ItemOrderStates.PLANNED:
                    dateModifiedLbl.setText(dateFormatShort.format(order.getDateModified()));
                    dateOrderedLbl.setText("");
                    dateReceivedLbl.setText("");

                    setOrderedBtn.setEnabled(true);
                    setReceivedBtn.setEnabled(false);

                    plannedIcon.setEnabled(true);
                    orderedIcon.setEnabled(false);
                    receivedIcon.setEnabled(false);
                    break;

                case Statics.ItemOrderStates.ORDERED:
                    dateModifiedLbl.setText(dateFormatShort.format(order.getDateModified()));
                    dateOrderedLbl.setText(dateFormatShort.format(order.getDateOrdered()));
                    dateReceivedLbl.setText("");

                    setOrderedBtn.setEnabled(false);
                    setReceivedBtn.setEnabled(true);

                    plannedIcon.setEnabled(false);
                    orderedIcon.setEnabled(true);
                    receivedIcon.setEnabled(false);
                    break;

                case Statics.ItemOrderStates.RECEIVED:
                    dateModifiedLbl.setText(dateFormatShort.format(order.getDateModified()));
                    dateOrderedLbl.setText(dateFormatShort.format(order.getDateOrdered()));
                    dateReceivedLbl.setText(dateFormatShort.format(order.getDateReceived()));

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