package com.waldo.inventory.gui.dialogs.editorderfileformatdialog;

import com.waldo.inventory.classes.OrderFileFormat;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class EditOrderFileFormatDialogLayout extends IDialog implements IEditedListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField nameTf;
    ITextField separatorTf;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderFileFormat orderFileFormat;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditOrderFileFormatDialogLayout(Application application, String title) {
        super(application, title);
        showTitlePanel(false);
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        nameTf = new ITextField("name");
        nameTf.addEditedListener(this, "name");

        separatorTf = new ITextField("Separator");
        separatorTf.addEditedListener(this, "separator");
    }

    @Override
    public void initializeLayouts() {

        getContentPanel().setLayout(new GridBagLayout());

        // - Name
        ILabel nameLabel = new ILabel("Name: ");
        nameLabel.setHorizontalAlignment(ILabel.RIGHT);
        nameLabel.setVerticalAlignment(ILabel.CENTER);

        // - Separarotor
        ILabel separatorLabel = new ILabel("Separator: ");
        separatorLabel.setHorizontalAlignment(ILabel.RIGHT);
        separatorLabel.setVerticalAlignment(ILabel.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        getContentPanel().add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        getContentPanel().add(nameTf, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        getContentPanel().add(separatorLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        getContentPanel().add(separatorTf, gbc);

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null && object instanceof OrderFileFormat) {
            orderFileFormat = (OrderFileFormat) object;

            nameTf.setText(orderFileFormat.getName());
            separatorTf.setText(String.valueOf(orderFileFormat.getSeparator()));
        }
    }
}