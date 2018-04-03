package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.tablemodels.IWizardSetItemsTableModel;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

class WizardParsePanel extends JPanel implements
        GuiUtils.GuiInterface,
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
    private IActions.ReplaceAction replaceAction;
    private IActions.DoItAction importSeriesAction;

    private ILabel numberOfItemsLbl;
    private ILabel numberOfLocationsLbl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Window parent;
    private Item selectedItem;
    private WizardSettings wizardSettings;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    WizardParsePanel(com.waldo.inventory.gui.components.IDialog parent) {
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
            AdvancedSearchDialog dialog = new AdvancedSearchDialog(parent, true);
            if (dialog.showDialog() == IDialog.OK) {
                Item newItem = dialog.getSelectedItem();

                if (newItem != null) {
                    tableModel.replaceItem(selectedItem, newItem);
                    this.selectedItem = newItem;
                }
            }
        }
    }

    private void renameItems() {
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

    private void saveAllSetItems() {
        if (wizardSettings != null) {
            Application.beginWait(this);
            try {
                for (Item item : tableModel.getItemList()) {
                    item.save();
                }
            } finally {
                Application.endWait(this);
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
            List<Item> setItems = new ArrayList<>(wizardSettings.getItems());
            tableModel.setItemList(setItems);
        }
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (wizardSettings != null) {
            EditItemDialog dialog = new EditItemDialog<>(parent, "Add set item", new Item());
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
            EditItemDialog dialog = new EditItemDialog<>(parent, "Edit set item", selectedItem);
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
        setItemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (selectedItem != null) {
                        EditItemDialog dialog = new EditItemDialog<>(parent, "Edit set item", selectedItem);
                        dialog.setAllowSave(false);
                        dialog.showDialog();
                        tableModel.updateTable();
                    }
                }
            }
        });

        numberOfItemsLbl = new ILabel();
        numberOfLocationsLbl = new ILabel();

        replaceAction = new IActions.ReplaceAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                replace(selectedItem);
            }
        };
        IActions.RenameAction renameAction = new IActions.RenameAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameItems();
            }
        };
        importSeriesAction = new IActions.DoItAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            tableModel.setItemList(new ArrayList<>(wizardSettings.getItems()));

            numberOfItemsLbl.setText(String.valueOf(wizardSettings.getNumberOfItems()));
            numberOfLocationsLbl.setText(String.valueOf(wizardSettings.getNumberOfLocations()));
        }
        updateEnabledComponents();
    }
}
