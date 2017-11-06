package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.tablemodels.IAbstractTableModel;

import javax.swing.*;
import java.awt.*;

public class ITablePanel<T extends DbObject> extends JPanel implements GuiInterface {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IAbstractTableModel<T> tableModel;
    private ITable<T> table;

    private ITableToolBar tableToolBar;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ITablePanel(IAbstractTableModel<T> tableModel) {
        super(new BorderLayout());

        this.tableModel = tableModel;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {

    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object... args) {

    }
}