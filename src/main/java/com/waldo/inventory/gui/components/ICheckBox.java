package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ICheckBox extends JCheckBox implements ItemListener {

    private IEditedListener editedListener;
    private String fieldName;

    public ICheckBox() {
        super();
    }

    public ICheckBox(String text) {
        super(text);
    }

    public ICheckBox(String text, boolean selected) {
        super(text, selected);
    }

    public void addEditedListener(IEditedListener listener, String fieldName) {
        addItemListener(this);
        this.editedListener = listener;
        setFieldName(fieldName);
    }

    private void setFieldName(String fieldName) {
        String firstChar = String.valueOf(fieldName.charAt(0));
        if (firstChar.equals(firstChar.toLowerCase())) {
            fieldName = firstChar.toUpperCase()
                    + fieldName.substring(1, fieldName.length());
        }

        this.fieldName = fieldName;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (editedListener != null) {
            try {
                DbObject guiObject = editedListener.getGuiObject();
                if (guiObject != null) {
                    ICheckBox checkBox = (ICheckBox) e.getSource();
                    String newVal = String.valueOf(checkBox.isSelected());

                    Method setMethod = guiObject.getClass().getDeclaredMethod("set" + fieldName, boolean.class);
                    Method getMethod = guiObject.getClass().getDeclaredMethod("is" + fieldName);

                    String oldVal = String.valueOf(getMethod.invoke(guiObject));
                    setMethod.invoke(guiObject, Boolean.valueOf(newVal));

                    editedListener.onValueChanged(ICheckBox.this, fieldName, oldVal, newVal);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
    }
}
