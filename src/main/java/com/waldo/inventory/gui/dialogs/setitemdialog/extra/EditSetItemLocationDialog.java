package com.waldo.inventory.gui.dialogs.setitemdialog.extra;

import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditSetItemLocationDialog extends IDialog implements ActionListener {

    private ITextField startLocationTv;
    private JButton startLocationBtn;

    private JCheckBox leftRightCb;
    private JCheckBox upDownCb;
    private JCheckBox overWriteCb;

    private Location startLocation;
    private LocationType locationType;

    public EditSetItemLocationDialog(Application application, String title, LocationType locationType) {
        super(application, title);

        this.locationType = locationType;
        this.startLocation = SearchManager.sm().findLocation(locationType.getId(), 0,0);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public Location startLocation() {
       return startLocation;
    }

    public boolean leftToRight() {
        return leftRightCb.isSelected();
    }

    public boolean upDown() {
        return upDownCb.isSelected();
    }

    public boolean overWrite() {
        return overWriteCb.isSelected();
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
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        panel.add(new ILabel("Start location: "));
        panel.add(startLocationTv);
        panel.add(startLocationBtn);

        getContentPanel().add(panel);
        getContentPanel().add(leftRightCb);
        getContentPanel().add(upDownCb);
        getContentPanel().add(overWriteCb);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        startLocationTv.setText(startLocation.toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        EditItemLocation dialog = new EditItemLocation(application, "Locations", locationType, 0,0);
//        if (dialog.showDialog() == IDialog.OK) {
//            startLocation = SearchManager.sm().findLocation(
//                    locationType.getId(),
//                    dialog.getRow(),
//                    dialog.getCol()
//            );
//            startLocationTv.setText(startLocation.toString());
//        }
    }
}