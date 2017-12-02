package com.waldo.inventory.Utils.validators;

import com.waldo.inventory.Utils.Error;
import com.waldo.inventory.Utils.ErrorProvider;

import javax.swing.*;

public class NotEmptyValidator extends ErrorProvider {

    public NotEmptyValidator(JFrame parent, JComponent c) {
        super(parent, c);
    }

    public NotEmptyValidator(JComponent c) {
        super(c);
    }

    @Override
    protected Error ErrorDefinition(JComponent c) {
        try {
            if (c instanceof JTextField) {
                if(((JTextField) c).getText().isEmpty()) {
                    return new Error(Error.ERROR, "This field can't be empty");
                }
            } else if (c instanceof JTextArea) {
                if(((JTextArea) c).getText().isEmpty()) {
                    return new Error(Error.ERROR, "This field can't be empty");
                }
            } else {
                return new Error(Error.ERROR, "Not a text type component");
            }
            return new Error(Error.NO_ERROR, null);
        } catch (Exception e) {
            return new Error(Error.ERROR, e.getMessage());
        }
    }
}
