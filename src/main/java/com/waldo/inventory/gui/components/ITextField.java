package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Error;
import com.waldo.inventory.Utils.IconBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class ITextField extends JTextField implements FocusListener {

    private String hint = "";
    private boolean showingHint = false;
    private String beforeEditText = "";
    private boolean edited = false;
    private IEditedListener editedListener;
    private String originalText = hint;
    private String originalToolTip = "";
    private Border originalBorder;
    private BindingListener documentListener;
    private boolean showingError;

    private Error error;

    public ITextField() {
        this("", 15);
    }

    public ITextField(boolean enabled) {
        this("", 15);
        setEnabled(enabled);
    }

    public ITextField(String hint) {
        this(hint, 15);
    }

    public ITextField(String hint, int columns) {
        super(hint, columns);
        this.hint = hint;
        this.addFocusListener(this);
        this.setForeground(Color.gray);
//        this.setBorder(normalBorder);
        Font f = this.getFont();
        this.setFont(new Font(f.getName(), Font.BOLD, 15));
        showingHint = !hint.isEmpty();
        addMenu();

        originalBorder = getBorder();
    }

    public ITextField(IEditedListener listener, String fieldName) {
        this();
        addEditedListener(listener, fieldName);
    }

    @Override
    public void setText(String t) {
        if (documentListener != null) {
            documentListener.setEnabled(false);
        }
        try {
            super.setText(t);
        } catch (Exception e) {
            Status().setError("Error setting text." , e);
        }
        if (t != null && hint != null && !hint.isEmpty()) {
            showingHint = t.equals(hint);
        }
        if (documentListener != null) {
            documentListener.setEnabled(true);
        }
    }

    @Deprecated
    public void setTextBeforeEdit(String t) {
        beforeEditText = t;
        super.setText(t);
        if (t != null && hint != null && !hint.isEmpty()) {
            showingHint = t.equals(hint);
        }
    }

    public void clearText() {
        super.setText(hint);
    }


    @Override
    public String getText() {
        if (this.isEnabled()) {
            return showingHint ? "" : super.getText();
        } else {
            return super.getText().trim();
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (showingError) {
            setError(null);
        }
        if (this.isEnabled()) {
            this.setForeground(Color.BLACK);
//            this.setBorder(focusBorder);
            this.setOpaque(true);
            if (this.getText().isEmpty()) {
                showingHint = false;
                super.setText("");
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (this.isEnabled()) {
//            this.setBorder(normalBorder);
            if (this.getText().isEmpty()) {
                this.setForeground(Color.gray);
                setText(hint);
            }
        }
    }

    public void fireValueChanged() {
        if (documentListener != null) {
            documentListener.fireValueEdited(this.getDocument());
        }
    }

    public void addEditedListener(IEditedListener listener, String fieldName) {
        if (documentListener != null) {
            this.getDocument().removeDocumentListener(documentListener);
        }
        documentListener = new BindingListener(this, listener, fieldName);
        this.getDocument().addDocumentListener(documentListener);
    }

    public void addEditedListener(IEditedListener listener, String fieldName, Class fieldClass) {
        if (documentListener != null) {
            this.getDocument().removeDocumentListener(documentListener);
        }
        documentListener = new BindingListener(this, listener, fieldName, fieldClass);
        this.getDocument().addDocumentListener(documentListener);
    }

    public void setError(String errorText) {
        if (errorText != null) {
            originalText = this.getText();
            originalBorder = this.getBorder();
            originalToolTip = this.getToolTipText();
            error = new Error(Error.ERROR, errorText);
            this.setBorder(new IconBorder(error.getImage(), originalBorder));
            this.setToolTipText(error.getMessage());
            showingError = true;
        } else {
            error = null;
            this.setText(originalText);
            this.setBorder(originalBorder);
            this.setToolTipText(originalToolTip);
            showingError= false;
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
            showingError = true;
        } else {
            error = null;
            this.setText(originalText);
            this.setBorder(originalBorder);
            this.setToolTipText(originalToolTip);
            showingError = false;
        }
    }

    private void addMenu() {
        JPopupMenu menu = new JPopupMenu();

        Action cut = new DefaultEditorKit.CutAction();
        cut.putValue(Action.NAME, "Cut");
        cut.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
        menu.add( cut );

        Action copy = new DefaultEditorKit.CopyAction();
        copy.putValue(Action.NAME, "Copy");
        copy.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
        menu.add( copy );

        Action paste = new DefaultEditorKit.PasteAction();
        paste.putValue(Action.NAME, "Paste");
        paste.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
        menu.add( paste );

        setComponentPopupMenu(menu);
    }
}
