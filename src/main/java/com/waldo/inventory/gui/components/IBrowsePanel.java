package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.OpenUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static com.waldo.inventory.gui.Application.imageResource;

public class IBrowsePanel extends ITextFieldButtonPanel implements ActionListener {

    public IBrowsePanel(String hint, String fieldName, IEditedListener editedListener) {
        super(hint, fieldName, editedListener, imageResource.readImage("Common.WebBrowse", 20));

        addButtonActionListener(this);
        setButtonToolTip();
        setTextFieldToolTip();
    }

    private void setButtonToolTip() {
        String tooltip = "Browse ";
        if (!hint.isEmpty() && getText().isEmpty()) {
            String firstChar = String.valueOf(hint.charAt(0));
            if (firstChar.equals(firstChar.toUpperCase())) {
                tooltip += firstChar.toLowerCase() + hint.substring(1, hint.length());
            }
        } else {
            tooltip += getText();
        }
        button.setToolTipText(tooltip);
    }

    private void setTextFieldToolTip() {
        String tooltip = null;
        if (!getText().isEmpty()) {
            tooltip = getText();
        }
        textField.setToolTipText(tooltip);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!getText().isEmpty()) {
            try {
                OpenUtils.browseLink(getText());
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(
                        IBrowsePanel.this,
                        "Unable to browse: " + getText(),
                        "Browse error",
                        JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        setButtonToolTip();
        setTextFieldToolTip();
    }
}
