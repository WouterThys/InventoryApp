package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Error;
import com.waldo.inventory.Utils.IconBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ITextArea extends JTextArea implements FocusListener {

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

    public ITextArea() {
        this("");
    }

    public ITextArea(String hint) {
        this(hint, 5, 15);
    }

    public ITextArea(String hint, int rows, int columns) {
        super(hint, rows, columns);
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
        showingHint = t.equals(hint);
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText();
    }

    @Override
    public void focusGained(FocusEvent e) {
        this.setForeground(Color.BLACK);
        this.setBorder(focusBorder);
        this.setOpaque(true);
        if(this.getText().isEmpty()) {
            setText("");
        }
    }
    @Override
    public void focusLost(FocusEvent e) {
        this.setBorder(normalBorder);
        if(this.getText().isEmpty()) {
            this.setForeground(Color.gray);
            setText(hint);
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
}
