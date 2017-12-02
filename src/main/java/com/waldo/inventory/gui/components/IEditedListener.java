package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.DbObject;

import java.awt.*;

public interface IEditedListener {
    void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue);
    DbObject getGuiObject();
}
