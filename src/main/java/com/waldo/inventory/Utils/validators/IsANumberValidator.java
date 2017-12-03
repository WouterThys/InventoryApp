package com.waldo.inventory.Utils.validators;

import com.waldo.inventory.Utils.Error;
import com.waldo.inventory.Utils.ErrorProvider;

import javax.swing.*;

public class IsANumberValidator extends ErrorProvider {
    public IsANumberValidator(JComponent c) {
        super(c);
    }

    @Override
    protected Error ErrorDefinition(JComponent c) {
        try {
            if (c instanceof JTextField) {
                Integer.parseInt(((JTextField) c).getText());
            } else if (c instanceof JTextArea) {
                Integer.parseInt(((JTextArea) c).getText());
            } else {
                return new Error(Error.ERROR, "Not a text type component");
            }
        } catch (NumberFormatException e) {
            return new Error(Error.ERROR, "Please enter a valid number");
        }
        return new Error(Error.NO_ERROR, null);
    }
}
