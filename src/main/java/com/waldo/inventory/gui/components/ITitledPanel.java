package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.SpringUtilities;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ITitledPanel extends JPanel {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    private String title;
    private final List<JComponent> componentList;
    private TitledBorder titledBorder;

    public ITitledPanel(String title, JComponent[] componentList) {
        this(title, componentList, VERTICAL);
    }

    public ITitledPanel(String title, JComponent[] componentList, int direction) {
        super(new BorderLayout(5,5));
        this.title = title;
        this.componentList = Arrays.asList(componentList);

        initializeComponents(direction);
    }

    private void initializeComponents(int direction) {
        JPanel content = new JPanel(new SpringLayout());

        // Title
        titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        for(JComponent c : componentList) {
            content.add(c);
        }

        if (direction == VERTICAL) {
            SpringUtilities.makeCompactGrid(content, componentList.size(), 1, 5, 5, 10, 10);
        } else {
            SpringUtilities.makeCompactGrid(content, 1, componentList.size(), 5, 5, 10, 10);
        }

        setBorder(titledBorder);
        add(content, BorderLayout.CENTER);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        titledBorder.setTitle(title);
    }
}
