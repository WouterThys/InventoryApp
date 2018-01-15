package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.gui.GuiInterface;

import javax.swing.*;
import java.awt.*;

public class ITextFieldActionPanel extends JPanel implements GuiInterface {

    protected final String hint;
    protected String text;

    protected final IEditedListener editedListener;
    protected final String fieldName;

    protected ITextField textField;
    protected Action action;

    public ITextFieldActionPanel(String hint) {
        this(hint, null);
    }

    public ITextFieldActionPanel(String hint, Action action) {
        this(hint, "", null, action);
    }

    public ITextFieldActionPanel(String hint, String fieldName, IEditedListener editedListener) {
        this(hint, fieldName, editedListener, null);
    }

    public ITextFieldActionPanel(String hint, String fieldName, IEditedListener editedListener, Action action) {
        super();

        this.text = "";
        this.hint = hint;
        this.fieldName = fieldName;
        this.editedListener = editedListener;
        this.action = action;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public void addFieldEditedListener(IEditedListener listener, String fieldName) {
        textField.addEditedListener(listener, fieldName);
    }

    public void setAction(Action action) {
        if (this.action == null) {
            add(GuiUtils.createNewToolbar(action), BorderLayout.EAST);
        }
        this.action = action;
    }


    @Override
    public void initializeComponents() {
        // Text field
        textField = new ITextField(hint);
        if (editedListener != null) {
            textField.addEditedListener(editedListener, fieldName);
        }
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        add(textField, BorderLayout.CENTER);
        if (action != null) {
            add(GuiUtils.createNewToolbar(action), BorderLayout.EAST);
        }
    }

    @Override
    public void updateComponents(Object... object) {

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setTextEnabled(enabled);
        setActionEnabled(enabled);
    }

    public void setTextEnabled(boolean enabled) {
        textField.setEnabled(enabled);
    }

    public void setActionEnabled(boolean enabled) {
        if (action != null) {
            action.setEnabled(enabled);
        }
    }

    public void setText(String text) {
        this.text = text;
        textField.setText(text);
    }

    public void clearText() {
        this.text = "";
        textField.clearText();
    }

    public String getText() {
        this.text = textField.getText();
        return text;
    }
}
