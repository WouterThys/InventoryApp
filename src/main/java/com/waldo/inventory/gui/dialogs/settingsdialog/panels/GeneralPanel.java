package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.settings.settingsclasses.GeneralSettings;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ICheckBox;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;

public class GeneralPanel extends JPanel implements GuiInterface {    
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IdBToolBar toolBar;
    private ILabel currentSettingLbl;
    private DefaultComboBoxModel<GeneralSettings> generalSettingsCbModel;
    private JComboBox<GeneralSettings> generalSettingsCb;

    private IComboBox<Statics.GuiDetailsView> detailsViewCb;
    private IComboBox<UIManager.LookAndFeelInfo> lookAndFeelCb;
    private ICheckBox fullScreenCb;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public GeneralPanel() {

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
