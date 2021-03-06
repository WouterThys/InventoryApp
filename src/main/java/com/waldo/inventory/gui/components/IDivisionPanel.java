package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class IDivisionPanel extends IPanel {

    private final ImageIcon level0Icon = imageResource.readIcon("Component.Green.SS");
    private final ImageIcon level1Icon = imageResource.readIcon("Component.Yellow.SS");
    private final ImageIcon level2Icon = imageResource.readIcon("Component.Red.SS");
    private final ImageIcon levelXIcon = imageResource.readIcon("Component.SS");

    private Division division;
    private boolean showParents;
    private List<DivisionLayout> divisionLayoutList = new ArrayList<>();

    public IDivisionPanel() {
        this(true);
    }

    public IDivisionPanel(boolean showParents) {
        super();
        this.showParents = showParents;
    }

    private void addDivisionToLayoutList(Division division) {
        if (division != null) {
            divisionLayoutList.add(0, new DivisionLayout(division));
        }
    }

    @Override
    public void initializeComponents() {
        divisionLayoutList.clear();
        if (division != null) {

            addDivisionToLayoutList(division);
            if (showParents) {
                List<Division> parents = division.getParentDivisions();

                for (Division parent : parents) {
                    addDivisionToLayoutList(parent);
                }
            }
        }
    }

    @Override
    public void initializeLayouts() {
        JPanel divisionsPanel = new JPanel();

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(divisionsPanel);
        for (DivisionLayout component : divisionLayoutList) {
            gbc.addLine(component.getToolTip(), component.getIcon(), component);
        }

        setLayout(new BorderLayout());
        add(divisionsPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0) {
            Division newDivision = (Division) args[0];

            if (division == null || !division.equals(newDivision)) {
                division = newDivision;

                removeAll();

                initializeComponents();
                initializeLayouts();

                repaint();
                revalidate();
            }
        }
    }


    private class DivisionLayout extends JPanel {

        private String toolTip;
        private ImageIcon icon;
        private Division division;

        DivisionLayout(Division division) {
            super(new BorderLayout());
            this.division = division;
            if (division != null) {

                // Icon
                switch (division.getLevel()) {
                    case 0:
                        icon = level0Icon;
                        break;
                    case 1:
                        icon = level1Icon;
                        break;
                    case 2:
                        icon = level2Icon;
                        break;
                    default:
                        icon = levelXIcon;
                        break;
                }

                // ToolTip
                toolTip = "Level " + division.getLevel();

                // Text field
                ITextField textField = new ITextField(false);
                textField.setText(division.toString());
                add(textField, BorderLayout.CENTER);
            }
        }

        @Override
        public String toString() {
            return "DivisionLayout{" +
                    "division=" + division +
                    "level=" + division.getLevel() +
                    '}';
        }

        String getToolTip() {
            return toolTip;
        }

        public ImageIcon getIcon() {
            return icon;
        }

        public Division getDivision() {
            return division;
        }
    }
}
