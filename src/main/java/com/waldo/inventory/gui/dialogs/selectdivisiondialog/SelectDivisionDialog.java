package com.waldo.inventory.gui.dialogs.selectdivisiondialog;

import com.waldo.inventory.classes.dbclasses.Division;

import java.awt.*;

public class SelectDivisionDialog extends SelectDivisionDialogLayout {


    public SelectDivisionDialog(Window window, Division selectDivision) {
        super(window);

        initializeComponents();
        initializeLayouts();
        updateComponents(selectDivision);

    }

    public Division getSelectedDivision() {
        return divisionTree.getSelectedDivision();
    }

}