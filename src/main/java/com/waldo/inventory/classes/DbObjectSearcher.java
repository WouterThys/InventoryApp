package com.waldo.inventory.classes;

import com.waldo.inventory.classes.dbclasses.DbObject;

import java.util.ArrayList;
import java.util.List;

public class DbObjectSearcher<T extends DbObject> {

    private boolean searched = false;
    private boolean inAdvanced = false;
    private boolean hasAdvancedSearchOption;
    private int[] searchOptions;
    private List<T> searchList;
    private List<T> resultList = new ArrayList<>();
    private int currentResultNdx = 0;

    public DbObjectSearcher(List<T> searchList) {
        this.searchList = searchList;
    }

    public DbObjectSearcher(List<T> searchList, int ... searchOptions) {
        this.searchList = searchList;
        this.searchOptions = searchOptions;
    }


    public List<T> search(String searchWord) {
        List<T> foundObjects;

        // Search list
        if (searchOptions == null || searchOptions.length == 0) {
            foundObjects = searchForObject(searchList, searchWord);
        } else {
            foundObjects = searchForObject(searchList /*, searchOptions*/, searchWord);
        }

        currentResultNdx = 0;
        resultList = foundObjects;
        return foundObjects;
    }

    public void clearSearch() {
        resultList.clear();
        currentResultNdx = 0;
    }

    public T getNextFoundObject() {
        currentResultNdx++;
        if (currentResultNdx >= resultList.size()) {
            currentResultNdx = 0;
        }
        return resultList.get(currentResultNdx);
    }

    public T getPreviousFoundObject() {
        currentResultNdx--;
        if (currentResultNdx < 0) {
            currentResultNdx = resultList.size()-1;
        }
        return resultList.get(currentResultNdx);
    }

    private List<T> searchForObject(List<T> listToSearch, String searchWord) {
        List<T> foundList = new ArrayList<>();
        if (listToSearch == null || listToSearch.size() == 0) {
            return foundList;
        }

        searchWord = searchWord.toUpperCase();

        for (T dbo : listToSearch) {
            if (dbo.hasMatch(searchWord)) {
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

    public void setSearchList(List<T> searchList) {
        this.searchList = searchList;
    }

    public List<T> getResultList() {
        return resultList;
    }

}
