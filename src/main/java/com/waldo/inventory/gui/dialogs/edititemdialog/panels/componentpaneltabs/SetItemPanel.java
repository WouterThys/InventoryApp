package com.waldo.inventory.gui.dialogs.edititemdialog.panels.componentpaneltabs;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.classes.dbclasses.SetItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.actions.CreateSetItemSeriesAction;
import com.waldo.inventory.gui.components.actions.UpdateSetItemLocationsAction;
import com.waldo.inventory.gui.components.tablemodels.ISetItemTableModel;
import com.waldo.inventory.gui.dialogs.setitemdialog.extra.EditSetItemDialog;
import com.waldo.inventory.gui.dialogs.setitemdialog.extra.EditSetItemLocationDialog;
import com.waldo.inventory.gui.dialogs.setitemdialog.extra.valueparserdialog.ValueParserDialog;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SetItemPanel extends JPanel implements GuiInterface, IdBToolBar.IdbToolBarListener, ListSelectionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ISetItemTableModel tableModel;
    private ITable setItemTable;
    private IdBToolBar toolBar;

    private UpdateSetItemLocationsAction locationsAction;
    private CreateSetItemSeriesAction seriesAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;
    private Item item;
    private SetItem selectedSetItem;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public SetItemPanel(Application application, Item item) {
        this.application = application;
        this.item = item;
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        toolBar.setEnabled(enabled);
        setItemTable.setEnabled(enabled);
        locationsAction.setEnabled(enabled);
        seriesAction.setEnabled(enabled);
    }

    private void updateEnabledComponents() {
        if (selectedSetItem == null) {
            toolBar.setEditActionEnabled(false);
            toolBar.setDeleteActionEnabled(false);
        } else {
            toolBar.setEditActionEnabled(true);
            toolBar.setDeleteActionEnabled(true);
        }
    }

    private void updateTable() {
        java.util.List<SetItem> list = SearchManager.sm().findSetItemsByItemId(item.getId());
        tableModel.setItemList(list);
    }

    java.util.List<SetItem> getSetItems() {
        return tableModel.getItemList();
    }

    private java.util.List<SetItem> getSelectedSetItems() {
        java.util.List<SetItem> setItems = new ArrayList<>();
        int[] selectedRows = setItemTable.getSelectedRows();
        if (selectedRows.length > 0) {
            for (int row : selectedRows) {
                SetItem si = (SetItem) setItemTable.getValueAtRow(row);
                if (si != null) {
                    setItems.add(si);
                }
            }
        }

        return setItems;
    }

    private void deleteSelectedSetItems(final java.util.List<SetItem> itemsToDelete) {
        int result = JOptionPane.CANCEL_OPTION;
        if (itemsToDelete.size() == 1) {
            result = JOptionPane.showConfirmDialog(
                    SetItemPanel.this,
                    "Are you sure you want to delete " + itemsToDelete.get(0) + "?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
        } else if (itemsToDelete.size() > 1) {
            result = JOptionPane.showConfirmDialog(
                    SetItemPanel.this,
                    "Are you sure you want to delete " + itemsToDelete.size() + " items?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
        }
        if (result == JOptionPane.OK_OPTION) {
            // Delete from table
            tableModel.removeItems(itemsToDelete);
            // Delete from db
            for (SetItem item : itemsToDelete) {
                item.delete();
            }
            selectedSetItem = null;
        }
    }

    private void addSetItems(java.util.List<SetItem> itemsToAdd) {
        List<SetItem> tmp = new ArrayList<>(tableModel.getItemList());
        for (int i = itemsToAdd.size()-1; i >= 0; i--) {
            itemsToAdd.get(i).setItemId(item.getId());
            if (tmp.contains(itemsToAdd.get(i))) {
                itemsToAdd.remove(i);
            }
        }
        tableModel.addItems(itemsToAdd);
    }

    private void onEdit() {
        if (selectedSetItem != null) {
            EditSetItemDialog itemDialog = new EditSetItemDialog(application, "Edit " + selectedSetItem.toString(), selectedSetItem);
            itemDialog.showDialog();
            tableModel.updateTable();
            updateEnabledComponents();
        }
    }

    private void computeLocations(boolean leftRight, boolean upDown, boolean overWrite, Location startLocation) {
        LocationType locationType = item.getLocation().getLocationType();

        long typeId = locationType.getId();
        int row = startLocation.getRow();
        int col = startLocation.getCol();

//        int maxRow = locationType.getRows();
//        int maxCol = locationType.getColumns();
//
//        for (SetItem setItem : getSetItems()) {
//            if (setItem.getLocationId() <= DbObject.UNKNOWN_ID || overWrite) {
//                Location loc = SearchManager.sm().findLocation(typeId, row, col);
//                setItem.setLocationId(loc.getId());
//
//                int newRow = row;
//                int newCol = leftRight ? (col + 1) : (col - 1);
//                if (leftRight && newCol >= maxCol) {
//                    newRow = upDown ? (row+1) : (row-1);
//                    newCol = 0;
//                }
//
//                if (newRow >= 0 && newRow < maxRow) {
//                    row = newRow;
//                } else {
//                    return;
//                }
//                if (newCol >= 0 && newCol < maxCol) {
//                    col = newCol;
//                } else {
//                    return;
//                }
//            }
//        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Table
        tableModel = new ISetItemTableModel(new ComparatorUtils.SetItemComparator());
        setItemTable = new ITable<>(tableModel);
        setItemTable.getSelectionModel().addListSelectionListener(this);
        setItemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);
        setItemTable.setExactColumnWidth(0, 36);
        setItemTable.setDefaultRenderer(ILabel.class, new ITableEditors.AmountRenderer());

        // Tool bar
        toolBar = new IdBToolBar(this, IdBToolBar.VERTICAL);

        // Actions
        seriesAction = new CreateSetItemSeriesAction() {
            @Override
            public void onCreateSeries() {
                ValueParserDialog dialog = new ValueParserDialog(application, "Parse values");
                if (dialog.showDialog() == IDialog.OK) {
                    List<SetItem> items = dialog.getSetItems();
                    if (items != null) {
                        addSetItems(items);
                    }
                }
            }
        };
        locationsAction = new UpdateSetItemLocationsAction() {
            @Override
            public void onUpdateLocations() {
                EditSetItemLocationDialog dialog = new EditSetItemLocationDialog(application, "Set locations", item.getLocation().getLocationType());
                if (dialog.showDialog() == IDialog.OK) {
                    computeLocations(
                            dialog.leftToRight(),
                            dialog.upDown(),
                            dialog.overWrite(),
                            dialog.startLocation()
                    );
                    updateTable();
                }
            }
        };
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JScrollPane pane = new JScrollPane(setItemTable);
        pane.setPreferredSize(new Dimension(400, 200));

        toolBar.addSeparateAction(locationsAction);
        toolBar.addAction(seriesAction);

        add(pane, BorderLayout.CENTER);
        add(toolBar, BorderLayout.EAST);

        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length != 0 && args[0] != null) {
            if (args[0] instanceof Item) {
                item = (Item) args[0];

                updateTable();
                updateEnabledComponents();
            }
        } else {
            item = null;
        }
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        updateTable();
        updateEnabledComponents();
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        SetItem setItem = new SetItem();
        setItem.setLocationId(item.getLocationId());
        setItem.setItemId(item.getId());
        EditSetItemDialog itemDialog = new EditSetItemDialog(application, "Add set item", setItem);
        if (itemDialog.showDialog() == IDialog.OK) {
            SetItem si = itemDialog.getSetItem();

            List<SetItem> setItems = new ArrayList<>();
            setItems.add(si);
            addSetItems(setItems);
            updateEnabledComponents();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        deleteSelectedSetItems(getSelectedSetItems());
        updateEnabledComponents();
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        onEdit();
    }

    //
    // List selection
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = setItemTable.getSelectedRow();
            if (row >= 0) {
                selectedSetItem = (SetItem) setItemTable.getValueAtRow(row);
                updateEnabledComponents();
            }
        }
    }
}
