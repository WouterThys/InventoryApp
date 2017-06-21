package com.waldo.inventory.gui.components;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;

import static com.waldo.inventory.gui.Application.imageResource;

public class ITree extends JTree {

    public ITree (TreeModel treeModel) {
        super(treeModel);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setClosedIcon(imageResource.readImage("DivisionTree.ClosedIcon"));
        renderer.setOpenIcon(imageResource.readImage("DivisionTree.OpenIcon"));
        renderer.setLeafIcon(imageResource.readImage("DivisionTree.LeafIcon"));

        setCellRenderer(renderer);
    }
}
