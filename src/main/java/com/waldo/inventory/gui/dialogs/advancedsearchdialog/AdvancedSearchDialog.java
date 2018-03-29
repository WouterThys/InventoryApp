package com.waldo.inventory.gui.dialogs.advancedsearchdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseEvent;

public class AdvancedSearchDialog extends AdvancedSearchDialogLayout {

    // TODO #1 private Search.DbObjectSearch<Item> searcher;

    // TODO #1 private Search.SearchListener<Item> itemSearchListener;

    public AdvancedSearchDialog(Window parent, String title, SearchType searchType, Object... args) {
        super(parent, title, searchType, args);

        initializeComponents();
        initializeLayouts();

        initializeListeners();

        // TODO #1 searcher = new Search.DbObjectSearch<>(
        // TODO #1         cache().getItemList(), itemSearchListener);

        updateComponents();
    }

    public Item getSelectedItem() {
        return tableGetSelected();
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
    void onSearch(String searchWord) {
        if (searchWord != null && !searchWord.isEmpty()) {
            tableClear();
            beginWait();
            try {
                // TODO #1 searcher.search(searchWord);
            } finally {
                endWait();
            }

        } else {
            setError("Enter a search word");
            tableClear();
        }
    }

    @Override
    void onSearch(DbObject dbObject) {
        if (dbObject != null) {
            // TODO #1 searcher.search(dbObject);
        } else {
            setError("Invalid object");
            tableClear();
        }
    }

    @Override
    void onNext() {
        // TODO #1
//        if (searcher.hasSearchResults()) {
//            searcher.findNextObject();
//        }
    }

    @Override
    void onPrevious() {
        // TODO #1
//        if (searcher.hasSearchResults()) {
//            searcher.findPreviousObject();
//        }
    }

    //
    // Dialog
    //

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
    private void initializeListeners() {
        // TODO #1
//        itemSearchListener = new Search.SearchListener<Item>() {
//            @Override
//            public void onObjectsFound(List<Item> foundObjects) {
//                int size = foundObjects.size();
//                if (size > 0) {
//                    setInfo(String.valueOf(size) + " results found!!");
//                    addResults(new ArrayList<>(foundObjects));
//                    tableSelect(0);
//                }
//                updateEnabledComponents();
//            }
//
//            @Override
//            public void onSearchCleared() {
//                tableClear();
//                clearResultText();
//                updateEnabledComponents();
//            }
//
//            @Override
//            public void onNextSearchObject(Item next) {
//                tableSelect(next);
//            }
//
//            @Override
//            public void onPreviousSearchObject(Item previous) {
//                tableSelect(previous);
//            }
//        };
    }
}
