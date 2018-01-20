package com.waldo.inventory.gui.dialogs.customlocationdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.actions.IActions;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

abstract class CustomLocationDialogLayout extends IDialog implements
        ILocationMapPanel.LocationClickListener,
        ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ILocationMapPanel locationMapPanel;

    ITextArea inputTa;
    JButton convertBtn;

    // Extra
    ITextField nameTf;
    IActions.SaveAction saveNameAction;

    ITextField aliasTf;
    IActions.SearchAction searchAliasAction;
    IActions.SaveAction saveAliasAction;


     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LocationType locationType;
     ILocationButton selectedLocationButton;
     List<Location> newLocationList;

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
        boolean enabled = selectedLocationButton != null;

        nameTf.setEnabled(enabled);
        saveNameAction.setEnabled(enabled);

        aliasTf.setEnabled(enabled);
        saveAliasAction.setEnabled(enabled);
        searchAliasAction.setEnabled(enabled);
    }

    void setButtonDetails(Location location) {
        if (location != null) {
            nameTf.setText(location.getName());
            aliasTf.setText(location.getAlias());
        } else {
            nameTf.clearText();
            nameTf.clearText();
        }
    }

    boolean isInLocationList(int row, int col) {
        for (Location location : newLocationList) {
            if (location.getRow() == row && location.getCol() == col) {
                return true;
            }
        }
        return false;
    }

    private void setLocationDetails() {
        if (newLocationList.size() > 0) {
            if (locationType.hasLayoutDefinition()) {
                inputTa.setText(locationType.getLayoutDefinition());
            } else {
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
            }
        } else {
            inputTa.clearText();
        }
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel detailPanel = new JPanel(new GridBagLayout());
        TitledBorder resultBorder = GuiUtils.createTitleBorder("Result");

        GuiUtils.GridBagHelper gbh = new GuiUtils.GridBagHelper(detailPanel);
        gbh.addLine("Name: ", GuiUtils.createComponentWithActions(nameTf, saveNameAction));
        gbh.addLine("Alias: ", GuiUtils.createComponentWithActions(aliasTf, searchAliasAction, saveAliasAction));

        centerPanel.add(locationMapPanel, BorderLayout.CENTER);
        centerPanel.add(detailPanel, BorderLayout.SOUTH);
        centerPanel.setBorder(resultBorder);

        return centerPanel;
    }

    private JPanel createEastPanel() {
        JPanel eastPanel = new JPanel(new BorderLayout());
        JPanel customPanel = new JPanel(new BorderLayout());
        TitledBorder customBorder = GuiUtils.createTitleBorder("Values");

        customPanel.add(new JScrollPane(inputTa), BorderLayout.CENTER);
        customPanel.add(convertBtn, BorderLayout.SOUTH);
        customPanel.setBorder(customBorder);

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
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setEnabled(false);
        getButtonNeutral().setText("Save");

        // West panel
        locationMapPanel = new ILocationMapPanel(application, this, true);
        locationMapPanel.setPreferredSize(new Dimension(300,300));

        inputTa = new ITextArea();
        convertBtn = new JButton("Convert");
        convertBtn.addActionListener(this);

        // Extra
        nameTf = new ITextField();
        saveNameAction = new IActions.SaveAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.setSource(saveNameAction);
                actionPerformed(e);
            }
        };

        aliasTf = new ITextField();
        saveAliasAction = new IActions.SaveAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.setSource(saveAliasAction);
                actionPerformed(e);
            }
        };
        searchAliasAction = new IActions.SearchAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.setSource(searchAliasAction);
                actionPerformed(e);
            }
        };
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
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null && object[0] instanceof LocationType) {
            locationType = (LocationType) object[0];
            newLocationList = copyLocations(locationType.getLocations());
        } else {
            locationType = null;
            newLocationList = new ArrayList<>();
        }

        locationMapPanel.setLocations(newLocationList);
        setLocationDetails();

        updateEnabledComponents();
    }

    private List<Location> copyLocations(List<Location> locations) {
        ArrayList<Location> copy = new ArrayList<>();

        for (Location location : locations) {
            copy.add(location.createCopy());
        }

        return copy;
    }
}