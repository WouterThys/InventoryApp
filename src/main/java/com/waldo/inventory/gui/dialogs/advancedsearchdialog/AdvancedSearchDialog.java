package com.waldo.inventory.gui.dialogs.advancedsearchdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.classes.search.ItemFinder;
import com.waldo.inventory.classes.search.ObjectMatch;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchDialog extends AdvancedSearchDialogLayout {

    public AdvancedSearchDialog(Window parent, boolean allowMultiSelect) {
        super(parent, allowMultiSelect);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public Item getSelectedItem() {
        return tableGetSelected();
    }

    public List<Item> getAllSelectedItems() {
        return tableGetAllSelected();
    }

    @Override
    void onMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            DbObject object = getSelectedItem();
            if (object != null) {
                Item item = (Item) object;
                EditItemDialog editItemDialog = new EditItemDialog<>(this, "Edit item", item);
                editItemDialog.showDialog();
                tableUpdate();
            }
        }
    }

    @Override
    List<ObjectMatch<Item>> onSearch(String searchWord) {
        List<ObjectMatch<Item>> result = new ArrayList<>();
        if (searchWord != null && !searchWord.isEmpty()) {
            tableClear();
            beginWait();
            try {
                // TODO: don't do this every time..
                ItemFinder.divisionFilter.setFilters(divisionFilterPanel.getSelected());
                ItemFinder.manufacturerFilter.setFilters(manufacturerFilterPanel.getSelected());
                ItemFinder.locationFilter.setFilters(locationFilterPanel.getSelected());

                searchWord = searchWord.toUpperCase();
                result.addAll(ItemFinder.searchByKeyWord(searchWord));
            } finally {
                endWait();
            }
        }
        return result;
    }

    @Override
    List<ObjectMatch<Item>> onSearch(DbObject dbObject) {
        List<ObjectMatch<Item>> result = new ArrayList<>();
        if (dbObject != null) {
            if (dbObject instanceof PcbItemProjectLink) {

                tableClear();
                beginWait();
                try {
                    // TODO: don't do this every time..
                    ItemFinder.divisionFilter.setFilters(divisionFilterPanel.getSelected());
                    ItemFinder.manufacturerFilter.setFilters(manufacturerFilterPanel.getSelected());
                    ItemFinder.locationFilter.setFilters(locationFilterPanel.getSelected());

                    result.addAll(ItemFinder.searchByPcbItem((PcbItemProjectLink) dbObject, ItemFinder.divisionFilter.hasFilter()));
                } finally {
                    endWait();
                }
            }
        }
        return result;
    }

    //
    // Table item selected
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        updateEnabledComponents();
    }

    //
    // Search
    //
    @Override
    public void onObjectsFound(List<Item> foundObjects) {
        addResults(itemSearchPnl.getResult());
    }

    @Override
    public void onNextObjectSelected(Item next) {

    }

    @Override
    public void onPreviousObjectSelected(Item previous) {

    }

    @Override
    public void onSearchCleared() {
        tableClear();
    }
}
