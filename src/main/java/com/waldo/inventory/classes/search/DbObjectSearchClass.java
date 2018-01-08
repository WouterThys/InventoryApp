package com.waldo.inventory.classes.search;

import com.waldo.inventory.classes.dbclasses.DbObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class DbObjectSearchClass<T0 extends DbObject> {

    private boolean searched = false;
    private boolean inAdvanced = false;
    private boolean hasAdvancedSearchOption;
    private int[] searchOptions;
    private List<T0> searchList;
    private List<T0> resultList = new ArrayList<>();
    private int currentResultNdx = 0;

    private Search.SearchListener<T0> searchListener;

    DbObjectSearchClass(List<T0> searchList) {
        this(searchList, null);
    }

    DbObjectSearchClass(List<T0> searchList, Search.SearchListener<T0> searchListener) {
        this.searchList = searchList;
        this.searchListener = searchListener;
    }

    public void search(final String searchWord) {
        search(searchWord, true);
    }

    public void search(final String searchWord, boolean async) {
        resultList.clear();

        if (async) {
            SwingUtilities.invokeLater(() -> {
                doSearch(searchWord);
            });
        } else {
            doSearch(searchWord);
        }
    }

    private void doSearch(String searchWord) {
        // Search list
        if (searchOptions == null || searchOptions.length == 0) {
            resultList = searchForObject(searchList, searchWord);
        } else {
            resultList = searchForObject(searchList /*, searchOptions*/, searchWord);
        }

        currentResultNdx = 0;

        if (searchListener != null) {
            searchListener.onObjectsFound(resultList);
        }
    }

    public void search(DbObject dbObject) {
        search(dbObject, true);
    }

    public void search(DbObject dbObject, boolean async) {
        resultList.clear();

        if (async) {
            SwingUtilities.invokeLater(() -> doSearch(dbObject));
        } else {
            doSearch(dbObject);
        }
    }

    private void doSearch(DbObject dbObject) {
        resultList = searchForObject(searchList, dbObject);
        currentResultNdx = 0;
        if (searchListener != null)  {
            searchListener.onObjectsFound(resultList);
        }

    }

    public void clearSearch() {
        resultList.clear();
        currentResultNdx = 0;

        if (searchListener != null) {
            searchListener.onSearchCleared();
        }
    }

    public boolean hasSearchResults() {
        return resultList != null && resultList.size() > 0;
    }

    public void findNextObject() {
        if (searchListener != null) {
            currentResultNdx++;
            if (currentResultNdx >= resultList.size()) {
                currentResultNdx = 0;
            }
            searchListener.onNextSearchObject(resultList.get(currentResultNdx));
        }
    }

    public void findPreviousObject() {
        if (searchListener != null) {
            currentResultNdx--;
            if (currentResultNdx < 0) {
                currentResultNdx = resultList.size()-1;
            }
            searchListener.onPreviousSearchObject(resultList.get(currentResultNdx));
        }
    }

    private List<T0> searchForObject(List<T0> listToSearches, String searchWord) {
        List<T0> foundList = new ArrayList<>();
        if (listToSearches == null || listToSearches.size() == 0) {
            return foundList;
        }

        searchWord = searchWord.toUpperCase();

        for (T0 dbo : listToSearches) {
            if (dbo.hasMatch(searchWord)) {
                foundList.add(dbo);
            }
        }

        return foundList;
    }

    private List<T0> searchForObject(List<T0> listToSearches, DbObject searchObject) {
        List<T0> foundList = new ArrayList<>();
        if (listToSearches == null || listToSearches.size() == 0) {
            return foundList;
        }

        for (T0 dbo : listToSearches) {
            if (dbo.hasMatch(searchObject)) {
                foundList.add(dbo);
            }
        }

        return foundList;
    }

    public boolean isSearched() {
        return searched;
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public boolean isInAdvanced() {
        return inAdvanced;
    }

    public void setInAdvanced(boolean inAdvanced) {
        this.inAdvanced = inAdvanced;
    }

    public boolean isHasAdvancedSearchOption() {
        return hasAdvancedSearchOption;
    }

    public void setHasAdvancedSearchOption(boolean hasAdvancedSearchOption) {
        this.hasAdvancedSearchOption = hasAdvancedSearchOption;
    }

    public void setSearchOptions(int[] searchOptions) {
        this.searchOptions = searchOptions;
    }

    public void setSearchList(List<T0> searchList) {
        this.searchList = searchList;
    }

    public List<T0> getResultList() {
        return resultList;
    }

}
