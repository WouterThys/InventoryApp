package com.waldo.inventory.gui.dialogs.packagedialog.editpackagetypedialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.PackageType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import java.awt.*;

public abstract class EditPackageTypeDialogLayout extends IDialog implements IEditedListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField nameTf;
    private ISpinner pinsSp;
    private ICheckBox allowOtherPinNumbersCb;
    private ITextArea descriptionTa;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PackageType packageType;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditPackageTypeDialogLayout(Application application, String title) {
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
        setResizable(true);

        // Name
        nameTf = new ITextField(this, "name");

        // Pins
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(2, 0, Integer.MAX_VALUE, 1);
        pinsSp = new ISpinner(spinnerModel);
        pinsSp.addEditedListener(this, "defaultPins");

        // Allow other pin numbers
        allowOtherPinNumbersCb = new ICheckBox("Allow other pin numbers ");
        allowOtherPinNumbersCb.addEditedListener(this, "allowOtherPinNumbers");

        // Description
        descriptionTa = new ITextArea();
        descriptionTa.addEditedListener(this, "description");
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(northPanel);
        gbc.addLine("Name: ", nameTf);
        gbc.addLine("Pins: ", pinsSp);
        gbc.addLine("", allowOtherPinNumbersCb);

        getContentPanel().add(northPanel, BorderLayout.NORTH);
        getContentPanel().add(new JScrollPane(descriptionTa), BorderLayout.CENTER);
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length > 0 && object[0] != null) {
            packageType = (PackageType) object[0];

            nameTf.setText(packageType.getName());
            pinsSp.setValue(packageType.getDefaultPins());
            allowOtherPinNumbersCb.setSelected(packageType.isAllowOtherPinNumbers());
            descriptionTa.setText(packageType.getDescription());
        }
    }
}