package com.waldo.inventory.gui.dialogs.customlocationdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ILocationButton;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class CustomLocationDialog extends CustomLocationDialogLayout {


    public CustomLocationDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(null);

    }

    private List<ILocationButton> convertInput(String input) {
        List<ILocationButton> buttonList = new ArrayList<>();

        if (input != null && !input.isEmpty()) {
            String rows[] = input.split("\\r?\\n");

            int r = 0;
            int c = 0;

            for (String row : rows) {
                String cols[] = row.split(",");
                for (String col : cols) {
                    ILocationButton btn = new ILocationButton(r, c);
                    buttonList.add(btn);
                    c++;
                }
                c = 0;
                r++;
            }
        }

        return buttonList;
    }

    //
    // Location map button click
    //
    @Override
    public void onClick(ActionEvent e, List<DbObject> items, int row, int column) {

    }

    //
    // Convert btn click
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        String input = inputTa.getText();

        locationButtonList = convertInput(input);

        locationMapPanel.drawButtons(locationButtonList);
    }
}