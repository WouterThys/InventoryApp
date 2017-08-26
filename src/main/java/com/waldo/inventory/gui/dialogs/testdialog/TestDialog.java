package com.waldo.inventory.gui.dialogs.testdialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.Application;

public class TestDialog extends TestDialogLayout {

private Item item;

    public TestDialog(Application application, String title, Item item) {
        super(application, title);


        this.item = item;

        initializeComponents();
        initializeLayouts();
        updateComponents(item);

    }

    @Override
    protected void onOK() {
        item.save();
        super.onOK();
    }
}