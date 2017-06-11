package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ISpinner extends JSpinner {

    private IEditedListener editedListener;
    private ChangeListener changeListener;

    private String fieldName;

    public ISpinner() {
        super();
    }

    public ISpinner(SpinnerModel model) {
        super(model);
    }

    public void addEditedListener(IEditedListener listener, String fieldName) {
        this.editedListener = listener;
        setFieldName(fieldName);
        setChangeListener();
    }

    private void setFieldName(String fieldName) {
        String firstChar = String.valueOf(fieldName.charAt(0));
        if (firstChar.equals(firstChar.toLowerCase())) {
            fieldName = firstChar.toUpperCase()
                    + fieldName.substring(1, fieldName.length());
        }

        this.fieldName = fieldName;
    }

    private void setChangeListener() {
        changeListener = e -> {
            try {
                DbObject guiObject = editedListener.getGuiObject();
                if (guiObject != null) {
                    ISpinner spinner = (ISpinner) e.getSource();
                    String newVal = String.valueOf(spinner.getValue());

                    Method setMethod = guiObject.getClass().getDeclaredMethod("set" + fieldName, String.class);
                    Method getMethod = guiObject.getClass().getDeclaredMethod("get" + fieldName);

                    String oldVal = String.valueOf(getMethod.invoke(guiObject));
                    setMethod.invoke(guiObject, newVal);

                    editedListener.onValueChanged(ISpinner.this, fieldName, oldVal, newVal);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                e1.printStackTrace();
            }
        };
        addChangeListener(changeListener);
    }
}
