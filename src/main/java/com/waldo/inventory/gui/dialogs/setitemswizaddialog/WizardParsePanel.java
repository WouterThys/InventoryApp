package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.tablemodels.IWizardSetItemsTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class WizardParsePanel extends JPanel implements GuiInterface, IdBToolBar.IdbToolBarListener, ListSelectionListener {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IWizardSetItemsTableModel tableModel;
    private ITable<Item> setItemTable;

    // Item actions
    private IdBToolBar toolBar;
    // Replace action

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private Item selectedItem;

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
    private void updateEnabledComponents() {
        boolean enabled = selectedItem != null;

        toolBar.setEditActionEnabled(enabled);
        toolBar.setDeleteActionEnabled(enabled);
    }

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
        setItems.sort(new ComparatorUtils.ItemValueComparator());
        return setItems;
    }
    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    //
    // Table selected
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            selectedItem = setItemTable.getSelectedItem();
            updateEnabledComponents();
        }
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        List<Item> setItems = createItemsFromSettings(settings);
        tableModel.setItemList(setItems);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {

    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedItem != null) {

        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {

    }

    //
    // Gui
    //
    @Override
    public void initializeComponents() {
        tableModel = new IWizardSetItemsTableModel();
        setItemTable = new ITable<>(tableModel);
        setItemTable.getSelectionModel().addListSelectionListener(this);

        toolBar = new IdBToolBar(this);
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
