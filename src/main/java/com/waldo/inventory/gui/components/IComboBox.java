package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IComboBox<E extends DbObject> extends JComboBox<E> {

    private IEditedListener editedListener;
    private ItemListener itemListener;

    private String fieldName;

    public IComboBox() {
        super();
    }

    public IComboBox(ComboBoxModel<E> model) {
        super(model);
    }

    public void addEditedListener(IEditedListener listener, String fieldName) {
        this.editedListener = listener;
        setFieldName(fieldName);
        setItemListener();
    }

    private void setFieldName(String fieldName) {
        String firstChar = String.valueOf(fieldName.charAt(0));
        if (firstChar.equals(firstChar.toLowerCase())) {
            fieldName = firstChar.toUpperCase()
                    + fieldName.substring(1, fieldName.length());
        }

        this.fieldName = fieldName;
    }

    private void setItemListener() {
        itemListener = e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    try {
                        DbObject guiObject = editedListener.getGuiObject();
                        if (guiObject != null) {
                            String newVal = String.valueOf(((DbObject) e.getItem()).getId());

                            Method setMethod = guiObject.getClass().getDeclaredMethod("set" + fieldName, String.class);
                            Method getMethod = guiObject.getClass().getDeclaredMethod("get" + fieldName);

                            String oldVal = String.valueOf(getMethod.invoke(guiObject));
                            setMethod.invoke(guiObject, newVal);

                            editedListener.onValueChanged(IComboBox.this, fieldName, oldVal, newVal);
                        }
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                        e1.printStackTrace();
                    }
                }
            };
        addItemListener(itemListener);
    }

}
