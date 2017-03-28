package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.classes.Category;

public interface CategoriesChangedListener {
    void onCategoryAdded(Category category);
    void onCategoryUpdated(Category category);
    void onCategoryDeleted(Category category);
}
