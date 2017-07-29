package com.waldo.inventory.gui.dialogs.packagedialog.extras;

import com.waldo.inventory.classes.DimensionType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.*;

public class EditDimensionDialog extends IDialog {

    private DimensionType dimensionType;

    private ITextField nameTf;
    private ITextField widthTf;
    private ITextField heightTf;

    public EditDimensionDialog(Application application, String title, DimensionType dimensionType) {
        super(application, title);

        showTitlePanel(false);

        initializeComponents();
        initializeLayouts();
        updateComponents(dimensionType);

    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    @Override
    protected void onOK() {
        if (verify()) {
            super.onOK();
        }
    }

    private boolean verify() {
        boolean ok = true;

        String name = nameTf.getText();
        String widthTxt = widthTf.getText();
        String heightTxt =  heightTf.getText();

        if (name == null || name.isEmpty()) {
            nameTf.setError("Name can't be empty..");
            ok = false;
        } else {
            dimensionType.setName(name);
        }

        if (widthTxt != null && !widthTxt.isEmpty()) {
            try {
                double d = Double.valueOf(widthTxt);
                dimensionType.setWidth(d);
            } catch (Exception e) {
                widthTf.setError("This should be a number..");
                ok = false;
            }
        }

        if (heightTxt != null && !heightTxt.isEmpty()) {
            try {
                double d = Double.valueOf(heightTxt);
                dimensionType.setHeight(d);
            } catch (Exception e) {
                heightTf.setError("This should be a number..");
                ok = false;
            }
        }

        return ok;
    }

    @Override
    public void initializeComponents() {
        nameTf = new ITextField();
        widthTf = new ITextField();
        heightTf = new ITextField();
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels
        ILabel nameLabel = new ILabel("Name: ");
        ILabel widthLabel = new ILabel("Width");
        ILabel heightLabel = new ILabel("Height");

        // - name
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1;
        getContentPanel().add(nameLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 2;
        getContentPanel().add(nameTf, gbc);

        // - width
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1;
        getContentPanel().add(widthLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 3; gbc.weighty = 0;
        getContentPanel().add(widthTf, gbc);

        // - height
        gbc.gridx = 1; gbc.weightx = 0;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1;
        getContentPanel().add(heightLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 3; gbc.weighty = 0;
        getContentPanel().add(heightTf, gbc);

        // - border
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null && object instanceof DimensionType) {
            dimensionType = (DimensionType) object;

            nameTf.setText(dimensionType.getName());
            widthTf.setText(String.valueOf(dimensionType.getWidth()));
            heightTf.setText(String.valueOf(dimensionType.getHeight()));
        }
    }
}