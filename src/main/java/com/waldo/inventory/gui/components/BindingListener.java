package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.DbObject;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BindingListener implements DocumentListener {

    private String fieldName = "";
    public final Class fieldClass;

    private final Component component;
    private final IEditedListener editedListener;
    private boolean enabled;

    public BindingListener(Component component, IEditedListener editedListener, String fieldName, Class fieldClass) {
        this.component = component;
        this.editedListener = editedListener;
        this.fieldClass = fieldClass;

        if (!fieldName.isEmpty()) {
            String firstChar = String.valueOf(fieldName.charAt(0));
             if (firstChar.equals(firstChar.toLowerCase())) {
                fieldName = firstChar.toUpperCase() + fieldName.substring(1, fieldName.length());
            }
        }

        this.fieldName = fieldName;
    }

    public BindingListener(Component component, IEditedListener editedListener, String fieldName) {
        this(component, editedListener, fieldName, String.class);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        dataUpdated(e.getDocument());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        dataUpdated(e.getDocument());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        dataUpdated(e.getDocument());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void fireValueEdited(Document d) {
        dataUpdated(d);
    }

    private void dataUpdated(Document d) {
        try {
            if (editedListener != null && enabled) {
                DbObject guiObject = editedListener.getGuiObject();
                if (guiObject != null) {
                    String newVal = d.getText(
                            d.getStartPosition().getOffset(),
                            d.getEndPosition().getOffset() - 1
                    );
                    newVal = newVal.trim();

                    String oldVal = "";
                    if (!fieldName.isEmpty()) {

                        Method setMethod = guiObject.getClass().getMethod("set" + fieldName, fieldClass);
                        Method getMethod = guiObject.getClass().getMethod("get" + fieldName);

                        oldVal = String.valueOf(getMethod.invoke(guiObject)).trim();
                        switch (fieldClass.getTypeName()) {
                            case "int":
                                if (newVal.isEmpty()) newVal = "0";
                                setMethod.invoke(guiObject, Integer.valueOf(newVal));
                                break;
                            case "double":
                                if (newVal.isEmpty()) newVal = "0";
                                setMethod.invoke(guiObject, Double.valueOf(newVal));
                                break;
                            case "float":
                                if (newVal.isEmpty()) newVal = "0";
                                setMethod.invoke(guiObject, Float.valueOf(newVal));
                                break;
                            case "long":
                                if (newVal.isEmpty()) newVal = "0";
                                setMethod.invoke(guiObject, Long.valueOf(newVal));
                                break;
                            default:
                                setMethod.invoke(guiObject, newVal);
                                break;
                        }
                    }
                    editedListener.onValueChanged(component, fieldName, oldVal, newVal);
                }
            }
        } catch (BadLocationException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
            e1.printStackTrace();
        }
    }
}
