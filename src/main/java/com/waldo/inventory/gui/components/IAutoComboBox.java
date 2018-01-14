package com.waldo.inventory.gui.components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.event.ItemEvent;

public class IAutoComboBox<T> extends IComboBox<T> {

    private class AutoTextFieldEditor extends BasicComboBoxEditor {

        private IAutoTextField getAutoTextFieldEditor() {
            return (IAutoTextField) editor;
        }

        AutoTextFieldEditor(java.util.List list, IEditedListener listener, String fieldName) {
            editor = new IAutoTextField(list, IAutoComboBox.this, listener, fieldName);
        }
    }

    private AutoTextFieldEditor autoTextFieldEditor;
    private boolean isFired;

    public IAutoComboBox(java.util.List<T> list) {
        this(list, null, "");
    }

    public IAutoComboBox(java.util.List<T> list, IEditedListener listener, String fieldName) {
        isFired = false;
        autoTextFieldEditor = new AutoTextFieldEditor(list, listener, fieldName);
        setEditable(true);
        setModel(new DefaultComboBoxModel<T>((T[]) list.toArray()) {
            protected void fireContentsChanged(Object obj, int i, int j) {
                if (!isFired) {
                    super.fireContentsChanged(obj, i, j);
                }
            }
        });
        setEditor(autoTextFieldEditor);
    }

    public boolean isCaseSensitive() {
        return autoTextFieldEditor.getAutoTextFieldEditor().isCaseSensitive();
    }

    public void setCaseSensitive(boolean flag) {
        autoTextFieldEditor.getAutoTextFieldEditor().setCaseSensitive(flag);
    }

    public boolean isStrict() {
        return autoTextFieldEditor.getAutoTextFieldEditor().isStrict();
    }

    public void setStrict(boolean flag) {
        autoTextFieldEditor.getAutoTextFieldEditor().setStrict(flag);
    }

    public java.util.List getDataList() {
        return autoTextFieldEditor.getAutoTextFieldEditor().getDataList();
    }

    public void setDataList(java.util.List<T> list) {
        autoTextFieldEditor.getAutoTextFieldEditor().setDataList(list);
        setModel(new DefaultComboBoxModel<T>((T[]) list.toArray()));
    }

    void setSelectedValue(Object obj) {
        if (!isFired) {
            isFired = true;
            setSelectedItem(obj);
            fireItemStateChanged(new ItemEvent(this, 701, selectedItemReminder, 1));
            isFired = false;
        }
    }

    protected void fireActionEvent() {
        if (!isFired)
            super.fireActionEvent();
    }



    @Override
    public void updateUI() {
        super.updateUI();
        UIManager.put("ComboBox.squareButton", Boolean.FALSE);
        setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                JButton b = new JButton();
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setVisible(false);
                return b;
            }
        });
        //setBorder(BorderFactory.createLineBorder(java.awt.Color.gray));
    }

}