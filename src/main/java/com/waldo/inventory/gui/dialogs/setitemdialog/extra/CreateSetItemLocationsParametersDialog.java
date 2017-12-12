package com.waldo.inventory.gui.dialogs.setitemdialog.extra;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.dialogs.edititemlocationdialog.EditItemLocation;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateSetItemLocationsParametersDialog extends IDialog implements ActionListener {

    private ITextField startLocationTv;
    private JButton startLocationBtn;

    private JCheckBox leftRightCb;
    private JCheckBox upDownCb;
    private JCheckBox overWriteCb;

    private JSpinner numberPerLocationSp;

    private LocationType locationType;
    private Location startLocation;

    public CreateSetItemLocationsParametersDialog(Application application, String title, LocationType locationType) {
        super(application, title);

        this.locationType = locationType;
        this.startLocation = SearchManager.sm().findLocation(locationType.getId(), 0,0);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public Location getStartLocation() {
       return startLocation;
    }

    public boolean getLeftToRight() {
        return leftRightCb.isSelected();
    }

    public boolean getUpDown() {
        return upDownCb.isSelected();
    }

    public boolean getOverWrite() {
        return overWriteCb.isSelected();
    }

    public int getNumberPerLocation() {
        return ((SpinnerNumberModel) numberPerLocationSp.getModel()).getNumber().intValue();
    }


    @Override
    public void initializeComponents() {
        showTitlePanel(false);

        startLocationTv = new ITextField();
        startLocationTv.setEnabled(false);
        startLocationBtn = new JButton("Set");
        startLocationBtn.addActionListener(this);

        leftRightCb = new JCheckBox("Left -> Right", true);
        upDownCb = new JCheckBox("Up -> Down", true);
        overWriteCb = new JCheckBox("Over-write known locations", true);

        SpinnerNumberModel numberModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        numberPerLocationSp = new JSpinner(numberModel);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new ILabel("Start location: "));
        topPanel.add(startLocationTv);
        topPanel.add(startLocationBtn);

        JPanel leftPanel = new JPanel();
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,3));
        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(leftPanel);
        gbc.addLine("", leftRightCb);
        gbc.addLine("", upDownCb);
        gbc.addLine("", overWriteCb);

        JPanel bottomPanel = new JPanel();
        gbc = new PanelUtils.GridBagHelper(bottomPanel);
        gbc.addLineVertical("# set items per location", numberPerLocationSp);

        JPanel centerPanel = new JPanel();
        centerPanel.add(leftPanel);

        getContentPanel().add(topPanel, BorderLayout.PAGE_START);
        getContentPanel().add(centerPanel, BorderLayout.CENTER);
        getContentPanel().add(bottomPanel, BorderLayout.PAGE_END);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        startLocationTv.setText(startLocation.toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EditItemLocation dialog = new EditItemLocation(application, "Locations", startLocation);
        if (dialog.showDialog() == IDialog.OK) {
            startLocation = SearchManager.sm().findLocation(
                    locationType.getId(), startLocation.getRow(), startLocation.getCol()
            );
            startLocationTv.setText(startLocation.toString());
        }
    }
}