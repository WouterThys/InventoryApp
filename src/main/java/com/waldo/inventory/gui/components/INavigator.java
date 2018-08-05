package com.waldo.inventory.gui.components;

import com.waldo.utils.icomponents.IPanel;

import javax.swing.*;

import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class INavigator extends IPanel {

    private enum Direction {
        Up   (imageResource.readIcon("Arrow.Open.SS")),
        Right(imageResource.readIcon("Arrow.Right.SS")),
        Down (imageResource.readIcon("Arrow.Close.SS")),
        Left (imageResource.readIcon("Arrow.Left.SS"));

        final ImageIcon icon;
        Direction(ImageIcon icon) {
            this.icon = icon;
        }

        public ImageIcon getIcon() {
            return icon;
        }
    }

    private class NavigationButton extends JButton {

        private Direction direction;

        NavigationButton(Direction direction) {
            super(direction.getIcon());
            setPreferredSize(new Dimension(20,20));
        }
    }


    private NavigationButton uBtn;
    private NavigationButton rBtn;
    private NavigationButton dBtn;
    private NavigationButton lBtn;

    public INavigator() {
        super();

        initializeComponents();
        initializeLayouts();
    }

    @Override
    public void initializeComponents() {

        uBtn = new NavigationButton(Direction.Up);
        rBtn = new NavigationButton(Direction.Right);
        dBtn = new NavigationButton(Direction.Down);
        lBtn = new NavigationButton(Direction.Left);

    }

    @Override
    public void initializeLayouts() {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0;
        gbc.gridx = 1;
        add(uBtn, gbc);

        gbc.gridy = 1;
        gbc.gridx = 2;
        add(rBtn, gbc);

        gbc.gridy = 2;
        gbc.gridx = 1;
        add(dBtn, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        add(lBtn, gbc);


    }

    @Override
    public void updateComponents(Object... objects) {

    }
}
