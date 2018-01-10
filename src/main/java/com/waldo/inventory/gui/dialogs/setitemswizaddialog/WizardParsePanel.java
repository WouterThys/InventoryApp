package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.waldo.inventory.classes.Value;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.IWizardSetItemsTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class WizardParsePanel extends JPanel implements GuiInterface {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IWizardSetItemsTableModel tableModel;
    private ITable<Item> setItemTable;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    WizardParsePanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<Item> createItemsFromSettings(WizardSettings settings) {
        List<Item> setItems = new ArrayList<>();
        if (settings != null) {
            int nameCnt = 1;
            Item item;
            for (Value value : settings.getValues()) {
                String name = settings.getSelectedSet().getName() + " - " + settings.getTypeName() + String.valueOf(nameCnt);
                item = new Item(
                        name,
                        value,
                        settings.getManufacturer(),
                        settings.getPackageType(),
                        settings.getPins(),
                        settings.getAmount(),
                        settings.getLocation(value),
                        settings.getSelectedSet());

                setItems.add(item);

                nameCnt++;
            }

        }
        return setItems;
    }
    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        tableModel = new IWizardSetItemsTableModel();
        setItemTable = new ITable<>(tableModel);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JScrollPane pane = new JScrollPane(setItemTable);
        pane.setPreferredSize(new Dimension(400, 200));

        add(pane);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            WizardSettings settings = (WizardSettings) args[0];
            List<Item> setItems = createItemsFromSettings(settings);
            tableModel.setItemList(setItems);
        } else {

        }
    }
}
