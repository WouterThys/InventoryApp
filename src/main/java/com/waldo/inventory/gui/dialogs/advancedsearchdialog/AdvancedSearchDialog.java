package com.waldo.inventory.gui.dialogs.advancedsearchdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.search.Search;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class AdvancedSearchDialog extends AdvancedSearchDialogLayout {

    private Search.DbObjectSearch<Item> searcher;

    private Search.SearchListener<Item> itemSearchListener;

    public AdvancedSearchDialog(Window parent, String title, SearchType searchType, Object... args) {
        super(parent, title, searchType, args);

        initializeComponents();
        initializeLayouts();

        initializeListeners();

        searcher = new Search.DbObjectSearch<>(
                cache().getItems(), itemSearchListener);

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
                searcher.search(searchWord);
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
            searcher.search(dbObject);
        } else {
            setError("Invalid object");
            tableClear();
        }
    }

    @Override
    void onNext() {
        if (searcher.hasSearchResults()) {
            searcher.findNextObject();
        }
    }

    @Override
    void onPrevious() {
        if (searcher.hasSearchResults()) {
            searcher.findPreviousObject();
        }
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
        itemSearchListener = new Search.SearchListener<Item>() {
            @Override
            public void onObjectsFound(List<Item> foundObjects) {
                int size = foundObjects.size();
                if (size > 0) {
                    setInfo(String.valueOf(size) + " results found!!");
                    addResults(new ArrayList<>(foundObjects));
                    tableSelect(0);
                }
                updateEnabledComponents();
            }

            @Override
            public void onSearchCleared() {
                tableClear();
                clearResultText();
                updateEnabledComponents();
            }

            @Override
            public void onNextSearchObject(Item next) {
                tableSelect(next);
            }

            @Override
            public void onPreviousSearchObject(Item previous) {
                tableSelect(previous);
            }
        };
    }
}
