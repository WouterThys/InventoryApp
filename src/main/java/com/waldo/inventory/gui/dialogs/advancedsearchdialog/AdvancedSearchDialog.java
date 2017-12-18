package com.waldo.inventory.gui.dialogs.advancedsearchdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.SetItem;
import com.waldo.inventory.classes.search.Search;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.event.ListSelectionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class AdvancedSearchDialog extends AdvancedSearchDialogLayout {

    private Search.DbObjectSearch2<Item, SetItem> searcher;

    private Search.SearchListener<Item> itemSearchListener;
    private Search.SearchListener<SetItem> setItemSearchListener;

    public AdvancedSearchDialog(Application application, String title, SearchType searchType, Object... args) {
        super(application, title, searchType, args);

        initializeComponents();
        initializeLayouts();

        initializeListeners();

        searcher = new Search.DbObjectSearch2<>(
                cache().getItems(), itemSearchListener,
                cache().getSetItems(), setItemSearchListener);

        updateComponents();
    }

    public Item getSelectedItem() {
        return null;
        //return tableGetSelected();
    }

    @Override
    void onMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            EditItemDialog editItemDialog = new EditItemDialog(application, "Edit item", getSelectedItem());
            editItemDialog.showDialog();
            tableUpdate();
        }
    }

    @Override
    void onSearch(String searchWord) {
        if (searchWord != null && !searchWord.isEmpty()) {
            searcher.search(searchWord);
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
                if (size == 0) {
                    setInfo("No items found..");
                } else if (size == 1) {
                    setInfo("1 item found!");
                    addResults(new ArrayList<>(foundObjects));
                } else  {
                    setInfo(String.valueOf(size) + " results found!!");
                    addResults(new ArrayList<>(foundObjects));
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

        setItemSearchListener = new Search.SearchListener<SetItem>() {
            @Override
            public void onObjectsFound(List<SetItem> foundObjects) {
                int size = foundObjects.size();
                if (size == 0) {
                    setInfo("No items found..");
                } else if (size == 1) {
                    setInfo("1 item found!");
                    addResults(new ArrayList<>(foundObjects));
                } else  {
                    setInfo(String.valueOf(size) + " results found!!");
                    addResults(new ArrayList<>(foundObjects));
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
            public void onNextSearchObject(SetItem next) {
                tableSelect(next);
            }

            @Override
            public void onPreviousSearchObject(SetItem previous) {
                tableSelect(previous);
            }
        };
    }
}
