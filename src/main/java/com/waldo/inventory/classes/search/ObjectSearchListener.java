package com.waldo.inventory.classes.search;

import com.waldo.inventory.classes.dbclasses.DbObject;

import java.util.List;

public interface ObjectSearchListener<T extends DbObject> {
    List<ObjectMatch<T>> searchByKeyWord(List<T> searchList, String searchTerm);
}
