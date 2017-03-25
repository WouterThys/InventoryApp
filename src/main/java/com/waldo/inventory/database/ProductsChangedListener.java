package com.waldo.inventory.database;

import com.waldo.inventory.classes.Product;

public interface ProductsChangedListener {
    void onProductAdded(Product product);
    void onProductUpdated(Product product);
    void onProductDeleted(Product product);
}
