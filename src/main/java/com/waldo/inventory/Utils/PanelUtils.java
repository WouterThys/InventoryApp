package com.waldo.inventory.Utils;

import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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



    public static class GridBagHelper extends GridBagConstraints {

        private JPanel panel;

        public GridBagHelper(JPanel panel) {
            this.panel = panel;
            this.panel.setLayout(new GridBagLayout());

            insets = new Insets(2,2,2,2);
            anchor = GridBagConstraints.EAST;

            gridx = 0;
            gridy = 0;
        }

        public void addLineVertical(String labelText, JComponent component) {
            gridx = 0; weightx = 0;
            gridy = 0; weighty = 0;
            fill = GridBagConstraints.HORIZONTAL;
            panel.add(new ILabel(labelText, ILabel.LEFT), this);

            gridx = 0; weightx = 1;
            gridy = 1; weighty = 1;
            fill = GridBagConstraints.BOTH;
            panel.add(component, this);
        }

        public void addLine(String labelText, JComponent component) {
            addLine(labelText, component, GridBagConstraints.HORIZONTAL);
        }

        public void addLine(String labelText, JComponent component, int fill) {
            int oldGw = gridwidth;
            int oldGh = gridheight;

            weightx = 0; weighty = 0;
            gridwidth = 1;
            this.fill = GridBagConstraints.NONE;
            panel.add(new ILabel(labelText, ILabel.RIGHT), this);


            gridwidth = oldGw;
            gridheight = oldGh;
            gridx = 1; weightx = 1;
            this.fill = fill;
            if (component != null) {
                panel.add(component, this);
            }


            gridx = 0; gridy++;
        }

        public void add(JComponent component, int x, int y) {
            add(component, x, y, 1, weighty);
        }

        public void add(JComponent component, int x, int y, double weightX, double weightY) {
            int oldX = gridx;
            int oldY = gridy;
            double oldWeightX = weightx;
            double oldWeightY = weighty;

            gridx = x; weightx = weightX;
            gridy = y; weighty = weightY;
            panel.add(component, this);

            gridx = oldX; weightx = oldWeightX;
            gridy = oldY; weighty = oldWeightY;

            gridx = 0;
            gridy++;
        }



        public JPanel getPanel() {
            return panel;
        }

        public void setPanel(JPanel panel) {
            this.panel = panel;
        }
    }

    public static class IBrowseWebPanel extends ITextFieldButtonPanel implements ActionListener {

        public IBrowseWebPanel(String hint, String fieldName, IEditedListener editedListener) {
            super(hint, fieldName, editedListener, imageResource.readImage("Common.WebBrowse", 20));

            addButtonActionListener(this);
            setButtonToolTip();
            setTextFieldToolTip();
        }

        private void setButtonToolTip() {
            String tooltip = "Browse ";
            if (!hint.isEmpty() && getText().isEmpty()) {
                String firstChar = String.valueOf(hint.charAt(0));
                if (firstChar.equals(firstChar.toUpperCase())) {
                    tooltip += firstChar.toLowerCase() + hint.substring(1, hint.length());
                }
            } else {
                tooltip += getText();
            }
            button.setToolTipText(tooltip);
        }

        private void setTextFieldToolTip() {
            String tooltip = null;
            if (!getText().isEmpty()) {
                tooltip = getText();
            }
            textField.setToolTipText(tooltip);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!getText().isEmpty()) {
                try {
                    OpenUtils.browseLink(getText());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(
                            IBrowseWebPanel.this,
                            "Unable to browse: " + getText(),
                            "Browse error",
                            JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        }

        @Override
        public void setText(String text) {
            super.setText(text);
            setButtonToolTip();
            setTextFieldToolTip();
        }
    }

}
