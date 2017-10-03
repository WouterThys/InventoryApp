package com.waldo.inventory.Utils;

import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.gui.Application.imageResource;
import static java.awt.GridBagConstraints.BOTH;

public class PanelUtils {

    public static GridBagConstraints createFieldConstraints(int gridLocX, int gridLocY) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridLocX;
        constraints.gridy = gridLocY;
        constraints.weightx = 3;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(2,2,2,2);
        constraints.fill = BOTH;
        return constraints;
    }

    public static JPanel createBrowsePanel(ITextField urlTf, JButton browseBtn) {
        JPanel browsePanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0; constraints.weightx = 1;
        constraints.gridy = 0; constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        browsePanel.add(urlTf, constraints);
        constraints.gridx = 1; constraints.weightx = 0;
        constraints.gridy = 0; constraints.weighty = 0;
        constraints.fill = GridBagConstraints.VERTICAL;
        browsePanel.add(browseBtn, constraints);

        return browsePanel;
    }

    public static JPanel createFileOpenPanel(ITextField fileTf, JButton openBtn) {
        JPanel iconPathPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = createFieldConstraints(0,0);
        constraints.gridwidth = 1;
        iconPathPanel.add(fileTf, constraints);
        constraints = createFieldConstraints(1,0);
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        iconPathPanel.add(openBtn, constraints);
        return iconPathPanel;
    }

    public static JPanel createComboBoxWithButton(JComboBox comboBox, ActionListener listener) {
        JPanel boxPanel = new JPanel(new BorderLayout());
        JButton button = new JButton(imageResource.readImage("Toolbar.AddIcon", 16));
        button.addActionListener(listener);
        boxPanel.add(comboBox, BorderLayout.CENTER);
        boxPanel.add(button, BorderLayout.EAST);
        return boxPanel;
    }

    public static TitledBorder createTitleBorder(String name) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(name);
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);
        return titledBorder;
    }



    public class GridBagHelper {

        private JPanel panel;

        private GridBagConstraints gbc;
        private int x = 0;
        private int y = 0;

        public GridBagHelper(JPanel panel) {
            this.panel = panel;
            this.gbc = new GridBagConstraints();

            gbc.insets = new Insets(2,2,2,2);
            gbc.anchor = GridBagConstraints.NORTHEAST;
        }

        public void addLine(String labelText, JComponent component) {
            addLine(labelText, component, GridBagConstraints.HORIZONTAL);
        }

        public void addLine(String labelText, JComponent component, int fill) {
            gbc.gridx = 0; gbc.weightx = 0;
            gbc.gridy = 0; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(new ILabel(labelText, ILabel.RIGHT), gbc);

            gbc.gridx = 1; gbc.weightx = 1;
            gbc.gridy = 0; gbc.weighty = 1;
            gbc.fill = fill;
            panel.add(component, gbc);
        }


        public JPanel getPanel() {
            return panel;
        }

        public void setPanel(JPanel panel) {
            this.panel = panel;
        }

        public GridBagConstraints getGbc() {
            return gbc;
        }

        public void setGbc(GridBagConstraints gbc) {
            this.gbc = gbc;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

}
