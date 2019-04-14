package com.waldo.inventory.gui.components;

import com.waldo.utils.icomponents.IPanel;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class INavigator extends IPanel {

    public interface NavigatorListener {
        void onMoved(Direction direction);
    }

    public enum Direction {
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

    private class NavigationButton extends JButton  {

        private final Direction direction;
        private final NavigatorListener navigatorListener;
        private final Timer timer;
        private final int timeDelay = 100;

        NavigationButton(Direction direction, NavigatorListener navigatorListener) {
            super(direction.getIcon());
            setPreferredSize(new Dimension(20,20));

            this.direction = direction;
            this.navigatorListener = navigatorListener;
            this.timer = new Timer(timeDelay, e -> move());

            addActionListener(e -> {
                move();
                timer.stop();
            });

            final ButtonModel model = getModel();
            model.addChangeListener(e -> {
                if (model.isPressed() && !timer.isRunning()) {
                    timer.start();
                } else if (!model.isPressed() && timer.isRunning()) {
                    timer.stop();
                }
            });
        }

        private void move() {
            if (navigatorListener != null) {
                SwingUtilities.invokeLater(() -> navigatorListener.onMoved(direction));
            }
        }
    }

    private NavigationButton uBtn;
    private NavigationButton rBtn;
    private NavigationButton dBtn;
    private NavigationButton lBtn;

    private NavigatorListener navigatorListener;

    public INavigator(NavigatorListener navigatorListener) {
        super();
        this.navigatorListener = navigatorListener;
        initializeComponents();
        initializeLayouts();
    }

    @Override
    public void initializeComponents() {

        uBtn = new NavigationButton(Direction.Up, navigatorListener);
        rBtn = new NavigationButton(Direction.Right, navigatorListener);
        dBtn = new NavigationButton(Direction.Down, navigatorListener);
        lBtn = new NavigationButton(Direction.Left, navigatorListener);

    }

    @Override
    public void initializeLayouts() {

        setLayout(new GridBagLayout());
        setOpaque(false);

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
