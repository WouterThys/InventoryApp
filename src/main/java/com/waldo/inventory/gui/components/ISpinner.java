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
    private Class fieldClass;

    private boolean selecting = false;

    public ISpinner() {
        super();
    }

    public ISpinner(SpinnerModel model) {
        super(model);
    }

    public void addEditedListener(IEditedListener listener, String fieldName) {
        this.editedListener = listener;
        setFieldName(fieldName);
        setFieldClass(int.class);
        setChangeListener();
    }

    public void addEditedListener(IEditedListener listener, String fieldName, Class fieldClass) {
        this.editedListener = listener;
        setFieldName(fieldName);
        setFieldClass(fieldClass);
        setChangeListener();
    }

    public void setTheValue(Object value) {
        selecting = true;
        super.setValue(value);
        selecting = false;
    }

    private void setFieldName(String fieldName) {
        String firstChar = String.valueOf(fieldName.charAt(0));
        if (firstChar.equals(firstChar.toLowerCase())) {
            fieldName = firstChar.toUpperCase()
                    + fieldName.substring(1, fieldName.length());
        }

        this.fieldName = fieldName;
    }

    private void setFieldClass(Class fieldClass) {
        this.fieldClass = fieldClass;
    }

    private void setChangeListener() {
        changeListener = e -> {
            if (!selecting) {
                try {
                    DbObject guiObject = editedListener.getGuiObject();
                    if (guiObject != null) {
                        ISpinner spinner = (ISpinner) e.getSource();
                        String newVal = String.valueOf(spinner.getValue());

                        Method setMethod = guiObject.getClass().getDeclaredMethod("set" + fieldName, fieldClass);
                        Method getMethod = guiObject.getClass().getDeclaredMethod("get" + fieldName);

                        String oldVal = String.valueOf(getMethod.invoke(guiObject));
                        switch (fieldClass.getTypeName()) {
                            case "int":
                                setMethod.invoke(guiObject, Integer.valueOf(newVal));
                                break;
                            case "double":
                                setMethod.invoke(guiObject, Double.valueOf(newVal));
                                break;
                            case "float":
                                setMethod.invoke(guiObject, Float.valueOf(newVal));
                                break;
                            case "long":
                                setMethod.invoke(guiObject, Long.valueOf(newVal));
                                break;
                            default:
                                setMethod.invoke(guiObject, newVal);
                                break;
                        }

                        editedListener.onValueChanged(ISpinner.this, fieldName, oldVal, newVal);
                    }
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        };
        addChangeListener(changeListener);
    }
}
