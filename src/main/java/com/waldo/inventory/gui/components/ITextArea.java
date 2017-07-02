package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Error;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ITextArea extends JTextArea implements FocusListener {

    private String hint = "";
    private boolean showingHint = false;
    private String beforeEditText = "";
    private boolean edited = false;
    private String originalText = hint;
    private String originalToolTip = "";
    private BindingListener documentListener;

    private Error error;

    public ITextArea() {
        this("");
    }

    public ITextArea(String hint) {
        this(hint, 2, 15);
    }

    public ITextArea(String hint, int rows, int columns) {
        super(hint, rows, columns);
        this.hint = hint;
        this.addFocusListener(this);
        this.setForeground(Color.gray);
//        this.setBorder(normalBorder);
        Font f = this.getFont();
        this.setFont(new Font(f.getName(), Font.BOLD, 15));
        showingHint = !hint.isEmpty();
        addMenu();
    }

    public void addEditedListener(IEditedListener listener, String fieldName) {
        if (documentListener != null) {
            this.getDocument().removeDocumentListener(documentListener);
        }
        documentListener = new BindingListener(this, listener, fieldName);
        this.getDocument().addDocumentListener(documentListener);
    }

    @Override
    public void setText(String t) {
        if (documentListener != null) {
            documentListener.setEnabled(false);
        }
        super.setText(t);
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
        super.setText("");
    }

    public boolean isEdited() {
        return (edited || !getText().equals(beforeEditText));
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText().trim();
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (this.isEnabled()) {
            this.setForeground(Color.BLACK);
//            this.setBorder(focusBorder);
            this.setOpaque(true);
            if (this.getText().isEmpty()) {
                setText("");
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

    public void setError(String errorText) {
        if (errorText != null) {
            originalText = this.getText();
//            originalBorder = this.getBorder();
            originalToolTip = this.getToolTipText();
            error = new Error(Error.ERROR, errorText);
//            this.setBorder(new IconBorder(error.getImage(), originalBorder));
            this.setToolTipText(error.getMessage());
        } else {
            error = null;
            this.setText(originalText);
//            this.setBorder(originalBorder);
            this.setToolTipText(originalToolTip);
        }
    }

    public void setWarning(String warningText) {
        if (warningText != null) {
            originalText = this.getText();
//            originalBorder = this.getBorder();
            originalToolTip = this.getToolTipText();
            error = new Error(Error.WARNING, warningText);
//            this.setBorder(new IconBorder(error.getImage(), originalBorder));
            this.setToolTipText(error.getMessage());
        } else {
            error = null;
            this.setText(originalText);
//            this.setBorder(originalBorder);
            this.setToolTipText(originalToolTip);
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
