package com.waldo.inventory.classes.search;

import com.waldo.inventory.classes.dbclasses.DbObject;

import java.util.List;

public class Search {

    private Search() {}

    public interface SearchListener<dbo extends DbObject> {
        void onObjectsFound(List<dbo> foundObjects);
        void onSearchCleared();
        void onNextSearchObject(dbo next);
        void onPreviousSearchObject(dbo previous);
    }

    public static class DbObjectSearch <T extends DbObject> {

        private final DbObjectSearchClass<T> searchClass;

        public DbObjectSearch(List<T> searchList, SearchListener<T> searchListener) {
            searchClass = new DbObjectSearchClass<>(searchList, searchListener);
        }

        public void setSearchList(List<T> searchList) {
            searchClass.setSearchList(searchList);
        }

        public void search(String searchWord) {
            searchClass.search(searchWord);
        }

        public void search(DbObject dbObject) {
            searchClass.search(dbObject);
        }

        public void clearSearch() {
            searchClass.clearSearch();
        }

        public void setSearched(boolean searched) {
            searchClass.setSearched(searched);
        }

        public boolean isSearched() {
            return searchClass.isSearched();
        }

        public void findPreviousObject() {
            searchClass.findPreviousObject();
        }

        public void findNextObject() {
            searchClass.findNextObject();
        }

        public boolean hasSearchResults() {
            return searchClass.hasSearchResults();
        }
    }

    public static class DbObjectSearch2 <T0 extends DbObject, T1 extends DbObject> {

        private final DbObjectSearchClass<T0> searchClass0;
        private final DbObjectSearchClass<T1> searchClass1;

        public DbObjectSearch2(List<T0> searchList0, SearchListener<T0> searchListener0, List<T1> searchList1, SearchListener<T1> searchListener1) {
            searchClass0 = new DbObjectSearchClass<>(searchList0, searchListener0);
            searchClass1 = new DbObjectSearchClass<>(searchList1, searchListener1);
        }

        public void setSearchList(List<T0> searchList0, List<T1> searchList1) {
            searchClass0.setSearchList(searchList0);
            searchClass1.setSearchList(searchList1);
        }

        public void search(String searchWord) {

                searchClass0.search(searchWord, false);
                searchClass1.search(searchWord, false);

        }

        public void search(DbObject dbObject) {

                searchClass0.search(dbObject, false);
                searchClass1.search(dbObject, false);

        }

        public void clearSearch() {
            searchClass0.clearSearch();
            searchClass1.clearSearch();
        }

        public void setSearched(boolean searched) {
            searchClass0.setSearched(searched);
            searchClass1.setSearched(searched);
        }

        public boolean isSearched() {
            return (searchClass0.isSearched() || searchClass1.isSearched());
        }

        public void findPreviousObject() {
            searchClass0.findPreviousObject();
            searchClass1.findPreviousObject();
        }

        public void findNextObject() {
            searchClass0.findNextObject();
            searchClass1.findNextObject();
        }

        public boolean hasSearchResults() {
            return searchClass0.hasSearchResults() || searchClass1.hasSearchResults();
        }
    }

}
