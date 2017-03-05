package com.waldo.inventory.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * This class handles all the details of validation and
 * decorating the component.
 */
public abstract class ErrorProvider extends InputVerifier {

    private Border originalBorder;
    private Color originalBackgroundColor;
    private String originalTooltipText;
    private Object parent;

    /**
     * Constructor
     * @param c The JComponent to be validated.
     */
    public ErrorProvider(JComponent c) {
        originalBorder = c.getBorder();
        originalBackgroundColor = c.getBackground();
        originalTooltipText = c.getToolTipText();
    }

    /**
     * Constructor with parent
     * @param parent A JDialog that implements the ValidationStatus interface.
     * @param c JComponent to be validated.
     */
    public ErrorProvider(JFrame parent, JComponent c) {
        this(c);
        this.parent = parent;
    }

    /**
     * Define your custom Error in this method and return aan Error object.
     * @param c The JComponent to be validated.
     * @return Error
     * @see Error
     */
    protected abstract Error ErrorDefinition(JComponent c);

    /**
     * This method is called by Java when a component needs to be validated.
     * @param c The JComponent to be validated.
     * @return True if valid
     */
    public boolean verify(JComponent c) {
        Error error = ErrorDefinition(c);
        if (error.getErrorType() == Error.NO_ERROR) {
            c.setBackground(originalBackgroundColor);
            c.setBorder(originalBorder);
            c.setToolTipText(originalTooltipText);
        } else {
            c.setBorder(new IconBorder(error.getImage(), originalBorder));
            c.setBackground(error.getColor());
            c.setToolTipText(error.getMessage());
        }
        if (error.getErrorType() == Error.ERROR) {
            if (parent instanceof  ValidationStatus) {
                ((ValidationStatus)parent).reportStatus(false);
            }
            return false;
        } else {
            if (parent instanceof  ValidationStatus) {
                ((ValidationStatus)parent).reportStatus(true);
            }
            return true;
        }
    }
}
