package com.waldo.inventory.gui.panels.orderpanel.orderitemdetailpanel;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.classes.DistributorPartLink;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.IFormattedTextField;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

public  class OrderItemDetailPanelLayout extends JPanel implements GuiInterface, IEditedListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField itemRefTf;
    IFormattedTextField amountTf;
    JButton addBtn;
    JButton minBtn;
    JButton saveBtn;

    OrderItem orderItem;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderItemDetailPanelLayout(Application application) {
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        itemRefTf = new ITextField("Item ref");
        itemRefTf.addEditedListener(this, "distributorPartId"); 
        amountTf = new IFormattedTextField(NumberFormat.getNumberInstance());
        amountTf.addEditedListener(this, "amount");

        addBtn = new JButton("+");
        addBtn.setPreferredSize(new Dimension(100,30));
        minBtn = new JButton("-");
        minBtn.setPreferredSize(new Dimension(100,30));
        saveBtn = new JButton("Save");
        saveBtn.setEnabled(false);
    }

    @Override
    public void initializeLayouts() {
        // Labels
        ILabel referenceLbl = new ILabel("Order reference: ");
        referenceLbl.setHorizontalAlignment(ILabel.RIGHT);
        referenceLbl.setVerticalAlignment(ILabel.CENTER);
        ILabel amountLbl = new ILabel("Order amount: ");
        amountLbl.setHorizontalAlignment(ILabel.RIGHT);
        amountLbl.setVerticalAlignment(ILabel.CENTER);

        // Panels
        JPanel fieldPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new GridBagLayout());

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Reference
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldPanel.add(referenceLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldPanel.add(itemRefTf, gbc);

        // Amount
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldPanel.add(amountLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldPanel.add(amountTf, gbc);

        // Add and subtract buttons
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        fieldPanel.add(addBtn, gbc);

        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        fieldPanel.add(minBtn, gbc);

//        gbc.gridx = 1; gbc.weightx = 1;
//        gbc.gridy = 1; gbc.weighty = 1;
//        gbc.gridwidth = 3;
//        gbc.fill = GridBagConstraints.BOTH;
//        buttonPanel.add(saveBtn, gbc);

        // Add to this layout
        setLayout(new BorderLayout());
        add(fieldPanel, BorderLayout.CENTER);
        add(saveBtn, BorderLayout.SOUTH);

    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length == 0) {
            setVisible(false);
            orderItem = null;
        } else {
            orderItem = (OrderItem) object[0];
            setVisible(true);
            DistributorPartLink distributorPartLink = orderItem.getDistributorPartLink();
            if (distributorPartLink != null) {
                itemRefTf.setText(distributorPartLink.getItemRef());
            } else {
                itemRefTf.setText("");
            }
            amountTf.setValue(orderItem.getAmount());
            saveBtn.setEnabled(false);
        }
    }

    //
    // Edit change listener
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        saveBtn.setEnabled(true);
    }

    @Override
    public DbObject getGuiObject() {
        return orderItem;
    }


}
