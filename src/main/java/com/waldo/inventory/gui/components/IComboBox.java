package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

public class IComboBox<E> extends JComboBox<E> {

    private DefaultComboBoxModel<E> comboBoxModel;
    private boolean showUnknown;
    private List<E> itemList;
    private Comparator<E> comparator;

    private IEditedListener editedListener;
    private String fieldName = "";
    private Class fieldClass;

    public IComboBox() {
        super();
    }

    public IComboBox(ComboBoxModel<E> comboBoxModel) {
        super(comboBoxModel);
    }

    public IComboBox(List<E> itemList, Comparator<E> comparator, boolean showUnknown) {
        super();

        this.itemList = itemList;
        this.comparator = comparator;
        this.showUnknown = showUnknown;
        this.comboBoxModel = new DefaultComboBoxModel<>();
        setModel(comboBoxModel);

        updateList();
    }

    public void updateList(List<E> itemList) {
        if (itemList != null) {
            this.itemList = itemList;
            updateList();
        }
    }

    public void updateList() {
        if (itemList != null) {
            if (comparator != null) {
                itemList.sort(comparator);
            }

            comboBoxModel.removeAllElements();
            for (E item : itemList) {
                if (item instanceof DbObject && !((DbObject)item).isUnknown() || showUnknown) {
                    comboBoxModel.addElement(item);
                }
            }
        }
    }

    public void addEditedListener(IEditedListener listener, String fieldName) {
        this.editedListener = listener;
        setFieldName(fieldName);
        setFieldClass(long.class);
        setItemListener();
    }

    public void addEditedListener(IEditedListener listener, String fieldName, Class fieldClass) {
        this.editedListener = listener;
        setFieldName(fieldName);
        setFieldClass(fieldClass);
        setItemListener();
    }

    private void setFieldName(String fieldName) {
        if (!fieldName.isEmpty()) {
            String firstChar = String.valueOf(fieldName.charAt(0));
            if (firstChar.equals(firstChar.toLowerCase())) {
                fieldName = firstChar.toUpperCase()
                        + fieldName.substring(1, fieldName.length());
            }
        }

        this.fieldName = fieldName;
    }

    private void setFieldClass(Class fieldClass) {
        this.fieldClass = fieldClass;
    }

    private void setItemListener() {
        ItemListener itemListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {

                SwingUtilities.invokeLater(() -> {
                    try {
                        DbObject guiObject = editedListener.getGuiObject();
                        if (guiObject != null) {

                            String newVal;
                            if (e.getItem() instanceof DbObject) {
                                newVal = String.valueOf(((DbObject) e.getItem()).getId());
                            } else {
                                newVal = String.valueOf(e.getItem());
                            }
                            String oldVal;
                            if (!fieldName.isEmpty()) {
                                Method setMethod = guiObject.getClass().getDeclaredMethod("set" + fieldName, fieldClass);
                                Method getMethod = guiObject.getClass().getDeclaredMethod("get" + fieldName);

                                oldVal = String.valueOf(getMethod.invoke(guiObject));

                                if (!newVal.equals(oldVal)) {
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

                                    editedListener.onValueChanged(IComboBox.this, fieldName, oldVal, newVal);
                                }
                            }
                        }
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                        e1.printStackTrace();
                    }
                });
            }
        };
        addItemListener(itemListener);
    }

}
