package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;

import java.awt.*;

public interface IEditedListener {
    void onValueChanged(Component component, Object previousValue, Object newValue);
    DbObject getGuiObject();
}
