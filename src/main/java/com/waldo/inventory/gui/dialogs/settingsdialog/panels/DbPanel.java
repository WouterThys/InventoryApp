package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;

import javax.swing.*;

public class DbPanel extends JPanel implements GuiInterface {    
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public DbPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
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
    public void updateComponents(Object object) {

    }
}