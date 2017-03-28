package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.classes.Product;

public interface ProductsChangedListener {
    void onProductAdded(Product product);
    void onProductUpdated(Product product);
    void onProductDeleted(Product product);
}
