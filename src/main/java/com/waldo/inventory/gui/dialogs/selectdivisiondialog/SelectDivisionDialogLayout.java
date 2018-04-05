package com.waldo.inventory.gui.dialogs.selectdivisiondialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.gui.components.trees.IDivisionTree;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

abstract class SelectDivisionDialogLayout extends IDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    IDivisionTree divisionTree;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SelectDivisionDialogLayout(Window window) {
        super(window, "Select division");
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        getButtonOK().setEnabled(false);

        // Divisions
        List<Division> rootDivisions = SearchManager.sm().findDivisionsWithoutParent();
        rootDivisions.sort(new ComparatorUtils.DbObjectNameComparator<>());
        Division rootDivision = Division.createDummyDivision("Dummy", rootDivisions);

        divisionTree = new IDivisionTree(rootDivision, false);
        divisionTree.getSelectionModel().addTreeSelectionListener(e -> getButtonOK().setEnabled((e.getPath() != null)));
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().add(new JScrollPane(divisionTree));
        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            Division division = (Division)args[0];
            if (division.canBeSaved()) {
                divisionTree.setSelectedItem(division);
            }
        }
    }
}