package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.ResourceManager;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import java.net.URL;

public class ITree extends JTree {

    public ITree (TreeModel treeModel) {
        super();

        setModel(treeModel);

        URL url = ITree.class.getResource("/settings/Settings.properties");
        ResourceManager resourceManager = new ResourceManager(url.getPath());

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setClosedIcon(resourceManager.readImage("DivisionTree.ClosedIcon"));
        renderer.setOpenIcon(resourceManager.readImage("DivisionTree.OpenIcon"));
        renderer.setLeafIcon(resourceManager.readImage("DivisionTree.LeafIcon"));

        setCellRenderer(renderer);

    }
}
