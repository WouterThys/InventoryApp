package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.DoItAction;
import com.waldo.inventory.gui.components.actions.ReplaceAction;
import com.waldo.inventory.gui.components.tablemodels.IWizardSetItemsTableModel;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialogLayout;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class WizardParsePanel extends JPanel implements
        GuiInterface,
        IdBToolBar.IdbToolBarListener,
        ListSelectionListener,
        CacheChangedListener<Item> {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IWizardSetItemsTableModel tableModel;
    private ITable<Item> setItemTable;

    // Item actions
    private IdBToolBar toolBar;
    private ReplaceAction replaceAction;
    private DoItAction importSeriesAction;

    private ILabel numberOfItemsLbl;
    private ILabel numberOfLocationsLbl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private final IDialog parent;
    private Item selectedItem;
    private WizardSettings wizardSettings;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    WizardParsePanel(Application application, IDialog parent) {
        this.application = application;
        this.parent = parent;

        parent.addCacheListener(Item.class, this);

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void replace(Item selectedItem) {
        if (selectedItem != null) {
            AdvancedSearchDialog dialog = new AdvancedSearchDialog(application, "Search", AdvancedSearchDialogLayout.SearchType.SearchWord);
            if(dialog.showDialog() == IDialog.OK) {
                Item newItem = (Item) dialog.getSelectedItem();

                if (newItem != null) {
                    tableModel.replaceItem(selectedItem, newItem);
                    this.selectedItem = newItem;
                }
            }
        }
    }

    public void saveAllSetItems() {
        if (wizardSettings != null) {
            parent.beginWait();
            try {
                for (Item item : tableModel.getItemList()) {
                    item.save();
                }
            } finally {
                parent.endWait();
            }
        }
    }


    private void updateEnabledComponents() {
        boolean enabled = selectedItem != null;

        toolBar.setEditActionEnabled(enabled);
        toolBar.setDeleteActionEnabled(enabled);
        replaceAction.setEnabled(enabled);
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
    // Cache
    //
    @Override
    public void onInserted(Item item) {
        wizardSettings.getSelectedSet().addSetItem(item);
    }

    @Override
    public void onUpdated(Item item) {
        wizardSettings.getSelectedSet().addSetItem(item);
    }

    @Override
    public void onDeleted(Item item) {
        wizardSettings.getSelectedSet().removeSetItem(item);
    }

    @Override
    public void onCacheCleared() {

    }

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
        if (wizardSettings != null) {
            List<Item> setItems = createItemsFromSettings(wizardSettings);
            tableModel.setItemList(setItems);
        }
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (wizardSettings != null) {
            EditItemDialog dialog = new EditItemDialog<>(application, "Add set item", new Item());
            dialog.setValuesForSet(wizardSettings.getSelectedSet());
            dialog.setAllowSave(false);
            if (dialog.showDialog() == IDialog.OK) {
                tableModel.addItem(dialog.getItem());
            }
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedItem != null) {
            tableModel.removeItem(selectedItem);
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedItem != null) {
            EditItemDialog dialog = new EditItemDialog<>(application, "Edit set item", selectedItem);
            dialog.setAllowSave(false);
            dialog.showDialog();
            tableModel.updateTable();
        }
    }

    //
    // Gui
    //
    @Override
    public void initializeComponents() {
        tableModel = new IWizardSetItemsTableModel();
        setItemTable = new ITable<>(tableModel);
        setItemTable.getSelectionModel().addListSelectionListener(this);

        numberOfItemsLbl = new ILabel();
        numberOfLocationsLbl = new ILabel();

        replaceAction = new ReplaceAction() {
            @Override
            public void onReplace() {
                replace(selectedItem);
            }
        };
        importSeriesAction = new DoItAction() {
            @Override
            public void onDoIt() {
                saveAllSetItems();
            }
        };

        toolBar = new IdBToolBar(this);
        toolBar.addAction(replaceAction);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel infoPnl = new JPanel(new BorderLayout());
        JPanel lblPnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(lblPnl);
        gbc.addLine("# set items: ", numberOfItemsLbl);
        gbc.addLine("# locations: ", numberOfLocationsLbl);

        JToolBar extraTb = GuiUtils.createNewToolbar(importSeriesAction);

        infoPnl.add(lblPnl, BorderLayout.CENTER);
        infoPnl.add(extraTb, BorderLayout.EAST);

        JPanel tablePnl = new JPanel(new BorderLayout());
        JPanel tbPnl = new JPanel(new BorderLayout());

        JScrollPane pane = new JScrollPane(setItemTable);
        pane.setPreferredSize(new Dimension(400, 200));

        tbPnl.add(toolBar, BorderLayout.WEST);
        tablePnl.add(tbPnl, BorderLayout.PAGE_START);
        tablePnl.add(pane, BorderLayout.CENTER);

        infoPnl.setBorder(BorderFactory.createCompoundBorder(
                GuiUtils.createTitleBorder("Result"),
                BorderFactory.createEmptyBorder(2,50,2,2)
        ));

        tablePnl.setBorder(BorderFactory.createCompoundBorder(
                GuiUtils.createTitleBorder("Set items"),
                BorderFactory.createEmptyBorder(2,5,10,5)
        ));

        add(infoPnl, BorderLayout.NORTH);
        add(tablePnl, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            wizardSettings = (WizardSettings) args[0];
            List<Item> setItems = createItemsFromSettings(wizardSettings);
            tableModel.setItemList(setItems);

            numberOfItemsLbl.setText(String.valueOf(wizardSettings.getNumberOfItems()));
            numberOfLocationsLbl.setText(String.valueOf(wizardSettings.getNumberOfLocations()));
        } else {

        }
        updateEnabledComponents();
    }
}
