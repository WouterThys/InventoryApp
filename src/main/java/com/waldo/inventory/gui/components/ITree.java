package com.waldo.inventory.gui.components;

import javax.swing.*;
import javax.swing.tree.TreeModel;

public class ITree extends JTree {

    public ITree (TreeModel treeModel) {
        super();

        setModel(treeModel);
    }
}
