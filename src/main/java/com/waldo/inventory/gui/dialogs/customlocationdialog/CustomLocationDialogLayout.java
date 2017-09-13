package com.waldo.inventory.gui.dialogs.customlocationdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.Location;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class CustomLocationDialogLayout extends IDialog implements
        ILocationMapPanel.LocationClickListener,
        ActionListener,
        ChangeListener,
        ItemListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ILocationMapPanel locationMapPanel;

    // Easy number of rows and columns
    ISpinner rowsSpinner;
    ISpinner columnsSpinner;

    // Custom
    JCheckBox customTb;
    ITextArea inputTa;
    JButton convertBtn;

    // Extra
    ITextField nameTf;
    ITextField aliasTf;
    JButton setNameBtn;
    JButton setAliasBtn;


     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     LocationType locationType;
     ILocationButton selectedLocationButton;
     List<Location> locationList;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    CustomLocationDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        if (selectedLocationButton == null) {
            nameTf.setEnabled(false);
            aliasTf.setEnabled(false);
        } else {
            nameTf.setEnabled(true);
            aliasTf.setEnabled(true);
        }

        if (customTb.isSelected()) {
            columnsSpinner.setEnabled(false);
            rowsSpinner.setEnabled(false);

            inputTa.setEnabled(true);
            convertBtn.setEnabled(true);
        } else {
            columnsSpinner.setEnabled(true);
            rowsSpinner.setEnabled(true);

            inputTa.setEnabled(false);
            convertBtn.setEnabled(false);
        }
    }

    void setButtonDetails(Location location) {
        if (location != null) {
            nameTf.setText(location.getName());
            aliasTf.setText(location.getAlias());
        } else {
            nameTf.clearText();
            aliasTf.clearText();
        }
    }

    void setLocationDetails(LocationType locationType) {
        if (locationType != null) {
            if (locationType.isCustom()) {
                customTb.setSelected(true);
                columnsSpinner.setValue(1);
                rowsSpinner.setValue(1);
                fillTextArea(locationType);
            } else {
                customTb.setSelected(false);
                columnsSpinner.setValue(locationType.getColumns());
                rowsSpinner.setValue(locationType.getRows());
                inputTa.clearText();
            }
        } else {
            customTb.setSelected(false);
            columnsSpinner.setValue(1);
            rowsSpinner.setValue(1);
            inputTa.clearText();
        }
    }

    private void fillTextArea(LocationType locationType) {
        if (locationType != null) {
            StringBuilder input = new StringBuilder();
            List<ILocationButton> tmp = new ArrayList<>(locationMapPanel.getLocationButtons());
            int r = 0;
            while (tmp.size() > 0) {
                List<ILocationButton> buttonsPerRow = locationMapPanel.locationButtonsForRow(r, tmp);

                for (ILocationButton button : buttonsPerRow) {
                    input.append(button.getName()).append(",");
                }
                input.append("\n");
                tmp.removeAll(buttonsPerRow);
                r++;
            }
            inputTa.setText(input.toString());
        } else {
            inputTa.clearText();
        }
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel detailPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        detailPanel.add(new ILabel("Name: ", ILabel.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        detailPanel.add(PanelUtils.createBrowsePanel(nameTf, setNameBtn), gbc);

        // Alias
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        detailPanel.add(new ILabel("Alias: ", ILabel.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        detailPanel.add(PanelUtils.createBrowsePanel(aliasTf, setAliasBtn), gbc);

        centerPanel.add(locationMapPanel, BorderLayout.CENTER);
        centerPanel.add(detailPanel, BorderLayout.SOUTH);

        return centerPanel;
    }

    private JPanel createEastPanel() {
        JPanel eastPanel = new JPanel(new BorderLayout());
        JPanel simplePanel = new JPanel(new GridBagLayout());
        JPanel customPanel = new JPanel(new BorderLayout());

        TitledBorder simpelBorder = PanelUtils.createTitleBorder("Simple");
        eastPanel.setBorder(simpelBorder);
        TitledBorder customBorder = PanelUtils.createTitleBorder("Custom");
        customBorder.setBorder(customBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Rows
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        simplePanel.add(new ILabel("Rows: ", ILabel.LEFT), gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        simplePanel.add(rowsSpinner, gbc);

        // Columns
        gbc.gridx = 1; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        simplePanel.add(new ILabel("Columns: ", ILabel.LEFT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        simplePanel.add(columnsSpinner, gbc);

        customPanel.add(customTb, BorderLayout.NORTH);
        customPanel.add(new JScrollPane(inputTa), BorderLayout.CENTER);
        customPanel.add(convertBtn, BorderLayout.SOUTH);

        eastPanel.add(simplePanel, BorderLayout.NORTH);
        eastPanel.add(customPanel, BorderLayout.CENTER);

        return eastPanel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        showTitlePanel(false);
        setResizable(true);

        // West panel
        locationMapPanel = new ILocationMapPanel(application, this);
        locationMapPanel.setPreferredSize(new Dimension(300,300));

        // Easy number of rows and columns
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        rowsSpinner = new ISpinner(spinnerNumberModel);
        rowsSpinner.setPreferredSize(new Dimension(60, 30));
        rowsSpinner.addChangeListener(this);
        spinnerNumberModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        columnsSpinner = new ISpinner(spinnerNumberModel);
        columnsSpinner.setPreferredSize(new Dimension(60, 30));
        columnsSpinner.addChangeListener(this);

        // Custom
        customTb = new JCheckBox("Custom");
        customTb.addItemListener(this);
        inputTa = new ITextArea();
        convertBtn = new JButton("Convert");
        convertBtn.addActionListener(this);

        // Extra
        nameTf = new ITextField("Name");
        aliasTf = new ITextField("Alias");
        setNameBtn = new JButton(imageResource.readImage("Common.Edit", 16));
        setAliasBtn = new JButton(imageResource.readImage("Common.Edit", 16));
        setNameBtn.addActionListener(this);
        setAliasBtn.addActionListener(this);

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(createCenterPanel(), BorderLayout.CENTER);
        getContentPanel().add(createEastPanel(), BorderLayout.EAST);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null && object instanceof LocationType) {
            locationType = (LocationType) object;
            locationList = locationType.getLocations();
        } else {
            locationType = null;
            locationList = new ArrayList<>();
        }

        locationMapPanel.updateComponents(locationType);
        setLocationDetails(locationType);

        updateEnabledComponents();
    }
}