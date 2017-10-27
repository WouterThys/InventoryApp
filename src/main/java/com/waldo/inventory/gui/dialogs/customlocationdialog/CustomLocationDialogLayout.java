package com.waldo.inventory.gui.dialogs.customlocationdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.Location;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class CustomLocationDialogLayout extends IDialog implements
        ILocationMapPanel.LocationClickListener,
        ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ILocationMapPanel locationMapPanel;

    ITextArea inputTa;
    JButton convertBtn;

    // Extra
    ITextFieldButtonPanel namePanel;
    ITextFieldButtonPanel aliasPanel;


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
        namePanel.setEnabled(enabled);
        namePanel.setEnabled(enabled);
    }

    void setButtonDetails(Location location) {
        if (location != null) {
            namePanel.setText(location.getName());
            aliasPanel.setText(location.getAlias());
        } else {
            namePanel.clearText();
            aliasPanel.clearText();
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
        TitledBorder resultBorder = PanelUtils.createTitleBorder("Result");

        PanelUtils.GridBagHelper gbh = new PanelUtils.GridBagHelper(detailPanel);
        gbh.addLine("Name: ", namePanel);
        gbh.addLine("Alias: ", aliasPanel);

        centerPanel.add(locationMapPanel, BorderLayout.CENTER);
        centerPanel.add(detailPanel, BorderLayout.SOUTH);
        centerPanel.setBorder(resultBorder);

        return centerPanel;
    }

    private JPanel createEastPanel() {
        JPanel eastPanel = new JPanel(new BorderLayout());
        JPanel customPanel = new JPanel(new BorderLayout());
        TitledBorder customBorder = PanelUtils.createTitleBorder("Values");

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
        namePanel = new ITextFieldButtonPanel("Name", imageResource.readImage("Locations.Edit", 16), this);
        aliasPanel = new ITextFieldButtonPanel("Alias",imageResource.readImage("Locations.Edit", 16), this );

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