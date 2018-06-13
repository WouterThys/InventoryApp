package com.waldo.inventory.gui.components;

import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;

public class IMenuBar extends JPanel {

    private Box box;
    private boolean first;

    public IMenuBar() {
        box = Box.createHorizontalBox();
        first = true;

        setLayout(new BorderLayout());
        add(box, BorderLayout.CENTER);
    }

    private ILabel createGroupTitle(String name) {
        ILabel groupTitleLbl = new ILabel(name, SwingConstants.CENTER);
        groupTitleLbl.setFont(11, Font.ITALIC);
        groupTitleLbl.setForeground(Color.gray);
        return groupTitleLbl;
    }

    private JButton createActionBtn(AbstractAction abstractAction) {
        JButton btn = new JButton(abstractAction);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMinimumSize(new Dimension(124,32));
        btn.setPreferredSize(new Dimension(124,32));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        return btn;
    }

    public void addGroup(String name, int columns, AbstractAction ... actions) {
        //if (!first) {
            box.add(new JSeparator(JSeparator.VERTICAL));
            first = false;
        //}
        if (columns <= 0) {
            columns = 1;
        }
        int rows = actions.length / columns;
        int count = 0;

        JPanel groupPnl = new JPanel(new BorderLayout());
        ILabel groupLbl = createGroupTitle(name);

        Box tbBox = Box.createHorizontalBox();
        for (int i = 0; i < columns; i++) {
            JToolBar groupTb = GuiUtils.createNewToolbar();
            groupTb.setOrientation(JToolBar.VERTICAL);
            groupTb.add(Box.createGlue());
            for (int j = 0; j < rows; j++) {
                if (count < actions.length) {
                    AbstractAction action = actions[count];
                    groupTb.add(createActionBtn(action));
                    count++;
                }
            }
            tbBox.add(groupTb);
        }

        groupPnl.add(groupLbl, BorderLayout.NORTH);
        groupPnl.add(tbBox, BorderLayout.CENTER);

        box.add(groupPnl);
    }

    public void addGroup(String name, AbstractAction ... actions) {
        addGroup(name, 1, actions);
    }
}
