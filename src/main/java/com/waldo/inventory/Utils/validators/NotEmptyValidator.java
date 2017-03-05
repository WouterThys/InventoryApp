package com.waldo.inventory.Utils.validators;

import com.waldo.inventory.Utils.Error;
import com.waldo.inventory.Utils.ErrorProvider;

import javax.swing.*;

public class NotEmptyValidator extends ErrorProvider {

    public NotEmptyValidator(JComponent c) {
        super(c);
    }

    @Override
    protected Error ErrorDefinition(JComponent c) {
        return null;
    }
}
