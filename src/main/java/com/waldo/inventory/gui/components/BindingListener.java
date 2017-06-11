package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BindingListener implements DocumentListener {

    private String fieldName;
    private Component component;
    private IEditedListener editedListener;
    private boolean enabled;

    public BindingListener(Component component, IEditedListener editedListener, String fieldName) {
        this.component = component;
        this.editedListener = editedListener;

        String firstChar = String.valueOf(fieldName.charAt(0));
        if (firstChar.equals(firstChar.toLowerCase())) {
            fieldName = firstChar.toUpperCase()
                    + fieldName.substring(1, fieldName.length());
        }

        this.fieldName = fieldName;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        dataUpdated(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        dataUpdated(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        dataUpdated(e);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private void dataUpdated(DocumentEvent e) {
        try {
            if (editedListener != null && enabled) {
                DbObject guiObject = editedListener.getGuiObject();
                if (guiObject != null) {
                    String newTxt = e.getDocument().getText(
                            e.getDocument().getStartPosition().getOffset(),
                            e.getDocument().getEndPosition().getOffset() - 1
                    );

                    Method setMethod = guiObject.getClass().getDeclaredMethod("set" + fieldName, String.class);
                    Method getMethod = guiObject.getClass().getDeclaredMethod("get" + fieldName);

                    String oldTxt = String.valueOf(getMethod.invoke(guiObject)).trim();
                    setMethod.invoke(guiObject, newTxt.trim());

                    editedListener.onValueChanged(component, fieldName, oldTxt, newTxt);
                }
            }
        } catch (BadLocationException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
            e1.printStackTrace();
        }
    }
}
