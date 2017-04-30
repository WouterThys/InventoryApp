package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Error;
import com.waldo.inventory.Utils.IconBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultEditorKit;
import java.text.Format;

public class IFormattedTextField extends JFormattedTextField {

    private String beforeEditText = "";
    private Object beforeEditValue = null;
    private boolean edited = false;
    private IEditedListener editedListener;
    private final Border empty = new EmptyBorder(4, 4, 4, 4);

    private Border originalBorder = empty;
    private String originalToolTip = "";
    private String originalText = "";

    private Error error;

    public IFormattedTextField(Format format) {
        super(format);
    }

    public IFormattedTextField(JFormattedTextField.AbstractFormatter formatter) {
        super(formatter);
    }

    public IFormattedTextField(JFormattedTextField.AbstractFormatterFactory factory) {
        super(factory);
    }

    public IFormattedTextField(JFormattedTextField.AbstractFormatterFactory factory, Object currentValue) {
        super(factory, currentValue);
    }

    public IFormattedTextField(Object object) {
        super(object);
    }

    public void setTextBeforeEdit(String t) {
        beforeEditText = t;
        super.setText(t);
    }

    public void addEditedListener(IEditedListener listener) {
        this.editedListener = listener;
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        if (editedListener != null) {
            editedListener.onValueChanged(this, originalText, t);
        }
    }

    public void setValueBeforeEdit(Object valueBeforeEdit) {
        beforeEditValue = valueBeforeEdit;
        super.setValue(valueBeforeEdit);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        if (editedListener != null) {
            editedListener.onValueChanged(this, originalText, value);
        }
    }

    public void setError(String errorText) {
        if (errorText != null) {
            originalText = this.getText();
            originalBorder = this.getBorder();
            originalToolTip = this.getToolTipText();
            error = new Error(Error.ERROR, errorText);
            this.setBorder(new IconBorder(error.getImage(), originalBorder));
            this.setToolTipText(error.getMessage());
        } else {
            error = null;
            this.setText(originalText);
            this.setBorder(originalBorder);
            this.setToolTipText(originalToolTip);
        }
    }

    public void setWarning(String warningText) {
        if (warningText != null) {
            originalText = this.getText();
            originalBorder = this.getBorder();
            originalToolTip = this.getToolTipText();
            error = new Error(Error.WARNING, warningText);
            this.setBorder(new IconBorder(error.getImage(), originalBorder));
            this.setToolTipText(error.getMessage());
        } else {
            error = null;
            this.setText(originalText);
            this.setBorder(originalBorder);
            this.setToolTipText(originalToolTip);
        }
    }

    public boolean isEdited() {
        return (edited || !getValue().equals(beforeEditValue));
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    private void addMenu() {
        JPopupMenu menu = new JPopupMenu();

        Action cut = new DefaultEditorKit.CutAction();
        cut.putValue(Action.NAME, "Cut");
        cut.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
        menu.add(cut);

        Action copy = new DefaultEditorKit.CopyAction();
        copy.putValue(Action.NAME, "Copy");
        copy.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
        menu.add(copy);

        Action paste = new DefaultEditorKit.PasteAction();
        paste.putValue(Action.NAME, "Paste");
        paste.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
        menu.add(paste);

        setComponentPopupMenu(menu);
    }
}
