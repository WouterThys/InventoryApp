package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.SpringUtilities;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ITitledEditPanel extends JPanel {

    private final String title;
    private final List<String> labelList;
    private final List<JComponent> componentList;

    public ITitledEditPanel(String title, String[] labelList, JComponent[] componentList) {
        super(new BorderLayout(5,5));
        this.title = title;
        this.componentList = Arrays.asList(componentList);
        this.labelList = Arrays.asList(labelList);

        initializeComponents();
    }

    private void initializeComponents() {
        JPanel content = new JPanel(new SpringLayout());

        // Title
        TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        for(int i = 0; i < labelList.size(); i++) {
            JLabel lbl = new JLabel(labelList.get(i), JLabel.TRAILING);
            lbl.setPreferredSize(new Dimension(100,20));
            content.add(lbl);
            lbl.setLabelFor(componentList.get(i));
            content.add(componentList.get(i));
        }

        SpringUtilities.makeCompactGrid(content, labelList.size(),2, 5,5,10,10);

        setBorder(titledBorder);
        add(content, BorderLayout.CENTER);

    }
}


