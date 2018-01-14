package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.DoItAction;
import com.waldo.inventory.gui.components.actions.RenameAction;
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
    private RenameAction renameAction;
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
            if (dialog.showDialog() == IDialog.OK) {
                Item newItem = dialog.getSelectedItem();

                if (newItem != null) {
                    tableModel.replaceItem(selectedItem, newItem);
                    this.selectedItem = newItem;
                }
            }
        }
    }

    public void renameItems() {
        List<Item> itemsList = tableModel.getItemList();
        if (itemsList.size() > 0) {
            int res = JOptionPane.showConfirmDialog(
                    this,
                    "Also rename old items?",
                    "Rename",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            int count = 1;
            for (Item item : itemsList) {
                if (item.getId() <= DbObject.UNKNOWN_ID || res == JOptionPane.YES_OPTION) {
                    item.setName(createItemName(wizardSettings, count));
                    count++;
                }
            }
            tableModel.updateTable();
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

    private void updateInfo() {
        numberOfItemsLbl.setText(String.valueOf(tableModel.getItemList().size()));
        //numberOfLocationsLbl.setText();
    }

    private Item findByValue(List<Item> itemList, Value value) {
        if (itemList != null && value != null) {
            for (Item item : itemList) {
                if (item.getValue().equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    private List<Item> createItemsFromSettings(WizardSettings settings) {
        List<Item> newSetItems = new ArrayList<>();
        if (settings != null) {

            if (settings.isKeepOldSetItems()) {
                newSetItems.addAll(settings.getSelectedSet().getSetItems());
            }

            int nameCnt = 0;
            for (Value value : settings.getValues()) {
                Item item;
                nameCnt++;
                if (settings.isKeepOldSetItems() && settings.isReplaceValues()) {
                    item = findByValue(newSetItems, value);
                    if (item != null) {
                        if (settings.isOverWriteLocations()) {
                            item.setLocationId(settings.getLocation(value).getId());
                        }
                        continue;
                    }
                }
                String name = createItemName(settings, nameCnt);
                item = new Item(
                        name,
                        settings.getTypeName(),
                        value,
                        settings.getManufacturer(),
                        settings.getPackageType(),
                        settings.getPins(),
                        settings.getAmount(),
                        settings.getLocation(value),
                        settings.getSelectedSet());


                newSetItems.add(item);
            }
        }
        newSetItems.sort(new ComparatorUtils.ItemValueComparator());
        return newSetItems;
    }

    private String createItemName(WizardSettings settings, int count) {
        return settings.getSelectedSet().getName() + " - " + settings.getTypeName() + String.valueOf(count);
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
            updateInfo();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        List<Item> selectedItems = new ArrayList<>(setItemTable.getSelectedItems());
        if (selectedItems.size() > 0) {
            for (Item item : selectedItems) {
                tableModel.removeItem(item);
            }
            updateInfo();
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
        setItemTable.setRowSorter(null);

        numberOfItemsLbl = new ILabel();
        numberOfLocationsLbl = new ILabel();

        replaceAction = new ReplaceAction() {
            @Override
            public void onReplace() {
                replace(selectedItem);
            }
        };
        renameAction = new RenameAction() {
            @Override
            public void onRename() {
                renameItems();
            }
        };
        importSeriesAction = new DoItAction() {
            @Override
            public void onDoIt() {
                saveAllSetItems();
            }
        };

        toolBar = new IdBToolBar(this);
        toolBar.addSeparateAction(renameAction);
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
                BorderFactory.createEmptyBorder(2, 50, 2, 2)
        ));

        tablePnl.setBorder(BorderFactory.createCompoundBorder(
                GuiUtils.createTitleBorder("Set items"),
                BorderFactory.createEmptyBorder(2, 5, 10, 5)
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
        }
        updateEnabledComponents();
    }
}
