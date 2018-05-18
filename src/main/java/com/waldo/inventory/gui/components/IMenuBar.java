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
        btn.setPreferredSize(new Dimension(124,32));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        return btn;
    }

    public void addGroup(String name, boolean addSeparator, AbstractAction ... actions) {
        if (addSeparator) {
            box.add(new JSeparator(JSeparator.VERTICAL));
        }

        JPanel groupPnl = new JPanel(new BorderLayout());
        ILabel groupLbl = createGroupTitle(name);

//        Box groupBox = Box.createVerticalBox();
//        for (AbstractAction action : actions) {
//            groupBox.add(createActionBtn(action));
//        }
        JToolBar groupBox = GuiUtils.createNewToolbar();
        groupBox.setOrientation(JToolBar.VERTICAL);
        groupBox.add(Box.createGlue());
        for (AbstractAction action : actions) {
            groupBox.add(createActionBtn(action));
        }

        groupPnl.add(groupLbl, BorderLayout.NORTH);
        groupPnl.add(groupBox, BorderLayout.CENTER);

        box.add(groupPnl);
    }

    public void addGroup(String name, AbstractAction ... actions) {
        addGroup(name, !first, actions);
        first = false;
    }
}
