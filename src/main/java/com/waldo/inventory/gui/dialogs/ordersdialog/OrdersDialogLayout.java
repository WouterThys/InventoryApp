package com.waldo.inventory.gui.dialogs.ordersdialog;


import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.*;

public class OrdersDialogLayout extends IDialog
        implements GuiInterface {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField nameField;
    JComboBox<Distributor> distributorCb;
    DefaultComboBoxModel<Distributor> distributorCbModel;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public OrdersDialogLayout(Application application, String title) {
        super(application, title);
        showTitlePanel(false);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
    *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        nameField = new ITextField("Order name");
        distributorCbModel = new DefaultComboBoxModel<>();
        distributorCb = new JComboBox<>(distributorCbModel);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Name
        ILabel nameLabel = new ILabel("Name: ");
        nameLabel.setHorizontalAlignment(JLabel.RIGHT);
        nameLabel.setVerticalAlignment(JLabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(nameField, gbc);

        // Distributor
        ILabel distributorLabel = new ILabel("Distributor: ");
        distributorLabel.setHorizontalAlignment(JLabel.RIGHT);
        distributorLabel.setVerticalAlignment(JLabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(distributorLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(distributorCb, gbc);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    }

    @Override
    public void updateComponents(Object object) {
        distributorCbModel.removeAllElements();
        for (Distributor d : DbManager.db().getDistributors()) {
            if (d.getId() != DbObject.UNKNOWN_ID) {
                distributorCbModel.addElement(d);
            }
        }
    }
}
