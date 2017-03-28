package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Error;
import com.waldo.inventory.Utils.IconBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ITextField extends JTextField implements FocusListener {

    private String hint = "";
    private boolean showingHint = false;

    private final Border line = BorderFactory.createLineBorder(Color.GRAY, 2, true);
    private final Border thinLine = BorderFactory.createLineBorder(Color.GRAY, 1, true);
    private final Border empty = new EmptyBorder(4,4,4,4);
    private final Border focusBorder = new CompoundBorder(line, empty);
    private final Border normalBorder = new CompoundBorder(thinLine, empty);

    private String originalText = hint;
    private Border originalBorder = empty;
    private String originalToolTip = "";

    private Error error;

    private DocumentListener documentListener;

    public ITextField() {
        this("", 15);
        addMenu();
    }

    public ITextField(String hint) {
        this(hint, 15);
    }

    public ITextField(String hint, int columns) {
        super(hint, columns);
        this.hint = hint;
        this.addFocusListener(this);
        this.setForeground(Color.gray);
        this.setBorder(normalBorder);
        Font f = this.getFont();
        this.setFont(new Font(f.getName(), Font.BOLD, 15));
        showingHint = !hint.isEmpty();
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        if (t != null && hint != null && !hint.isEmpty()) {
            showingHint = t.equals(hint);
        }
    }

    @Override
    public String getText() {
        if (this.isEnabled()) {
            return showingHint ? "" : super.getText();
        } else {
            return super.getText();
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (this.isEnabled()) {
            this.setForeground(Color.BLACK);
            this.setBorder(focusBorder);
            this.setOpaque(true);
            if (this.getText().isEmpty()) {
                setText("");
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (this.isEnabled()) {
            this.setBorder(normalBorder);
            if (this.getText().isEmpty()) {
                this.setForeground(Color.gray);
                setText(hint);
            }
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

    public void setTrackingField(final JTextComponent textField) {
        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textField.setText(ITextField.this.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textField.setText(ITextField.this.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textField.setText(ITextField.this.getText());
            }
        });
    }

    public void setTrackingField(final JLabel textField) {
        documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textField.setText(ITextField.this.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textField.setText(ITextField.this.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textField.setText(ITextField.this.getText());
            }
        };

        this.getDocument().addDocumentListener(documentListener);
    }

    public void removeTrackingField() {
        if (documentListener != null) {
            this.getDocument().removeDocumentListener(documentListener);
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
