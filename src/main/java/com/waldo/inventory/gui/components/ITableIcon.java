package com.waldo.inventory.gui.components;

import javax.swing.*;
import java.awt.*;

public class ITableIcon extends ILabel {

    private final ILabel textLabel = new ILabel("");
    private boolean hasIcon = true;

    public ITableIcon(Color background) {
        this(background, 0, true, null, "");
    }

    public ITableIcon(Color background, int row, boolean isSelected, ImageIcon icon) {
        this(background, row, isSelected, icon, "");
    }

    public ITableIcon(Color background, int row, boolean isSelected, String txt) {
        super(txt);
        updateBackground(background, row, isSelected);
        hasIcon = false;
        super.setOpaque(true);
    }

    public ITableIcon(Color background, int row, boolean isSelected, ImageIcon icon, String txt) {
        super(icon);
        createTextLabel(txt);
        updateBackground(background, row, isSelected);

        super.setOpaque(true);
        super.setLayout(new GridBagLayout());
        super.add(textLabel);
    }

    private void createTextLabel(String txt) {
        textLabel.setText(txt);
        textLabel.setForeground(Color.WHITE);
        Font f = textLabel.getFont();
        textLabel.setFont(new Font(f.getName(), Font.BOLD, f.getSize() - 5));
        textLabel.setOpaque(false);
    }

    private void updateBackground(Color background, int row, boolean isSelected) {
        if (row % 2 == 1 || isSelected) {
            super.setBackground(background);
        } else {
            super.setBackground(Color.WHITE);
        }
    }

    @Override
    public void setText(String text) {
        if (hasIcon) {
            textLabel.setText(text);
        } else {
            super.setText(text);
        }
    }
}
